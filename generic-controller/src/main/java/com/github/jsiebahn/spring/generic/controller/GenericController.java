package com.github.jsiebahn.spring.generic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jsiebahn.spring.generic.repository.GenericRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 26.11.14 19:14
 */
//@RestController
@RequestMapping("/api/rest/generic/{" + GenericController.CLASS_PARAM + "}")
public class GenericController {

    public static final String CLASS_PARAM = "class";
    public static final String ID_PARAM = "ID";

    @Autowired
    private GenericRepository genericRepository;

    @RequestMapping(method = RequestMethod.GET)
    public <T> List<T> getAll(@PathVariable(CLASS_PARAM) Class<T> T) {
        return genericRepository.findAll(T);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public <T> T post(@PathVariable(CLASS_PARAM) Class<T> T, @RequestBody String body) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        T newEntity = mapper.readValue(body, T);
        return genericRepository.save(T, newEntity);
    }


}
