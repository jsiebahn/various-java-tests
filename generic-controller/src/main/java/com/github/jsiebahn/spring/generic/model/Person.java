package com.github.jsiebahn.spring.generic.model;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 26.11.14 19:16
 */
public class Person {

    private Integer id;

    private String firstName;

    private String surName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }
}
