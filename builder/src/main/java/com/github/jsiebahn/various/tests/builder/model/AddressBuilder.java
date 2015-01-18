package com.github.jsiebahn.various.tests.builder.model;

/**
 * $Id$
 *
 * @author  jsiebahn
 * @since 21.09.14 08:48
 */
public class AddressBuilder {

    private Address address;

    private AddressBuilder() {
        this.address = new Address();
    }

    public static AddressBuilder configureAddress() {
        return new AddressBuilder();
    }

    public Address build() {
        return this.address;
    }

    public AddressBuilder street(String street) {
        this.address.setStreet(street);
        return this;
    }

    public AddressBuilder zipCode(String zipCode) {
        this.address.setZipCode(zipCode);
        return this;
    }

    public AddressBuilder city(String city) {
        this.address.setCity(city);
        return this;
    }
}
