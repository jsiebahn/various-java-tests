package com.github.jsiebahn.spring.generic.controller;

import com.github.jsiebahn.spring.generic.hateoas.HateoasEntity;
import com.github.jsiebahn.spring.generic.hateoas.HateoasResponse;
import com.github.jsiebahn.spring.generic.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 12.02.15 06:58
 */
@RestController
@RequestMapping("/admin/api")
public class ApiDescriptionController {

    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping(method = RequestMethod.OPTIONS)
    public HateoasResponse helloMapping(HttpServletResponse response) {
        HateoasResponse hateoasResponse = new HateoasResponse();

        hateoasResponse.addLink("self", UriComponentsBuilder.fromPath("/admin/api").build().toUri(),
                "Home", "The API entry point.");

        // this should be evaluated dynamically
        hateoasResponse.addLink(Person.class.getName(),
                UriComponentsBuilder.fromPath(EntityEditor.BASE_PATH + "/"
                                + Person.class.getName()).build().toUri(),
                "Persons",
                "List all Persons.");

        HateoasEntity options = new HateoasEntity();
        options.setLabel("General methods and links of this API.");
        options.setDescription("Receive an overview about what this API offers.");

        hateoasResponse.addMethod(HttpMethod.OPTIONS, options);

        // Add Allow header dynamically based on response methods keys
        response.setHeader(HttpHeaders.ALLOW, "OPTIONS");

        return hateoasResponse;
    }

    private List<EntityEditor> findEntityEditors() {
        applicationContext.getBeansWithAnnotation(EntityEditor.class);

        return null;
    }

}
