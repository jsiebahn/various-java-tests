package com.github.jsiebahn.various.tests.mandatory;

import com.sun.istack.internal.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class Person {

    private String firstName;

    private String surName;

    private int age;

    private List<String> emailAddresses = new ArrayList<>();

    public static InitialBuilder builder() {
        return new Builder();
    }

    private Person(String firstName, String surName, int age, List<String> emailAddresses) {
        this.firstName = firstName;
        this.surName = surName;
        this.age = age;
        this.emailAddresses.addAll(emailAddresses);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurName() {
        return surName;
    }

    public int getAge() {
        return age;
    }

    public List<String> getEmailAddresses() {
        return emailAddresses;
    }

    // Builder

    public interface InitialBuilder {
        NameBuilder withFirstName(@NotNull String firstName);
    }

    public interface NameBuilder {
        EmailBuilder withSurName(@NotNull String surName);
    }

    public interface EmailBuilder {
        FinalBuilder withEmailAddress(@NotNull String primaryEmail);
    }

    public interface FinalBuilder {
        FinalBuilder withAge(int age);
        FinalBuilder addEmailAddress(String additionalEmail);
        FinalBuilder addEmailAddresses(String... additionalEmails);
        FinalBuilder addEmailAddresses(List<String> additionalEmails);
        Person build();
    }

    public static class Builder implements InitialBuilder, NameBuilder, EmailBuilder, FinalBuilder {


        private String firstName;

        private String surName;

        private int age;

        private List<String> emailAddresses = new ArrayList<>();

        @Override
        public NameBuilder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        @Override
        public EmailBuilder withSurName(String surName) {
            this.surName = surName;
            return this;
        }

        @Override
        public FinalBuilder withEmailAddress(String primaryEmail) {
            this.emailAddresses.add(primaryEmail);
            return this;
        }

        @Override
        public FinalBuilder withAge(int age) {
            this.age = age;
            return this;
        }

        @Override
        public FinalBuilder addEmailAddress(String email) {
            addEmailAddresses(singletonList(email));
            return this;
        }

        @Override
        public FinalBuilder addEmailAddresses(String... additionalEmails) {
            this.addEmailAddresses(asList(additionalEmails));
            return this;
        }

        @Override
        public FinalBuilder addEmailAddresses(List<String> additionalEmails) {
            this.emailAddresses.addAll(additionalEmails.stream().filter(Objects::nonNull).collect(Collectors.toList()));
            return this;
        }

        @Override
        public Person build() {
            return new Person(firstName, surName, age, emailAddresses);
        }
    }
}
