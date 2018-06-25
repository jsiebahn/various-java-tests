package com.github.jsiebahn.spring.generic.hateoas;

import java.net.URI;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 12.02.15 07:19
 */
public class HateoasLink {

    private URI link;

    private String label;

    private String description;


    public URI getLink() {
        return link;
    }

    public void setLink(URI link) {
        this.link = link;
    }

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
