package com.github.jsiebahn.spring.generic.controller;

import com.github.jsiebahn.spring.generic.model.Person;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 28.11.14 08:11
 */
@EntityEditor(Person.class)
public class PersonEditor extends EntityEditorAdapter<Person> {

    @Override
    Object createEditorTemplate() {
        return new Object();
    }

}
