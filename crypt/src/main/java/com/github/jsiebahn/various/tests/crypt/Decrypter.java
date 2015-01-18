package com.github.jsiebahn.various.tests.crypt;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * A {@code Decrypter} performs the decryption. All implemented methods should be thread safe. Every
 * {@code Decrypter} shall have a corresponding {@link Encrypter}.
 *
 * @author jsiebahn
 * @since 24.10.14 07:01
 */
public interface Decrypter {

    /**
     * Decrypts a {@link String} into a {@link String}.
     *
     * @param encrypted the {@link String} to decrypt. The input may contain additional information
     *                  of the used encryption like a salt or an initialisation vector
     * @return the decrypted {@link String}
     */
    public String decrypt(String encrypted);

    /**
     * Decrypts an {@link java.io.InputStream} into a {@link java.io.OutputStream}.
     *
     * @param encrypted the data to decrypt. The input may contain additional information of the
     *                  used encryption like a salt or an initialisation vector
     * @param plain the plain data after decryption
     * @return if the decryption ended successfully
     */
    public boolean decrypt(InputStream encrypted, OutputStream plain);


    /**
     * @return returns {@code true} if the instance is ready to operate and can start with
     *      decryption. It may be not ready to operate if required data is missing to perform
     *      decryption or initialization is in progress.
     */
    public boolean isReadyToOperate();

}
