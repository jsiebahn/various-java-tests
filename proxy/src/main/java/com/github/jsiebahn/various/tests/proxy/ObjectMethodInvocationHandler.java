package com.github.jsiebahn.various.tests.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * An {@link InvocationHandler} for proxies that handles methods declared in {@link Object}. All
 * other methods are delegated.
 *
 * @author jsiebahn
 * @since 21.06.2018
 */
public class ObjectMethodInvocationHandler implements InvocationHandler {

    private static final Method TO_STRING =  getObjectMethod("toString");
    private static final Method EQUALS =  getObjectMethod("equals", Object.class);
    private static final Method HASH_CODE =  getObjectMethod("hashCode");

    /*
     * An instance to delegate most of the object methods to.
     */
    private final Object delegateObject = new Object();

    private InvocationHandler delegate;

    private Class<?> proxyInterface;

    /**
     *
     * @param proxyInterface the interface that is proxied
     * @param delegate the invocation handler to use for unhandled methods
     */
    public ObjectMethodInvocationHandler(Class<?> proxyInterface, InvocationHandler delegate) {
        this.delegate = delegate;
        this.proxyInterface = proxyInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (!Object.class.equals(method.getDeclaringClass())) {
            return delegate.invoke(proxy, method, args);
        }

        if (TO_STRING.equals(method)) {
            return "Proxy(" + proxyInterface.getSimpleName() + ")";
        }

        if (EQUALS.equals(method)) {
            return proxy == args[0] || proxy.hashCode() == args[0].hashCode();
        }

        if (HASH_CODE.equals(method)) {
            return proxyInterface.hashCode();
        }

        return method.invoke(delegateObject, args);
    }

    private static Method getObjectMethod(String name, Class... types) {
        try {
            return Object.class.getMethod(name, types);
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Failed to find method " + name + " in Object.", e);
        }
    }
}