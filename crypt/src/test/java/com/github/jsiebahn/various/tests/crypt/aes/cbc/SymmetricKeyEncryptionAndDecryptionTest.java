package com.github.jsiebahn.various.tests.crypt.aes.cbc;

import ch.qos.logback.classic.Level;
import com.github.jsiebahn.various.tests.crypt.internal.test.util.LoggingTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.github.jsiebahn.various.tests.crypt.internal.StringUtil.stringToByte;
import static com.github.jsiebahn.various.tests.crypt.internal.test.util.LoggingTestUtil.setLogLevel;
import static org.junit.Assert.*;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 24.10.14 08:13
 */
public class SymmetricKeyEncryptionAndDecryptionTest {

    private Level symmetricKeyUtilBefore;

    @Before
    public void disableLogging() {
        symmetricKeyUtilBefore = LoggingTestUtil.setLogLevel(Level.ERROR, SymmetricKeyUtil.class);
    }

    @After
    public void enableLogging() {
        LoggingTestUtil.setLogLevel(symmetricKeyUtilBefore, SymmetricKeyUtil.class);
    }

    @Test
    public void shouldNotBeReadyToOperateWhenConstructEncrypterWithNullValue() throws Exception {

        Level before = setLogLevel(Level.OFF, SymmetricKeyEncrypter.class);

        try {

            SymmetricKeyEncrypter encrypter;

            encrypter = new SymmetricKeyEncrypter((String) null);
            assertFalse(encrypter.isReadyToOperate());

            encrypter = new SymmetricKeyEncrypter((byte[]) null);
            assertFalse(encrypter.isReadyToOperate());

            assertNull(encrypter.encrypt("test"));

        }
        finally {
            setLogLevel(before, SymmetricKeyEncrypter.class);
        }

    }

    @Test
    public void shouldNotBeReadyToOperateWhenConstructDecrypterWithNullValue() throws Exception {

        Level before = setLogLevel(Level.OFF, SymmetricKeyDecrypter.class);

        try {

            SymmetricKeyDecrypter decrypter;

            decrypter = new SymmetricKeyDecrypter((String) null);
            assertFalse(decrypter.isReadyToOperate());

            decrypter = new SymmetricKeyDecrypter((byte[]) null);
            assertFalse(decrypter.isReadyToOperate());

            assertNull(decrypter.decrypt("test"));

        }
        finally {
            setLogLevel(before, SymmetricKeyDecrypter.class);
        }

    }

    @Test
    public void shouldWorkInAnyKeyTypeCombination() throws Exception {

        SymmetricKeyEncrypter encrypterFromBytes =
                new SymmetricKeyEncrypter(stringToByte("t\u00E4st"));
        assertTrue(encrypterFromBytes.isReadyToOperate());

        SymmetricKeyEncrypter encrypterFromString = new SymmetricKeyEncrypter("t\u00E4st");
        assertTrue(encrypterFromString.isReadyToOperate());

        SymmetricKeyDecrypter decrypterFromBytes =
                new SymmetricKeyDecrypter(stringToByte("t\u00E4st"));
        assertTrue(decrypterFromBytes.isReadyToOperate());

        SymmetricKeyDecrypter decrypterFromString = new SymmetricKeyDecrypter("t\u00E4st");
        assertTrue(decrypterFromString.isReadyToOperate());

        String plain = "test plain";
        assertEquals(plain, decrypterFromBytes.decrypt(encrypterFromBytes.encrypt(plain)));
        assertEquals(plain, decrypterFromString.decrypt(encrypterFromBytes.encrypt(plain)));
        assertEquals(plain, decrypterFromBytes.decrypt(encrypterFromString.encrypt(plain)));
        assertEquals(plain, decrypterFromString.decrypt(encrypterFromString.encrypt(plain)));

    }

}
