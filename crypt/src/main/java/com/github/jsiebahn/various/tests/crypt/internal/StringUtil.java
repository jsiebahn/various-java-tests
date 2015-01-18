package com.github.jsiebahn.various.tests.crypt.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * Some convenient methods for Strings.
 *
 * @author jsiebahn
 * @since 11.11.14 07:47
 */
public class StringUtil {

    /**
     * The logger used if errors occur.
     */
    private static final Logger log = LoggerFactory.getLogger(StringUtil.class);

    /**
     * The encoding used for String operations.
     */
    private static final String ENCODING = "UTF-8";


    //
    // api
    //

    /**
     * Converts a {@link String} into a {@code byte array} using {@link #ENCODING}.
     *
     * @param s the {@link String} to convert
     * @return the byte array representation of {@code s}
     */
    public static byte[] stringToByte(String s) {
        return stringToByte(s, ENCODING);
    }

    /**
     * Converts a {@code byte array} into a {@link String} using {@link #ENCODING}.
     *
     * @param bytes the {@code byte array} to convert
     * @return the {@link String} representation of {@code bytes}
     */
    public static String byteToString(byte[] bytes) {
        return byteToString(bytes, ENCODING);
    }


    //
    // helper
    //

    /**
     * Shall be accessed by unit tests only. Otherwise this method should be considered as private.
     * See {@link #stringToByte(String)}
     */
    static byte[] stringToByte(String s, String encoding) {
        if (s == null) {
            return null;
        }
        try {
            return s.getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
            log.error("Encoding not supported: {}", encoding, e);
            return null;
        }
    }

    /**
     * Shall be accessed by unit tests only. Otherwise this method should be considered as private.
     * See {@link #stringToByte(String)}
     */
    static String byteToString(byte[] bytes, String encoding) {
        if (bytes == null) {
            return null;
        }
        try {
            return new String(bytes, encoding);
        } catch (UnsupportedEncodingException e) {
            log.error("Encoding not supported: {}", encoding, e);
            return null;
        }
    }

}
