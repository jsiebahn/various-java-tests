package com.github.jsiebahn.various.tests.handlebars;

import com.github.jknack.handlebars.*;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * $Id$
 *
 * @author  jsiebahn
 * @since 30.09.14 07:19
 */
public class HandlebarTest {

    @Test
    public void testHandlebars() throws IOException {

        Handlebars handlebars = new Handlebars();

        Map<String, Object> data = new HashMap<>();
        data.put("msg", "a test & a demo");
        data.put("a", new String[] {"foo", "bar"});

        Template template = handlebars.compileInline("This is {{msg}}! " +
                "{{#a}}{{this}}{{#if @last}}!{{else}}, {{/if}}{{/a}} " +
                "Templates work with \\{{ and }}.");
        assertEquals("This is a test &amp; a demo! foo, bar! " +
                "Templates work with {{ and }}.", template.apply(data));

        List<String> parameters = template.collect(TagType.values());
        int others = 0;
        for (String param : parameters) {
            if (param.startsWith("@") || param.equals("this") || param.equals("if")) {
                others++;
                continue;
            }
            if (!data.containsKey(param)) {
                fail("Param " + param + " missing in data map.");
            }
        }
        for (String key : data.keySet()) {
            if (!parameters.contains(key)) {
                fail("Key " + key + " missing in key list.");
            }
        }
        assertEquals(2 + others, parameters.size());

    }

    @Test
    public void testHandleBarsNoEscape() throws IOException {

        Handlebars handlebars = new Handlebars();

        Template template;

        template = handlebars.compileInline("Hello {{{this}}}!");
        assertEquals("Hello <Max>!", template.apply("<Max>"));

        template = handlebars.compileInline("Hello {{this}}!");
        assertEquals("Hello &lt;Max&gt;!", template.apply("<Max>"));

        handlebars.registerHelper("test", new Helper<String>() {
            @Override
            public CharSequence apply(String context, Options options) throws IOException {
                String result =  "'" + context + "'";
                return new Handlebars.SafeString(result);
            }
        });

        template = handlebars.compileInline("Hello {{test this}}!");
        assertEquals("Hello '<Max>'!", template.apply("<Max>"));

    }

    @Test
    public void testHandleBarsInclude() throws IOException {

        Map<String, Object> data = new HashMap<>();
        data.put("bar", "A value");
        data.put("foo", "This is a paragraph");

        Handlebars handlebars = new Handlebars();
        handlebars.with(
                new ClassPathTemplateLoader("/com/github/jsiebahn/templates/general/", ".hbs"),
                new ClassPathTemplateLoader("/com/github/jsiebahn/various/tests/", ".hbs"));

        Template template = handlebars.compile("handlebars/outer");

        assertEquals("<div>\n" +
                "    <h1>General Template Header with A value</h1>\n" +
                "    \n<p>This is a paragraph</p>\n" +
                "</div>",
                template.apply(data));

    }
}
