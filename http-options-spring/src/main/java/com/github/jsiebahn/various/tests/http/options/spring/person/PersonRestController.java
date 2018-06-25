package com.github.jsiebahn.various.tests.http.options.spring.person;

import com.github.jsiebahn.various.tests.http.options.spring.BasicRestController;
import com.github.jsiebahn.various.tests.http.options.spring.DescribedRestController;
import com.github.jsiebahn.various.tests.http.options.spring.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 25.05.16 09:28
 */
@DescribedRestController
@RequestMapping("/api/persons")
public class PersonRestController extends BasicRestController<Person, String> {

    @Autowired
    private PersonRepository personRepository;

    @PostConstruct
    public void initData() {
        Person father = new Person();
        father.setFirstName("John");
        father.setSurName("Doe");
        father = personRepository.save(father);
        Person child = new Person();
        child.setFirstName("Jane");
        child.setSurName("Doe");
        child.setFather(father);
        personRepository.save(child);
    }

    @RequestMapping(value = "/{id}/children", method = RequestMethod.GET)
    public List<Person> findChildren(@PathVariable("id") Person person) {
        if (person == null) {
            throw new NotFoundException();
        }
        return toList(personRepository.findByFatherOrMother(person, person));
    }

}
