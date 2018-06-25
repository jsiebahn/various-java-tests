package com.github.jsiebahn.spring.generic.controller;

import com.github.jsiebahn.spring.generic.repository.GenericRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.EnumSet;
import java.util.List;

/**
 * Adapter for instances annotated by {@link EntityEditor} to simplify the use of the generic REST
 * API for editing entities. The {@code EntityEditorAdapter} implements all required HTTP methods
 * for the {@link EntityEditor} of a specific entityType.
 *
 * @author jsiebahn
 * @since 28.11.14 07:31
 */
public abstract class EntityEditorAdapter<T> {

    @Autowired
    private GenericRepository genericRepository;

    /**
     * Caches the evaluated  {@link EntityEditor#value() class} of the {@link EntityEditor}
     *
     * @see #getEntityType()
     */
    private Class<T> entityType;

    //
    // Request Mappings
    //

    /**
     * Finds all entities of given {@link #entityType}.
     *
     * @return the found entities
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<T> getAll() {
        return genericRepository.findAll(getEntityType());
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
        return genericRepository.save(getEntityType(), entity);
    }

    /**
     * Finds a single entity identified by the {@code id} in the path.
     *
     * @param entity the entity
     * @return the entity
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public T getEntity(@PathVariable("id") T entity) {
        return entity;
    }

    /**
     * Persists the given {@code entity}.
     *
     * @param entity the entity to persist
     * @return the entity
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public T updateEntity(@RequestBody T entity) {
        return genericRepository.save(getEntityType(), entity);
    }

    /**
     * Deletes the given {@code entity}.
     *
     * @param entity the entity to delete
     * @return if the entity has been deleted
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public boolean deleteEntity(@PathVariable("id") T entity) {
        return genericRepository.delete(getEntityType(), entity);
    }

    /**
     * @return the {@link #createEditorTemplate()}
     */
    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Object> getGeneralEditorTemplate() {

        HttpHeaders headers = new HttpHeaders();
        headers.setAllow(EnumSet.of(HttpMethod.GET, HttpMethod.POST, HttpMethod.OPTIONS));

        return new ResponseEntity<>(createEditorTemplate(), headers, HttpStatus.OK);
    }

    /**
     * @return the {@link #createEditorTemplate()}
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.OPTIONS)
    public ResponseEntity<Object> getEntityEditorTemplate(@RequestBody T entity) {

        if (entity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setAllow(
                EnumSet.of(HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.OPTIONS));

        return new ResponseEntity<>(createEditorTemplate(), headers, HttpStatus.OK);
    }



    /**
     * Builds an editor template instance for any ui that has to display an edit form for the
     * {@link #entityType}.
     *
     * @return tbd
     */
    // TODO shouldn't return Object
    abstract Object createEditorTemplate();

    /**
     * Finds and caches the {@link #entityType} from an {@link EntityEditor} annotation at the
     * current instances class.
     *
     * @return the {@link #entityType}
     */
    private Class<T> getEntityType() {
        if (entityType == null) {
            //noinspection unchecked
            entityType = this.getClass().getAnnotation(EntityEditor.class).value();
        }
        return entityType;
    }

}
