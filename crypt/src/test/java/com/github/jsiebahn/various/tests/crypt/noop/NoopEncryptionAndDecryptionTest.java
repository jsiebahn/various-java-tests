package com.github.jsiebahn.various.tests.crypt.noop;

import ch.qos.logback.classic.Level;
import com.github.jsiebahn.various.tests.crypt.internal.test.util.LoggingTestUtil;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static com.github.jsiebahn.various.tests.crypt.internal.test.util.LoggingTestUtil.logToTestAppender;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 30.10.14 06:56
 */
public class NoopEncryptionAndDecryptionTest {

    @Test
    public void testEncryptAndDecryptString() throws Exception  {

        LoggingTestUtil.LogAssert encryptLogAssert = logToTestAppender(NoopEncrypter.class);
        LoggingTestUtil.LogAssert decryptLogAssert = logToTestAppender(NoopDecrypter.class);

        try {
            NoopEncrypter encrypter = new NoopEncrypter();
            NoopDecrypter decrypter = new NoopDecrypter();

            assertTrue(encrypter.isReadyToOperate());
            assertTrue(decrypter.isReadyToOperate());

            String plain = "test plain";
            String encrypted = encrypter.encrypt(plain);
            assertEquals(plain, encrypted);
            assertEquals(plain, decrypter.decrypt(encrypted));

            encryptLogAssert.assertWarnings("Noop encrypt should log a warning.", 1);
            decryptLogAssert.assertWarnings("Noop decrypt should log a warning.", 1);
        }
        finally {
            encryptLogAssert.resetLogger();
            decryptLogAssert.resetLogger();
        }

    }

    @Test
    public void testEncryptAndDecryptStreams() throws Exception  {

        LoggingTestUtil.LogAssert encryptLogAssert = logToTestAppender(NoopEncrypter.class);
        LoggingTestUtil.LogAssert decryptLogAssert = logToTestAppender(NoopDecrypter.class);

        Level noopUtilBefore = LoggingTestUtil.setLogLevel(Level.OFF, NoopUtil.class);
        LoggingTestUtil.setLogLevel(noopUtilBefore, NoopUtil.class);

        try {

            NoopEncrypter encrypter = new NoopEncrypter();
            NoopDecrypter decrypter = new NoopDecrypter();

            assertTrue(encrypter.isReadyToOperate());
            assertTrue(decrypter.isReadyToOperate());

            InputStream plain = new ByteArrayInputStream("test plain".getBytes());
            ByteArrayOutputStream encrypted = new ByteArrayOutputStream();
            assertTrue(encrypter.encrypt(plain, encrypted));
            String encryptedString = new String(encrypted.toByteArray(), "UTF-8");
            assertEquals("test plain", encryptedString);
            InputStream encryptedStream = new ByteArrayInputStream("test encrypted".getBytes());
            ByteArrayOutputStream plainResult = new ByteArrayOutputStream();
            assertTrue(decrypter.decrypt(encryptedStream, plainResult));
            String decryptedString = new String(plainResult.toByteArray(), "UTF-8");
            assertEquals("test encrypted", decryptedString);

            LoggingTestUtil.setLogLevel(Level.OFF, NoopUtil.class);

            assertFalse(encrypter.encrypt(null, encrypted));
            assertFalse(decrypter.decrypt(null, plainResult));
            assertFalse(encrypter.encrypt(plain, null));
            assertFalse(decrypter.decrypt(encryptedStream, null));

            encryptLogAssert.assertWarnings("Noop encrypt should log a warning.", 3);
            decryptLogAssert.assertWarnings("Noop decrypt should log a warning.", 3);
        }
        finally {
            encryptLogAssert.resetLogger();
            decryptLogAssert.resetLogger();
            LoggingTestUtil.setLogLevel(noopUtilBefore, NoopUtil.class);
        }

    }
}
