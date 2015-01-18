package com.github.jsiebahn.spring.generic.controller;

import com.github.jsiebahn.spring.generic.repository.GenericRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 28.11.14 07:31
 */
public abstract class EntityEditorAdapter<T> {

    @Autowired
    private GenericRepository genericRepository;

    private Class<T> type;

    //
    // Request Mappings
    //

    /**
     * Finds all entities of given {@code clazz type}.
     *
     * @return the found entities
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<T> getAll() {
        return genericRepository.findAll(getEntityClass());
    }

    /**
     * Persists the given new {@code entity}.
     *
     * @param entity the entity to persist
     * @return the new entity
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public T create(@RequestBody T entity) {
        return genericRepository.save(getEntityClass(), entity);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public T getEntity(@PathVariable("id") T entity) {
        return entity;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public T updateEntity(@RequestBody T entity) {
        return genericRepository.save(getEntityClass(), entity);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public boolean deleteEntity(@PathVariable("id") T entity) {
        return genericRepository.delete(getEntityClass(), entity);
    }


    private Class<T> getEntityClass() {
        if (type == null) {
            // TODO check possible errors
            //noinspection unchecked
            type = this.getClass().getAnnotation(EntityEditor.class).value();
        }
        return type;
    }

}
