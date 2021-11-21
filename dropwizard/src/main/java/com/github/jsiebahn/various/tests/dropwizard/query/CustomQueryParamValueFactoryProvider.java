package com.github.jsiebahn.various.tests.dropwizard.query;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.spi.internal.ValueFactoryProvider;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

/**
 * Maps query params to a request scoped {@link Custom} instance.
 */
@Singleton
public class CustomQueryParamValueFactoryProvider implements ValueFactoryProvider { // ValueFactoryProvider from .internal. required :(

    @Context
    private UriInfo uriInfo;

    private ServiceLocator serviceLocator;


    @Inject
    protected CustomQueryParamValueFactoryProvider(ServiceLocator locator) {
        this.serviceLocator = locator;
    }


    @Override
    public Factory<?> getValueFactory(Parameter parameter) {
        if (!parameter.isAnnotationPresent(CustomQueryParam.class) || !Custom.class.equals(parameter.getRawType())) {
            return null;
        }
        Factory<Custom> valueFactory = new Factory<Custom>() {
            @Override
            public Custom provide() {
                MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters();
                // TODO here the Custom has to be extracted from known query params
                return new Custom(queryParameters);
            }

            @Override
            public void dispose(Custom instance) {
                // intentionally empty
            }
        };
        serviceLocator.inject(valueFactory);
        return valueFactory;
    }

    @Override
    public PriorityType getPriority() {
        return Priority.NORMAL;
    }

    public static class Binder extends AbstractBinder {

        @Override
        protected void configure() {
            bind(CustomQueryParamValueFactoryProvider.class).to(ValueFactoryProvider.class).in(Singleton.class);
        }
    }

}
