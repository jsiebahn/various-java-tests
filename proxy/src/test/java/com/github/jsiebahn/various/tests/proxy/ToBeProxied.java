package com.github.jsiebahn.various.tests.proxy;

/**
 * @author jsiebahn
 * @since 21.06.2018
 */
public interface ToBeProxied {

    String getValue(String value);

    default String getDefaultValue() {
        return getValue("default");
    }

}