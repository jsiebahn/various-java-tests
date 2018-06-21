package com.github.jsiebahn.various.tests.proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author jsiebahn
 * @since 21.06.2018
 */
public class ProxyTest {

    @Test
    public void shouldInvokeRealDefaultMethod() {

        String actual = InterfaceProxyBuilder.createProxy(ToBeProxied.class).getDefaultValue();

        assertEquals("default", actual);

    }

    @Test
    public void shouldInvokeProxiedMethod() {

        ToBeProxied proxy = InterfaceProxyBuilder.createProxy(ToBeProxied.class);
        String actual = proxy.getValue("passedThroughByProxy");

        assertEquals("passedThroughByProxy", actual);

    }

    @Test
    public void shouldNotFailWhenCallingObjectEquals() {
        ToBeProxied proxy = InterfaceProxyBuilder.createProxy(ToBeProxied.class);
        ToBeProxied proxy2 = InterfaceProxyBuilder.createProxy(ToBeProxied.class);

        //noinspection SimplifiableJUnitAssertion
        assertTrue(proxy.equals(proxy2));
    }

    @Test
    public void shouldBeEqualWithItself() {
        ToBeProxied proxy = InterfaceProxyBuilder.createProxy(ToBeProxied.class);

        //noinspection SimplifiableJUnitAssertion,EqualsWithItself
        assertTrue(proxy.equals(proxy));
    }

    @Test
    public void shouldNotFailWhenCallingObjectHashCode() {
        ToBeProxied proxy = InterfaceProxyBuilder.createProxy(ToBeProxied.class);

        assertTrue(proxy.hashCode() > 0);
    }

    @Test
    public void shouldNotFailWhenCallingObjectToString() {
        ToBeProxied proxy = InterfaceProxyBuilder.createProxy(ToBeProxied.class);

        assertTrue(proxy.toString().contains("ToBeProxied"));
    }

    @Test
    public void shouldNotFailWhenCallingObjectNotify() {
        ToBeProxied proxy = InterfaceProxyBuilder.createProxy(ToBeProxied.class);

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (proxy) {
            proxy.notify();
        }
    }

    @Test
    public void shouldNotFailWhenCallingObjectNotifyAll() {
        ToBeProxied proxy = InterfaceProxyBuilder.createProxy(ToBeProxied.class);

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (proxy) {
            proxy.notifyAll();
        }
    }

    @Test
    public void shouldNotFailWhenCallingObjectWait() throws InterruptedException {
        ToBeProxied proxy = InterfaceProxyBuilder.createProxy(ToBeProxied.class);

        synchronizedDelayInNewThread(proxy);

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (proxy) {
            proxy.wait();
        }
    }

    @Test
    public void shouldNotFailWhenCallingObjectWaitOneParam() throws InterruptedException {
        ToBeProxied proxy = InterfaceProxyBuilder.createProxy(ToBeProxied.class);

        synchronizedDelayInNewThread(proxy);

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (proxy) {
            proxy.wait(1000);
        }
    }

    @Test
    public void shouldNotFailWhenCallingObjectWaitTwoParams() throws InterruptedException {
        ToBeProxied proxy = InterfaceProxyBuilder.createProxy(ToBeProxied.class);

        synchronizedDelayInNewThread(proxy);

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (proxy) {
            proxy.wait(1000, 10);
        }
    }

    @Test
    public void shouldConsiderProxyInstanceToBeInstanceOfProxyInterface() {
        ToBeProxied proxy = InterfaceProxyBuilder.createProxy(ToBeProxied.class);

        //noinspection ConstantConditions
        assertTrue(proxy instanceof ToBeProxied);
    }

    private void synchronizedDelayInNewThread(ToBeProxied proxy) {
        new Thread(() -> {
            synchronized (proxy) {
                try {
                    Thread.sleep(50);
                }
                catch (InterruptedException ignored) {
                }
                proxy.notifyAll();
            }
        }).start();
    }

}