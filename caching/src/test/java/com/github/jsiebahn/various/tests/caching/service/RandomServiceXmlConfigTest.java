package com.github.jsiebahn.various.tests.caching.service;

import com.github.jsiebahn.various.tests.caching.config.SpringCacheXmlConfig;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 17.01.15 09:50
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringCacheXmlConfig.class)
public class RandomServiceXmlConfigTest extends AbstractRandomServiceTest {

    // test cases defined in abstract base class

}
