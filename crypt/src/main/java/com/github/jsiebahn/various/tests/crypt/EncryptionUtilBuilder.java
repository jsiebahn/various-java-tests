package com.github.jsiebahn.various.tests.crypt;

import com.github.jsiebahn.various.tests.crypt.noop.NoopDecrypter;
import com.github.jsiebahn.various.tests.crypt.noop.NoopEncrypter;
import com.github.jsiebahn.various.tests.crypt.aes.cbc.SymmetricKeyDecrypter;
import com.github.jsiebahn.various.tests.crypt.aes.cbc.SymmetricKeyEncrypter;
import com.github.jsiebahn.various.tests.crypt.rsa.RsaDecrypter;
import com.github.jsiebahn.various.tests.crypt.rsa.RsaEncrypter;
import com.github.jsiebahn.various.tests.crypt.rsa.RsaKeyFactory;
import com.github.jsiebahn.various.tests.crypt.rsa.RsaKeySpecPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

/**
 * Builds instances of {@link EncryptionUtil}.
 *
 * @author jsiebahn
 * @since 29.10.14 07:12
 */
public class EncryptionUtilBuilder {

    private static final Logger log = LoggerFactory.getLogger(EncryptionUtilBuilder.class);

    /**
     * Used to synchronize {@link #withKeyPair(java.io.File, java.io.File)} to avoid multiple
     * creation of keys. If the same paths are used to store the keys, they may be created twice and
     * different keys are loaded which will cause that encrypted objects can not be decrypted by
     * another instance of {@link EncryptionUtil}.
     */
    private static final Object KEY_CREATION_LOCK = new Object();

    /**
     * The {@link EncryptionUtil#encrypter} for the {@link EncryptionUtil} to build.
     */
    private Encrypter encrypter;

    /**
     * The {@link EncryptionUtil#decrypter} for the {@link EncryptionUtil} to build.
     */
    private Decrypter decrypter;


    //
    // builder construction
    //

    /**
     * Use {@link #encryptionUtil()} to create an instance.
     */
    private EncryptionUtilBuilder() {
        // nothing to do here
    }

    /**
     * The entry point to create an {@link EncryptionUtil}.
     * @return a new {@link EncryptionUtilBuilder} instance
     */
    public static EncryptionUtilBuilder encryptionUtil() {
        return new EncryptionUtilBuilder();
    }


    //
    // Fluent setters
    //

    // Fluent private key setters for asymmetric public private key encryption

    /**
     * Reads the {@code private key} from a resource in the classpath and creates the
     * {@link #decrypter}. Use this method if the private key is bundled with the application.
     *
     * @param resource the path to a resource in the classpath
     * @return the current instance
     */
    public EncryptionUtilBuilder withPrivateKeyFromClasspath(String resource) {
        return this.withPrivateKeyFromInputStream(this.getClass().getResourceAsStream(resource));
    }

    /**
     * Reads the {@code private key} from a file with a location specified by {@code filePath} and
     * creates the {@link #decrypter}. Use this method if the private key is located somewhere on
     * the host file system.
     *
     * @param filePath the path to a file in the file system
     * @return the current instance
     */
    public EncryptionUtilBuilder withPrivateKeyFromFile(String filePath) {
        return this.withPrivateKeyFromFile(new File(filePath));
    }

    /**
     * Reads the {@code private key} from a file and creates the {@link #decrypter}. Use this method
     * if the private key is located somewhere on the host file system.
     *
     * @param file a file in the file system
     * @return the current instance
     */
    public EncryptionUtilBuilder withPrivateKeyFromFile(File file) {
        try {
            return this.withPrivateKeyFromInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            log.error("Public key file not found: {}", file.getPath());
        }
        return this;
    }

    /**
     * Reads the {@code private key} from an {@link java.io.InputStream} and creates the
     * {@link #decrypter}. Use this method if the private key is loaded from any not directly
     * supported type of resource which can be converted to an input stream.
     *
     * @param is an input stream with the content of a key
     * @return the current instance
     */
    public EncryptionUtilBuilder withPrivateKeyFromInputStream(InputStream is) {

        RsaKeyFactory factory = new RsaKeyFactory();
        RSAPrivateKeySpec privateKeySpec = factory.readPrivateKey(is);

        this.decrypter = new RsaDecrypter(privateKeySpec);

        return this;
    }


    // Fluent public key setters for asymmetric public private key encryption

    /**
     * Reads the {@code public key} from a resource in the classpath and creates the
     * {@link #encrypter}. Use this method if the private key is bundled with the application.
     *
     * @param resource the path to a resource in the classpath
     * @return the current instance
     */
    public EncryptionUtilBuilder withPublicKeyFromClasspath(String resource) {
        return this.withPublicKeyFromInputStream(this.getClass().getResourceAsStream(resource));
    }

    /**
     * Reads the {@link public key} from a file with a location specified by {@code filePath} and
     * creates the {@link #encrypter}. Use this method if the public key is located somewhere on the
     * host file system.
     *
     * @param filePath the path to a file in the file system
     * @return the current instance
     */
    public EncryptionUtilBuilder withPublicKeyFromFile(String filePath) {
        return this.withPublicKeyFromFile(new File(filePath));
    }

    /**
     * Reads the {@code public key} from a file and creates the {@link #encrypter}. Use this method
     * if the public key is located somewhere on the host file system.
     *
     * @param file a file in the file system
     * @return the current instance
     */
    public EncryptionUtilBuilder withPublicKeyFromFile(File file) {
        try {
            return this.withPublicKeyFromInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            log.error("Private key file not found: {}", file.getPath());
        }
        return this;
    }

    /**
     * Reads the {@code #public key} from an {@link InputStream} and creates the {@link #encrypter}.
     * Use this method if the public key is loaded from any not directly supported type of resource
     * which can be converted to an input stream.
     *
     * @param is an input stream with the content of a key
     * @return the current instance
     */
    public EncryptionUtilBuilder withPublicKeyFromInputStream(InputStream is) {

        RsaKeyFactory factory = new RsaKeyFactory();
        RSAPublicKeySpec publicKeySpec = factory.readPublicKey(is);

        this.encrypter = new RsaEncrypter(publicKeySpec);

        return this;
    }


    // Fluent combined key setters for asymmetric public private key encryption

    /**
     * Loads the {@code private key} from {@code privateKeyFilePath} and the {@code public key}
     * from {@code publicKeyFilePath} if both files exist. If one of the files does not exist, both
     * keys will be generated and stored in the given location. Internally
     * {@link #withKeyPair(java.io.File, java.io.File)} will be used to synchronize this method
     * across all instances.
     *
     * @param privateKeyFilePath the path to a private key file in the file system
     * @param publicKeyFilePath the path to a public key file in the file system
     * @return the current instance
     */
    public EncryptionUtilBuilder withKeyPair(String privateKeyFilePath, String publicKeyFilePath) {
        return this.withKeyPair(new File(privateKeyFilePath), new File(publicKeyFilePath));
    }

    /**
     * Loads the {@code private key} from {@code privateKeyFile} and the {@code public key} from
     * {@code publicKeyFile} if both files exist. If one of the files does not exist, both keys will
     * be generated and stored in the given location. This method is synchronized across all
     * instances to avoid duplicate key creation.
     * When key files are read, the {@link #encrypter} and {@link #decrypter} instances will be
     * created with the given or created keys.
     * The method will fail if exactly one of the files does not exist or can not be read and if
     * both files do not exist and at least one of the files can not be created.
     *
     * @param privateKeyFile the file where the private key is stored or will be stored
     * @param publicKeyFile the file where the public key is stored or will be stored
     * @return the current instance
     */
    public EncryptionUtilBuilder withKeyPair(File privateKeyFile, File publicKeyFile) {
        // synchronize to avoid duplicate key creation for the same paths
        synchronized (KEY_CREATION_LOCK) {
            // load the keys if they exist
            if (privateKeyFile.exists() && publicKeyFile.exists()) {
                this.withPrivateKeyFromFile(privateKeyFile);
                this.withPublicKeyFromFile(publicKeyFile);
                return this;
            }

            // the keys do not exist and have to be created and stored

            RsaKeyFactory factory = new RsaKeyFactory();
            RsaKeySpecPair keySpec;
            try {
                keySpec = factory.createKeys(new FileOutputStream(privateKeyFile),
                        new FileOutputStream(publicKeyFile));
            } catch (FileNotFoundException e) {
                log.error("Unable to save generated keys.", e);
                return this;
            }

            this.decrypter = new RsaDecrypter(keySpec.getPrivateKeySpec());
            this.encrypter = new RsaEncrypter(keySpec.getPublicKeySpec());

            return this;
        }
    }


    // Fluent key setters for symmetric key encryption

    /**
     * Creates an {@link EncryptionUtil} for symmetric key encryption.
     *
     * @param passPhrase the pass phrase to use as key for encryption and decryption.
     * @return the current instance
     */
    public EncryptionUtilBuilder withPassPhrase(String passPhrase) {
        this.encrypter = new SymmetricKeyEncrypter(passPhrase);
        this.decrypter = new SymmetricKeyDecrypter(passPhrase);
        return this;
    }

    /**
     * Creates an {@link EncryptionUtil} for symmetric key encryption.
     *
     * @param passPhrase the pass phrase to use as key for encryption and decryption.
     * @return the current instance
     */
    public EncryptionUtilBuilder withPassPhrase(byte[] passPhrase) {
        this.encrypter = new SymmetricKeyEncrypter(passPhrase);
        this.decrypter = new SymmetricKeyDecrypter(passPhrase);
        return this;
    }


    // Fluent setter for noop

    /**
     * Creates an {@link EncryptionUtil} with {@link Encrypter} and {@link Decrypter} that do not
     * modify the input. The plain Object will be the same as the encrypted Object. The
     * initialisation and every encryption and decryption process will log a warning, that this
     * configuration should only be used in test and development scenarios. The logger for
     * {@link EncryptionUtilBuilder}, {@link NoopEncrypter} and {@link NoopDecrypter} may be
     * configured to error level in test and development environments but shall never suppress
     * warnings in productive environments.
     *
     * @return the current instance
     */
    public EncryptionUtilBuilder withNoop() {
        log.warn("Initialising EncryptionUtil without encryption should only be used in test "
                + "and development scenarios.");
        this.encrypter = new NoopEncrypter();
        this.decrypter = new NoopDecrypter();
        return this;
    }


    // Fluent direct setters

    /**
     * Creates an {@link EncryptionUtil} with the given {@code encrypter} as
     * {@link EncryptionUtil#encrypter}.
     *
     * @param encrypter the {@link Encrypter} to use for encryption
     * @return the current instance
     */
    public EncryptionUtilBuilder withEncrypter(Encrypter encrypter) {
        this.encrypter = encrypter;
        return this;
    }

    /**
     * Creates an {@link EncryptionUtil} with the given {@code decrypter} as
     * {@link EncryptionUtil#decrypter}.
     *
     * @param decrypter the {@link Decrypter} to use for decryption
     * @return the current instance
     */
    public EncryptionUtilBuilder withDecrypter(Decrypter decrypter) {
        this.decrypter = decrypter;
        return this;
    }


    //
    // Fluent final creation with checking the encryption
    //

    /**
     * Creates the desired {@link EncryptionUtil} with {@link EncryptionUtil#encrypter} and
     * {@link EncryptionUtil#decrypter} as configured through the "with" methods above. Will return
     * {@code null} if no {@link Encrypter} or no {@link Decrypter} is configured or if a simple
     * test encryption and decryption fails.
     *
     * @return the created {@link EncryptionUtil} or {@code null} if not configured properly
     */
    public EncryptionUtil build() {

        // build() is for encryption and decryption, no null values allowed
        if (this.encrypter == null || this.decrypter == null) {
            log.error("Building EncryptionUtil for encryption and decryption impossible with "
                    + "configured Encrypter {} and Decrypter {}.", this.encrypter, this.decrypter);
            return null;
        }

        EncryptionUtil encryptionUtil = new EncryptionUtil(this.encrypter, this.decrypter);

        // test if the couple of encrypter and decrypter is configured properly
        String plain = "plain test";
        String encrypted = encryptionUtil.encrypt(plain);
        if (!plain.equals(encryptionUtil.decrypt(encrypted))) {
            log.error("Configured EncryptionUtil is not able to decrypt what itself encrypted with"
                    + "configured Encrypter {} and Decrypter {}.", this.encrypter, this.decrypter);
            return null;
        }

        return encryptionUtil;
    }

    /**
     * Creates the desired {@link EncryptionUtil} with {@link EncryptionUtil#encrypter} as
     * configured through the "with" methods above for encryption only. Will return {@code null} if
     * no {@link Encrypter} is configured. The created {@link EncryptionUtil} needs to be used with
     * {@link EncryptionUtil#encrypt(String, boolean)} setting the second parameter {@code force}
     * to {@code true} to encrypt.
     *
     * @return the created {@link EncryptionUtil} or {@code null} if not configured properly
     */
    public EncryptionUtil buildForEncryption() {

        // buildForEncryption() is for encryption, no null values allowed for the encrypter
        if (this.encrypter == null) {
            log.error("Building EncryptionUtil for encryption impossible with configured "
                    + "Encrypter {} and Decrypter {}.", this.encrypter, this.decrypter);
            return null;
        }

        return new EncryptionUtil(this.encrypter, null);
    }

    /**
     * Creates the desired {@link EncryptionUtil} with {@link EncryptionUtil#decrypter} as
     * configured through the "with" methods above for decryption only. Will return {@code null} if
     * no {@link Decrypter} is configured.
     *
     * @return the created {@link EncryptionUtil} or {@code null} if not configured properly
     */
    public EncryptionUtil buildForDecryption() {

        // buildForDecryption() is for decryption, no null values allowed for the decrypter
        if (this.decrypter == null) {
            log.error("Building EncryptionUtil for decryption impossible with configured "
                    + "Encrypter {} and Decrypter {}.", this.encrypter, this.decrypter);
            return null;
        }

        return new EncryptionUtil(null, this.decrypter);
    }

}
