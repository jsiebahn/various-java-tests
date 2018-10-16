package com.github.jsiebahn.various.tests.mandatory;

import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class PersonTest {

    @Test
    public void shouldRequireMandatoryFields() {

        Person person = Person.builder()
                .withFirstName("John")
                .withSurName("Doe")
                .withEmailAddress("j.doe@example.com")
                .build();

        assertThat(person)
                .extracting(
                        Person::getFirstName,
                        Person::getSurName,
                        Person::getAge,
                        Person::getEmailAddresses
                )
                .contains("John", "Doe", 0, singletonList("j.doe@example.com"));

    }

    @Test
    public void shouldSetOptionalFields() {

        Person person = Person.builder()
                .withFirstName("John")
                .withSurName("Doe")
                .withEmailAddress("j.doe@example.com")
                .addEmailAddress("john.doe@example.com")
                .withAge(42)
                .build();

        assertThat(person)
                .extracting(
                        Person::getFirstName,
                        Person::getSurName,
                        Person::getAge,
                        Person::getEmailAddresses
                )
                .contains("John", "Doe", 42, asList("j.doe@example.com", "john.doe@example.com"));

    }

    @Test
    public void shouldAddMultipleAdditionalEmailAddressesIgnoringNullValues() {
        Person person = Person.builder()
                .withFirstName("John")
                .withSurName("Doe")
                .withEmailAddress("j.doe@example.com")
                .addEmailAddresses("john.doe@example.com")
                .addEmailAddresses("jdo@example.com", null)
                .addEmailAddresses(asList("johndoe@example.com", null))
                .build();

        assertThat(person)
                .extracting(
                        Person::getFirstName,
                        Person::getSurName,
                        Person::getAge,
                        Person::getEmailAddresses
                )
                .contains(
                        "John",
                        "Doe",
                        0,
                        asList("j.doe@example.com", "john.doe@example.com", "jdo@example.com", "johndoe@example.com")
                );

    }
}