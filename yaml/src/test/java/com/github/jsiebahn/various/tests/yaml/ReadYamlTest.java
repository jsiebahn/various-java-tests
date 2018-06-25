package com.github.jsiebahn.various.tests.yaml;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 17.11.16 07:50
 */
public class ReadYamlTest {

    @Test
    public void shouldReadYaml() throws Exception {

        Yaml yaml = new Yaml();

        Map map = yaml.loadAs(getClass().getResourceAsStream("/test.yaml"), Map.class);

        System.out.println(map.toString());

    }
}
