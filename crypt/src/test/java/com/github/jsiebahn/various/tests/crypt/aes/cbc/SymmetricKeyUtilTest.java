package com.github.jsiebahn.various.tests.crypt.aes.cbc;

import ch.qos.logback.classic.Level;
import com.github.jsiebahn.various.tests.crypt.internal.StringUtil;
import com.github.jsiebahn.various.tests.crypt.internal.test.util.LoggingTestUtil;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import static com.github.jsiebahn.various.tests.crypt.aes.cbc.SymmetricKeyUtil.createValidKey;
import static com.github.jsiebahn.various.tests.crypt.internal.test.util.LoggingTestUtil.LogAssert;
import static com.github.jsiebahn.various.tests.crypt.internal.test.util.LoggingTestUtil.setLogLevel;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 25.10.14 10:38
 */
public class SymmetricKeyUtilTest {

    @Test
    public void testExtendCoverage() {
        new SymmetricKeyUtil();
    }

    @Test
    public void testCreateValidKey() throws Exception {

        Level before = setLogLevel(Level.OFF, SymmetricKeyUtil.class);

        try {

            String cipherAlgorithm = SymmetricKeyUtil.ALGORITHM;
            String hashAlgorithm = SymmetricKeyUtil.HASH_ALGORITHM;

            // just check if there are no exceptions and no duplicates

            byte[] validKeyTest = createValidKey(StringUtil.stringToByte("test"), cipherAlgorithm,
                    hashAlgorithm);
            assertNotNull(validKeyTest);

            byte[] validKeyEmpty = createValidKey(StringUtil.stringToByte(""), cipherAlgorithm,
                    hashAlgorithm);
            assertNotNull(validKeyEmpty);
            assertThat(validKeyTest, not(equalTo(validKeyEmpty)));


            byte[] validKeyT = createValidKey(StringUtil.stringToByte("t"), cipherAlgorithm,
                    hashAlgorithm);
            assertNotNull(validKeyT);
            assertThat(validKeyTest, not(equalTo(validKeyT)));
            assertThat(validKeyEmpty, not(equalTo(validKeyT)));

            byte[] validKeyTT = createValidKey(StringUtil.stringToByte("tt"), cipherAlgorithm,
                    hashAlgorithm);
            assertNotNull(validKeyTT);
            assertThat(validKeyTest, not(equalTo(validKeyTT)));
            assertThat(validKeyEmpty, not(equalTo(validKeyTT)));
            assertThat(validKeyT, not(equalTo(validKeyTT)));

        }
        finally {
            setLogLevel(before, SymmetricKeyUtil.class);
        }

    }

    @Test
    public void testCreateValidKeyFail() throws Exception {
        Level before = setLogLevel(Level.OFF, SymmetricKeyUtil.class);

        try {
            // hashing impossible, no valid key created
            assertNull(createValidKey(StringUtil.stringToByte("test"),
                    SymmetricKeyUtil.ALGORITHM, "noValidAlgorithm"));
        }
        finally {
            setLogLevel(before, SymmetricKeyUtil.class);
        }
    }

    @Test
    public void testCrateCipherFail() {

        LogAssert logAssert = LoggingTestUtil.logToTestAppender(SymmetricKeyUtil.class);

        try {
            assertNull(SymmetricKeyUtil.createCipher("noValidAlgorithm", "SunJCE"));
            logAssert.assertErrors(1);

            assertNull(SymmetricKeyUtil.createCipher("AES/CBC/noValidPadding", "SunJCE"));
            logAssert.assertErrors(2);

            assertNull(SymmetricKeyUtil.createCipher("AES/CBC/PKCS5PADDING", "noValidProvider"));
            logAssert.assertErrors(3);

            assertNotNull(SymmetricKeyUtil.createCipher());
            logAssert.assertErrors(3);
        }
        finally {
            logAssert.resetLogger();
        }
    }

    @Test
    public void testInitCipherEncrypt() throws Exception {

        byte[] validKey = StringUtil.stringToByte("0123456789012345");

        Cipher cipher = Cipher.getInstance(SymmetricKeyUtil.TRANSFORMATION);
        SecretKeySpec secretKeySpec = new SecretKeySpec(validKey, SymmetricKeyUtil.ALGORITHM);

        byte[] iv1 = SymmetricKeyUtil.initCipherForEncrypt(cipher, secretKeySpec);
        byte[] iv2 = SymmetricKeyUtil.initCipherForEncrypt(cipher, secretKeySpec);

        assertThat(iv1, not(equalTo(iv2)));

    }

    @Test
    public void testCreateSecretKeySpec() {
        assertNotNull(SymmetricKeyUtil.createSecretKeySpec("test".getBytes()));
        assertNull(SymmetricKeyUtil.createSecretKeySpec(null));
    }

    @Test
    public void testInitCipherDecrypt() throws Exception {

        byte[] validKey = StringUtil.stringToByte("0123456789012345");

        Cipher cipher = Cipher.getInstance(SymmetricKeyUtil.TRANSFORMATION);
        SecretKeySpec secretKeySpec = new SecretKeySpec(validKey, SymmetricKeyUtil.ALGORITHM);

        byte[] iv = SymmetricKeyUtil.initCipherForEncrypt(cipher, secretKeySpec);

        assertTrue(SymmetricKeyUtil.initCipherForDecrypt(cipher, secretKeySpec, iv));

    }


}
