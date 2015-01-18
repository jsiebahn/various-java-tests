package com.github.jsiebahn.spring.generic.converter;

import com.github.jsiebahn.spring.generic.controller.EntityEditor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 26.11.14 19:17
 */
@Component
public class StringToClassConverter implements Converter<String, Class<?>>,
        ApplicationContextAware {

    ApplicationContext applicationContext;

    List<Object> beans;

    Map<String, Class> types = new HashMap<>();

    @Override
    public Class<?> convert(String s) {
        if (s == null) {
            return null;
        }
        Class cachedType = types.get(s);
        if (cachedType != null) {
            return cachedType;
        }
        String requiredMapping = EntityEditor.BASE_PATH + "/" + s;
        for (Object bean : getBeans()) {
            RequestMapping requestMapping = bean.getClass().getAnnotation(RequestMapping.class);
            if (requestMapping == null) {
                continue;
            }
            for (String mapping : requestMapping.value()) {
                if (mapping == null) {
                    continue;
                }
                if (requiredMapping.equals(mapping)) {
                    Class type = findType(bean);
                    if (type != null) {
                        types.put(s, type);
                        return type;
                    }
                }
            }
        }
        applicationContext.getBeanNamesForAnnotation(EntityEditor.class);
        try {
            return getClass().getClassLoader().loadClass(s);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private Class findType(Object bean) {
        EntityEditor entityEditor = bean.getClass().getAnnotation(EntityEditor.class);
        if (entityEditor == null) {
            return null;
        }
        return entityEditor.value();
    }

    private List<Object> getBeans() {
        if (this.beans == null) {
            Map<String, Object> beans = applicationContext.getBeansWithAnnotation(EntityEditor.class);
            this.beans = new ArrayList<>(beans.values());
        }
        return beans;
    }
}
