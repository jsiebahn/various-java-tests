package com.github.jsiebahn.various.tests.crypt.noop;

import com.github.jsiebahn.various.tests.crypt.Encrypter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 29.10.14 23:08
 */
public class NoopEncrypter implements Encrypter {

    private static final Logger log = LoggerFactory.getLogger(NoopEncrypter.class);

    @Override
    public String encrypt(String plain) {
        warn();
        return plain;
    }

    @Override
    public boolean encrypt(InputStream plain, OutputStream encrypted) {
        warn();
        return NoopUtil.inputStreamToOutputStream(plain, encrypted);
    }

    @Override
    public boolean isReadyToOperate() {
        return true;
    }

    private void warn() {
        log.warn("Encryption with {} should only be used in test and development scenarios.",
                this.getClass().getName());
    }
}
