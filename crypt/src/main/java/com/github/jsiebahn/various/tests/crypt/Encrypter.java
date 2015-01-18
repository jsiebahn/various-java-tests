package com.github.jsiebahn.various.tests.crypt;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * An {@code Encrypter} performs the encryption. All implemented methods should be thread safe.
 * Every {@code Encrypter} shall have a corresponding {@link Decrypter}. Because an
 * {@code Encrypter} is able to return {@link String} values as encryption result, the encrypted
 * result of {@link #encrypt(String)} and
 * {@link #encrypt(java.io.InputStream, java.io.OutputStream)} shall be encoded in a way that a
 * valid {@link String} is created. An easy way to achieve this, is to use Base64 encoding on the
 * raw encrypted byte array. The corresponding {@link Decrypter} of course has to decode the
 * encrypted result before decryption starts.
 *
 * @author jsiebahn
 * @since 24.10.14 06:54
 */
public interface Encrypter {

    /**
     * Encrypts a {@link String} into a {@link String}.
     *
     * @param plain the {@link String} to encrypt
     * @return the encrypted {@link String}
     */
    public String encrypt(String plain);

    /**
     * Encrypts an {@link InputStream} into an {@link OutputStream}.
     *
     * @param plain the {@link InputStream} which content should be encrypted.
     * @param encrypted the output stream where the encrypted result is written to.
     * @return if the encryption ended successfully
     */
    public boolean encrypt(InputStream plain, OutputStream encrypted);


    /**
     * @return returns {@code true} if the instance is ready to operate and can start with
     *      encryption. It may be not ready to operate if required data is missing to perform
     *      encryption or initialization is in progress.
     */
    public boolean isReadyToOperate();
}
