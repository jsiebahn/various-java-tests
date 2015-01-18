package com.github.jsiebahn.various.tests.crypt.aes.cbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Arrays;

/**
 * Utility class for symmetric encryption. This utility encapsulates methods used by the
 * {@link com.github.jsiebahn.various.tests.crypt.Encrypter} and the
 * {@link com.github.jsiebahn.various.tests.crypt.Decrypter} for symmetric key encryption to ensure they use
 * the same patterns and configurations.
 *
 * @author jsiebahn
 * @since 25.10.14 10:31
 */
public class SymmetricKeyUtil {


    /**
     * The algorithm used for encryption and decryption.
     */
    static final String ALGORITHM = "AES";

    /**
     * The combined value of {@link #ALGORITHM}, block mode and padding mode used for encryption
     * and decryption.
     */
    static final String TRANSFORMATION = ALGORITHM + "/CBC/PKCS5PADDING";

    /**
     * The provider used to create the {@link Cipher} for {@link #TRANSFORMATION}.
     */
    static final String PROVIDER = "SunJCE";

    /**
     * The algorithm used to get random bytes from a {@link SecureRandom}.
     */
    static final String RANDOM_ALGORITHM = "SHA1PRNG";

    /**
     * The provider used to get random bytes from a {@link SecureRandom}.
     */
    static final String RANDOM_PROVIDER = "SUN";

    /**
     * Algorithm used to create hashes in key generation process.
     */
    static final String HASH_ALGORITHM = "SHA-256";

    /**
     * The logger used by this class.
     */
    private static final Logger log = LoggerFactory.getLogger(SymmetricKeyUtil.class);

    /**
     * Creates a {@link Cipher} instance for symmetric key encryption or decryption using
     * {@link #TRANSFORMATION} from {@link #PROVIDER}.
     * The cipher is not yet initialised and can be used for encryption or decryption after it is
     * initialised with either {@link #initCipherForEncrypt(Cipher, SecretKeySpec)} or
     * {@link #initCipherForDecrypt(Cipher, SecretKeySpec, byte[])}.
     *
     * @return a new {@link Cipher} instance
     */
    public static Cipher createCipher() {

        return createCipher(TRANSFORMATION, PROVIDER);

    }


    /**
     * Initialises the given {@code cipher} for encryption with the statically defined
     * {@link #TRANSFORMATION} using a random initialisation vector created
     * with {@link SecureRandom}. The initialisation vector will be returned and should be bundled
     * with the encrypted data. It is required for decryption. There is no need to keep the
     * initialisation vector secret. This method should never throw any exception. If there is any
     * problem with initialising the {@code cipher}, the return value will be {@code null}.
     *
     * @param cipher the {@link Cipher} to initialise for encryption with
     *      {@value #TRANSFORMATION}. It should be created with the configured
     *      {@link #TRANSFORMATION} and the configured {@link #PROVIDER} using
     *      {@link Cipher#getInstance(String, String)} as done by {@link #createCipher()}
     * @param secretKeySpec the {@link SecretKeySpec} used by the {@code cipher} to encrypt the data
     * @return the initialisation vector used by the initialised {@code cipher} or {@code null} if
     *      the initialisation of the {@code cipher} failed
     */
    public static byte[] initCipherForEncrypt(Cipher cipher, SecretKeySpec secretKeySpec) {
        try {
            SecureRandom random = SecureRandom.getInstance(RANDOM_ALGORITHM, RANDOM_PROVIDER);
            byte[] ivKey = new byte[16];
            random.nextBytes(ivKey);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(ivKey);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            return ivKey;
        } catch (InvalidKeyException e) {
            log.error("Invalid key '{}' for {}", secretKeySpec, TRANSFORMATION, e);
        } catch (InvalidAlgorithmParameterException e) {
            log.error("Invalid algorithm param (iv?) for {}", secretKeySpec, TRANSFORMATION, e);
        } catch (NoSuchAlgorithmException e) {
            log.error("No such secure random algorithm: {}", RANDOM_ALGORITHM, e);
        } catch (NoSuchProviderException e) {
            log.error("No such secure random provider: {}", RANDOM_PROVIDER, e);
        }
        return null;
    }

    /**
     * Initialises the given {@code cipher} for decryption with the statically defined
     * {@link #TRANSFORMATION} using the given initialisation vector created
     * in the encryption process. This method should never throw any exception. If there is any
     * problem with initialising the {@code cipher}, the return value will be {@code false}.
     *
     * @param cipher the {@link Cipher} to initialise for decryption with
     *      {@value #TRANSFORMATION}. It should be created with the configured
     *      {@link #TRANSFORMATION} and the configured {@link #PROVIDER} using
     *      {@link Cipher#getInstance(String, String)} as done by {@link #createCipher()}
     * @param secretKeySpec the {@link SecretKeySpec} used by the {@code cipher} to decrypt the data
     * @param iv the random initialisation vector shall be the same as used for encryption
     * @return if the cipher could be initialised
     */
    public static boolean initCipherForDecrypt(Cipher cipher, SecretKeySpec secretKeySpec,
            byte[] iv) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));
            return true;
        } catch (InvalidKeyException e) {
            log.error("Invalid key '{}' for {}", secretKeySpec, TRANSFORMATION, e);
        } catch (InvalidAlgorithmParameterException e) {
            log.error("Invalid algorithm parameter '{}' for {}", iv, TRANSFORMATION, e);
        }
        return false;
    }

    /**
     * Creates a {@link SecretKeySpec} that can be used for symmetric key encryption or decryption.
     * The {@link SecretKeySpec} will be derived from the given {@code key}.
     *
     * @param key any byte array used as key for encryption or decryption. It will be hashed to
     *      create a valid key according the requirements of the algorithm
     * @return the {@link SecretKeySpec} or {@code null} if no valid key can be derived from the
     *      given key
     */
    public static SecretKeySpec createSecretKeySpec(byte[] key) {

        byte[] validKey = createValidKey(key, ALGORITHM, HASH_ALGORITHM);

        if (validKey == null) {
            log.error("Could not create valid key from '{}'", key);
            return null;
        }

        return new SecretKeySpec(validKey, ALGORITHM);
    }


    //
    // Methods extracted for unit testing impossible corner cases to satisfy coverage.
    //

    /**
     * Creates a {@link Cipher} instance for symmetric key encryption or decryption using
     * {@link #TRANSFORMATION} from {@link #PROVIDER}.
     * The cipher is not yet initialised and can be used for encryption or decryption after it is
     * initialised with either {@link #initCipherForEncrypt(Cipher, SecretKeySpec)} or
     * {@link #initCipherForDecrypt(Cipher, SecretKeySpec, byte[])}. Exceptions thrown by
     * {@link Cipher#getInstance(String, String)} will be logged on error level.
     *
     * @param transformation the transformation to use, e.g. "AES/CBC/PKCS5PADDING"
     * @param provider the provider to use, e.g. "SunJCE"
     * @return a new {@link Cipher} instance or {@code null} if
     *      {@link Cipher#getInstance(String, String)} throws an {@link Exception}
     */
    static Cipher createCipher(String transformation, String provider) {
        try {

            return Cipher.getInstance(transformation, provider);

        } catch (NoSuchAlgorithmException e) {
            // should never happen as the ALGORITHM is defined statically
            log.error("No such algorithm: {}", transformation, e);
        } catch (NoSuchPaddingException e) {
            // should never happen as the ALGORITHM is defined statically
            log.error("No such padding: {}", transformation, e);
        } catch (NoSuchProviderException e) {
            // should never happen as the PROVIDER is defined statically
            log.error("No such provider: {}", provider, e);
        }

        return null;

    }

    /**
     * Creates a valid key for encryption and decryption with the given {@code cipherAlgorithm}.
     * The valid key is derived from the given {@code key} using a {@link MessageDigest} with the
     * given {@code hashAlgorithm}. The size of the valid key depends on the
     * {@link Cipher#getMaxAllowedKeyLength(String)}. Without JCE it should be 128 bit and with JCE
     * it should be 256 bit. Although JCE allows bigger keys, the configured AES algorithm can only
     * work with a maximum key size of 256 bit.
     *
     * @param key the original key which can be any byte array
     * @param cipherAlgorithm the algorithm to use for encryption or decryption to
     * @param hashAlgorithm the algorithm used for hashing the given {@code key}
     * @return a valid key for encryption and decryption that can be used to create a
     *      {@link SecretKeySpec} for the given {@code cipherAlgorithm}
     */
    static byte[] createValidKey(byte[] key, String cipherAlgorithm, String hashAlgorithm) {

        if (key == null) {
            log.error("Unable to create valid key from null.");
            return null;
        }

        // evaluate the maximum allowed key length to provide the strongest possible encryption
        int maxKeyLen;
        try {
            maxKeyLen = Cipher.getMaxAllowedKeyLength(cipherAlgorithm);
            // normalize to multiple of 8
            maxKeyLen -= maxKeyLen % 8;
        }
        catch (NoSuchAlgorithmException e) {
            log.warn("Unable to determine the maximum allowed key length for {}. "
                    + "Using 128bit to allow encryption without JCE.", cipherAlgorithm, e);
            maxKeyLen = 128;
        }
        if (maxKeyLen <= 128) {
            log.info("The max allowed key length is {}bit. You should consider installing the "
                    + "Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy "
                    + "Files to accomplish stronger encryption.", maxKeyLen);
        }
        if (maxKeyLen > 256) {
            log.debug("The maximum allowed key length is {}bit. {} will use 256bit.", maxKeyLen,
                    cipherAlgorithm);
            maxKeyLen = 256;
        }
        log.info("Creating a valid key with {}bit for {}.", maxKeyLen, cipherAlgorithm);

        // generate a valid key from a hash of the input key
        MessageDigest sha;
        try {
            sha = MessageDigest.getInstance(hashAlgorithm);
        } catch (NoSuchAlgorithmException e) {
            // should not happen as SHA-256 should be available in every VM
            log.error("Algorithm not found: {}", hashAlgorithm, e);
            return null;
        }
        byte[] validKey = sha.digest(key);
        validKey = Arrays.copyOf(validKey, maxKeyLen / 8);

        return validKey;
    }

}
