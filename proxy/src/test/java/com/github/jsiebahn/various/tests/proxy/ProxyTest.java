package com.github.jsiebahn.various.tests.proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationHandler;

import com.github.jsiebahn.various.tests.proxy.elsewhere.ToBeProxied;
import org.junit.Test;

/**
 * @author jsiebahn
 * @since 21.06.2018
 */
public class ProxyTest {

    private InvocationHandler invocationHandler = (proxy, method, args) -> {
        if ("getValue".equals(method.getName()) && method.getParameterCount() == 1 && String.class
                .equals(method.getParameterTypes()[0])) {
            return "getValue received argument: " + args[0];
        }
        return null;
    };

    private final ToBeProxied proxy = InterfaceProxyBuilder.createProxy(
            ToBeProxied.class, invocationHandler);

    @Test
    public void shouldInvokeRealDefaultMethod() {

        String actual = proxy.getDefaultValue();

        assertEquals("getValue received argument: default", actual);

    }

    @Test
    public void shouldInvokeProxiedMethod() {

        String actual = proxy.getValue("passedThroughByProxy");

        assertEquals("getValue received argument: passedThroughByProxy", actual);

    }

    @Test
    public void shouldNotFailWhenCallingObjectEquals() {
        ToBeProxied proxy2 = InterfaceProxyBuilder.createProxy(ToBeProxied.class, (proxy1, method, args) -> null);

        //noinspection SimplifiableJUnitAssertion
        assertTrue(proxy.equals(proxy2));
    }

    @Test
    public void shouldBeEqualWithItself() {
        //noinspection SimplifiableJUnitAssertion,EqualsWithItself
        assertTrue(proxy.equals(proxy));
    }

    @Test
    public void shouldNotFailWhenCallingObjectHashCode() {
        assertTrue(proxy.hashCode() > 0);
    }

    @Test
    public void shouldNotFailWhenCallingObjectToString() {
        assertTrue(proxy.toString().contains("ToBeProxied"));
    }

    @Test
    public void shouldNotFailWhenCallingObjectNotify() {
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (proxy) {
            proxy.notify();
        }
    }

    @Test
    public void shouldNotFailWhenCallingObjectNotifyAll() {
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (proxy) {
            proxy.notifyAll();
        }
    }

    @Test
    public void shouldNotFailWhenCallingObjectWait() throws InterruptedException {
        synchronizedDelayInNewThread(proxy);

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (proxy) {
            proxy.wait();
        }
    }

    @Test
    public void shouldNotFailWhenCallingObjectWaitOneParam() throws InterruptedException {
        synchronizedDelayInNewThread(proxy);

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (proxy) {
            proxy.wait(1000);
        }
    }

    @Test
    public void shouldNotFailWhenCallingObjectWaitTwoParams() throws InterruptedException {
        synchronizedDelayInNewThread(proxy);

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (proxy) {
            proxy.wait(1000, 10);
        }
    }

    @Test
    public void shouldConsiderProxyInstanceToBeInstanceOfProxyInterface() {
        //noinspection ConstantConditions
        assertTrue(proxy instanceof ToBeProxied);
    }

    private void synchronizedDelayInNewThread(Object o) {
        new Thread(() -> {
            synchronized (o) {
                try {
                    Thread.sleep(50);
                }
                catch (InterruptedException ignored) {
                }
                o.notifyAll();
            }
        }).start();
    }

}