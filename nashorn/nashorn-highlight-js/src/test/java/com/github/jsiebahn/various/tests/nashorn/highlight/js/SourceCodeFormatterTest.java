package com.github.jsiebahn.various.tests.nashorn.highlight.js;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

/**
 * @author jsiebahn
 * @since 30.01.16 08:29
 */
public class SourceCodeFormatterTest {

    private static final Logger log = LoggerFactory.getLogger(SourceCodeFormatter.class);

    @Test
    public void testFormatSourceCode() throws Exception {

        SourceCodeFormatter formatter = new SourceCodeFormatter();

        String formattedSourceCode;

        long start = System.currentTimeMillis();

        // CSS
        formattedSourceCode = formatter.formatSourceCode("body.foo { color: #123;}", "css");
        assertEquals("<span class=\"hljs-selector-tag\">body</span><span " +
                "class=\"hljs-selector-class\">.foo</span> { <span " +
                "class=\"hljs-attribute\">color</span>: <span class=\"hljs-number\">#123</span>;}",
                formattedSourceCode);

        log.info("Formatting one source took {}ms", System.currentTimeMillis() - start);

        // HTML
        formattedSourceCode = formatter.formatSourceCode("<p><b>Foo</b></p>", "html");
        assertEquals("<span class=\"hljs-tag\">&lt;<span class=\"hljs-name\">p</span>&gt;" +
                "</span><span class=\"hljs-tag\">&lt;<span class=\"hljs-name\">b</span>&gt;" +
                "</span>Foo<span class=\"hljs-tag\">&lt;/<span class=\"hljs-name\">b</span>&gt;" +
                "</span><span class=\"hljs-tag\">&lt;/<span class=\"hljs-name\">p</span>&gt;</span>",
                formattedSourceCode);


        log.info("Formatting two sources took {}ms", System.currentTimeMillis() - start);

        // HTML
        formattedSourceCode = formatter.formatSourceCode("<h1>Foo</h1>", "html");
        assertEquals("<span class=\"hljs-tag\">&lt;<span class=\"hljs-name\">h1</span>&gt;" +
                "</span>Foo<span class=\"hljs-tag\">&lt;/<span class=\"hljs-name\">h1</span>&gt;" +
                "</span>",
                formattedSourceCode);


        log.info("Formatting three sources took {}ms", System.currentTimeMillis() - start);

    }
}