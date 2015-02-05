package com.github.jsiebahn.spring.generic.config;

import com.github.jsiebahn.spring.generic.controller.PersonEditor;
import com.github.jsiebahn.spring.generic.repository.GenericRepository;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 26.11.14 19:26
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackageClasses = {PersonEditor.class, GenericRepository.class})
@Import({MappingConfig.class})
public class SpringConfig {
}
