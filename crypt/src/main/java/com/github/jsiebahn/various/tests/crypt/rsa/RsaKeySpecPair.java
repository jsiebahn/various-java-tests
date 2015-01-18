package com.github.jsiebahn.various.tests.crypt.rsa;

import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

/**
 * Container object to hold public and private RSA keys. It can be created with a random pair of
 * public and private keys by
 * {@link RsaKeyFactory#createKeys(java.io.OutputStream, java.io.OutputStream)}.
 *
 * @author jsiebahn
 * @since 02.11.14 08:57
 */
public class RsaKeySpecPair {

    /**
     * The private key spec of this pair of private and public key specs.
     */
    private RSAPrivateKeySpec privateKeySpec;

    /**
     * The public key spec of this pair of private and public key specs.
     */
    private RSAPublicKeySpec publicKeySpec;


    //
    // constructor
    //

    public RsaKeySpecPair(RSAPrivateKeySpec privateKeySpec, RSAPublicKeySpec publicKeySpec) {
        this.setPrivateKeySpec(privateKeySpec);
        this.setPublicKeySpec(publicKeySpec);
    }


    //
    // getter and setter
    //

    public RSAPrivateKeySpec getPrivateKeySpec() {
        return privateKeySpec;
    }

    public void setPrivateKeySpec(RSAPrivateKeySpec privateKeySpec) {
        this.privateKeySpec = privateKeySpec;
    }

    public RSAPublicKeySpec getPublicKeySpec() {
        return publicKeySpec;
    }

    public void setPublicKeySpec(RSAPublicKeySpec publicKeySpec) {
        this.publicKeySpec = publicKeySpec;
    }
}
