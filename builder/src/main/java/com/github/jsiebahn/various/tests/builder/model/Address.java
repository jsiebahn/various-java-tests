package com.github.jsiebahn.various.tests.builder.model;

/**
 * $Id$
 *
 * @author  jsiebahn
 * @since 21.09.14 08:47
 */
public class Address {

    public String street;

    public String zipCode;

    public String city;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
