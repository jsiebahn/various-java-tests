package com.github.jsiebahn.spring.generic.config;

import com.github.jsiebahn.spring.generic.controller.GenericController;
import com.github.jsiebahn.spring.generic.converter.EntityConverter;
import com.github.jsiebahn.spring.generic.converter.StringToClassConverter;
import com.github.jsiebahn.spring.generic.repository.GenericRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 26.11.14 19:26
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackageClasses = {GenericController.class, GenericRepository.class, StringToClassConverter.class})
public class SpringConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private StringToClassConverter stringToClassConverter;

    @Autowired
    private EntityConverter entityConverter;


    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(entityConverter);
        registry.addConverter(stringToClassConverter);
    }

}
