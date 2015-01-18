package com.github.jsiebahn.various.tests.crypt.rsa;

import ch.qos.logback.classic.Level;
import com.github.jsiebahn.various.tests.crypt.AbstractEncryptionUtilTest;
import com.github.jsiebahn.various.tests.crypt.Decrypter;
import com.github.jsiebahn.various.tests.crypt.Encrypter;
import com.github.jsiebahn.various.tests.crypt.internal.test.util.LoggingTestUtil;
import org.junit.After;
import org.junit.Before;

import java.io.ByteArrayOutputStream;

/**
 * $Id$
 *
 * @author  jsiebahn
 * @since 03.11.14 07:36
 */
public class RsaEncryptionUtilTest extends AbstractEncryptionUtilTest {

    private static RsaKeySpecPair rsaKeySpecPair;

    private Level beforeLevelEncrypter;
    private Level beforeLevelDecrypter;

    @Before
    public void disableLogging() {
        // beforeLevelEncrypter = LoggingTestUtil.setLogLevel(Level.OFF, RsaEncrypter.class);
        // beforeLevelDecrypter = LoggingTestUtil.setLogLevel(Level.OFF, RsaDecrypter.class);
    }

    @After
    public void enableLogging() {
        LoggingTestUtil.setLogLevel(beforeLevelEncrypter, RsaEncrypter.class);
        LoggingTestUtil.setLogLevel(beforeLevelDecrypter, RsaDecrypter.class);
    }


    @Before
    public void initKeySpec() {
        if (rsaKeySpecPair == null) {
            RsaKeyFactory factory = new RsaKeyFactory();
            rsaKeySpecPair = factory.createKeys(new ByteArrayOutputStream(), new ByteArrayOutputStream());
        }
    }

    @Override
    protected Encrypter createEncrypter() {
        return new RsaEncrypter(rsaKeySpecPair.getPublicKeySpec());
    }

    @Override
    protected Decrypter createDecrypter() {
        return new RsaDecrypter(rsaKeySpecPair.getPrivateKeySpec());
    }
}
