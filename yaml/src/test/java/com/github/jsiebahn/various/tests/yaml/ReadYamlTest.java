package com.github.jsiebahn.various.tests.yaml;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.util.Iterator;
import java.util.Map;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 17.11.16 07:50
 */
public class ReadYamlTest {

    @Test
    public void shouldReadYaml() {

        Yaml yaml = new Yaml();

        Map map = yaml.loadAs(getClass().getResourceAsStream("/test.yaml"), Map.class);

        System.out.println(map.toString());

    }

    @Test
    public void shouldReadYamlInMarkdown() {

        Yaml yaml = new Yaml();
        Iterator<Object> yamls = yaml.loadAll("---\n"
                + "layout: post\n"
                + "published-on: 1 January 2000\n"
                + "title: Blogging Like a Boss\n"
                + "---\n"
                + "\n"
                + "Content goes here.").iterator();
        Object o = yamls.next();

        Map map = (Map) o;
        //noinspection unchecked
        assertThat(map).containsOnly(
                Assertions.entry("layout", "post"),
                Assertions.entry("published-on", "1 January 2000"),
                Assertions.entry("title", "Blogging Like a Boss")

        );

        Object next = yamls.next();
        assertThat(next).isEqualTo("Content goes here.");

        assertThat(yamls.hasNext()).isFalse();
    }
}
