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

    /**
     *
     * @param interfaceType interface type that should be implemented as proxy
     * @param invocationHandler the invocation handler that should handle invoked methods that are
     *      not {@code default} interface implementations and not methods declared in {@link Object}
     * @param <I> the type of the proxy to implement
     * @return the interface proxy instance
     */
    public static <I> I createProxy(Class<I> interfaceType, InvocationHandler invocationHandler) {

        if (!interfaceType.isInterface()) {
            throw new IllegalArgumentException(
                    "Only interfaces can be proxied with the " + InterfaceProxyBuilder.class);
        }

        Object proxyInstance = Proxy.newProxyInstance(InterfaceProxyBuilder.class.getClassLoader(),
                new Class[] { interfaceType },
                new DefaultMethodInvocationHandler(
                        new ObjectMethodInvocationHandler(interfaceType, invocationHandler)
                )
        );

        //noinspection unchecked
        return (I) proxyInstance;

    }


}