package com.github.jsiebahn.various.tests.http.options.spring.person;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 25.05.16 12:09
 */
@Repository
public interface PersonRepository extends CrudRepository<Person, String> {

    Iterable<Person> findByFatherOrMother(Person father, Person mother);
}
