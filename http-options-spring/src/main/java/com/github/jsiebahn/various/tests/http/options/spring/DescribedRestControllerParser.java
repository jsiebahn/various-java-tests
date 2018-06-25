package com.github.jsiebahn.various.tests.http.options.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.env.PropertyResolver;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 25.05.16 09:45
 */
class DescribedRestControllerParser implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(DescribedRestControllerParser.class);

    @Autowired
    private PropertyResolver propertyResolver;

    @Override
    public Object postProcessBeforeInitialization(
            Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(
            Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(DescribedRestController.class)) {
            findMappings(bean.getClass());
        }

        return bean;
    }

    void findMappings(Class<?> handlerType) {
        log.info("Checking {} for request mappings.", handlerType);

        // Avoid repeated calls to getMappingForMethod which would rebuild RequestMappingInfo instances
        final Map<Method, RequestMappingInfo> mappings = new IdentityHashMap<>();
        final Class<?> userType = ClassUtils.getUserClass(handlerType);

        MethodIntrospector.selectMethods(userType, (ReflectionUtils.MethodFilter) method -> {
                    RequestMappingInfo mapping = getMappingForMethod(method, userType);
                    if (mapping != null) {
                        mappings.put(method, mapping);
                        return true;
                    }
                    else {
                        return false;
                    }
                });

        mappings.entrySet().stream()
                .filter((e) -> e.getValue().getPatternsCondition() != null)
                .filter((e) -> e.getValue().getPatternsCondition().getPatterns() != null)
                .filter((e) -> e.getValue().getPatternsCondition().getPatterns().size() > 0)
                .filter((e) -> e.getValue().getMethodsCondition() != null)
                .filter((e) -> e.getValue().getMethodsCondition().getMethods() != null)
                .filter((e) -> e.getValue().getMethodsCondition().getMethods().size() > 0)
                .sorted((o1, o2) -> {
                    RequestMappingInfo v1 = o1.getValue();
                    RequestMappingInfo v2 = o2.getValue();
                    String p1 = v1.getPatternsCondition().getPatterns().iterator().next();
                    String p2 = v2.getPatternsCondition().getPatterns().iterator().next();
                    int compare = p1.compareTo(p2);
                    if (compare != 0) {
                        return compare;
                    }
                    RequestMethod m1 = v1.getMethodsCondition().getMethods().iterator().next();
                    RequestMethod m2 = v2.getMethodsCondition().getMethods().iterator().next();

                    return m1.compareTo(m2);
                })
                .forEach((e) -> {
                    e.getValue().getPatternsCondition().getPatterns().stream().forEach((p) ->
                        log.info("Found mapping: {} {}",
                                e.getValue().getMethodsCondition().getMethods(),
                                propertyResolver.resolvePlaceholders(p)
                        )
                    );
                    // TODO register options
                });
    }

    /**
     * Uses method and type-level @{@link RequestMapping} annotations to create
     * the RequestMappingInfo.
     * @return the created RequestMappingInfo, or {@code null} if the method
     * does not have a {@code @RequestMapping} annotation.
     */
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo info = createRequestMappingInfo(method);
        if (info != null) {
            RequestMappingInfo typeInfo = createRequestMappingInfo(handlerType);
            if (typeInfo != null) {
                info = typeInfo.combine(info);
            }
        }
        return info;
    }


    /**
     * Delegates to {@link #createRequestMappingInfo(RequestMapping, RequestCondition)},
     * supplying the appropriate custom {@link RequestCondition} depending on whether
     * the supplied {@code annotatedElement} is a class or method.
     */
    private RequestMappingInfo createRequestMappingInfo(AnnotatedElement element) {
        RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(element, RequestMapping.class);
        RequestCondition<?> condition = null;
//        RequestCondition<?> condition = (element instanceof Class<?> ?
//                getCustomTypeCondition((Class<?>) element) : getCustomMethodCondition((Method) element));
        return (requestMapping != null ? createRequestMappingInfo(requestMapping, condition) : null);
    }


    /**
     * Create a {@link RequestMappingInfo} from the supplied
     * {@link RequestMapping @RequestMapping} annotation, which is either
     * a directly declared annotation, a meta-annotation, or the synthesized
     * result of merging annotation attributes within an annotation hierarchy.
     */
    protected RequestMappingInfo createRequestMappingInfo(
            RequestMapping requestMapping, RequestCondition<?> customCondition) {

        return RequestMappingInfo
                .paths(resolveEmbeddedValuesInPatterns(requestMapping.path()))
                .methods(requestMapping.method())
                .params(requestMapping.params())
                .headers(requestMapping.headers())
                .consumes(requestMapping.consumes())
                .produces(requestMapping.produces())
                .mappingName(requestMapping.name())
                .customCondition(customCondition)
                // .options(this.config) TODO
                .build();
    }
    /**
     * Resolve placeholder values in the given array of patterns.
     * @return a new array with updated patterns
     */
    protected String[] resolveEmbeddedValuesInPatterns(String[] patterns) {
        return patterns;
//        if (this.embeddedValueResolver == null) {   // TODO
//            return patterns;
//        }
//        else {
//            String[] resolvedPatterns = new String[patterns.length];
//            for (int i = 0; i < patterns.length; i++) {
//                resolvedPatterns[i] = this.embeddedValueResolver.resolveStringValue(patterns[i]);
//            }
//            return resolvedPatterns;
//        }
    }


}
