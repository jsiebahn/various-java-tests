package com.github.jsiebahn.various.tests.crypt.aes.cbc;

import com.github.jsiebahn.various.tests.crypt.Decrypter;
import com.github.jsiebahn.various.tests.crypt.internal.StringUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Base64InputStream;
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
import java.util.Arrays;

/**
 * Decrypts {@code Strings} and {@code InputStreams} of content encrypted by the
 * {@link SymmetricKeyEncrypter}. The input is expected to be base64 encoded. The first 16 bytes of
 * the base64 decoded input are assumed to be used as initialisation vector. Obviously the given
 * {@link SymmetricKeyEncrypter#SymmetricKeyEncrypter(String) key} needs to be the same as used for
 * encryption.
 *
 * @author jsiebahn
 * @since 27.10.14 07:40
 */
public class SymmetricKeyDecrypter implements Decrypter {

    private static final Logger log = LoggerFactory.getLogger(SymmetricKeyDecrypter.class);

    /**
     * The key spec to use.
     */
    private SecretKeySpec secretKeySpec;

    /**
     * If this instance is correctly initialised.
     */
    private boolean initialised;


    public SymmetricKeyDecrypter(String key) {
        this(StringUtil.stringToByte(key));
    }

    public SymmetricKeyDecrypter(byte[] key) {
        if (key == null) {
            return;
        }
        this.secretKeySpec = SymmetricKeyUtil.createSecretKeySpec(key);
        initialised = this.secretKeySpec != null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String decrypt(String encrypted) {

        if (!checkInitialised()) {
            return null;
        }

        if (encrypted == null) {
            return null;
        }

        byte[] ivAndEncryptedData = Base64.decodeBase64(encrypted);

        if (ivAndEncryptedData == null) {
            log.error("Encrypted data without initialisation vector.");
            return null;
        }
        if (ivAndEncryptedData.length < 16) {
            log.error("Encrypted data without initialisation vector. Length: {}",
                    ivAndEncryptedData.length);
            return null;
        }

        byte[] iv = Arrays.copyOf(ivAndEncryptedData, 16);
        byte[] data = Arrays.copyOfRange(ivAndEncryptedData, 16, ivAndEncryptedData.length);

        Cipher cipher = initCipher(iv);

        if (cipher == null) {
            return null;
        }

        try {
            final byte[] plain = cipher.doFinal(data);
            return StringUtil.byteToString(plain);
        } catch (IllegalBlockSizeException e) {
            log.error("Illegal block size in decryption. Data length: {}", data.length, e);
        } catch (BadPaddingException e) {
            log.error("Bad padding size in decryption. Data length: {}", data.length, e);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean decrypt(InputStream encrypted, OutputStream plain) {
        if (!initialised) {
            return false;
        }

        if (encrypted == null) {
            log.error("encrypted InputStream is null.");
            return false;
        }

        if (plain == null) {
            log.error("plain OutputStream is null.");
            return false;
        }

        CipherOutputStream cipherOs = null;
        try (InputStream base64Decoder = new Base64InputStream(encrypted)) {

            byte[] iv = new byte[16];
            int i = 0;
            while (i < 16) {
                int intValue = base64Decoder.read();
                if (intValue == -1) {
                    log.error("Can not read initialisation vector, stream ended at byte {}", i);
                    return false;
                }
                iv[i] = (byte) intValue;
                i++;
            }

            Cipher cipher = initCipher(iv);

            cipherOs = new CipherOutputStream(plain, cipher);
            byte[] b = new byte[1];
            int bytesRead = base64Decoder.read(b);
            while (bytesRead != -1) {
                cipherOs.write(b, 0, bytesRead);
                bytesRead = base64Decoder.read(b);
            }

        } catch (IOException e) {
            log.error("Can not read from input stream or write to output stream.", e);
            return false;
        }
        finally {
            if (cipherOs != null) {
                try {
                    cipherOs.flush();
                    cipherOs.close();
                } catch (IOException e) {
                    log.error("Unable to flush and close cipher stream.", e);
                }
            }
        }

        return true;
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

    private Cipher initCipher(byte[] initialisationVector) {

        Cipher cipher = SymmetricKeyUtil.createCipher();

        if (!SymmetricKeyUtil.initCipherForDecrypt(
                cipher,
                this.secretKeySpec,
                initialisationVector)) {
            return null;
        }

        return cipher;

    }

    private boolean checkInitialised() {
        if (!initialised) {
            log.error("{} not initialised.", this);
            return false;
        }
        return true;
    }

}
