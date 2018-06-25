package com.github.jsiebahn.various.tests.http.options.spring;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 25.05.16 16:31
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
}
