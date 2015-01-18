package com.github.jsiebahn.various.tests.crypt.noop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utility for stream handling used by the {@link NoopEncrypter} and {@link NoopDecrypter}
 * internally.
 *
 * @author jsiebahn
 * @since 29.10.14 23:17
 */
public class NoopUtil {

    private static final Logger log = LoggerFactory.getLogger(NoopUtil.class);

    /**
     * Copies the content of the {@code source} {@link InputStream} to the {@code target}
     * {@link OutputStream} using blocks of 16KB. After copying, both streams will be
     * {@link AutoCloseable#close() closed}. {@link IOException}s are caught. If they occur, the
     * result will be {@code false}.
     *
     * @param source the {@link InputStream} with the source content. If {@code null} this method
     *               does nothing and returns {@code false}
     * @param target the {@link OutputStream} where the {@code source} content is written to. If
     *               {@code null} this method does nothing and returns {@code false}.
     * @return if {@code source} has been copied to {@code target}
     */
    static boolean inputStreamToOutputStream(InputStream source, OutputStream target) {

        if (source == null || target == null) {
            log.error("Could not copy from source '{}' to target '{}'.", source, target);
            return false;
        }

        try (InputStream is = source; OutputStream os = target) {
            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = is.read(data, 0, data.length)) != -1) {
                os.write(data, 0, nRead);
            }

            os.flush();
        } catch (IOException e) {
            log.error("Could not read from input stream or could not write to output stream.", e);
            return false;
        }

        return true;
    }

}
