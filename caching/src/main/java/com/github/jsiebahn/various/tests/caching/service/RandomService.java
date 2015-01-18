package com.github.jsiebahn.various.tests.caching.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 17.01.15 09:45
 */
@Service
public class RandomService {

    @Cacheable("randomService")
    public int createRandomNumber(int max) {
        return (int) Math.floor(Math.random() * max);
    }

    @CacheEvict(value = "randomService", allEntries = true)
    public void evict() {
        // intentionally do nothing
    }

    @CacheEvict(value = "randomService")
    public void evict(int max) {
        // intentionally do nothing
    }

}
