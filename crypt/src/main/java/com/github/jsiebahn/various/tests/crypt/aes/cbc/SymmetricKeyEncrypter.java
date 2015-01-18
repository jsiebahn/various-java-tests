package com.github.jsiebahn.various.tests.crypt.aes.cbc;

import com.github.jsiebahn.various.tests.crypt.Encrypter;
import com.github.jsiebahn.various.tests.crypt.internal.StringUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * An {@link Encrypter} that encrypts with symmetric key encryption. Internally AES in counter
 * block mode is used. The encrypted result is prepended by the 16 byte initialisation vector which
 * is required for decryption. All data added to the stream is base64 encoded.
 *
 * @author jsiebahn
 * @since 24.10.14 07:19
 */
public class SymmetricKeyEncrypter implements Encrypter {

    private static final Logger log = LoggerFactory.getLogger(SymmetricKeyEncrypter.class);

    /**
     * The key spec to use.
     */
    private SecretKeySpec secretKeySpec;

    /**
     * If this instance is correctly initialised.
     */
    private boolean initialised;

    public SymmetricKeyEncrypter(String key) {

        this(StringUtil.stringToByte(key));

    }

    public SymmetricKeyEncrypter(byte[] key) {
        if (key == null) {
            return;
        }
        this.secretKeySpec = SymmetricKeyUtil.createSecretKeySpec(key);

        if (this.secretKeySpec == null) {
            return;
        }

        // do a test encryption to check initialisation state
        initialised = true;
        initialised = encrypt("test") != null;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String encrypt(String plain) {

        if (!checkInitialised()) {
            return null;
        }

        byte[] plainBytes = StringUtil.stringToByte(plain);
        if (plainBytes == null) {
            return null;
        }

        try {
            Cipher cipher = SymmetricKeyUtil.createCipher();

            if (cipher == null) {
                return null;
            }

            byte[] iv = SymmetricKeyUtil.initCipherForEncrypt(cipher, this.secretKeySpec);
            byte[] encrypted = cipher.doFinal(plainBytes);
            byte[] ivAndEncrypted = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, ivAndEncrypted, 0, iv.length);
            System.arraycopy(encrypted, 0, ivAndEncrypted, iv.length, encrypted.length);
            return Base64.encodeBase64String(ivAndEncrypted);
        } catch (IllegalBlockSizeException e) {
            log.error("Illegal block size: {} bytes", plainBytes.length, e);
        } catch (BadPaddingException e) {
            log.error("Bad padding: {} bytes", plainBytes.length, e);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean encrypt(InputStream plain, OutputStream encrypted) {

        if (!checkInitialised()) {
            return false;
        }

        if (plain == null) {
            log.error("plain InputStream is null.");
            return false;
        }

        if (encrypted == null) {
            log.error("encrypted OutputStream is null.");
            return false;
        }

        Cipher cipher = SymmetricKeyUtil.createCipher();

        if (cipher == null) {
            return false;
        }

        byte[] iv = SymmetricKeyUtil.initCipherForEncrypt(cipher, this.secretKeySpec);

        try (OutputStream base64Out = new Base64OutputStream(encrypted);
             CipherOutputStream cipherOs = new CipherOutputStream(base64Out, cipher)) {

            base64Out.write(iv, 0, iv.length);

            byte[] tempBytes = new byte[1];
            int readLen = plain.read(tempBytes);
            while (readLen != -1) {
                cipherOs.write(tempBytes, 0, readLen);
                readLen = plain.read(tempBytes);
            }

            return true;
        } catch (IOException e) {
            log.error("Could read from input stream or write to output stream.", e);
        }
        finally {
            try {
                plain.close();
            } catch (IOException e) {
                log.error("Could not close plain input stream.", e);
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReadyToOperate() {
        return checkInitialised();
    }


    //
    // helper
    //


    private boolean checkInitialised() {
        if (this.secretKeySpec == null) {
            log.error("Secret key spec is null.");
        }

        if (!initialised) {
            log.error("{} not initialised.", this);
            return false;
        }
        return true;
    }
}
