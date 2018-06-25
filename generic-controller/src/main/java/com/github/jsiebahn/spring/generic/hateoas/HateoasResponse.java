package com.github.jsiebahn.spring.generic.hateoas;

import org.springframework.http.HttpMethod;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 12.02.15 07:18
 */
public class HateoasResponse {

    private Map<String, HateoasLink> links = new HashMap<>();

    private Map<HttpMethod, HateoasEntity> methods = new HashMap<>();


    public Map<String, HateoasLink> getLinks() {
        return links;
    }

    public HateoasResponse addLink(String rel, HateoasLink hateoasLink) {
        this.links.put(rel, hateoasLink);
        return this;
    }

    public HateoasResponse addLink(String rel, URI link) {
        return this.addLink(rel, link, null);
    }

    public HateoasResponse addLink(String rel, URI link, String label) {
        return this.addLink(rel, link, label, null);
    }

    public HateoasResponse addLink(String rel, URI link, String label, String description) {
        HateoasLink hateoasLink = new HateoasLink();
        hateoasLink.setLink(link);
        hateoasLink.setLabel(label);
        hateoasLink.setDescription(description);
        addLink(rel, hateoasLink);
        return this;
    }

    public Map<HttpMethod, HateoasEntity> getMethods() {
        return methods;
    }

    public void addMethod(HttpMethod method, HateoasEntity entityDescription) {
        this.methods.put(method, entityDescription);
    }
}
