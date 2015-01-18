package com.github.jsiebahn.various.tests.crypt;

import ch.qos.logback.classic.Level;
import com.github.jsiebahn.various.tests.crypt.internal.test.util.LoggingTestUtil;
import com.github.jsiebahn.various.tests.crypt.noop.NoopDecrypter;
import com.github.jsiebahn.various.tests.crypt.noop.NoopEncrypter;
import org.junit.Test;

import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.Assert.*;

/**
 * Tests corner cases of {@link EncryptionUtil} that are not covered by the tests for specific
 * encryption and decryption strategies in the implementations of
 * {@link AbstractEncryptionUtilTest}.
 *
 * @author jsiebahn
 * @since 29.10.14 19:43
 */
public class EncryptionUtilTest {

    @Test
    public void testEncryptOnlyWithoutDecrypter() throws Exception {

        Level before = LoggingTestUtil.setLogLevel(Level.ERROR, EncryptionUtil.class);

        Level minLevel = Level.ERROR;
        if (before != null && !Level.ERROR.isGreaterOrEqual(before)) {
            minLevel = before;
        }

        try {
            String encrypted;
            byte[] encryptedBytes;

            EncryptionUtil encryptionUtil = new EncryptionUtil(new NoopEncrypter(), null);

            String plain = "plain test";

            LoggingTestUtil.setLogLevel(Level.OFF, EncryptionUtil.class);

            encrypted = encryptionUtil.encrypt(plain);
            assertNull(encrypted);
            encryptedBytes = encryptionUtil.encrypt(plain.getBytes("UTF-8"));
            assertNull(encryptedBytes);

            LoggingTestUtil.setLogLevel(minLevel, EncryptionUtil.class);

            encrypted = encryptionUtil.encrypt(plain, true);
            assertNotNull(encrypted);
            encryptedBytes = encryptionUtil.encrypt(plain.getBytes("UTF-8"), true);
            assertNotNull(encryptedBytes);

            LoggingTestUtil.setLogLevel(Level.OFF, EncryptionUtil.class);

            assertNull(encryptionUtil.decrypt(encrypted));
            assertNull(encryptionUtil.decrypt(encryptedBytes));

        }
        finally {
            LoggingTestUtil.setLogLevel(before, EncryptionUtil.class);
        }

    }

    @Test
    public void testDecryptOnlyWithoutEncrypter() throws Exception {
        Level before = LoggingTestUtil.setLogLevel(Level.OFF, EncryptionUtil.class);

        try {
            EncryptionUtil encryptionUtil = new EncryptionUtil(null, new NoopDecrypter());
            String plain = "plain test";
            String encrypted = "plain test";
            byte[] encryptedBytes = encrypted.getBytes("UTF-8");
            assertNull(encryptionUtil.encrypt(plain));

            LoggingTestUtil.setLogLevel(before, EncryptionUtil.class);

            assertEquals(plain, encryptionUtil.decrypt(encrypted));
            assertArrayEquals(plain.getBytes("UTF-8"), encryptionUtil.decrypt(encryptedBytes));
        }
        finally {
            LoggingTestUtil.setLogLevel(before, EncryptionUtil.class);
        }

    }


    @Test
    public void testEncryptOnlyWithDecrypter() throws Exception {

        Level before = LoggingTestUtil.setLogLevel(Level.ERROR, EncryptionUtil.class);

        Level minLevel = Level.ERROR;
        if (before != null && !Level.ERROR.isGreaterOrEqual(before)) {
            minLevel = before;
        }

        try {
            String encrypted;
            byte[] encryptedBytes;

            EncryptionUtil encryptionUtil = new EncryptionUtil(new NoopEncrypter(),
                    new NotReadyToOperateDecrypter());

            String plain = "plain test";

            LoggingTestUtil.setLogLevel(Level.OFF, EncryptionUtil.class);

            encrypted = encryptionUtil.encrypt(plain);
            encryptedBytes = encryptionUtil.encrypt(plain.getBytes("UTF-8"));
            assertNull(encrypted);
            assertNull(encryptedBytes);

            LoggingTestUtil.setLogLevel(minLevel, EncryptionUtil.class);

            encrypted = encryptionUtil.encrypt(plain, true);
            encryptedBytes = encryptionUtil.encrypt(plain.getBytes("UTF-8"), true);
            assertNotNull(encrypted);
            assertNotNull(encryptedBytes);

            LoggingTestUtil.setLogLevel(Level.OFF, EncryptionUtil.class);

            assertNull(encryptionUtil.decrypt(encrypted));
            assertNull(encryptionUtil.decrypt(encryptedBytes));

        }
        finally {
            LoggingTestUtil.setLogLevel(before, EncryptionUtil.class);
        }

    }

    @Test
    public void testDecryptOnlyWithEncrypter() throws Exception {

        Level before = LoggingTestUtil.setLogLevel(Level.OFF, EncryptionUtil.class);

        try {
            EncryptionUtil encryptionUtil = new EncryptionUtil(new NotReadyToOperateEncrypter(),
                    new NoopDecrypter());
            String plain = "plain test";
            String encrypted = "plain test";
            byte[] encryptedBytes = encrypted.getBytes("UTF-8");
            assertNull(encryptionUtil.encrypt(plain));
            assertNull(encryptionUtil.encrypt(plain.getBytes("UTF-8")));
            assertNull(encryptionUtil.encrypt(plain, true));
            assertNull(encryptionUtil.encrypt(plain.getBytes("UTF-8"), true));

            LoggingTestUtil.setLogLevel(before, EncryptionUtil.class);

            assertEquals(plain, encryptionUtil.decrypt(encrypted));
            assertArrayEquals(plain.getBytes("UTF-8"), encryptionUtil.decrypt(encryptedBytes));
        }
        finally {
            LoggingTestUtil.setLogLevel(before, EncryptionUtil.class);
        }

    }


    //
    // test classes to test isConfiguredFor...()
    //

    private static class NotReadyToOperateEncrypter implements Encrypter {

        @Override
        public String encrypt(String plain) {
            return null;
        }

        @Override
        public boolean encrypt(InputStream plain, OutputStream encrypted) {
            return false;
        }

        @Override
        public boolean isReadyToOperate() {
            return false;
        }
    }

    private static class NotReadyToOperateDecrypter implements Decrypter {

        @Override
        public String decrypt(String encrypted) {
            return null;
        }

        @Override
        public boolean decrypt(InputStream encrypted, OutputStream plain) {
            return false;
        }

        @Override
        public boolean isReadyToOperate() {
            return false;
        }
    }
}
