package com.github.jsiebahn.various.tests.builder.model;

import org.junit.Test;

import static com.github.jsiebahn.various.tests.builder.model.AddressBuilder.configureAddress;
import static org.junit.Assert.assertEquals;

/**
 * $Id$
 *
 * @author  jsiebahn
 * @since 21.09.14 08:57
 */
public class AddressBuilderTest {

    @Test
    public void testConfigureAddress() throws Exception {

        Address address = configureAddress()
                .street("Doe Lane 42")
                .zipCode("12345")
                .city("Capital City")
                .build();

        assertEquals("Doe Lane 42", address.getStreet());
        assertEquals("12345", address.getZipCode());
        assertEquals("Capital City", address.getCity());

    }
}
