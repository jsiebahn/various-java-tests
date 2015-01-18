package com.github.jsiebahn.spring.generic.config;

import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.Filter;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 26.11.14 19:36
 */
public class WebInitializer  extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return null;
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{SpringConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected Filter[] getServletFilters() {
        CharacterEncodingFilter utf8EncodingFilter = new CharacterEncodingFilter();
        utf8EncodingFilter.setEncoding("UTF-8");
        utf8EncodingFilter.setForceEncoding(true);
        return new Filter[]{utf8EncodingFilter};
    }
}
