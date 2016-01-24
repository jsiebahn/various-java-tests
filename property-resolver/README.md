About `PropertyResolver`
========================

`PropertyResolver` provides configuration properties from various places. The
`PropertyResolver` stores these places as `PropertiesContext`. Properties can be
accessed from the `PropertyResolver` singleton instance by static methods.

By default `PropertyResolver` reads properties from `properties` files and system 
properties. The resulting properties contexts are considered in this order:

1. Properties from `application.properties` in the working directory
2. Properties from `application.properties` in the root of the classpath
3. Properties from `System#getProperties()`

In the applications `main(String[])` method properties from the command line arguments can
be initialized using `#initArgs(String[])`.

Command line arguments will be parsed quite simple:

* Two dashes `--` at the beginning will be removed
* The argument is split at the first equals `=` sign
* The argument is ignored if there is no equals `=` sign
* The argument is ignored if it starts with an equals `=` sign
* The part before the first equals sign will be the property key
* The part after the first equals sign will be the property value

The properties from the `ArgumentPropertiesContext` will precede the properties from
other contexts. So the order will be like this after command line arguments are initialized:

1. Properties from command line arguments
2. Properties from `application.properties` in the working directory
3. Properties from `application.properties` in the root of the classpath
4. Properties from `System#getProperties()`

The `PropertyResolver` will look for a property named `config.path` in every
`PropertiesContext`. That property should point to a `properties` file and may be
relative to the working directory. If that file exists and is readable, another
`PropertiesContext` will be created containing the properties of that file. The new context
precedes the context that defined the `config.path`. So the final order will be:

1. Properties from the file located at `config.path`
   defined in `application.properties` in the command line arguments
2. Properties from command line arguments
3. Properties from the file located at `config.path`
   defined in `application.properties` in the working directory
4. Properties from `application.properties` in the working directory
5. Properties from the file located at `config.path`
   defined in `application.properties` in the root of the classpath
6. Properties from `application.properties` in the root of the classpath
7. Properties from the file located at `config.path`
   defined in `System#getProperties()`
8. Properties from `System#getProperties()`

Please be aware that also `PropertiesContext`s from a `config.path` will be checked
for another `config.path` property. To avoid infinite recursion this check will be omitted
after 10 iterations.

Properties files are parsed using `Properties#load(InputStream)`. So they are expected to
be encoded in `ISO 8859-1`.

All in all the behaviour of the `PropertyResolver` is quite similar to property resolving
in Spring Boot. Honestly this is not very surprising because the `PropertyResolver` was
implemented while porting a Spring Boot web application to a more basic framework with a smaller
footprint and faster startup times to run on weak machines.

`PropertyResolver` uses the `slf4j` API for logging.

Usage example:

```java
public class MyApplication {
    public static void main(String[] args) {
        PropertyResolver.initArgs(args);
        String stage = PropertyResolver.stringOf("deployment.stage");
        int port = PropertyResolver.intOf("server.port");
        boolean logProperties = PropertyResolver.booleanOf("config.logProperties");
        if (logProperties) {
            PropertyResolver.logAllProperties();
        }
        // ...
    }
}
```