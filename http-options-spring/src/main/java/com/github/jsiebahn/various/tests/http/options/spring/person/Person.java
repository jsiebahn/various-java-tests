package com.github.jsiebahn.various.tests.http.options.spring.person;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.jsiebahn.various.tests.http.options.spring.Identifiable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 25.05.16 12:08
 */
public class Person implements Identifiable<String> {

    @Id
    private String id;

    private String firstName;

    private String surName;

    @DBRef
    @JsonSerialize(using = PersonRestController.LinkSerializer.class)
    private Person father;

    @DBRef
    @JsonSerialize(using = PersonRestController.LinkSerializer.class)
    private Person mother;


    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurName() {
        return surName;
    }

    public Person getFather() {
        return father;
    }

    public Person getMother() {
        return mother;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public void setFather(Person father) {
        this.father = father;
    }

    public void setMother(Person mother) {
        this.mother = mother;
    }
}
