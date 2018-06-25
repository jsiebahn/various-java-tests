package com.github.jsiebahn.various.tests.http.options.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jsiebahn.various.tests.http.options.spring.person.Person;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import static java.util.Collections.singletonMap;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 25.05.16 16:26
 */
public class ObjectMergerTest {

    ObjectMerger merger = new ObjectMerger(new ObjectMapper());

    @Test
    public void testMerge() throws Exception {
        Person p = new Person();
        p.setFirstName("John");
        p.setSurName("Doe");
        Map<String, Object> map = singletonMap("firstName", "Jane");
        Person person = merger.merge(p, map);

        Assert.assertEquals("Jane", person.getFirstName());
        Assert.assertEquals("Doe", person.getSurName());
    }
}