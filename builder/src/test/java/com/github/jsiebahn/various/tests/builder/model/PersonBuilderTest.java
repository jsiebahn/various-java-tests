package com.github.jsiebahn.various.tests.builder.model;

import org.junit.Test;

import static com.github.jsiebahn.various.tests.builder.model.AddressBuilder.configureAddress;
import static com.github.jsiebahn.various.tests.builder.model.PersonBuilder.configurePerson;
import static org.junit.Assert.assertEquals;

/**
 * $Id$
 *
 * @author  jsiebahn
 * @since 21.09.14 09:19
 */
public class PersonBuilderTest {

    @Test
    public void testConfigureMalePersonAlive() throws Exception {

        Person person = configurePerson()
                .firstname("John")
                .surname("Doe")
                .genderMale()
                .alive()
                .email("john@doe.com")
                .address(configureAddress()
                        .street("Doe Lane 42")
                        .zipCode("12345")
                        .city("Capital City")
                        .build())
                .build();

        assertEquals("John", person.getFirstname());
        assertEquals("Doe", person.getSurname());
        assertEquals(Person.Gender.MALE, person.getGender());
        assertEquals(true, person.isAlive());
        assertEquals("john@doe.com", person.getEmail());
        assertEquals("Doe Lane 42", person.getAddress().getStreet());
        assertEquals("12345", person.getAddress().getZipCode());
        assertEquals("Capital City", person.getAddress().getCity());

    }

    @Test
    public void testConfigureFemalePersonAlive() throws Exception {

        Person person = configurePerson()
                .firstname("Jane")
                .surname("Doe")
                .genderFemale()
                .alive()
                .email("jane@doe.com")
                .address(configureAddress()
                        .street("Doe Lane 42")
                        .zipCode("12345")
                        .city("Capital City")
                        .build())
                .build();

        assertEquals("Jane", person.getFirstname());
        assertEquals("Doe", person.getSurname());
        assertEquals(Person.Gender.FEMALE, person.getGender());
        assertEquals(true, person.isAlive());
        assertEquals("jane@doe.com", person.getEmail());
        assertEquals("Doe Lane 42", person.getAddress().getStreet());
        assertEquals("12345", person.getAddress().getZipCode());
        assertEquals("Capital City", person.getAddress().getCity());

    }


    @Test
    public void testConfigureAlternate() throws Exception {

        Person person;

        person = configurePerson()
                .gender(Person.Gender.FEMALE)
                .alive(true)
                .build();

        assertEquals(Person.Gender.FEMALE, person.getGender());


        assertEquals(true, person.isAlive());

        person = configurePerson()
                .gender(Person.Gender.MALE)
                .alive(false)
                .build();

        assertEquals(Person.Gender.MALE, person.getGender());
        assertEquals(false, person.isAlive());

    }

    @Test
    public void testConfigureUnknownPersonNotAlive() throws Exception {

        Person person = configurePerson()
                .firstname("Kim")
                .surname("Doe")
                .genderUnknown()
                .notAlive()
                .email("kim@doe.com")
                .address(configureAddress()
                        .street("Doe Lane 42")
                        .zipCode("12345")
                        .city("Capital City")
                        .build())
                .build();

        assertEquals("Kim", person.getFirstname());
        assertEquals("Doe", person.getSurname());
        assertEquals(Person.Gender.UNKNOWN, person.getGender());
        assertEquals(false, person.isAlive());
        assertEquals("kim@doe.com", person.getEmail());
        assertEquals("Doe Lane 42", person.getAddress().getStreet());
        assertEquals("12345", person.getAddress().getZipCode());
        assertEquals("Capital City", person.getAddress().getCity());

    }
}
