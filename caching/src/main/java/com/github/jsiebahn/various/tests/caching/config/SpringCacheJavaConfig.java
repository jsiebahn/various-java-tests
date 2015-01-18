package com.github.jsiebahn.various.tests.caching.config;

import com.github.jsiebahn.various.tests.caching.builder.EhCacheConfigurationBuilder;
import com.github.jsiebahn.various.tests.caching.service.RandomService;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 17.01.15 09:49
 */
@Configuration
@EnableCaching
@ComponentScan(basePackageClasses = RandomService.class)
public class SpringCacheJavaConfig implements CachingConfigurer {

    @Override
    public CacheManager cacheManager() {
        EhCacheCacheManager cacheManager = new EhCacheCacheManager();
        cacheManager.setCacheManager(ehCacheManager());
        return cacheManager;
    }

    /**
     * The CacheManager for eh cache does NOT use the provided ehcache.xml resource. It is
     * completely configured with Java config. An example using xml configuration is shown as
     * comment below.
     *
     * The used builder is a custom implementation for
     * convenience.
     */
    @Bean(destroyMethod="shutdown") // to correctly shut down the cache manager
    public net.sf.ehcache.CacheManager ehCacheManager() {

        return new net.sf.ehcache.CacheManager(
                // create default cache
                EhCacheConfigurationBuilder.createDefaultCache()
                        // configure the default cache
                        .withEternal(false)
                        .withTimeToIdleSeconds(120)
                        .withTimeToLiveSeconds(120)
                        .withDiskExpiryThreadIntervalSeconds(120)
                        .withMemoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU)
                        .withMaxElementsInMemory(100)
                        .withMaxElementsOnDisk(10000)

                        // create specific caches based on default cache configuration
                        .createCacheFromDefault().withName("randomService")

                        // set global configuration properties
                        .withDiskStorePath(System.getProperty("java.io.tmpdir"))

                        // create the complete cache configuration with all caches
                        .build()
        );

    }

//    @Override // required with spring 4.2.4
//    public CacheResolver cacheResolver() {
//        return null; // not required for this simple configuration example
//    }

    @Override
    public KeyGenerator keyGenerator() {
        return null; // not required for this simple configuration example
    }

//    @Override // required with spring 4.2.4
//    public CacheErrorHandler errorHandler() {
//        return null;  // not required for this simple configuration example
//    }
}
