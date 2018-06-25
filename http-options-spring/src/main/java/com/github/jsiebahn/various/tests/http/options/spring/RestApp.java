package com.github.jsiebahn.various.tests.http.options.spring;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 25.05.16 09:27
 */
@SpringBootApplication
public class RestApp {

    public static void main(String[] args) {
        SpringApplication.run(RestApp.class, args);
    }

    @Bean
    public BeanPostProcessor beanPostProcessor() {
        return new DescribedRestControllerParser();
    }

}
