package com.github.jsiebahn.spring.generic.converter;

import com.github.jsiebahn.spring.generic.controller.EntityEditor;
import com.github.jsiebahn.spring.generic.repository.GenericRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 29.11.14 08:16
 */
@Component
public class EntityConverter implements GenericConverter {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private GenericRepository genericRepository;

    /**
     * The types this converter can convert.
     */
    private Set<ConvertiblePair> convertiblePairs = new LinkedHashSet<>();

    @PostConstruct
    public void init() {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(EntityEditor.class);
        Collection <Object> editors =  beans.values();
        for(Object editor : editors) {
            EntityEditor entityEditor = editor.getClass().getAnnotation(EntityEditor.class);
            if (entityEditor == null) {
                continue;
            }
            Class<?> type = entityEditor.value();
            if (type == null) {
                continue;
            }
            convertiblePairs.add(new ConvertiblePair(String.class, type));
        }
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return convertiblePairs;
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        return genericRepository.find(targetType.getType(), (String) source);
    }
}
