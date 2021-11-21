package com.github.jsiebahn.various.tests.dropwizard.query;

import java.lang.annotation.*;

/**
 * Annotates {@link Custom} parameters to derive an instance from query parameters
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface CustomQueryParam {
}
