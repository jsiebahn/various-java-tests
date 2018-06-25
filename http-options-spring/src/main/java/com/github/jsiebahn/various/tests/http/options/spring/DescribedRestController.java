package com.github.jsiebahn.various.tests.http.options.spring;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.annotation.*;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 25.05.16 09:37
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Controller
@ResponseBody
public @interface DescribedRestController {
}
