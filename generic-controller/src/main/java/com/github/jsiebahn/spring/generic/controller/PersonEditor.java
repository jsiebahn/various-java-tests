package com.github.jsiebahn.spring.generic.controller;

import com.github.jsiebahn.spring.generic.model.Person;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 28.11.14 08:11
 */
@EntityEditor(Person.class)
@RequestMapping(EntityEditor.BASE_PATH + "/Person")
public class PersonEditor extends EntityEditorAdapter<Person> {
}
