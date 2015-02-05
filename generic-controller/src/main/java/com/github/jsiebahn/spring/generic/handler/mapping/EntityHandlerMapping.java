package com.github.jsiebahn.spring.generic.handler.mapping;

import com.github.jsiebahn.spring.generic.controller.EntityEditor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 03.02.15 08:47
 */
public class EntityHandlerMapping extends RequestMappingHandlerMapping {

    /**
     * Identifies types annotated with {@link EntityEditor} as handler the same way as if they were
     * {@link org.springframework.stereotype.Controller}s with a {@link RequestMapping} at type
     * level. Preserves the default behaviour of {@link RequestMappingHandlerMapping} as described
     * below:
     *
     * {@inheritDoc}
     */
    @Override
    protected boolean isHandler(Class<?> beanType) {
        return beanType.isAnnotationPresent(EntityEditor.class) || super.isHandler(beanType);
    }

    /**
     * {@link RequestMappingInfo} will be created for {@link EntityEditor} types. The default
     * behaviour of {@link RequestMappingHandlerMapping} for {@link RequestMapping} and
     * {@link org.springframework.stereotype.Controller} types is preserved as described below:
     *
     * {@inheritDoc}
     *
     * @see #createRequestMapping(EntityEditor)
     */
    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        if (handlerType.isAnnotationPresent(EntityEditor.class)) {

            // nearly the same as super
            RequestMappingInfo info = null;
            RequestMapping methodAnnotation = AnnotationUtils.findAnnotation(method, RequestMapping.class);
            if (methodAnnotation != null) {
                RequestCondition<?> methodCondition = getCustomMethodCondition(method);
                info = createRequestMappingInfo(methodAnnotation, methodCondition);

                // difference from super
                EntityEditor editorAnnotation = AnnotationUtils.findAnnotation(
                        handlerType,
                        EntityEditor.class);
                RequestMapping typeAnnotation = createRequestMapping(editorAnnotation);
                // end difference from super

                if (typeAnnotation != null) {
                    RequestCondition<?> typeCondition = getCustomTypeCondition(handlerType);
                    info = createRequestMappingInfo(typeAnnotation, typeCondition).combine(info);
                }
            }
            // end nearly the same as super

            return info;
        }
        else {
            return super.getMappingForMethod(method, handlerType);
        }
    }

    /**
     * Creates a custom {@link RequestMapping} instance from an {@link EntityEditor} instance to
     * use the default Spring way to create the required {@link RequestMappingInfo} for the handler.
     *
     * @param entityEditor the {@link EntityEditor} of an editor type for a specific entity class
     * @return a new {@link RequestMapping} build from the {@link EntityEditor#BASE_PATH} and the
     *      {@link Class#getName() class name} of the entity. The {@link RequestMapping} should
     *      be used for the type annotation and defines only the
     *      {@link RequestMapping#value() base path} of the {@link EntityEditor}
     */
    private RequestMapping createRequestMapping(final EntityEditor entityEditor) {
        return new RequestMapping() {

            @Override
            public String name() {
                return null;
            }

            @Override
            public String[] value() {
                return new String[] {EntityEditor.BASE_PATH + "/" + entityEditor.value().getName()};
            }

            @Override
            public RequestMethod[] method() {
                return null;
            }

            @Override
            public String[] params() {
                return null;
            }

            @Override
            public String[] headers() {
                return null;
            }

            @Override
            public String[] consumes() {
                return null;
            }

            @Override
            public String[] produces() {
                return null;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return RequestMapping.class;
            }
        };
    }

}
