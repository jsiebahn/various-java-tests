package com.github.jsiebahn.spring.generic.config;

import com.github.jsiebahn.spring.generic.handler.mapping.EntityHandlerMapping;
import com.github.jsiebahn.spring.generic.repository.GenericRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 05.02.15 06:56
 */
@Configuration
@ComponentScan(basePackageClasses = {GenericRepository.class})
@Import({ConverterConfig.class})
public class MappingConfig extends WebMvcConfigurationSupport {

    @Bean
    @Override
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        EntityHandlerMapping handlerMapping = new EntityHandlerMapping();

        // copied from super
        handlerMapping.setOrder(0);
        handlerMapping.setInterceptors(getInterceptors());
        handlerMapping.setContentNegotiationManager(mvcContentNegotiationManager());

        PathMatchConfigurer configurer = getPathMatchConfigurer();
        if (configurer.isUseSuffixPatternMatch() != null) {
            handlerMapping.setUseSuffixPatternMatch(configurer.isUseSuffixPatternMatch());
        }
        if (configurer.isUseRegisteredSuffixPatternMatch() != null) {
            handlerMapping.setUseRegisteredSuffixPatternMatch(configurer.isUseRegisteredSuffixPatternMatch());
        }
        if (configurer.isUseTrailingSlashMatch() != null) {
            handlerMapping.setUseTrailingSlashMatch(configurer.isUseTrailingSlashMatch());
        }
        if (configurer.getPathMatcher() != null) {
            handlerMapping.setPathMatcher(configurer.getPathMatcher());
        }
        if (configurer.getUrlPathHelper() != null) {
            handlerMapping.setUrlPathHelper(configurer.getUrlPathHelper());
        }
        // end copied from super

        return handlerMapping;
    }

}
