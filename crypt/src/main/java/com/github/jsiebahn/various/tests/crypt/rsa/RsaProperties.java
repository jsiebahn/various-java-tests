package com.github.jsiebahn.various.tests.crypt.rsa;

/**
 * Holds static properties to configure keys and ciphers for RSA.
 *
 * @author jsiebahn
 * @since 08.11.14 13:27
 */
class RsaProperties {

    /**
     * The algorithm used for asymmetric encryption and decryption with RSA.
     */
    static final String ALGORITHM = "RSA";

    /**
     * The transformation includes algorithm, block mode and padding for encryption and decryption
     * with RSA.
     */
    static final String TRANSFORMATION = ALGORITHM + "/ECB/PKCS1Padding";

    /**
     * The provider of the encryption and decryption {@link javax.crypto.Cipher} using RSA.
     */
    static final String PROVIDER = "SunJCE";

    /**
     * The provider used to generate RSA key pairs.
     */
    static final String KEY_GEN_PROVIDER = "SunRsaSign";

    /**
     * The size of the RSA keys.
     */
    static final int KEY_SIZE = 2048;

}
