package com.github.jsiebahn.various.tests.automatic;

import com.github.jsiebahn.various.tests.builder.model.Address;

import java.util.List;

/**
 * $Id$
 *
 * @author  jsiebahn
 * @since 21.09.14 09:53
 */
public class AnotherPerson {


    private String firstname;

    private String surname;

    private Gender gender;

    private boolean alive;

    private String email;

    private List<Address> addresses;

    public static enum Gender {
        UNKNOWN, MALE, FEMALE
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }
}
