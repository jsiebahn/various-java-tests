package com.github.jsiebahn.spring.generic.config;

import com.github.jsiebahn.spring.generic.converter.EntityConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 05.02.15 06:10
 */
@Configuration
@ComponentScan(basePackageClasses = EntityConverter.class)
public class ConverterConfig  extends WebMvcConfigurerAdapter {

    @Autowired
    private EntityConverter entityConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(entityConverter);
    }

}
