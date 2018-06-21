package com.github.jsiebahn.various.tests.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Creates instances of interface types using {@link Proxy}.
 *
 * @author jsiebahn
 * @since 21.06.2018
 */
public class InterfaceProxyBuilder {

    public static <I> I createProxy(Class<I> interfaceType) {

        InvocationHandler invocationHandler = (proxy, method, args) -> {
            // TODO temporary implementation for the one and only test class
            if ("getValue".equals(method.getName()) && method.getParameterCount() == 1 && String.class
                    .equals(method.getParameterTypes()[0])) {
                return args[0];
            }
            return null;
        };

        Object proxyInstance = Proxy.newProxyInstance(InterfaceProxyBuilder.class.getClassLoader(),
                new Class[] { interfaceType },
                new DefaultMethodInvocationHandler(invocationHandler)
        );

        //noinspection unchecked
        return (I) proxyInstance;

    }


}