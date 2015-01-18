package com.github.jsiebahn.various.tests.automatic;

import com.github.jsiebahn.various.tests.builder.model.Address;

import java.util.Arrays;
import java.util.List;

/**
 * $Id$
 *
 * @author  jsiebahn
 * @since 21.09.14 10:11
 */
public class AnotherPersonBuilder {
    private String firstname;
    private String surname;
    private AnotherPerson.Gender gender;
    private boolean alive;
    private String email;
    private List<Address> addresses;

    private AnotherPersonBuilder() {
    }

    public static AnotherPersonBuilder anAnotherPerson() {
        return new AnotherPersonBuilder();
    }

    public AnotherPersonBuilder firstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    public AnotherPersonBuilder surname(String surname) {
        this.surname = surname;
        return this;
    }

    public AnotherPersonBuilder gender(AnotherPerson.Gender gender) {
        this.gender = gender;
        return this;
    }

    public AnotherPersonBuilder alive(boolean alive) {
        this.alive = alive;
        return this;
    }

    public AnotherPersonBuilder email(String email) {
        this.email = email;
        return this;
    }

    public AnotherPersonBuilder addresses(Address... addresses) {
        this.addresses = Arrays.asList(addresses);
        return this;
    }

    public AnotherPerson build() {
        AnotherPerson anotherPerson = new AnotherPerson();
        anotherPerson.setFirstname(firstname);
        anotherPerson.setSurname(surname);
        anotherPerson.setGender(gender);
        anotherPerson.setAlive(alive);
        anotherPerson.setEmail(email);
        anotherPerson.setAddresses(addresses);
        return anotherPerson;
    }
}
