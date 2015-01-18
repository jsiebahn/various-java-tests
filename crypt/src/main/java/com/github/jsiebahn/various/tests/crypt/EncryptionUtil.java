package com.github.jsiebahn.various.tests.crypt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A utility that wraps the java crypt api for easy access without exceptions to perform encryption
 * and decryption tasks. The {@code EncryptionUtil} shall be created and configured through the
 * {@link EncryptionUtilBuilder} for symmetric key encryption which uses the same pass phrase for
 * encryption and decryption, asymmetric encryption with a public key for encryption and a private
 * key for decryption or a noop encryption implementation for test and development scenarios.
 *
 * The basic goal of {@link EncryptionUtil} is to avoid and catch all the possible exceptions
 * thrown by the classes of {@code javax.crypto.*}, {@code java.security.*} and {@code java.io.*}.
 * Instead of throwing exceptions, errors are logged and {@code null} values are returned. When
 * using {@link EncryptionUtilBuilder} to create instances of {@code EncryptionUtil} these error
 * logs and {@code null} values should only occur while creating the {@code EncryptionUtil}. Once
 * the instance is created it should just perform encryption and decryption as expected.
 *
 * If you are tired of catching exceptions and need encryption, {@code EncryptionUtil} is the right
 * tool for you.
 *
 * As the {@code EncryptionUtil} uses standard Java APIs, it gives you the security and bugs the
 * Java standard API of {@code javax.crypto} and {@code java.security} offers. There will be no
 * warranty for insecure encryption or loss of data. How the API is accessed can and should be
 * reviewed and judged in the sub packages where the {@code Encrypter}s and {@link Decrypter}s are
 * implemented.
 *
 * @author jsiebahn
 * @since 23.10.14 06:57
 */
public class EncryptionUtil {

    private static Logger log = LoggerFactory.getLogger(EncryptionUtil.class);

    /**
     * The {@link Encrypter} used to encrypt data.
     */
    private Encrypter encrypter;

    /**
     * The {@link Decrypter} used to decrypt data.
     */
    private Decrypter decrypter;


    /**
     * Creates an {@link EncryptionUtil} instance with the given {@code encrypter} and
     * {@code decrypter} to perform the encryption and decryption tasks. {@code null} values may be
     * used if the new instance shall either perform encryption or decryption. If the
     * {@code decrypter} is {@code null} {@link #encrypt(String, boolean) encryption} needs to be
     * forced to avoid the validation violation that an encrypted object can be decrypted through
     * this instance.
     *
     * To create a properly configured {@link EncryptionUtil},
     * {@link EncryptionUtilBuilder#encryptionUtil()} should be used. The fluent "with..." setters
     * will help to get an {@link EncryptionUtil} with a suitable couple of {@code encrypter} and
     * {@code decrypter}.
     *
     * @param encrypter the {@link #encrypter} to use for encryption
     * @param decrypter the {@link #decrypter} to use for decryption
     */
    public EncryptionUtil(Encrypter encrypter, Decrypter decrypter) {
        super();
        this.encrypter = encrypter;
        this.decrypter = decrypter;
    }


    //
    // encryption api
    //

    /**
     * Encrypts the given {@code plain} text into an encrypted {@link String} with the
     * {@link #encrypter}. The instance has to be initialized with a valid {@link #decrypter} to
     * use this method. It ensures that the encrypted {@link String} can be
     * {@link #decrypt(String) decrypted as String} and
     * {@link #decrypt(java.io.InputStream, java.io.OutputStream) decrypted as InputStream} with
     * the same instance of {@link EncryptionUtil}. If decryption should be performed from a
     * {@link #decrypt(byte[]) byte array}, the result should be
     * {@link String#getBytes(java.nio.charset.Charset) converted} using {@code UTF-8}. If the
     * instance is initialized without {@link #decrypter}, {@link #encrypt(String, boolean)} should
     * be used for encryption instead of this method.
     *
     * @param plain the plain text that should be encrypted
     * @return the encrypted text. Usually the {@link #encrypter} will be encode the result using
     *      {@code Base64} to create a valid {@code String} from the encrypted binary data.
     */
    public String encrypt(String plain) {
        return encrypt(plain, false);
    }

    /**
     * Encrypts the given {@code plain} text into an encrypted {@link String} with the
     * {@link #encrypter}. The instance has to be initialized with a valid {@link #decrypter} to
     * use this method if {@code force} is given as {@code false}. Then it ensures that the
     * encrypted {@link String} can be {@link #decrypt(String) decrypted as String} and
     * {@link #decrypt(java.io.InputStream, java.io.OutputStream) decrypted as InputStream} with
     * the same instance of {@link EncryptionUtil}. If decryption should be performed from a
     * {@link #decrypt(byte[]) byte array}, the result should be
     * {@link String#getBytes(java.nio.charset.Charset) converted} using {@code UTF-8}.
     *
     * @param plain the plain text that should be encrypted
     * @param force if encryption should be performed although there is no {@link #decrypter} that
     *      {@link Decrypter#isReadyToOperate() is ready to operate}
     * @return the encrypted text. Usually the {@link #encrypter} will be encode the result using
     *      {@code Base64} to create a valid {@code String} from the encrypted binary data.
     */
    public String encrypt(String plain, boolean force) {
        if (!isConfiguredForEncryption(force)) {
            return null;
        }
        return encrypter.encrypt(plain);
    }

    /**
     * Encrypts the given {@code plain} text from the {@link InputStream} and writes the encrypted
     * content to the given {@code encrypted} {@link OutputStream} using the {@link #encrypter}.
     * The instance has to be initialized with a valid {@link #decrypter} to use this method. It
     * ensures that the encrypted {@link String} can be {@link #decrypt(String) decrypted as String}
     * and {@link #decrypt(java.io.InputStream, java.io.OutputStream) decrypted as InputStream}
     * with the same instance of {@link EncryptionUtil}. If decryption should be performed from a
     * {@link #decrypt(byte[]) byte array}, the result should be
     * {@link String#getBytes(java.nio.charset.Charset) converted} using {@code UTF-8}. If the
     * instance is initialized without {@link #decrypter},
     * {@link #encrypt(InputStream, OutputStream, boolean)} should be used for encryption instead
     * of this method.
     *
     * @param plain the stream where the plain content is read from
     * @param encrypted the stream where the encrypted content is written to
     * @return if the encryption has been successful
     */
    public boolean encrypt(InputStream plain, OutputStream encrypted) {
        return encrypt(plain, encrypted, false);
    }

    /**
     * Encrypts the given {@code plain} text from the {@link InputStream} and writes the encrypted
     * content to the given {@code encrypted} {@link OutputStream} using the {@link #encrypter}.
     * The instance has to be initialized with a valid {@link #decrypter} to use this method if
     * {@code force} is given as {@code false}. Then it ensures that the encrypted content can be
     * {@link #decrypt(String) decrypted as String} and
     * {@link #decrypt(java.io.InputStream, java.io.OutputStream) decrypted as InputStream} with
     * the same instance of {@link EncryptionUtil}. If decryption should be performed from a
     * {@link #decrypt(byte[]) byte array}, the result should be
     * {@link String#getBytes(java.nio.charset.Charset) converted} using {@code UTF-8}.
     *
     * @param plain the stream where the plain content is read from
     * @param encrypted the stream where the encrypted content is written to
     * @param force if encryption should be performed although there is no {@link #decrypter} that
     *      {@link Decrypter#isReadyToOperate() is ready to operate}
     * @return if the encryption has been successful
     */
    public boolean encrypt(InputStream plain, OutputStream encrypted, boolean force) {
        return isConfiguredForEncryption(force) && encrypter.encrypt(plain, encrypted);
    }

    public byte[] encrypt(byte[] plain) {
        return encrypt(plain, false);
    }

    public byte[] encrypt(byte[] plain, boolean force) {
        if (plain == null) {
            return null;
        }
        ByteArrayInputStream is = new ByteArrayInputStream(plain);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        if (!encrypt(is, os, force)) {
            return null;
        }
        return os.toByteArray();
    }


    //
    // decryption api
    //

    public String decrypt(String encrypted) {
        if (!isConfiguredForDecryption()) {
            return null;
        }
        return decrypter.decrypt(encrypted);
    }

    public boolean decrypt(InputStream encrypted, OutputStream plain) {
        return isConfiguredForDecryption() && this.decrypter.decrypt(encrypted, plain);
    }


    public byte[] decrypt(byte[] encrypted) {
        if (encrypted == null) {
            return null;
        }
        ByteArrayInputStream is = new ByteArrayInputStream(encrypted);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        if (!decrypt(is, os)) {
            return null;
        }
        return os.toByteArray();
    }


    //
    // helper
    //

    /**
     * Checks if this instance is configured to process encryption. The methods logs at info level
     * if the encryption is forced without a {@link #decrypter} that
     * {@link Decrypter#isReadyToOperate() is ready to operate }. It logs at error level if there
     * is no {@link #encrypter} that {@link Encrypter#isReadyToOperate() is ready to operate}.
     *
     * @param force if encryption should be performed although there is no {@link #decrypter} that
     *      {@link Decrypter#isReadyToOperate() is ready to operate}
     * @return if this instance is configured to process encryption
     */
    boolean isConfiguredForEncryption(boolean force) {
        if (force && this.encrypter != null && this.encrypter.isReadyToOperate() &&
                (this.decrypter == null || !this.decrypter.isReadyToOperate())) {
            if (this.decrypter == null) {
                log.info("Forced encryption without decrypter may impede decryption.");
            }
            else {
                log.info("Forced encryption with decrypter not ready to operate may impede "
                        + "decryption.");
            }
            return true;
        }
        else if (this.encrypter == null || !this.encrypter.isReadyToOperate()
                || this.decrypter == null || !this.decrypter.isReadyToOperate()) {
            log.error("Encryption omitted: [encrypter: {}; decrypter: {}; force: {}]",
                    this.encrypter != null, this.decrypter != null, force);
            return false;
        }
        return true;
    }


    /**
     * Checks if this instance is configured to process decryption. This method logs at error level
     * if there is no {@link #decrypter} that
     * {@link Decrypter#isReadyToOperate() is ready to operate}.
     *
     * @return if this instance is configured to process decryption
     */
    boolean isConfiguredForDecryption() {
        if (this.decrypter == null) {
            log.error("Decrypter is null. Unable to decrypt.");
            return false;
        }
        if (!this.decrypter.isReadyToOperate()) {
            log.error("Decrypter is not ready to operate. Unable to decrypt.");
            return false;
        }
        return true;
    }

}
