package com.github.jsiebahn.various.tests.crypt.noop;

import ch.qos.logback.classic.Level;
import com.github.jsiebahn.various.tests.crypt.AbstractEncryptionUtilTest;
import com.github.jsiebahn.various.tests.crypt.Decrypter;
import com.github.jsiebahn.various.tests.crypt.Encrypter;
import com.github.jsiebahn.various.tests.crypt.internal.test.util.LoggingTestUtil;
import org.junit.After;
import org.junit.Before;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 01.11.14 07:11
 */
public class NoopEncryptionUtilTest extends AbstractEncryptionUtilTest {

    private Level beforeLevelEncrypter;
    private Level beforeLevelDecrypter;
    private Level beforeLevelNoopUtil;

    @Before
    public void disableLogging() {
        beforeLevelEncrypter = LoggingTestUtil.setLogLevel(Level.OFF, NoopEncrypter.class);
        beforeLevelDecrypter = LoggingTestUtil.setLogLevel(Level.OFF, NoopDecrypter.class);
        beforeLevelNoopUtil = LoggingTestUtil.setLogLevel(Level.OFF, NoopUtil.class);
    }

    @After
    public void enableLogging() {
        LoggingTestUtil.setLogLevel(beforeLevelEncrypter, NoopEncrypter.class);
        LoggingTestUtil.setLogLevel(beforeLevelDecrypter, NoopDecrypter.class);
        LoggingTestUtil.setLogLevel(beforeLevelNoopUtil, NoopUtil.class);
    }

    @Override
    public void shouldEncryptTwiceWithDifferentResult() {
        // results are equal by intention
    }

    @Override
    public void shouldNotBeModifiable() {
        // modification on plain text can not be tested
    }

    @Override
    public void shouldNotDecryptImpossibleInput() throws Exception {
        // there is no impossible input for the noop decryption
    }

    @Override
    protected Encrypter createEncrypter() {
        return new NoopEncrypter();
    }

    @Override
    protected Decrypter createDecrypter() {
        return new NoopDecrypter();
    }

}
