package com.github.jsiebahn.various.tests.dropwizard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jsiebahn.various.tests.dropwizard.query.CustomQueryParamValueFactoryProvider;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;

import java.util.ServiceLoader;

public class App extends Application<Configuration> {

    public static void main(String[] args) throws Exception {

        ServiceLoader<StringProvider> stringProviders = ServiceLoader.load(StringProvider.class);
        stringProviders.forEach(sp -> System.out.println(sp.getText()));
        // new App().run(args);
    }

    @Override
    public void run(Configuration configuration, Environment environment) {
        environment.jersey().register(new CustomQueryParamValueFactoryProvider.Binder());
        environment.jersey().register(CustomQueryParamController.class);
        new ObjectMapper().readValue()
    }
}
