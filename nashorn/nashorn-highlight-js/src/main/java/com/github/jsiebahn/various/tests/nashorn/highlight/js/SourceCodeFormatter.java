package com.github.jsiebahn.various.tests.nashorn.highlight.js;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * <p>
 * Utility used to format source code as Html text. {@code SourceCodeFormatter} internally uses
 * <a href="https://highlightjs.org/">highlight.js</a> in version 9.1.0 through the {@code nashorn}
 * engine.
 * </p>
 * <p>Example:</p>
 * <pre>
 *     SourceCodeFormatter formatter = new SourceCodeFormatter();
 *     String html = formatter.formatSourceCode("body {height: 100%;}", "css");
 * </pre>
 * <p>
 * The {@code SourceCodeFormatter} comes without styles for the generated Html code. But
 * {@code highlight.js} has a lot of themes. Choose your favourite at
 * <a href="https://highlightjs.org/static/demo/">the highlight.js demo page</a> or check the
 * <a href="https://github.com/isagalaev/highlight.js">GitHub repository of Ivan Sagalaevs</a>.
 * </p>
 *
 * @author jsiebahn
 * @since 30.01.16 08:15
 */
public class SourceCodeFormatter {

    private static final Logger log = LoggerFactory.getLogger(SourceCodeFormatter.class);

    /**
     * The engine that executes the JavaScript code.
     */
    private ScriptEngine engine;

    /**
     * Derived from the {@link #engine}. Used to execute JavaScript functions.
     */
    private Invocable highlightJsAdapter;

    /**
     * Cache of languages that have already been added to the {@link #engine}. Used to avoid reading
     * them twice.
     */
    private List<String> addedLanguages = new ArrayList<>();

    /**
     * The aliases derived from the {@code alias.properties}. The key is the required language and
     * the value contains the language name of the parser.
     */
    private Properties aliases;

    /**
     * Initializes the {@link #engine} with the JavaScript adapter and the main highlight.js script.
     */
    public SourceCodeFormatter() {

        aliases = new Properties();
        try {
            aliases.load(getClass().getResourceAsStream("aliases.properties"));
        } catch (IOException e) {
            log.error("Could not read aliases.");
        }

        this.engine = new ScriptEngineManager().getEngineByName("nashorn");

        evalScriptResource("adapter.js");
        evalScriptResource("highlightJs/highlight.js");

        this.highlightJsAdapter = (Invocable) engine;

    }

    /**
     * Formats the given source code assuming it represents a source of the given language.
     *
     * @param sourceCode a raw source code string
     * @param language the name of the language. Must be one of the property keys in
     *      {@link #aliases}
     * @return the html formatted source code containing {@code span} with hljs css classes to
     *      highlight keywords, comments and other syntax. Falls back to the given source code if
     *      any error occurs.
     */
    public String formatSourceCode(String sourceCode, String language) {

        addLanguage(language);

        Object result = sourceCode;
        try {
            result = highlightJsAdapter.invokeFunction("highlight", language, sourceCode);
        } catch (ScriptException e) {
            log.error("Script execution of highlight.js failed for language {}.", language, e);
        } catch (NoSuchMethodException e) {
            log.error("Executing JavaScript function 'highlight' failed for language {}." +
                    "Is adapter.js added to the engine?", language, e);
        }
        return result.toString();
    }


    //
    // helper
    //

    /**
     * Adds and evals the given JavaScript resource to the {@link #engine}.
     *
     * @param scriptResourceName the resource name and path, may be relative to this package
     */
    private void evalScriptResource(String scriptResourceName) {
        try (InputStream scriptResource = getClass().getResourceAsStream(scriptResourceName)) {
            engine.eval(new InputStreamReader(scriptResource));
        } catch (ScriptException e) {
            log.error("Could not parse {}", scriptResourceName);
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error("Could not read {}", scriptResourceName);
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param language the language to add, e.g. {@code css} is to be expected a js file in the
     *                 languages dir of the classpath relative to this class. Aliases will be
     *                 derived from {@code aliases.properties}.
     */
    private void addLanguage(String language) {

        String realLanguage = aliases.getProperty(language);
        if (realLanguage == null) {
            log.error("Could not find the real language for given {}", language);
            return;
        }

        if (addedLanguages.contains(realLanguage)) {
            log.debug("Already added {}", realLanguage);
            return;
        }

        if (!realLanguage.equals(language)) {
            log.info("Found real language {} for alias {}", realLanguage, language);
        }

        log.debug("Adding language {} to highlight.js", realLanguage);

        String scriptResourceName = "highlightJs/languages/" + realLanguage + ".js";
        StringBuilder scriptFunction = new StringBuilder();

        try (InputStream scriptSource = getClass()
                .getResourceAsStream(scriptResourceName)) {
            int nRead;
            byte[] data = new byte[1];
            while ((nRead = scriptSource.read(data, 0, data.length)) != -1) {
                if (nRead > 0) {
                    scriptFunction.append((char) data[0]);
                }
            }
        }
        catch (IOException e) {
            log.error("Could not read language function from classpath resource '{}'",
                    scriptResourceName, e);
            return;
        }
        catch (NullPointerException e) {
            log.error("Could not read language {}. Resource does not exist.",
                    realLanguage, realLanguage);
            // just add to avoid continuous retry: non existent resource is unrecoverable
            addedLanguages.add(realLanguage);
            return;
        }

        String script = String.format("require('hljs').registerLanguage('%s', %s);",
                language, scriptFunction.toString());
        log.debug("Adding {} by using require call \n {}", realLanguage, script);

        try {
            engine.eval(new StringReader(script));
        } catch (ScriptException e) {
            log.error("Could not register language {}", realLanguage, e);
        }

        addedLanguages.add(realLanguage);
    }

}
