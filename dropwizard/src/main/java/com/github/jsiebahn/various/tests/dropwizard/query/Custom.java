package com.github.jsiebahn.various.tests.dropwizard.query;

import javax.ws.rs.core.MultivaluedMap;

/**
 * A custom entity derived from query params.
 */
public class Custom {

    private MultivaluedMap<String, String> all;

    public Custom(MultivaluedMap<String, String> all) {
        this.all = all;
    }

    public MultivaluedMap<String, String> getAll() {
        return all;
    }
}
