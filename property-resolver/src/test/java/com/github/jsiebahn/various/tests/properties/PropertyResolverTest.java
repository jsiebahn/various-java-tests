package com.github.jsiebahn.various.tests.properties;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;

import static com.github.jsiebahn.various.tests.properties.PropertyResolver.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author jsiebahn
 * @since 25.01.16 07:52
 */
public class PropertyResolverTest {

    @Test
    public void testInitArgs() throws Exception {

        assertEquals(4711, intOf("i"));

        File tempConfig = File.createTempFile(getClass().getSimpleName(), ".properties");

        String content = "";
        // recursive check
        content += "config.path = " + tempConfig.getAbsolutePath() + "\n";
        content += "i = 42";

        FileOutputStream os = new FileOutputStream(tempConfig);
        os.write(content.getBytes(Charset.forName("ISO-8859-1")));
        os.close();

        initArgs(new String[] {
                "d=1.5",
                "--f=0.5",
                "config.path=" + tempConfig.getAbsolutePath(),
                "l=68719476736",
                "s=localhost"
        });

        logAllProperties();

        assertEquals(System.getProperty("java.version"), stringOf("java.version"));

        assertEquals(42, intOf("i"));
        assertEquals(42, intOf("i", 815));
        assertEquals(0, intOf("xi"));
        assertEquals(2, intOf("xi", 2));
        assertEquals(2, intOf("s", 2));

        assertEquals(68719476736L, longOf("l"));
        assertEquals(68719476736L, longOf("l", 815L));
        assertEquals(0L, longOf("xl"));
        assertEquals(68719476737L, longOf("xl", 68719476737L));
        assertEquals(68719476737L, longOf("s", 68719476737L));

        assertEquals(1.5D, doubleOf("d"), 0.00001);
        assertEquals(1.5D, doubleOf("d", 2.0D), 0.00001);
        assertEquals(0.0D, doubleOf("xd"), 0.00001);
        assertEquals(2.0D, doubleOf("xd", 2.0D), 0.00001);
        assertEquals(2.0D, doubleOf("s", 2.0D), 0.00001);

        assertEquals(0.5F, floatOf("f"), 0.00001);
        assertEquals(0.5F, floatOf("f", 2.0F), 0.00001);
        assertEquals(0.0F, floatOf("xf"), 0.00001);
        assertEquals(2.0F, floatOf("xf", 2.0F), 0.00001);
        assertEquals(2.0F, floatOf("s", 2.0F), 0.00001);

        assertEquals("Property Value", stringOf("fromAppClasspath"));
        assertEquals("localhost", stringOf("s", "theDefault"));
        assertEquals("theDefault", stringOf("doesNotExist", "theDefault"));
        assertEquals("", stringOf("doesNotExist"));

        assertFalse(booleanOf("thisIsFalse"));
        assertFalse(booleanOf("thisIsAlsoFalse"));
        assertFalse(booleanOf("thisDoesNotExits"));

        assertTrue(booleanOf("thisIsTrue"));
        assertTrue(booleanOf("thisDoesNotExits", true));

    }
}