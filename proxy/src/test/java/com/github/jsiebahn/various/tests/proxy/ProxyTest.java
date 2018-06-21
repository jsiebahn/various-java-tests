package com.github.jsiebahn.various.tests.proxy;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author jsiebahn
 * @since 21.06.2018
 */
public class ProxyTest {

    @Test
    public void shouldInvokeRealDefaultMethod() {

        String actual = InterfaceProxyBuilder.createProxy(ToBeProxied.class).getDefaultValue();

        Assert.assertEquals("default", actual);

    }

    @Test
    public void shouldInvokeProxiedMethod() {

        String actual = InterfaceProxyBuilder.createProxy(ToBeProxied.class).getValue("passedThroughByProxy");

        Assert.assertEquals("passedThroughByProxy", actual);

    }
}