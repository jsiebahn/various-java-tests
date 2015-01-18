package com.github.jsiebahn.various.tests.crypt.rsa;

import com.github.jsiebahn.various.tests.crypt.Decrypter;
import com.github.jsiebahn.various.tests.crypt.aes.cbc.SymmetricKeyDecrypter;
import org.apache.commons.codec.binary.Base64InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;

import static com.github.jsiebahn.various.tests.crypt.internal.StringUtil.byteToString;
import static com.github.jsiebahn.various.tests.crypt.internal.StringUtil.stringToByte;

/**
 * Decrypts data that has been encrypted with {@link RsaEncrypter#encrypt(String)} or
 * {@link RsaEncrypter#encrypt(java.io.InputStream, java.io.OutputStream)}.
 *
 * @author jsiebahn
 * @since 03.11.14 07:25
 */
public class RsaDecrypter implements Decrypter {

    private static final Logger log = LoggerFactory.getLogger(RsaDecrypter.class);


    private PrivateKey privateKey;

    private boolean initialised;

    public RsaDecrypter(RSAPrivateKeySpec privateKeySpec) {
        try {
            KeyFactory factory = KeyFactory.getInstance(RsaProperties.ALGORITHM);
            this.privateKey = factory.generatePrivate(privateKeySpec);
        } catch (NoSuchAlgorithmException e) {
            // should never happen as the ALGORITHM is defined statically
            log.error("Algorithm not found: {}", RsaProperties.ALGORITHM, e);
            initialised = false;
            return;
        } catch (InvalidKeySpecException e) {
            log.error("Unable to create private key for key spec: {}", privateKeySpec, e);
            initialised = false;
            return;
        }

        initialised = this.privateKey != null;
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

        byte[] encryptedData = stringToByte(encrypted);

        if (encryptedData == null) {
            log.error("Encrypted data is null.");
            return null;
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ByteArrayInputStream in = new ByteArrayInputStream(encryptedData)) {

            if (decrypt(in, out)) {
                byte[] plainBytes = out.toByteArray();
                return byteToString(plainBytes);
            }
            log.error("Unable to decrypt plain text.");

        } catch (IOException e) {
            log.error("IOException on writing.", e);
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

        Cipher cipher = initCipher();
        if (cipher == null) {
            return false;
        }

        int aesKeyLength = -1;

        try (InputStream base64Decoder = new Base64InputStream(encrypted)) {

            byte[] aesKeyLengthRaw = new byte[2];
            if (base64Decoder.read(aesKeyLengthRaw) != 2) {
                log.error("Could not read aes key length.");
                return false;
            }
            aesKeyLength = convertAesKeyLength(aesKeyLengthRaw);
            if (aesKeyLength <= 0) {
                log.error("Could not read an encrypted aes key of length: {}.", aesKeyLength);
                return false;
            }
            byte[] encryptedAesKey = new byte[aesKeyLength];
            if (base64Decoder.read(encryptedAesKey) != aesKeyLength) {
                log.error("Could not read aes key.");
                return false;
            }

            final byte[] plainAesKey = cipher.doFinal(encryptedAesKey);

            Decrypter aesDecrypter = new SymmetricKeyDecrypter(plainAesKey);

            if (aesDecrypter.decrypt(base64Decoder, plain)) {
                return true;
            }

            log.error("Could not decrypt payload.");

        } catch (IOException e) {
            log.error("Can not read from input stream or write to output stream.", e);
        } catch (IllegalBlockSizeException e) {
            log.error("Illegal block size in decryption. Data length: {}", aesKeyLength, e);
        } catch (BadPaddingException e) {
            log.error("Bad padding size in decryption. Data length: {}", aesKeyLength, e);
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



    private Cipher initCipher() {

        try {
            Cipher cipher = Cipher.getInstance(RsaProperties.TRANSFORMATION, RsaProperties.PROVIDER);

            cipher.init(Cipher.DECRYPT_MODE, this.privateKey);

            return cipher;
        } catch (NoSuchAlgorithmException e) {
            // should never happen as the TRANSFORMATION is defined statically
            log.error("Algorithm not found: {}", RsaProperties.TRANSFORMATION, e);
            return null;
        } catch (NoSuchPaddingException e) {
            // should never happen as the ALGORITHM is defined statically
            log.error("Padding not found: {}", RsaProperties.TRANSFORMATION, e);
            return null;
        } catch (InvalidKeyException e) {
            log.error("Invalid key: {}", this.privateKey, e);
            return null;
        } catch (NoSuchProviderException e) {
            log.error("No such provider: {}", RsaProperties.PROVIDER, e);
            return null;
        }

    }

    private int convertAesKeyLength(byte[] keyLengthInBytes) {
        return ((int) keyLengthInBytes[0]) * 128 + ((int) keyLengthInBytes[1]);
    }

    private boolean checkInitialised() {
        if (!initialised) {
            log.error("{} not initialised.", this);
            return false;
        }
        return true;
    }
}
