package com.github.jsiebahn.various.tests.caching.builder;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.DiskStoreConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Fluent Builder for creating the {@link Configuration} of caches for using
 * {@link org.springframework.cache.annotation.Cacheable} with EhCache.
 * </p>
 * <p>
 * When using this java based approach, no xml configuration file for the ehcache is needed.
 * </p>
 * <p>
 * The builder is used to provide the {@link Configuration} for a
 * {@link net.sf.ehcache.CacheManager} in a Spring
 * {@link org.springframework.context.annotation.Configuration} class.
 * </p>
 * <p>
 * Example:
 * </p>
 * <pre>
 * @Configuration
 * @EnableCaching
 * public class SpringCacheJavaConfig implements CachingConfigurer {
 *
 *     @Override
 *     public CacheManager cacheManager() {
 *         EhCacheCacheManager cacheManager = new EhCacheCacheManager();
 *         cacheManager.setCacheManager(ehCacheManager());
 *         return cacheManager;
 *     }
 *
 *     @Bean(destroyMethod="shutdown")
 *     public net.sf.ehcache.CacheManager ehCacheManager() {
 *
 *         return new net.sf.ehcache.CacheManager(
 *                 // create default cache
 *                 EhCacheConfigurationBuilder.createDefaultCache()
 *                 // configure the default cache
 *                 .withEternal(false)
 *                 .withTimeToIdleSeconds(120)
 *                 .withTimeToLiveSeconds(120)
 *                 .withDiskExpiryThreadIntervalSeconds(120)
 *                 .withMemoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU)
 *                 .withMaxElementsInMemory(100)
 *                 .withMaxElementsOnDisk(10000)
 *
 *                 // create specific caches based on default cache configuration
 *                 .createCacheFromDefault().withName("aSpecificCache")
 *
 *                 // set global configuration properties
 *                 .withDiskStorePath(System.getProperty("java.io.tmpdir"))
 *
 *                 // create the complete cache configuration with all caches
 *                 .build()
 *             );
 *     }
 *
 *     // ...
 *
 * }
 * </pre>
 *
 * @author jsiebahn
 * @since 17.01.15 11:06
 */
public class EhCacheConfigurationBuilder {

    /**
     * The default cache configuration will be created configured first and is used as base for all
     * {@link #cacheConfigurations}.
     */
    private CacheConfiguration defaultCacheConfiguration;

    /**
     * Contains all specific {@link CacheConfiguration}s
     */
    private List<CacheConfiguration> cacheConfigurations = new ArrayList<>();

    /**
     * Where caches should be written to disk.
     */
    private String diskStorePath;


    //
    // hidden constructor
    //

    /**
     * Use {@link #createDefaultCache()} to get a new instance of
     * {@link EhCacheConfigurationBuilder}.
     */
    private EhCacheConfigurationBuilder() {

    }


    //
    // cache creation
    //

    /**
     * Creates the default cache and the builder to configure it using the {@code with*} methods.
     *
     * @return the current builder instance used to configure the default cache configuration
     */
    public static EhCacheConfigurationBuilder createDefaultCache() {
        EhCacheConfigurationBuilder builder = new EhCacheConfigurationBuilder();
        builder.defaultCacheConfiguration = new CacheConfiguration();
        builder.defaultCacheConfiguration.setName("default");
        return builder;
    }

    /**
     * Creates a new cache configuration based on the default configuration. All values that should
     * differ from the {@link #createDefaultCache() default cache configuration} must be overridden.
     *
     * This will finish the configuration of the current cache.
     *
     * @return the builder instance to define a name for the new cache
     */
    public CacheConfigurationCloneBuilder createCacheFromDefault() {
        return new CacheConfigurationCloneBuilder(defaultCacheConfiguration, this);
    }

    /**
     * When one created {@link CacheConfiguration}s separately, they may be used as template to
     * create new cache configuration. The given {@code cacheConfigurationTemplate} will be cloned
     * to create a new cache.
     *
     * @param cacheConfigurationTemplate the {@link CacheConfiguration} to clone for the new cache
     * @return the builder instance to define a name for the new cache
     */
    public CacheConfigurationCloneBuilder createCacheFromTemplate(CacheConfiguration
            cacheConfigurationTemplate) {
        return new CacheConfigurationCloneBuilder(cacheConfigurationTemplate, this);
    }

    /**
     * Creates a new cache configuration based on the currently configured configuration. All values
     * that should differ from the currently configured cache must be overridden.
     *
     * This will finish the configuration of the current cache.
     *
     * @return the builder instance to define a name for the new cache
     */
    public CacheConfigurationCloneBuilder createCacheFromCurrent() {
        return new CacheConfigurationCloneBuilder(currentConfiguration(), this);
    }

    /**
     * Creates a new cache configuration with default values from eh cache. Note that these are not
     * the values configured for the default cache with this builder. The resulting cache
     * configuration will be blank and usually does not work without further configuration.
     *
     * This will finish the configuration of the current cache.
     *
     * @return the builder instance to define a name for the new cache
     */
    public CacheConfigurationCloneBuilder createBlankCache() {
        return new CacheConfigurationCloneBuilder(new CacheConfiguration(), this);
    }

    /**
     * Creates a new cache configuration based on the cache with the given
     * {@code nameOfCacheToClone}. All values that should differ from that cache configuration must
     * be overridden after the new cache is created with
     * {@link CacheConfigurationCloneBuilder#withName(String)}.
     *
     * This will finish the configuration of the current cache.
     *
     * @param nameOfCacheToClone the name of the cache which configuration should be cloned,
     *      {@code null} or {@code "default"} may be used to clone the
     *      {@link #createDefaultCache() default cache}, but using {@link #createCacheFromDefault()}
     *      would be more convenient for this purpose
     * @return the builder instance to define a name for the new cache
     * @throws IllegalStateException if the cache to clone has not been configured yet with this
     *      {@link EhCacheConfigurationBuilder}
     */
    public CacheConfigurationCloneBuilder createCacheFrom(String nameOfCacheToClone) {
        if (nameOfCacheToClone == null || "default".equals(nameOfCacheToClone)) {
            return createCacheFromDefault();
        }
        CacheConfiguration toClone = null;
        for (CacheConfiguration cacheConfiguration : cacheConfigurations) {
            if (cacheConfiguration.getName().equals(nameOfCacheToClone)) {
                toClone = cacheConfiguration;
                break;
            }
        }
        if (toClone == null) {
            throw new IllegalStateException("Cache with name '" + nameOfCacheToClone
                    + "' has not been configured in this builder yet. "
                    + "The new cache can not be used as template.");
        }

        return new CacheConfigurationCloneBuilder(toClone, this);
    }


    //
    // build the configuration
    //

    /**
     * Creates and returns the {@link Configuration} for the default cache and all other caches.
     *
     * @return the final {@link Configuration} to be used for an
     *      {@link net.sf.ehcache.CacheManager}
     */
    public Configuration build() {
        Configuration config = new Configuration();
        config.addDefaultCache(defaultCacheConfiguration);
        for(CacheConfiguration cacheConfiguration : cacheConfigurations) {
            config.addCache(cacheConfiguration);
        }
        if (diskStorePath != null) {
            DiskStoreConfiguration diskStoreConfiguration = new DiskStoreConfiguration();
            diskStoreConfiguration.setPath(diskStorePath);
            config.addDiskStore(diskStoreConfiguration);
        }
        return config;
    }


    //
    // fluent setters for global configuration
    //

    /**
     * Sets the path of the diskStore for all caches.
     *
     * @param diskStorePath the path to use for the disk store
     * @return this builder instance
     */
    public EhCacheConfigurationBuilder withDiskStorePath(String diskStorePath) {
        this.diskStorePath = diskStorePath;
        return this;
    }


    //
    // fluent setters for cache configuration
    //

    public EhCacheConfigurationBuilder withMaxElementsInMemory(int maxElementsInMemory) {
        currentConfiguration().setMaxElementsInMemory(maxElementsInMemory);
        return this;
    }

    public EhCacheConfigurationBuilder withMaxElementsOnDisk(int maxElementsOnDisk) {
        currentConfiguration().setMaxElementsOnDisk(maxElementsOnDisk);
        return this;
    }

    public EhCacheConfigurationBuilder withMemoryStoreEvictionPolicy(MemoryStoreEvictionPolicy
            memoryStoreEvictionPolicy) {
        currentConfiguration().setMemoryStoreEvictionPolicyFromObject(memoryStoreEvictionPolicy);
        return this;
    }

    public EhCacheConfigurationBuilder withMemoryStoreEvictionPolicy(
            String memoryStoreEvictionPolicy) {
        currentConfiguration().setMemoryStoreEvictionPolicy(memoryStoreEvictionPolicy);
        return this;
    }

    public EhCacheConfigurationBuilder withClearOnFlush(boolean clearOnFlush) {
        currentConfiguration().setClearOnFlush(clearOnFlush);
        return this;
    }

    public EhCacheConfigurationBuilder withEternal(boolean eternal) {
        currentConfiguration().setEternal(eternal);
        return this;
    }

    public EhCacheConfigurationBuilder withTimeToIdleSeconds(long timeToIdleSeconds) {
        currentConfiguration().setTimeToIdleSeconds(timeToIdleSeconds);
        return this;
    }

    public EhCacheConfigurationBuilder withTimeToLiveSeconds(long timeToLiveSeconds) {
        currentConfiguration().setTimeToLiveSeconds(timeToLiveSeconds);
        return this;
    }

    public EhCacheConfigurationBuilder withOverflowToDisk(boolean overflowToDisk) {
        currentConfiguration().setOverflowToDisk(overflowToDisk);
        return this;
    }

    public EhCacheConfigurationBuilder withDiskPersistent(boolean diskPersistent) {
        currentConfiguration().setDiskPersistent(diskPersistent);
        return this;
    }

    public EhCacheConfigurationBuilder withDiskSpoolBufferSizeMB(int diskSpoolBufferSizeMB) {
        currentConfiguration().setDiskSpoolBufferSizeMB(diskSpoolBufferSizeMB);
        return this;
    }

    public EhCacheConfigurationBuilder withDiskExpiryThreadIntervalSeconds(long
            diskExpiryThreadIntervalSeconds) {
        currentConfiguration().setDiskExpiryThreadIntervalSeconds(diskExpiryThreadIntervalSeconds);
        return this;
    }


    //
    // internal helper
    //

    /**
     * @return The cache configuration that is currently configured with this builder. That will be
     *      the last cache configuration created with {@link #createCacheFromDefault()} or the
     *      {@link #defaultCacheConfiguration} if no specific cache has been added yet.
     */
    private CacheConfiguration currentConfiguration() {
        if (cacheConfigurations.size() > 0) {
            return cacheConfigurations.get(cacheConfigurations.size() - 1);
        }
        return defaultCacheConfiguration;
    }


    //
    // helper classes
    //

    /**
     * Helper class to clone cache configurations.
     */
    public static class CacheConfigurationCloneBuilder {

        /**
         * The cache configuration that should be used for the new cache.
         */
        private CacheConfiguration toClone;

        /**
         * The builder that is used to build the whole configuration and to specify the parameters
         * of the new cache.
         */
        private EhCacheConfigurationBuilder baseBuilder;

        /**
         * Creates the new instance to clone the given {@code toClone} cache for the given
         * {@code baseBuilder}.
         *
         * @param toClone the cache configuration which parameters should be used for the new cache
         * @param baseBuilder the builder to return for further configuration
         */
        protected CacheConfigurationCloneBuilder(CacheConfiguration toClone,
                EhCacheConfigurationBuilder baseBuilder) {
            this.toClone = toClone;
            this.baseBuilder = baseBuilder;
        }

        /**
         * Creates the new cache based on the {@link #toClone cache to clone} and adds it to the
         * {@link #baseBuilder}
         *
         * @param newName the name of the new cache
         * @return the {@link EhCacheConfigurationBuilder} instance for further configuration of the
         *      new cache
         */
        public EhCacheConfigurationBuilder withName(String newName) {
            try {
                CacheConfiguration configuration = toClone.clone();
                configuration.setName(newName);
                baseBuilder.cacheConfigurations.add(configuration);
                return baseBuilder;
            }
            catch (CloneNotSupportedException e) {
                // this should not happen
                throw new RuntimeException(e);
            }
        }

    }

}
