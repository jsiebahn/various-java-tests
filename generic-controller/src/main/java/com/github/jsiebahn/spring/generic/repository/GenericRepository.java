package com.github.jsiebahn.spring.generic.repository;

import com.github.jsiebahn.spring.generic.model.Person;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 26.11.14 19:19
 */
@Component
public class GenericRepository {

    private Map<Integer, Person> persons = new HashMap<>();

    @PostConstruct
    public void init() {
        initPerson(1, "John", "Doe");
        initPerson(2, "Jane", "Doe");
    }



    public <T> List<T> findAll(Class<T> T) {
        if (T.equals(Person.class)) {
            return (List<T>) new ArrayList<>(persons.values());
        }
        return null;
    }

    public <T> T find(Class<T> T, String id) {
        if (T.equals(Person.class)) {
            return (T) persons.get(Integer.parseInt(id));
        }
        return null;
    }

    public <T> T save(Class<T> T, T entity) {
        if (T.equals(Person.class)) {
            Person p = (Person) entity;

            if (p.getId() <= 0) {
                int id = 0;
                for (int existingId : persons.keySet()) {
                    id = Math.max(id, existingId);
                }
                id++;
                p.setId(id);
            }

            persons.put(p.getId(), p);
            return find(T, "" + p.getId());
        }
        return null;
    }

    public <T> boolean delete(Class<T> T, T entity) {
        if (T.equals(Person.class)) {
            int size = persons.size();
            Person p = (Person) entity;
            persons.remove(p.getId());
            return size == persons.size() + 1;
        }
        return false;
    }



    private void initPerson(int id, String firstName, String surName) {
        Person p = new Person();
        p.setId(id);
        p.setFirstName(firstName);
        p.setSurName(surName);
        persons.put(id, p);
    }

}
