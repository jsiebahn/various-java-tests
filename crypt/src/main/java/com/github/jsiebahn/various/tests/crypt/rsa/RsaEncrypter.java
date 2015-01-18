package com.github.jsiebahn.various.tests.crypt.rsa;

import com.github.jsiebahn.various.tests.crypt.Encrypter;
import com.github.jsiebahn.various.tests.crypt.aes.cbc.SymmetricKeyEncrypter;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import static com.github.jsiebahn.various.tests.crypt.internal.StringUtil.byteToString;
import static com.github.jsiebahn.various.tests.crypt.internal.StringUtil.stringToByte;

/**
 * Encrypts data with {@link SymmetricKeyEncrypter} using a random key for the symmetric encryption
 * which will be encrypted with a public key. The encrypted symmetric key will be part of the
 * encryption result. It is used to decrypt the payload with {@link RsaDecrypter#decrypt(String)}
 * or {@link RsaDecrypter#decrypt(java.io.InputStream, java.io.OutputStream)} if the corresponding
 * private key is set to decrypt the symmetric key that is shipped along with the encryption result.
 *
 * @author jsiebahn
 * @since 03.11.14 07:12
 */
public class RsaEncrypter implements Encrypter {

    private static final Logger log = LoggerFactory.getLogger(RsaEncrypter.class);

    private PublicKey publicKey;

    private boolean initialised;


    public RsaEncrypter(RSAPublicKeySpec publicKeySpec) {

        try {
            KeyFactory factory = KeyFactory.getInstance(RsaProperties.ALGORITHM);
            this.publicKey = factory.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException e) {
            // should never happen as the ALGORITHM is defined statically
            log.error("Algorithm not found: {}", RsaProperties.ALGORITHM, e);
            initialised = false;
            return;
        } catch (InvalidKeySpecException e) {
            log.error("Unable to create public key for key spec: {}", publicKeySpec, e);
            initialised = false;
            return;
        }


        // do a test encryption to check initialisation state
        initialised = this.publicKey != null;
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

        byte[] plainBytes = stringToByte(plain);
        if (plainBytes == null) {
            return null;
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                ByteArrayInputStream in = new ByteArrayInputStream(stringToByte(plain))) {

            if (encrypt(in, out)) {
                return byteToString(out.toByteArray());
            }
            log.error("Unable to encrypt plain text.");

        } catch (IOException e) {
            log.error("IOException on writing.", e);
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

        Cipher cipher = initCipher();

        if (cipher == null) {
            return false;
        }

        byte[] aesKey = createAesKey();

        byte[] encryptedAesKey = encryptAesKey(aesKey);

        byte[] aesKeyLength = createEncryptedAesKeyLength(encryptedAesKey);

        Encrypter aesEncrypter = new SymmetricKeyEncrypter(aesKey);


        try (OutputStream base64Out = new Base64OutputStream(encrypted)) {

            base64Out.write(aesKeyLength);
            base64Out.write(encryptedAesKey);
            if (aesEncrypter.encrypt(plain, base64Out)) {
                return true;
            }

            log.error("Could not encrypt payload.");

        } catch (IOException e) {
            log.error("Could not read from input stream or write to output stream.", e);
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



    private Cipher initCipher() {

        try {
            Cipher cipher = Cipher.getInstance(RsaProperties.TRANSFORMATION, RsaProperties.PROVIDER);
            cipher.init(Cipher.ENCRYPT_MODE, this.publicKey);
            return cipher;

        } catch (NoSuchAlgorithmException e) {
            // should never happen as the ALGORITHM is defined statically
            log.error("Algorithm not found: {}", RsaProperties.TRANSFORMATION, e);
            return null;
        } catch (NoSuchPaddingException e) {
            // should never happen as the ALGORITHM is defined statically
            log.error("Padding not found for ALGORITHM: {}", RsaProperties.TRANSFORMATION, e);
            return null;
        } catch (InvalidKeyException e) {
            log.error("Unable to init cipher for public key: {}", this.publicKey, e);
            return null;
        } catch (NoSuchProviderException e) {
            log.error("No such provider: {}", RsaProperties.PROVIDER, e);
            return null;
        }

    }

    private byte[] createAesKey() {
        RsaKeyFactory factory = new RsaKeyFactory();

        return factory.createAesKey(RsaProperties.KEY_SIZE);
    }

    private byte[] encryptAesKey(byte[] aesKey) {
        Cipher cipher = initCipher();

        if (cipher == null) {
            return null;
        }

        try {
            return cipher.doFinal(aesKey);
        } catch (IllegalBlockSizeException e) {
            log.error("Illegal block size: {} bytes, {}", aesKey.length, RsaProperties.ALGORITHM, e);
        } catch (BadPaddingException e) {
            log.error("Bad padding: {} bytes, {}", aesKey.length, RsaProperties.ALGORITHM, e);
        }

        return null;
    }

    private byte[] createEncryptedAesKeyLength(byte[] encryptedAesKey) {

        int length = encryptedAesKey.length;
        byte firstByte = (byte) (length / 128);
        byte secondByte = (byte) (length % 128);
        return new byte[] {firstByte, secondByte};

    }

    private boolean checkInitialised() {
        if (!initialised) {
            log.error("{} not initialised.", this);
            return false;
        }
        return true;
    }
}
