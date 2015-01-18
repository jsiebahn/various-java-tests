package com.github.jsiebahn.various.tests.crypt;

import ch.qos.logback.classic.Level;
import com.github.jsiebahn.various.tests.crypt.aes.cbc.SymmetricKeyDecrypter;
import com.github.jsiebahn.various.tests.crypt.aes.cbc.SymmetricKeyEncrypter;
import com.github.jsiebahn.various.tests.crypt.aes.cbc.SymmetricKeyUtil;
import com.github.jsiebahn.various.tests.crypt.internal.test.util.LoggingTestUtil;
import com.github.jsiebahn.various.tests.crypt.noop.NoopDecrypter;
import com.github.jsiebahn.various.tests.crypt.noop.NoopEncrypter;
import com.github.jsiebahn.various.tests.crypt.rsa.RsaKeyFactory;
import com.github.jsiebahn.various.tests.crypt.rsa.RsaKeySpecPair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Tests {@link EncryptionUtilBuilder}
 *
 * @author jsiebahn
 * @since 01.11.14 09:32
 */
public class EncryptionUtilBuilderTest {

    private static final Level FORCE_LOG_LEVEL = Level.OFF;

    private Map<Class, Level> beforeLevels = new HashMap<>();

    @Before
    public void disableLogging() {
        beforeLevels.put(EncryptionUtilBuilder.class,
                LoggingTestUtil.setLogLevel(FORCE_LOG_LEVEL, EncryptionUtilBuilder.class));
        beforeLevels.put(EncryptionUtil.class,
                LoggingTestUtil.setLogLevel(FORCE_LOG_LEVEL, EncryptionUtil.class));
        beforeLevels.put(NoopEncrypter.class,
                LoggingTestUtil.setLogLevel(FORCE_LOG_LEVEL, NoopEncrypter.class));
        beforeLevels.put(NoopDecrypter.class,
                LoggingTestUtil.setLogLevel(FORCE_LOG_LEVEL, NoopDecrypter.class));
        beforeLevels.put(SymmetricKeyDecrypter.class,
                LoggingTestUtil.setLogLevel(FORCE_LOG_LEVEL, SymmetricKeyDecrypter.class));
        beforeLevels.put(SymmetricKeyUtil.class,
                LoggingTestUtil.setLogLevel(FORCE_LOG_LEVEL, SymmetricKeyUtil.class));
    }

    @After
    public void enableLogging() {
        for(Map.Entry<Class, Level> entry : beforeLevels.entrySet()) {
            LoggingTestUtil.setLogLevel(entry.getValue(), entry.getKey());
        }
    }


    @Test
    public void testBuildFail() {
        assertNull(EncryptionUtilBuilder.encryptionUtil().build());
    }

    @Test
    public void testBuildForEncryptionFail() {
        assertNull(EncryptionUtilBuilder.encryptionUtil().buildForEncryption());
    }

    @Test
    public void testBuildForDecryptionFail() {
        assertNull(EncryptionUtilBuilder.encryptionUtil().buildForDecryption());
    }

    @Test
    public void testBuildNoop() {

        EncryptionUtil encryptionUtil = EncryptionUtilBuilder
                .encryptionUtil()
                .withNoop()
                .build();
        assertConfiguredForEncryptionAndDecryption(encryptionUtil);
    }

    @Test
    public void testBuildForEncryptionNoop() {

        EncryptionUtil encryptionUtil = EncryptionUtilBuilder
                .encryptionUtil()
                .withNoop()
                .buildForEncryption();
        assertConfiguredForEncryption(encryptionUtil);
    }

    @Test
    public void testBuildForDecryptionNoop() {

        EncryptionUtil encryptionUtil = EncryptionUtilBuilder
                .encryptionUtil()
                .withNoop()
                .buildForDecryption();
        assertConfiguredForDecryption(encryptionUtil);
    }


    @Test
    public void testBuildSymmetricKey() {

        EncryptionUtil encryptionUtil = EncryptionUtilBuilder
                .encryptionUtil()
                .withPassPhrase("test phrase")
                .build();
        assertConfiguredForEncryptionAndDecryption(encryptionUtil);
    }

    @Test
    public void testBuildSymmetricKeyFromBytes() {

        EncryptionUtil encryptionUtil = EncryptionUtilBuilder
                .encryptionUtil()
                .withPassPhrase("test phrase".getBytes())
                .build();
        assertConfiguredForEncryptionAndDecryption(encryptionUtil);
    }

    @Test
    public void testBuildForEncryptionSymmetricKey() {

        EncryptionUtil encryptionUtil = EncryptionUtilBuilder
                .encryptionUtil()
                .withPassPhrase("test phrase")
                .buildForEncryption();
        assertConfiguredForEncryption(encryptionUtil);
    }

    @Test
    public void testBuildForDecryptionSymmetricKey() {

        EncryptionUtil encryptionUtil = EncryptionUtilBuilder
                .encryptionUtil()
                .withPassPhrase("test phrase")
                .buildForDecryption();
        assertConfiguredForDecryption(encryptionUtil);
    }

    @Test
    public void testNotMatchingEncrypterAndDecrypter() {

        assertNull(EncryptionUtilBuilder
                .encryptionUtil()
                .withEncrypter(new SymmetricKeyEncrypter("test plain"))
                .withDecrypter(new NoopDecrypter())
                .build());

        assertNull(
                EncryptionUtilBuilder.encryptionUtil()
                        .withEncrypter(new NoopEncrypter())
                        .withDecrypter(new SymmetricKeyDecrypter("test plain"))
                        .build());

    }

    @Test
    public void testWithKeysFromClasspath() throws Exception {

        assertConfiguredForEncryptionAndDecryption(
                EncryptionUtilBuilder.encryptionUtil()
                        .withPrivateKeyFromClasspath("prv.key")
                        .withPublicKeyFromClasspath("pub.key")
                        .build());

    }

    @Test
    public void testWithKeysFromFile() throws Exception {

        RsaKeyFactory factory = new RsaKeyFactory();
        File privateKeyFile = File.createTempFile(this.getClass().getCanonicalName(), ".prv.key");
        privateKeyFile.deleteOnExit();
        File publicKeyFile = File.createTempFile(this.getClass().getCanonicalName(), ".pub.key");
        publicKeyFile.deleteOnExit();
        FileOutputStream privateKey = new FileOutputStream(privateKeyFile);
        FileOutputStream publicKey = new FileOutputStream(publicKeyFile);
        RsaKeySpecPair keys = factory.createKeys(privateKey, publicKey);
        assertNotNull(keys);

        assertConfiguredForEncryptionAndDecryption(
                EncryptionUtilBuilder.encryptionUtil()
                        .withPrivateKeyFromFile(privateKeyFile.getAbsolutePath())
                        .withPublicKeyFromFile(publicKeyFile.getAbsolutePath())
                        .build());

    }

    @Test
    public void testWithKeysFromFileFail() throws Exception {

        File privateKeyFile = new File("doesNotExist." + UUID.randomUUID().toString());
        File publicKeyFile = new File("doesNotExist." + UUID.randomUUID().toString());

        assertNull(
                EncryptionUtilBuilder.encryptionUtil()
                        .withPrivateKeyFromFile(privateKeyFile.getAbsolutePath())
                        .withPublicKeyFromFile(publicKeyFile.getAbsolutePath())
                        .build());

    }

    @Test
    public void testWithKeyPair() throws Exception {

        File privateKeyFile = File.createTempFile(this.getClass().getCanonicalName(), ".prv.key");
        privateKeyFile.delete();
        privateKeyFile.deleteOnExit();
        File publicKeyFile = File.createTempFile(this.getClass().getCanonicalName(), ".pub.key");
        publicKeyFile.delete();
        publicKeyFile.deleteOnExit();

        System.out.println(privateKeyFile.getAbsolutePath());
        System.out.println(publicKeyFile.getAbsolutePath());
        // creates the keys
        assertConfiguredForEncryptionAndDecryption(
                EncryptionUtilBuilder.encryptionUtil().withKeyPair(
                        privateKeyFile.getAbsolutePath(), publicKeyFile.getAbsolutePath()).build());

        // uses the keys
        assertConfiguredForEncryptionAndDecryption(
                EncryptionUtilBuilder.encryptionUtil().withKeyPair(
                        privateKeyFile.getAbsolutePath(), publicKeyFile.getAbsolutePath()).build());

        File keyDir = File.createTempFile(this.getClass().getCanonicalName(), "keys");
        try {
            keyDir.delete();
            keyDir.mkdir();
            keyDir.setWritable(false);

            File privateKey = new File(keyDir, "prv.key");
            File publicKey = new File(keyDir, "prv.key");
            assertNull(
                    EncryptionUtilBuilder.encryptionUtil().withKeyPair(
                            privateKey.getAbsolutePath(), publicKey.getAbsolutePath())
                            .build());
        }
        finally {
            keyDir.setWritable(true);
            keyDir.delete();
        }
    }

    private void assertConfiguredForEncryptionAndDecryption(EncryptionUtil encryptionUtil) {

        assertNotNull(encryptionUtil);
        assertTrue(encryptionUtil.isConfiguredForEncryption(false));
        assertTrue(encryptionUtil.isConfiguredForEncryption(true));
        assertTrue(encryptionUtil.isConfiguredForDecryption());

    }

    private void assertConfiguredForEncryption(EncryptionUtil encryptionUtil) {

        assertNotNull(encryptionUtil);
        assertFalse(encryptionUtil.isConfiguredForEncryption(false));
        assertTrue(encryptionUtil.isConfiguredForEncryption(true));
        assertFalse(encryptionUtil.isConfiguredForDecryption());

    }

    private void assertConfiguredForDecryption(EncryptionUtil encryptionUtil) {

        assertNotNull(encryptionUtil);
        assertFalse(encryptionUtil.isConfiguredForEncryption(false));
        assertFalse(encryptionUtil.isConfiguredForEncryption(true));
        assertTrue(encryptionUtil.isConfiguredForDecryption());

    }

}
