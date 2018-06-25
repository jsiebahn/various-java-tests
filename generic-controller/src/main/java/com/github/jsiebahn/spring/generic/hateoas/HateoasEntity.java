package com.github.jsiebahn.spring.generic.hateoas;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 12.02.15 07:21
 */
public class HateoasEntity {

    private String label;

    private String description;

    // TODO field descriptions


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
