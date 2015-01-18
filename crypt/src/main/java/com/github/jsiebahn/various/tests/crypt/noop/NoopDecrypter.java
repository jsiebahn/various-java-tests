package com.github.jsiebahn.various.tests.crypt.noop;

import com.github.jsiebahn.various.tests.crypt.Decrypter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 29.10.14 23:13
 */
public class NoopDecrypter implements Decrypter {

    private static final Logger log = LoggerFactory.getLogger(NoopDecrypter.class);

    @Override
    public String decrypt(String encrypted) {
        warn();
        return encrypted;
    }

    @Override
    public boolean decrypt(InputStream encrypted, OutputStream plain) {
        warn();
        return NoopUtil.inputStreamToOutputStream(encrypted, plain);
    }

    @Override
    public boolean isReadyToOperate() {
        return true;
    }

    private void warn() {
        log.warn("Decryption with {} should only be used in test and development scenarios.",
                this.getClass().getName());
    }

}
