package com.github.jsiebahn.various.tests.caching.config;

import com.github.jsiebahn.various.tests.caching.service.RandomService;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 17.01.15 09:49
 */
@Configuration
@EnableCaching
@ComponentScan(basePackageClasses = RandomService.class)
public class SpringCacheXmlConfig implements CachingConfigurer {

    @Override
    public CacheManager cacheManager() {
        EhCacheCacheManager cacheManager = new EhCacheCacheManager();
        cacheManager.setCacheManager(ehcache().getObject());
        return cacheManager;
    }


    @Bean
    public EhCacheManagerFactoryBean ehcache() {
        EhCacheManagerFactoryBean manager = new EhCacheManagerFactoryBean();
        manager.setConfigLocation(new ClassPathResource("ehcache.xml", this.getClass()));
        return manager;
    }

    @Override // required with spring 4.2.4
    public CacheResolver cacheResolver() {
        return null; // not required for this simple configuration example
    }

    @Override
    public KeyGenerator keyGenerator() {
        return null; // not required for this simple configuration example
    }

    @Override // required with spring 4.2.4
    public CacheErrorHandler errorHandler() {
        return null;  // not required for this simple configuration example
    }
}
