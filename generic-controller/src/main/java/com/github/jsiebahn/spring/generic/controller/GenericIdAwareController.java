package com.github.jsiebahn.spring.generic.controller;

import com.github.jsiebahn.spring.generic.repository.GenericRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.github.jsiebahn.spring.generic.controller.GenericController.CLASS_PARAM;
import static com.github.jsiebahn.spring.generic.controller.GenericController.ID_PARAM;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 26.11.14 20:23
 */
//@RestController
@RequestMapping("/api/rest/generic/{" + CLASS_PARAM + "}/{" + ID_PARAM + "}")
public class GenericIdAwareController {

    @Autowired
    private GenericRepository genericRepository;


    @ModelAttribute(ID_PARAM)
    public <T> T loadEntity(@PathVariable(CLASS_PARAM) Class<T> T, @PathVariable(ID_PARAM) String id) {
        if (T == null || id == null) {
            return null;
        }
        return genericRepository.find(T, id);
    }


    @RequestMapping(method = RequestMethod.GET)
    public <T> T get(@ModelAttribute(ID_PARAM) T entity) {
        return entity;
    }

    @RequestMapping(method = RequestMethod.POST)
    public <T> T post(@PathVariable(CLASS_PARAM) Class<T> T, @RequestBody T entity) throws IOException {
        if (entity == null) {
            return null;
        }
//        ObjectMapper mapper = new ObjectMapper();
//        T newEntity = mapper.readValue(body, T);
//        return genericRepository.save(T, newEntity);
        return genericRepository.save(T, entity);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public <T> boolean delete(@PathVariable(CLASS_PARAM) Class<T> T, @ModelAttribute(ID_PARAM) T entity) {
        boolean successfulDelete = false;
        if (entity != null) {
            successfulDelete = genericRepository.delete(T, entity);
        }
        return successfulDelete;
    }

}
