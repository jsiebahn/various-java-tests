package com.github.jsiebahn.various.tests.dropwizard;

import com.github.jsiebahn.various.tests.dropwizard.query.Custom;
import com.github.jsiebahn.various.tests.dropwizard.query.CustomQueryParam;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/filter")
@Singleton
public class CustomQueryParamController {

    @GET
    @Path("/method")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFilter(@CustomQueryParam Custom custom) {
        return Response.ok(custom).build();
    }
}
