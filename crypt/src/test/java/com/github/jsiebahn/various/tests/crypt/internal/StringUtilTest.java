package com.github.jsiebahn.various.tests.crypt.internal;

import ch.qos.logback.classic.Level;

import org.junit.Test;

import static com.github.jsiebahn.various.tests.crypt.internal.StringUtil.byteToString;
import static com.github.jsiebahn.various.tests.crypt.internal.StringUtil.stringToByte;
import static com.github.jsiebahn.various.tests.crypt.internal.test.util.LoggingTestUtil.setLogLevel;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests {@link StringUtil}.
 *
 * @author jsiebahn
 * @since 11.11.14 07:53
 */
public class StringUtilTest {

    @Test
    public void testExtendCoverage() {
        new StringUtil();
    }

    @Test
    public void testStringToByte() throws Exception {
        assertNull(stringToByte(null));
        assertEquals(0, stringToByte("").length);
        assertEquals(10, stringToByte("0123456789").length);
    }

    @Test
    public void testStringToByteFail() throws Exception {
        Level before = setLogLevel(Level.OFF, StringUtil.class);

        try {
            assertNull(stringToByte("", "noValidEncoding"));
        }
        finally {
            setLogLevel(before, StringUtil.class);
        }

    }

    @Test
    public void testByteToString() throws Exception {
        assertNull(byteToString(null));
        assertEquals(0, byteToString("".getBytes()).length());
        assertEquals(10, byteToString("0123456789".getBytes()).length());
    }

    @Test
    public void testByteToStringFail() throws Exception {
        Level before = setLogLevel(Level.OFF, StringUtil.class);

        try {
            assertNull(byteToString("".getBytes(), "noValidEncoding"));
        }
        finally {
            setLogLevel(before, StringUtil.class);
        }

    }
}
