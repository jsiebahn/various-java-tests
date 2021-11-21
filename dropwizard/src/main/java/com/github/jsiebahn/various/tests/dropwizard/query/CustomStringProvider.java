package com.github.jsiebahn.various.tests.dropwizard.query;

import com.github.jsiebahn.various.tests.dropwizard.StringProvider;

public class CustomStringProvider implements StringProvider {

    private String customString;

    public CustomStringProvider(String customString) {
        this.customString = customString;
    }

    @Override
    public String getText() {
        return "custom: " + customString;
    }
}
