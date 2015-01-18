package com.github.jsiebahn.various.tests.crypt.rsa;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

/**
 * This class handles creation and loading of RSA keys.
 *
 * @author jsiebahn
 * @since 02.11.14 08:55
 */
public class RsaKeyFactory {

    /**
     * The algorithm used to get random bytes from a {@link SecureRandom}.
     */
    static final String RANDOM_ALGORITHM = "SHA1PRNG";

    /**
     * The provider used to get random bytes from a {@link SecureRandom}.
     */
    static final String RANDOM_PROVIDER = "SUN";

    private static final Logger log = LoggerFactory.getLogger(RsaKeyFactory.class);


    /**
     * Creates a new pair of public and private key to be used with rsa encryption. The keys are
     * saved into the given streams.
     *
     * @param privateKeyLocation  the stream to save the private key into
     * @param publicKeyLocation the stream to save the public key into
     * @return the created keys bundled in a {@link RsaKeySpecPair} or {@code null} if an error occurred
     */
    public RsaKeySpecPair createKeys(OutputStream privateKeyLocation, OutputStream publicKeyLocation) {

        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance(RsaProperties.ALGORITHM,
                    RsaProperties.KEY_GEN_PROVIDER);
        } catch (NoSuchAlgorithmException e) {
            log.error("Algorithm {} not found for KeyPairGenerator.", RsaProperties.ALGORITHM, e);
            return null;
        } catch (NoSuchProviderException e) {
            log.error("No such provider: {}", RsaProperties.KEY_GEN_PROVIDER, e);
            return null;
        }
        kpg.initialize(RsaProperties.KEY_SIZE);
        KeyPair kp = kpg.genKeyPair();

        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance(RsaProperties.ALGORITHM,
                    RsaProperties.KEY_GEN_PROVIDER);
        } catch (NoSuchAlgorithmException e) {
            log.error("Algorithm {} not found for KeyFactory.", RsaProperties.ALGORITHM, e);
            return null;
        } catch (NoSuchProviderException e) {
            log.error("No such provider: {}", RsaProperties.KEY_GEN_PROVIDER, e);
            return null;
        }

        RSAPublicKeySpec publicKeySpec;
        RSAPrivateKeySpec privateKeySpec;
        try {
            publicKeySpec = keyFactory.getKeySpec(kp.getPublic(), RSAPublicKeySpec.class);
            privateKeySpec = keyFactory.getKeySpec(kp.getPrivate(), RSAPrivateKeySpec.class);
        } catch (InvalidKeySpecException e) {
            log.error("Invalid key spec.", e);
            return null;
        }

        RsaKeySpecPair keySpec = new RsaKeySpecPair(privateKeySpec, publicKeySpec);

        // save the keys
        try (OutputStream base64Public = new Base64OutputStream(publicKeyLocation);
             ObjectOutputStream publicKeyStream = new ObjectOutputStream(base64Public);
             OutputStream base64Private = new Base64OutputStream(privateKeyLocation);
             ObjectOutputStream privateKeyStream = new ObjectOutputStream(base64Private)) {

            publicKeyStream.writeObject(keySpec.getPublicKeySpec().getModulus());
            publicKeyStream.writeObject(keySpec.getPublicKeySpec().getPublicExponent());

            privateKeyStream.writeObject(keySpec.getPrivateKeySpec().getModulus());
            privateKeyStream.writeObject(keySpec.getPrivateKeySpec().getPrivateExponent());

        }
        catch (IOException e) {
            log.error("IOException while saving the keys into the given streams.", e);
            return null;
        }

        return keySpec;
    }

    /**
     * Reads the {@link RSAPrivateKeySpec} from the {@code inputStream}. The stream is expected to
     * be derived from the {@link OutputStream} written by
     * {@link #createKeys(java.io.OutputStream, java.io.OutputStream)}. So it should contain the two
     * {@link BigInteger}s of the private key as serialized objects.
     *
     * @param inputStream the {@link InputStream} containing {@code modulus} and
     *      {@code privateExponent} of an {@link RSAPrivateKeySpec}
     * @return the {@link RSAPrivateKeySpec} read from the given {@code inputStream} or {@code null}
     *      if the private key could not be read from the {@code inputStream}
     */
    public RSAPrivateKeySpec readPrivateKey(InputStream inputStream) {

        try (Base64InputStream base64Is = new Base64InputStream(inputStream);
             ObjectInputStream objectIs = new ObjectInputStream(base64Is)) {
            BigInteger modulus = (BigInteger) objectIs.readObject();
            BigInteger exponent = (BigInteger) objectIs.readObject();
            return new RSAPrivateKeySpec(modulus, exponent);
        } catch (IOException e) {
            log.error("Could not read private key from input stream.", e);
        } catch (ClassNotFoundException e) {
            log.error("Unknown type in private key input stream.", e);
        } catch (ClassCastException e) {
            log.error("Unexpected type in private key input stream.", e);
        }

        return null;

    }

    /**
     * Reads the {@link RSAPublicKeySpec} from the {@code inputStream}. The stream is expected to
     * be derived from the {@link OutputStream} written by
     * {@link #createKeys(java.io.OutputStream, java.io.OutputStream)}. So it should contain the two
     * {@link BigInteger}s of the public key as serialized objects expecting the modulus being the
     * first one.
     *
     * @param inputStream the {@link InputStream} containing {@code modulus} and
     *      {@code publicExponent} of an {@link RSAPublicKeySpec}
     * @return the {@link RSAPublicKeySpec} read from the given {@code inputStream} or {@code null}
     *      if the public key could not be read from the {@code inputStream}
     */
    public RSAPublicKeySpec readPublicKey(InputStream inputStream) {

        try (Base64InputStream base64Is = new Base64InputStream(inputStream);
             ObjectInputStream objectIs = new ObjectInputStream(base64Is)) {
            BigInteger modulus = (BigInteger) objectIs.readObject();
            BigInteger exponent = (BigInteger) objectIs.readObject();
            return new RSAPublicKeySpec(modulus, exponent);
        } catch (IOException e) {
            log.error("Could not read public key from input stream.", e);
        } catch (ClassNotFoundException e) {
            log.error("Unknown type in public key input stream.", e);
        } catch (ClassCastException e) {
            log.error("Unexpected type in public key input stream.", e);
        }

        return null;

    }

    byte[] createAesKey(int rsaKeyLength) {

        try {
            SecureRandom random = SecureRandom.getInstance(RANDOM_ALGORITHM, RANDOM_PROVIDER);
            byte[] aesKey = new byte[rsaKeyLength / 8 - 11];
            random.nextBytes(aesKey);
            return aesKey;
        } catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithm {}", RANDOM_ALGORITHM, e);
            return null;
        } catch (NoSuchProviderException e) {
            log.error("NoSuchProvider {}", RANDOM_PROVIDER, e);
        }

        return null;

    }




}
