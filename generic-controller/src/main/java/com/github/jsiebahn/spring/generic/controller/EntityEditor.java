package com.github.jsiebahn.spring.generic.controller;

import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

/**
 * <p>
 * Identifies a class as editor for an entity. The editor class shall be identified with
 * {@code EntityEditor} and published with
 * {@link org.springframework.web.bind.annotation.RequestMapping} at class level. The
 * {@code RequestMapping} shall start with the {@link #BASE_PATH} followed by an unique identifier
 * for the entity type. The fully qualified name of the entity class will work and avoids any
 * conflicts with other libraries or build in entities. The entity editor shall provide a REST API
 * for the entity with these methods:
 * </p>
 * <dl>
 *     <dt>get all</dt>
 *     <dd>GET /BASE_PATH/com.example.MyEntity</dd>
 *     <dt>get single entity</dt>
 *     <dd>GET /BASE_PATH/com.example.MyEntity/{id}</dd>
 *     <dt>create entity</dt>
 *     <dd>PUT /BASE_PATH/com.example.MyEntity</dd>
 *     <dt>update entity</dt>
 *     <dd>POST /BASE_PATH/com.example.MyEntity/{id}</dd>
 *     <dt>delete entity</dt>
 *     <dd>DELETE /BASE_PATH/com.example.MyEntity/{id}</dd>
 * </ul>
 * <p>
 * POST, PUT and DELETE methods shall expect the incoming entity data as Json in the
 * {@link org.springframework.web.bind.annotation.RequestBody}.
 * </p>
 * <p>
 * For convenience there is {@link EntityEditorAdapter} which may be extended to have all required
 * methods implemented.
 * </p>
 *
 * @author jsiebahn
 * @since 28.11.14 07:32
 */
@RestController
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityEditor {

    /**
     * The base path of all entity editors. This can be used to define the
     * {@link org.springframework.web.bind.annotation.RequestMapping} for a specific entity editor.
     */
    public static final String BASE_PATH = "/admin/api/rest/generic";

    /**
     * @return The entity class that is handled by the entity editor.
     */
    Class value();

}
