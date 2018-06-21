package com.github.jsiebahn.various.tests.proxy;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * An {@link InvocationHandler} that invokes the real implementation of {@code default} methods
 * declared in interfaces. If the invoked method is not a {@code default} method, a configurable
 * delegate {@link InvocationHandler} is used.
 *
 * @author jsiebahn
 * @since 21.06.2018
 */
public class DefaultMethodInvocationHandler implements InvocationHandler {

    private InvocationHandler delegate;

    private double javaVersion;

    /*
     * Used for JDK8 to create {@link MethodHandles.Lookup} with private access privileges.
     *
     * @see <a href="https://stackoverflow.com/a/26211382">Stackoverflow: Java8 dynamic proxy and default methods</a>
     */
    private Constructor<MethodHandles.Lookup> lookupConstructor;

    /**
     * @param delegate the {@link InvocationHandler} to use, if the invoked method is not a default
     *      method
     */
    public DefaultMethodInvocationHandler(InvocationHandler delegate) {
        this.delegate = delegate;
        this.javaVersion = Double.parseDouble(System.getProperty("java.specification.version"));
        if (javaVersion < 1.9) {
            this.lookupConstructor = initLookupConstructorForJdk8();
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (!method.isDefault()) {
            return delegate.invoke(proxy, method, args);
        }

        // lookup constructor is set and accessible in JDK8
        if (javaVersion < 1.9) {
            if (this.lookupConstructor != null) {
                return invokeJdk8(proxy, method, args);
            }
            else {
                throw new UnsupportedOperationException("Failed to invoke default method "
                        + method + ". Maybe there are security restrictions in the JVM that "
                        + "prevent using setAccessible(true) for "
                        + "MethodHandles.Lookup(Class<?>, int). In this environment proxied "
                        + "interfaces can't declare default methods.");
            }
        }

        return invokeJdk9Plus(proxy, method, args);

    }

    /**
     * Uses the private {@link #lookupConstructor} of {@link MethodHandles.Lookup} to create a
     * {@link MethodHandle} for the default method to invoke.
     *
     * @see <a href="https://stackoverflow.com/a/26211382">Stackoverflow: Java8 dynamic proxy and default methods</a>
     */
    private Object invokeJdk8(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> declaringClass = method.getDeclaringClass();
        MethodHandles.Lookup lookup = this.lookupConstructor
                .newInstance(declaringClass, MethodHandles.Lookup.PRIVATE);
        MethodHandles.Lookup in = lookup.in(declaringClass);
        MethodHandle methodHandle = in.unreflectSpecial(method, declaringClass);
        MethodHandle boundMethodHandle = methodHandle.bindTo(proxy);
        return boundMethodHandle.invokeWithArguments(args);
    }

    /**
     * JDK9 and above allow access by setting the specialCaller to the same value as the reference
     * class. There is no need to invoke the private constructor. There is also no way to invoke it
     * because it can't be set accessible in newer JDK versions.
     *
     * @see <a href="http://mail.openjdk.java.net/pipermail/jigsaw-dev/2017-January/010741.html">OpenJDK Mailing List</a>
     */
    private Object invokeJdk9Plus(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> declaringClass = method.getDeclaringClass();
        MethodHandle methodHandle = MethodHandles.lookup().findSpecial(
                declaringClass,
                method.getName(),
                MethodType.methodType(method.getReturnType()),
                declaringClass);
        MethodHandle boundMethodHandle = methodHandle.bindTo(proxy);
        return boundMethodHandle.invokeWithArguments(args);
    }

    private Constructor<MethodHandles.Lookup> initLookupConstructorForJdk8() {
        try {
            Constructor<MethodHandles.Lookup> lookupConstructor = MethodHandles.Lookup.class
                    .getDeclaredConstructor(Class.class, int.class);
            lookupConstructor.setAccessible(true);
            return lookupConstructor;
        }
        catch (NoSuchMethodException e) {
            throw new IllegalAccessError(
                    "Unexpected Api: MethodHandles.Lookup has not the expected Constructor");
        }
        catch (SecurityException ignored) {
            // Ignore the SecurityException.
            // If nobody declares or invokes default methods, there will be no problem.
            return null;
        }
    }
}