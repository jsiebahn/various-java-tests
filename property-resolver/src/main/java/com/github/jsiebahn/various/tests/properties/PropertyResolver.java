package com.github.jsiebahn.various.tests.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * <p>
 * {@code PropertyResolver} provides configuration properties from various places. The
 * {@code PropertyResolver} stores these places as {@link PropertiesContext}. Properties can be
 * accessed from the {@link PropertyResolver} singleton instance by static methods.
 * </p>
 * <p>
 * By default {@code PropertyResolver} reads properties from {@code properties} files and
 * {@link System#getProperties() system properties}. The resulting properties contexts are
 * considered in this order:
 * </p>
 * <ol>
 *   <li>Properties from {@code application.properties} in the working directory</li>
 *   <li>Properties from {@code application.properties} in the root of the classpath</li>
 *   <li>Properties from {@link System#getProperties()}</li>
 * </ol>
 * <p>
 * In the applications {@code main(String[])} method properties from the command line arguments can
 * be initialized using {@link #initArgs(String[])}.
 * </p>
 * <p>
 * Command line arguments will be parsed quite simple:
 * </p>
 * <ul>
 *   <li>Two dashes {@code --} at the beginning will be removed</li>
 *   <li>The argument is split at the first equals {@code =} sign</li>
 *   <li>The argument is ignored if there is no equals {@code =} sign</li>
 *   <li>The argument is ignored if it starts with an equals {@code =} sign</li>
 *   <li>The part before the first equals sign will be the property key</li>
 *   <li>The part after the first equals sign will be the property value</li>
 * </ul>
 * <p>The properties from the {@link ArgumentPropertiesContext} will precede the properties from
 * other contexts. So the order will be like this after command line arguments are initialized:
 * </p>
 * <ol>
 *   <li>Properties from command line arguments</li>
 *   <li>Properties from {@code application.properties} in the working directory</li>
 *   <li>Properties from {@code application.properties} in the root of the classpath</li>
 *   <li>Properties from {@link System#getProperties()}</li>
 * </ol>
 * <p>
 * The {@code PropertyResolver} will look for a property named {@code config.path} in every
 * {@link PropertiesContext}. That property should point to a {@code properties} file and may be
 * relative to the working directory. If that file exists and is readable, another
 * {@link PropertiesContext} will be created containing the properties of that file. The new context
 * precedes the context that defined the {@code config.path}. So the final order will be:
 * </p>
 * <ol>
 *   <li>Properties from the file located at {@code config.path}
 *       defined in {@code application.properties} in the command line arguments</li>
 *   <li>Properties from command line arguments</li>
 *   <li>Properties from the file located at {@code config.path}
 *       defined in {@code application.properties} in the working directory</li>
 *   <li>Properties from {@code application.properties} in the working directory</li>
 *   <li>Properties from the file located at {@code config.path}
 *       defined in {@code application.properties} in the root of the classpath</li>
 *   <li>Properties from {@code application.properties} in the root of the classpath</li>
 *   <li>Properties from the file located at {@code config.path}
 *       defined in {@link System#getProperties()}</li>
 *   <li>Properties from {@link System#getProperties()}</li>
 * </ol>
 * <p>
 * Please be aware that also {@link PropertiesContext}s from a {@code config.path} will be checked
 * for another {@code config.path} property. To avoid infinite recursion this check will be omitted
 * after 10 iterations.
 * </p>
 * <p>
 * Properties files are parsed using {@link Properties#load(InputStream)}. So they are expected to
 * be encoded in {@code ISO 8859-1}.
 * </p>
 * <p>
 * All in all the behaviour of the {@code PropertyResolver} is quite similar to property resolving
 * in Spring Boot. Honestly this is not very surprising because the {@code PropertyResolver} was
 * implemented while porting a Spring Boot web application to a more basic framework with a smaller
 * footprint and faster startup times to run on weak machines.
 * </p>
 * <p>
 * {@code PropertyResolver} uses the {@code slf4j} API for logging.
 * </p>
 * <p>
 * Usage example:
 * </p>
 * <pre>
 *     public static void main(String[] args) {
 *         PropertyResolver.initArgs(args);
 *         String stage = PropertyResolver.stringOf("deployment.stage");
 *         int port = PropertyResolver.intOf("server.port");
 *         boolean logProperties = PropertyResolver.booleanOf("config.logProperties");
 *         if (logProperties) {
 *             PropertyResolver.logAllProperties();
 *         }
 *         // ...
 *     }
 * </pre>
 *
 * @author jsiebahn
 * @since 23.01.16 15:53
 */
public class PropertyResolver {

    /**
     * Logger used to inform about the resolved properties and make debugging of the configuration
     * possible.
     */
    private static final Logger log = LoggerFactory.getLogger(PropertyResolver.class);

    /**
     * The only instance of the property resolver. Will be initialized on first access.
     */
    private static PropertyResolver instance;

    /**
     * The contexts used to resolve properties.
     */
    private List<PropertiesContext> propertiesContexts;


    //
    // public API
    //

    /**
     * Creates a properties context from the given {@code args} and adds it with highest precedence.
     *
     * @param args the arguments to be parsed for defined properties. An argument will be added as
     *             property if it complies to the regex syntax {@code ^(\-\-)?([^ ]+)=(.*)$} where
     *             the second group is considered as key and the third group is considered as value.
     */
    public static void initArgs(String[] args) {
        ArgumentPropertiesContext argumentPropertiesContext = new ArgumentPropertiesContext(args);
        instance().add(argumentPropertiesContext, 0);
    }

    /**
     * Logs all properties from all {@code PropertiesContext}s on {@code INFO} level.
     * Properties are ordered by their keys. Properties that are omitted because they are
     * overridden in a {@code PropertiesContext} with higher precedence are marked with {@code *}.
     */
    public static void logAllProperties() {
        instance().listAll();
    }


    //
    // public methods to resolve property values for various types
    //

    /**
     * @param key the {@code key} of the property
     * @return the {@code integer} value of the property with the given {@code key}. {@code 0} if no
     *         property of {@code key} exists or the property can't be parsed as int.
     */
    public static int intOf(String key) {
        return intOf(key, 0);
    }


    /**
     * @param key the {@code key} of the property
     * @param defaultValue the value to use, when no property with the given {@code key} exists
     * @return the {@code integer} value of the property with the given {@code key}. {@code 0} if no
     *         property of {@code key} exists or the property can't be parsed as int.
     */
    public static int intOf(String key, int defaultValue) {
        String value = stringOf(key, null);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
            log.error("Property {} is not a number but {}", key, value);
            return defaultValue;
        }
    }

    /**
     * @param key the {@code key} of the property
     * @return the {@code long} value of the property with the given {@code key}. {@code 0L} if no
     *         property of {@code key} exists or the property can't be parsed as long.
     */
    public static long longOf(String key) {
        return longOf(key, 0L);
    }

    /**
     * @param key the {@code key} of the property
     * @param defaultValue the value to use, when no property with the given {@code key} exists
     * @return the {@code long} value of the property with the given {@code key}. {@code 0L} if no
     *         property of {@code key} exists or the property can't be parsed as long.
     */
    public static long longOf(String key, long defaultValue) {
        String value = stringOf(key, null);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value);
        }
        catch (NumberFormatException e) {
            log.error("Property {} is not a number but {}", key, value);
            return defaultValue;
        }
    }

    /**
     * @param key the {@code key} of the property
     * @return the {@code float} value of the property with the given {@code key}. {@code 0.0F} if
     *         no property of {@code key} exists or the property can't be parsed as float.
     */
    public static float floatOf(String key) {
        return floatOf(key, 0.0F);
    }

    /**
     * @param key the {@code key} of the property
     * @param defaultValue the value to use, when no property with the given {@code key} exists
     * @return the {@code float} value of the property with the given {@code key}. {@code 0.0F} if
     *         no property of {@code key} exists or the property can't be parsed as float.
     */
    public static float floatOf(String key, float defaultValue) {
        String value = stringOf(key, null);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(value);
        }
        catch (NumberFormatException e) {
            log.error("Property {} is not a number but {}", key, value);
            return defaultValue;
        }
    }


    /**
     * @param key the {@code key} of the property
     * @return the {@code double} value of the property with the given {@code key}. {@code 0.0D} if
     *         no property of {@code key} exists or the property can't be parsed as double.
     */
    public static double doubleOf(String key) {
        return doubleOf(key, 0.0D);
    }

    /**
     * @param key the {@code key} of the property
     * @param defaultValue the value to use, when no property with the given {@code key} exists
     * @return the {@code double} value of the property with the given {@code key}. {@code 0.0D} if
     *         no property of {@code key} exists or the property can't be parsed as double.
     */
    public static double doubleOf(String key, double defaultValue) {
        String value = stringOf(key, null);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value);
        }
        catch (NumberFormatException e) {
            log.error("Property {} is not a number but {}", key, value);
            return defaultValue;
        }
    }

    /**
     * @param key the {@code key} of the property
     * @return the {@code boolean} value of the property with the given {@code key}. {@code false}
     *         if no property of {@code key} exists.
     */
    public static boolean booleanOf(String key) {
        return booleanOf(key, false);
    }

    /**
     * @param key the {@code key} of the property
     * @param defaultValue the value to use, when no property with the given {@code key} exists
     * @return the {@code boolean} value of the property with the given {@code key}. {@code false}
     *         if no property of {@code key} exists.
     */
    public static boolean booleanOf(String key, boolean defaultValue) {
        String value = stringOf(key, null);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    /**
     * @param key the {@code key} of the property
     * @return the {@code string} value of the property with the given {@code key}. {@code ""} if no
     *         property with {@code key} exists.
     */
    public static String stringOf(String key) {
        return stringOf(key, "");
    }

    /**
     * @param key the {@code key} of the property
     * @param defaultValue the value to use, when no property with the given {@code key} exists
     * @return the {@code string} value of the property with the given {@code key}. {@code ""} if no
     *         property with {@code key} exists.
     */
    public static String stringOf(String key, String defaultValue) {
        return instance().getProperty(key, defaultValue);
    }


    //
    // private API
    //

    /**
     * @return the only {@code PropertyResolver} instance
     */
    private static PropertyResolver instance() {
        if (instance == null) {
            instance = new PropertyResolver();
        }
        return instance;
    }

    /**
     * <p>
     * Creates a new {@code PropertyResolver} and adds the following properties contexts:
     * </p>
     * <ul>
     * <li>{@code config.file}s derived from the {@code application.properties} in the working
     *     directory</li>
     * <li>Properties from {@code application.properties} in the working directory</li>
     * <li>{@code config.file}s derived from the {@code application.properties} in the classpath
     *     root</li>
     * <li>Properties from {@code application.properties} in the classpath root</li>
     * <li>{@code config.file}s derived from the {@link EnvironmentPropertiesContext}</li>
     * <li>the {@link EnvironmentPropertiesContext}</li>
     * </ul>
     */
    private PropertyResolver() {
        this.propertiesContexts = new ArrayList<>();

        // system properties
        add(new EnvironmentPropertiesContext(), 0);

        // properties from classpath
        InputStream cpProps = getClass().getResourceAsStream("/application.properties");
        if (cpProps != null) {
            add(new InputStreamPropertiesContext(cpProps, "Classpath application.properties"), 0);
        }

        // properties from working directory
        File props = null;
        try {
            props = new File("application.properties");
            if (props.exists() && props.isFile() && props.canRead()) {
                log.debug("Reading properties from {}", props.getAbsolutePath());
                FileInputStream fileProps = new FileInputStream(props);
                add(new InputStreamPropertiesContext(fileProps,
                        "application.properties in " + props.getParent()), 0);
            }
        }
        catch (Exception e) {
            log.error("Could not read properties from {}", getAbsolutePath(props));
        }

    }

    /**
     * Will add the given {@code propertiesContext} with highest precedence. If {@code iteration}
     * is less than 10, the {@code propertiesContext} is checked for {@code config.file}. The
     * properties from this file will then be added with higher precedence than the given
     * {@code propertiesContext} recursively while {@code iteration} will be incremented. If
     * {@code propertiesContext} is {@code null} it will be ignored.
     *
     * @param propertiesContext the properties context to add.
     * @param iteration the iteration to avoid infinite recursion when adding further contexts from
     *                  found {@code config.file} property.
     */
    private void add(PropertiesContext propertiesContext, int iteration) {
        if (propertiesContext == null) {
            return;
        }
        propertiesContexts.add(0, propertiesContext);
        if (iteration > 9) {
            log.error("Stopping recursive resolving of config.path after 10 iterations " +
                    "in PropertiesContext {}", propertiesContext.getName());
            return;
        }
        String propertiesFile = propertiesContext.getProperties().getProperty("config.path", null);
        if (propertiesFile != null) {
            File configFile = new File(propertiesFile);
            if (!configFile.exists() || !configFile.isFile()) {
                log.warn("Omitting properties of non existent file {} defined in {}",
                        getAbsolutePath(configFile), propertiesContext.getName());
            }
            if (!configFile.canRead()) {
                log.warn("Omitting properties of non readable file {} defined in {}",
                        getAbsolutePath(configFile), propertiesContext.getName());
            }
            try {
                add(new InputStreamPropertiesContext(new FileInputStream(configFile),
                                "config.path=" + propertiesFile + " from " + propertiesContext.getName()),
                        iteration + 1);
            }
            catch (Exception e) {
                log.error("Could not read properties from file {} defined in {}", configFile,
                        propertiesContext.getName());
            }
        }

    }

    /**
     * {@code null} and {@link Exception} safe determination of a {@link File}s absolute path.
     *
     * @param file the {@link File}
     * @return the {@link File#getAbsolutePath() absolute path of a file} or a fallback on error
     */
    private String getAbsolutePath(File file) {
        if (file == null) {
            return "";
        }
        try {
            return file.getAbsolutePath();
        }
        catch (Exception e) {
            return file.getName();
        }
    }

    /**
     * Lists all properties grouped by the {@link PropertiesContext} they belong to in the log.
     * Properties are ordered by their keys. Properties that are omitted because they are
     * overridden in a {@code PropertiesContext} with higher precedence are marked with {@code *}.
     */
    private void listAll() {
        List<String> keys = new ArrayList<>();

        log.info("---------------------------------------");
        for (PropertiesContext propertiesContext : propertiesContexts) {
            boolean hasOutput = false;
            Properties properties = propertiesContext.getProperties();
            log.info("Properties from {}", propertiesContext.getName());
            Set<String> unorderedNames = properties.stringPropertyNames();
            List<String> names = new ArrayList<>(unorderedNames);
            Collections.sort(names);
            for (String key : names) {
                hasOutput = true;
                if (!keys.contains(key)) {
                    keys.add(key);
                    log.info("  {} = {}", key, properties.getProperty(key));
                }
                else {
                    log.info("  * {} = {}", key, properties.getProperty(key));
                }
            }
            if (hasOutput) {
                log.info("---------------------------------------");
            }
        }


    }

    /**
     * Returns the value of the property with the given {@code key} from the
     * {@code PropertiesContext} with the highest precedence where the property is defined. If no
     * {@code PropertiesContext} contains a property with the given {@code key}, the given
     * {@code defaultValue} is returned.
     *
     * @param key the key of the property
     * @param defaultValue the value to use, if the property is not defined in any
     *                     {@code PropertiesContext}
     * @return the value of the property of the given {@code key} from the {@link PropertiesContext}
     *         with the highest precedence that contains the {@code key}. The {@code defaultValue}
     *         if the {@code key} contains in no {@code PropertiesContext}. May return {@code null}
     *         even if the {@code defaultValue} is not {@code null} in rare circumstances where the
     *         {@code key} exists having the value {@code null} stored in the
     *         {@code PropertiesContext}.
     */
    private String getProperty(String key, String defaultValue) {

        for (PropertiesContext propertiesContext : propertiesContexts) {
            Properties properties = propertiesContext.getProperties();
            if (properties == null) {
                continue;
            }
            if (properties.keySet().contains(key)) {
                return properties.getProperty(key);
            }
        }

        return defaultValue;

    }

    /**
     * A named context holding properties.
     */
    private interface PropertiesContext {

        /**
         * @return the properties of this context. Should never return {@code null}.
         */
        Properties getProperties();

        /**
         * @return a name to identify the source of this {@code PropertiesContext}
         */
        String getName();

    }

    /**
     * A {@link PropertiesContext} retrieving {@link Properties} from arguments.
     * See {@link PropertyResolver#initArgs(String[])}
     */
    private static class ArgumentPropertiesContext implements PropertiesContext {

        private Properties properties;

        public ArgumentPropertiesContext(String[] args) {
            log.debug("Reading from arguments: {}", (Object) args);
            properties = new Properties();
            for (String arg : args) {
                if (arg == null || arg.trim().isEmpty()) {
                    continue;
                }
                if (arg.startsWith("--")) {
                    arg = arg.substring(2);
                }
                int equalsIndex = arg.indexOf("=");
                if (equalsIndex < 1) {
                    // ignore properties without key/value
                    continue;
                }
                String key = arg.substring(0, equalsIndex);
                try {
                    String value = arg.substring(equalsIndex + 1);
                    properties.setProperty(key, value);
                }
                catch (IndexOutOfBoundsException e) {
                    properties.setProperty(key, "");
                }

            }

        }

        @Override
        public Properties getProperties() {
            return properties;
        }

        @Override
        public String getName() {
            return "Arguments";
        }
    }

    /**
     * A {@link PropertiesContext} reading {@link Properties} from an {@link InputStream} where the
     * {@code InputStream} represents the {@code ISO 8859-1} encoded content of a
     * {@code *.properties} file.
     */
    private static class InputStreamPropertiesContext implements PropertiesContext {

        private Properties properties;

        private String name;

        public InputStreamPropertiesContext(InputStream propertiesFileContent, String name) {
            this.name = name;
            properties = new Properties();
            try {
                properties.load(propertiesFileContent);
            } catch (IOException e) {
                log.error("Could not read properties from InputStream.");
            }
        }

        @Override
        public Properties getProperties() {
            return properties;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    /**
     * A {@link PropertiesContext} providing the {@link System#getProperties() system properties}.
     */
    private static class EnvironmentPropertiesContext implements PropertiesContext {

        private boolean firstTry = true;

        @Override
        public Properties getProperties() {
            try {
                return System.getProperties();
            } catch (SecurityException e) {
                if (firstTry) {
                    log.warn("Could not read System properties.");
                    firstTry = false;
                }
            }
            return new Properties();
        }

        @Override
        public String getName() {
            return "System Environment";
        }
    }

}
