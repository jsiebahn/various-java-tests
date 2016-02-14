package com.github.jsiebahn.various.tests.queue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 14.02.16 08:31
 */
public class ArrayBlockingQueueTest {


    private List<String[]> expectedItems;

    private List<String> actualItems;

    private ArrayBlockingQueue<String> queue;

    @Before
    public void cleanExpectation() {
        actualItems = new ArrayList<>();
        expectedItems = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            int n = random.nextInt(50) + 1;
            String[] strings = new String[n];
            for (int j = 0; j < n; j++) {
                strings[j] = pad(i) + "_test_" + pad(j);
            }
            expectedItems.add(strings);
        }
    }

    private String pad(int i) {
        return (i < 10 ? "0" : "") + i;
    }

    @Test
    public void shouldAddToBigQueue() throws Exception  {

        queue = new ArrayBlockingQueue<>(1000, false);

        doTestQueue();

    }

    @Test
    public void shouldAddToSmallQueue() throws Exception  {

        queue = new ArrayBlockingQueue<>(4, false);

        doTestQueue();

    }

    private void doTestQueue() throws Exception {
        final ExecutorService threadPool = Executors.newFixedThreadPool(5);

        for (int t = 0; t < 5; t++) {
            final int start = t * 4;
            threadPool.submit(() -> {
                for (int i = start; i < start + 4; i++) {
                    String[] strings = expectedItems.get(i);
                    for (String string : strings) {
                        try {
                            queue.offer(string, 5, TimeUnit.SECONDS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("w " + string);
//                        try {
//                            Thread.sleep(1 + new Random().nextInt(5));
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                    }
                }
            });
        }

        threadPool.shutdown();

        new Thread(() -> {
            while (!threadPool.isTerminated() || !queue.isEmpty()) {
                try {
                    String s = queue.poll(1, TimeUnit.SECONDS);
                    actualItems.add(s);
                    Thread.sleep(10);
                    System.out.println("r " + s);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(actualItems);
            for (String[] expectedItem : expectedItems) {
                assertOrder(expectedItem, actualItems);
            }
        }).run();

    }

    private void assertOrder(String[] expectedOrder, List<String> actualItems) {

        int aIndex = 0;
        for (int i = 0; i < expectedOrder.length; i++) {
            String expected = expectedOrder[i];
            boolean found = false;
            while (!found && aIndex < actualItems.size()) {
                if (expected.equals(actualItems.get(aIndex))) {
                    found = true;
                }
                aIndex++;
            }
            Assert.assertTrue("Item " + expected + " not found in expected order.", found);
        }


    }


}
