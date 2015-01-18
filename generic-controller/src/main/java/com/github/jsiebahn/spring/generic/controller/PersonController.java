package com.github.jsiebahn.spring.generic.controller;

import com.github.jsiebahn.spring.generic.model.Person;
import com.github.jsiebahn.spring.generic.repository.GenericRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.github.jsiebahn.spring.generic.controller.PersonController.ID_PARAM;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 26.11.14 22:46
 */
@RestController
@RequestMapping("/api/rest/fixed/Person/{" + ID_PARAM + "}")
public class PersonController {

    public static final String ID_PARAM = "id";

    @Autowired
    private GenericRepository genericRepository;

    @ModelAttribute(ID_PARAM)
    public Person loadEntity(@PathVariable(ID_PARAM) String id) {
        if (id == null) {
            return null;
        }
        return genericRepository.find(Person.class, id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Person post(@RequestBody @ModelAttribute(ID_PARAM) Person entity) {
        if (entity == null) {
            return null;
        }
        return genericRepository.save(Person.class, entity);
    }

}
