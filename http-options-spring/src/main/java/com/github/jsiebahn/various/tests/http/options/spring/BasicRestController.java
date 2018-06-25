package com.github.jsiebahn.various.tests.http.options.spring;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementing class should use {@link DescribedRestController} and a {@link RequestMapping}
 * suitable for the exposed entity class.
 *
 * @author jsiebahn
 * @since 25.05.16 11:21
 */
public abstract class BasicRestController<T extends Identifiable<ID>, ID extends Serializable>
        implements Converter<T, String> {

    @Autowired
    private CrudRepository<T, ID> repository;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ObjectMerger objectMerger;

    private static ApplicationContext staticApplicationContext;

    @PostConstruct
    public void enhanceObjectMapper() {
        if (BasicRestController.staticApplicationContext == null) {
            BasicRestController.staticApplicationContext = applicationContext;
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<T> findAll() {
        return toList(repository.findAll());
    }

    @RequestMapping(method = RequestMethod.POST)
    public T create(@RequestBody T newInstance) {
        return repository.save(newInstance);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public T findOne(@PathVariable("id") T instance) {
        if (instance == null) {
            throw new NotFoundException();
        }
        return instance;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public T replace(@PathVariable("id") T original, @RequestBody T newInstance) {
        // TODO set id, check for 201 or 200
        return repository.save(newInstance);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public T update(@PathVariable("id") T original, @RequestBody Map<String, Object> newData) throws IOException {
        if (original == null) {
            throw new NotFoundException();
        }
        newData.remove("id");
        T updated = objectMerger.merge(original, newData);
        return repository.save(updated);
    }


    protected List<T> toList(Iterable<T> data) {
        List<T> result = new ArrayList<>();
        data.forEach(result::add);
        return result;
    }

    @Override
    public String convert(T source) {
        try {
            return source.getClass().getField("id").get(source).toString();
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static class LinkSerializer<T extends Identifiable> extends JsonSerializer<T> {

        @Override
        public void serialize(T value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException, JsonProcessingException {
            if (value == null) {
                gen.writeString((String) null);
            }
            else {
                Class handlerType = findHandlerClass(value);
                if (handlerType != null) {
                    gen.writeString(createLink(value, handlerType));
                }
                else {
                    gen.writeString((String) null);
                }

            }

        }

        private String createLink(T value, Class handlerType) {
            return MvcUriComponentsBuilder.fromMethodName(handlerType,"findAll")
                    .buildAndExpand().encode().toUriString()
                     + convert(value);
        }

        private Class findHandlerClass(T value) {
            Map<String, BasicRestController> controllerMap = BasicRestController
                    .staticApplicationContext.getBeansOfType(BasicRestController.class);
            Class handlerType = null;
            for (BasicRestController basicRestController : controllerMap.values()) {

                Class<?> controllerClass = basicRestController.getClass();
                Type superType = controllerClass.getGenericSuperclass();
                ParameterizedType parameterizedSuperType = (ParameterizedType) superType;
                Type handledType = parameterizedSuperType.getActualTypeArguments()[0];

                if (handledType.getTypeName().equals(value.getClass().getName())) {
                    handlerType = controllerClass;
                    break;
                }

            }
            return handlerType;
        }

        public String convert(T source) {
            return source.getId().toString();
        }
    }

}
