package com.github.jsiebahn.various.tests.builder.model;

/**
 * $Id$
 *
 * @author  jsiebahn
 * @since 21.09.14 09:15
 */
public class PersonBuilder {

    private Person person;

    private PersonBuilder() {
        this.person = new Person();
    }

    public static PersonBuilder configurePerson() {
        return new PersonBuilder();
    }

    public Person build() {
        return this.person;
    }

    public PersonBuilder firstname(String firstname) {
        this.person.setFirstname(firstname);
        return this;
    }

    public PersonBuilder surname(String surname) {
        this.person.setSurname(surname);
        return this;
    }

    public PersonBuilder gender(Person.Gender gender) {
        this.person.setGender(gender);
        return this;
    }

    public PersonBuilder genderUnknown() {
        this.person.setGender(Person.Gender.UNKNOWN);
        return this;
    }

    public PersonBuilder genderMale() {
        this.person.setGender(Person.Gender.MALE);
        return this;
    }

    public PersonBuilder genderFemale() {
        this.person.setGender(Person.Gender.FEMALE);
        return this;
    }

    public PersonBuilder alive(boolean alive) {
        this.person.setAlive(alive);
        return this;
    }

    public PersonBuilder alive() {
        this.person.setAlive(true);
        return this;
    }

    public PersonBuilder notAlive() {
        this.person.setAlive(false);
        return this;
    }


    public PersonBuilder email(String email) {
        this.person.setEmail(email);
        return this;
    }

    public PersonBuilder address(Address address) {
        this.person.setAddress(address);
        return this;
    }


}
