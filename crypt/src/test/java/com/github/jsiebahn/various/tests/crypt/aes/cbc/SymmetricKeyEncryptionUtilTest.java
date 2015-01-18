package com.github.jsiebahn.various.tests.crypt.aes.cbc;

import ch.qos.logback.classic.Level;
import com.github.jsiebahn.various.tests.crypt.*;
import com.github.jsiebahn.various.tests.crypt.internal.test.util.LoggingTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 01.11.14 07:48
 */
public class SymmetricKeyEncryptionUtilTest extends AbstractEncryptionUtilTest {


    private static final Level FORCE_LOG_LEVEL = Level.OFF;

    private Map<Class, Level> beforeLevels = new HashMap<>();

    @Before
    public void disableLogging() {
        beforeLevels.put(EncryptionUtilBuilder.class,
                LoggingTestUtil.setLogLevel(FORCE_LOG_LEVEL, SymmetricKeyEncrypter.class));
        beforeLevels.put(EncryptionUtil.class,
                LoggingTestUtil.setLogLevel(FORCE_LOG_LEVEL, SymmetricKeyDecrypter.class));
        beforeLevels.put(SymmetricKeyUtil.class,
                LoggingTestUtil.setLogLevel(Level.ERROR, SymmetricKeyUtil.class));
    }

    @After
    public void enableLogging() {
        for(Map.Entry<Class, Level> entry : beforeLevels.entrySet()) {
            LoggingTestUtil.setLogLevel(entry.getValue(), entry.getKey());
        }
    }

    @Override
    protected Encrypter createEncrypter() {
        return new SymmetricKeyEncrypter("test key");
    }

    @Override
    protected Decrypter createDecrypter() {
        return new SymmetricKeyDecrypter("test key");
    }

    @Test
    @Override
    public void shouldNotBeModifiable() {
        super.shouldNotBeModifiable();
    }

}
