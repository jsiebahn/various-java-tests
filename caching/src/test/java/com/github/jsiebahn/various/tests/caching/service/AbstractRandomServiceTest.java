package com.github.jsiebahn.various.tests.caching.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 18.01.15 08:14
 */
public abstract class AbstractRandomServiceTest {

    @Autowired
    private RandomService randomService;

    @Test
    public void testCreateRandomNumberAndEvictAll() throws Exception {

        int valueMax1 = randomService.createRandomNumber(Integer.MAX_VALUE);
        int valueMax2 = randomService.createRandomNumber(Integer.MAX_VALUE);
        int valueHalf1 = randomService.createRandomNumber(Integer.MAX_VALUE / 2);
        int valueHalf2 = randomService.createRandomNumber(Integer.MAX_VALUE / 2);

        // value*2 should be loaded from cache
        assertEquals(valueMax1, valueMax2);
        assertEquals(valueHalf1, valueHalf2);
        assertNotEquals(valueMax1, valueHalf1);

        randomService.evict();
        int valueMax3 = randomService.createRandomNumber(Integer.MAX_VALUE);
        int valueHalf3 = randomService.createRandomNumber(Integer.MAX_VALUE / 2);

        // value*3 should be new because the cache is evicted
        assertNotEquals(valueMax1, valueMax3);
        assertNotEquals(valueHalf1, valueHalf3);
        assertNotEquals(valueMax3, valueHalf3);

    }

    @Test
    public void testCreateRandomNumberAndEvictOne() throws Exception {

        int valueMax1 = randomService.createRandomNumber(Integer.MAX_VALUE);
        int valueMax2 = randomService.createRandomNumber(Integer.MAX_VALUE);
        int valueHalf1 = randomService.createRandomNumber(Integer.MAX_VALUE / 2);
        int valueHalf2 = randomService.createRandomNumber(Integer.MAX_VALUE / 2);

        // value*2 should be loaded from cache
        assertEquals(valueMax1, valueMax2);
        assertEquals(valueHalf1, valueHalf2);
        assertNotEquals(valueMax1, valueHalf1);

        randomService.evict(Integer.MAX_VALUE);
        int valueMax3 = randomService.createRandomNumber(Integer.MAX_VALUE);
        int valueHalf3 = randomService.createRandomNumber(Integer.MAX_VALUE / 2);

        // valueMax3 should be new because the cache is evicted
        assertNotEquals(valueMax1, valueMax3);
        assertEquals(valueHalf1, valueHalf3);
        assertNotEquals(valueMax3, valueHalf3);

    }

}
