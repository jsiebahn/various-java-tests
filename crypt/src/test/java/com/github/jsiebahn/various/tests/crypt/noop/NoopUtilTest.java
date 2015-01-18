package com.github.jsiebahn.various.tests.crypt.noop;

import com.github.jsiebahn.various.tests.crypt.internal.test.util.LoggingTestUtil;
import org.junit.Test;

import java.io.*;

import static com.github.jsiebahn.various.tests.crypt.internal.test.util.LoggingTestUtil.LogAssert;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

/**
 * Tests {@link NoopUtil}
 *
 * @author jsiebahn
 * @since 13.11.14 07:53
 */
public class NoopUtilTest {

    @Test
    public void testExtendCoverage() {
        new NoopUtil();
    }

    @Test
    public void testInputStreamToOutputStream() throws Exception {

        InputStream is = new ByteArrayInputStream("test".getBytes());
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        assertTrue(NoopUtil.inputStreamToOutputStream(is, os));
        assertEquals("test", new String(os.toByteArray()));

    }

    @Test
    public void testInputStreamToOutputStreamInNull() throws Exception {

        LogAssert logAssert = LoggingTestUtil.logToTestAppender(NoopUtil.class);

        InputStream is = null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        assertFalse(NoopUtil.inputStreamToOutputStream(is, os));

        logAssert.assertErrors(1);
        logAssert.resetLogger();

    }

    @Test
    public void testInputStreamToOutputStreamOutNull() throws Exception {

        LogAssert logAssert = LoggingTestUtil.logToTestAppender(NoopUtil.class);

        InputStream is = new ByteArrayInputStream("test".getBytes());
        ByteArrayOutputStream os = null;

        assertFalse(NoopUtil.inputStreamToOutputStream(is, os));

        logAssert.assertErrors(1);
        logAssert.resetLogger();

    }

    @Test
    public void testInputStreamToOutputStreamNotReadable() throws Exception {

        LogAssert logAssert = LoggingTestUtil.logToTestAppender(NoopUtil.class);

        InputStream is = mock(InputStream.class);
        doThrow(new IOException("Exception")).when(is).read(any(byte[].class), anyInt(), anyInt());
        doThrow(new IOException("Exception")).when(is).read(any(byte[].class));
        doThrow(new IOException("Exception")).when(is).read();
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        assertFalse(NoopUtil.inputStreamToOutputStream(is, os));

        logAssert.assertErrors(1);
        logAssert.resetLogger();

    }

    @Test
    public void testInputStreamToOutputStreamNotWritable() throws Exception {

        LogAssert logAssert = LoggingTestUtil.logToTestAppender(NoopUtil.class);

        InputStream is = new ByteArrayInputStream("test".getBytes());
        OutputStream os = mock(OutputStream.class);
        doThrow(new IOException("Exception")).when(os).write(any(byte[].class), anyInt(), anyInt());
        doThrow(new IOException("Exception")).when(os).write(any(byte[].class));
        doThrow(new IOException("Exception")).when(os).write(anyInt());

        assertFalse(NoopUtil.inputStreamToOutputStream(is, os));

        logAssert.assertErrors(1);
        logAssert.resetLogger();

    }

}
