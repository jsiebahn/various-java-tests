package com.github.jsiebahn.various.tests.crypt;

import ch.qos.logback.classic.Level;
import com.github.jsiebahn.various.tests.crypt.internal.test.util.LoggingTestUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.UUID;

import static com.github.jsiebahn.various.tests.crypt.internal.StringUtil.byteToString;
import static com.github.jsiebahn.various.tests.crypt.internal.test.util.LoggingTestUtil.setLogLevel;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

/**
 * Test cases used for every matching combination of {@link Encrypter} and {@link Decrypter} to
 * validate that encrypted data with any of the encryption methods of {@link EncryptionUtil} can
 * be decrypted with any of the decryption methods.
 *
 * Other tests for the specific {@link Encrypter} and {@link Decrypter} may be implemented for
 * their special cases (e.g. for initialising keys), but this test should pass for every
 * implemented {@link Encrypter} and {@link Decrypter}.
 *
 * @author jsiebahn
 * @since 01.11.14 07:00
 */
public abstract class AbstractEncryptionUtilTest {

    private static final int numberOfRepeats = 10;

    private Level beforeLevel;

    /**
     * @return a new instance of {@link Encrypter} to test in the environment of an
     *      {@link EncryptionUtil}
     */
    protected abstract Encrypter createEncrypter();

    /**
     * @return a new instance of {@link Decrypter} to test in the environment of an
     *      {@link EncryptionUtil}
     */
    protected abstract Decrypter createDecrypter();


    @Before
    public void disableEncryptionUtilLogging() {
        beforeLevel = LoggingTestUtil.setLogLevel(Level.OFF, EncryptionUtil.class);
    }

    @After
    public void enableEncryptionUtilLogging() {
        LoggingTestUtil.setLogLevel(beforeLevel, EncryptionUtil.class);
    }

    @Test
    public void shouldHandleNullValues() {

        EncryptionUtil encryptionUtil = new EncryptionUtil(createEncrypter(), createDecrypter());

        String aNullString = null;
        assertNull(encryptionUtil.encrypt(aNullString));
        assertNull(encryptionUtil.decrypt(aNullString));

        byte[] aNullByte = null;
        assertNull(encryptionUtil.encrypt(aNullByte));
        assertNull(encryptionUtil.decrypt(aNullByte));

        byte[] aByteArray = new byte[1];
        assertFalse(encryptionUtil.encrypt(new ByteArrayInputStream(aByteArray), null));
        assertFalse(encryptionUtil.decrypt(new ByteArrayInputStream(aByteArray), null));

        assertFalse(encryptionUtil.encrypt(null, new ByteArrayOutputStream()));
        assertFalse(encryptionUtil.decrypt(null, new ByteArrayOutputStream()));

        assertFalse(encryptionUtil.encrypt(null, null));
        assertFalse(encryptionUtil.decrypt(null, null));

    }

    @Test
    public void shouldNotBeModifiable() {

        EncryptionUtil encryptionUtil = new EncryptionUtil(createEncrypter(), createDecrypter());

        String plain = "plain test";

        String encrypted = encryptionUtil.encrypt(plain);

        // try to modify the raw data
        int pos = encrypted.length() / 2;
        char[] chars = encrypted.toCharArray();
        chars[pos]++;
        String encryptedMod = new String(chars);

        assertNull(encryptionUtil.decrypt(encryptedMod));


        // try to modify base64 encoded data
        byte[] decoded = Base64.decodeBase64(encrypted);
        pos = decoded.length / 2;
        decoded[pos]++;
        encryptedMod = Base64.encodeBase64String(decoded);

        assertNull(encryptionUtil.decrypt(encryptedMod));

    }

    @Test
    public void shouldNotDecryptImpossibleInput() throws Exception {

        Decrypter decrypter = createDecrypter();
        assertTrue(decrypter.isReadyToOperate());

        Level before = setLogLevel(Level.OFF, decrypter.getClass());

        try {
            assertNull(decrypter.decrypt("test"));
        }
        finally {
            setLogLevel(before, decrypter.getClass());
        }

    }

    @Test
    public void shouldEncryptTwiceWithDifferentResult() {

        EncryptionUtil encryptionUtil = new EncryptionUtil(createEncrypter(), createDecrypter());

        String encrypted1 = encryptionUtil.encrypt("plain text");
        String encrypted2 = encryptionUtil.encrypt("plain text");

        assertThat(encrypted1, not(equalTo(encrypted2)));

    }

    @Test
    public void shouldEncryptAndDecryptStrings() throws Exception {

        EncryptionUtil encryptionUtil = new EncryptionUtil(createEncrypter(), createDecrypter());

        encryptAndDecryptStrings(encryptionUtil, encryptionUtil, false);

        EncryptionUtil encryptEncryptionUtil = new EncryptionUtil(createEncrypter(), null);
        EncryptionUtil decryptEncryptionUtil = new EncryptionUtil(null, createDecrypter());

        encryptAndDecryptStrings(encryptEncryptionUtil, decryptEncryptionUtil, true);

    }

    @Test
    public void shouldEncryptStringsAndDecryptStreams() throws Exception {

        EncryptionUtil encryptionUtil = new EncryptionUtil(createEncrypter(), createDecrypter());

        encryptStringsAndDecryptStreams(encryptionUtil, encryptionUtil, false);

        EncryptionUtil encryptEncryptionUtil = new EncryptionUtil(createEncrypter(), null);
        EncryptionUtil decryptEncryptionUtil = new EncryptionUtil(null, createDecrypter());

        encryptStringsAndDecryptStreams(encryptEncryptionUtil, decryptEncryptionUtil, true);

    }

    @Test
    public void shouldEncryptStringsAndDecryptByteArray() throws Exception {

        EncryptionUtil encryptionUtil = new EncryptionUtil(createEncrypter(), createDecrypter());

        encryptStringsAndDecryptByteArray(encryptionUtil, encryptionUtil, false);

        EncryptionUtil encryptEncryptionUtil = new EncryptionUtil(createEncrypter(), null);
        EncryptionUtil decryptEncryptionUtil = new EncryptionUtil(null, createDecrypter());

        encryptStringsAndDecryptByteArray(encryptEncryptionUtil, decryptEncryptionUtil, true);

    }

    @Test
    public void shouldEncryptAndDecryptStreams() throws Exception {

        EncryptionUtil encryptionUtil = new EncryptionUtil(createEncrypter(), createDecrypter());

        encryptAndDecryptStreams(encryptionUtil, encryptionUtil, false);

        EncryptionUtil encryptEncryptionUtil = new EncryptionUtil(createEncrypter(), null);
        EncryptionUtil decryptEncryptionUtil = new EncryptionUtil(null, createDecrypter());

        encryptAndDecryptStreams(encryptEncryptionUtil, decryptEncryptionUtil, true);

    }

    @Test
    public void shouldEncryptStreamAndDecryptString() throws Exception {

        EncryptionUtil encryptionUtil = new EncryptionUtil(createEncrypter(), createDecrypter());

        encryptStreamAndDecryptString(encryptionUtil, encryptionUtil, false);

        EncryptionUtil encryptEncryptionUtil = new EncryptionUtil(createEncrypter(), null);
        EncryptionUtil decryptEncryptionUtil = new EncryptionUtil(null, createDecrypter());

        encryptStreamAndDecryptString(encryptEncryptionUtil, decryptEncryptionUtil, true);

    }

    @Test
    public void shouldEncryptStreamAndDecryptByteArray() throws Exception {

        EncryptionUtil encryptionUtil = new EncryptionUtil(createEncrypter(), createDecrypter());

        encryptStreamAndDecryptByteArray(encryptionUtil, encryptionUtil, false);

        EncryptionUtil encryptEncryptionUtil = new EncryptionUtil(createEncrypter(), null);
        EncryptionUtil decryptEncryptionUtil = new EncryptionUtil(null, createDecrypter());

        encryptStreamAndDecryptByteArray(encryptEncryptionUtil, decryptEncryptionUtil, true);

    }

    @Test
    public void shouldEncryptAndDecryptByteArrays() throws Exception {

        EncryptionUtil encryptionUtil = new EncryptionUtil(createEncrypter(), createDecrypter());

        encryptAndDecryptByteArrays(encryptionUtil, encryptionUtil, false);

        EncryptionUtil encryptEncryptionUtil = new EncryptionUtil(createEncrypter(), null);
        EncryptionUtil decryptEncryptionUtil = new EncryptionUtil(null, createDecrypter());

        encryptAndDecryptByteArrays(encryptEncryptionUtil, decryptEncryptionUtil, true);

    }

    @Test
    public void shouldEncryptByteArrayAndDecryptString() throws Exception {

        EncryptionUtil encryptionUtil = new EncryptionUtil(createEncrypter(), createDecrypter());

        encryptByteArrayAndDecryptString(encryptionUtil, encryptionUtil, false);

        EncryptionUtil encryptEncryptionUtil = new EncryptionUtil(createEncrypter(), null);
        EncryptionUtil decryptEncryptionUtil = new EncryptionUtil(null, createDecrypter());

        encryptByteArrayAndDecryptString(encryptEncryptionUtil, decryptEncryptionUtil, true);

    }

    @Test
    public void shouldEncryptByteArrayAndDecryptStream() throws Exception {

        EncryptionUtil encryptionUtil = new EncryptionUtil(createEncrypter(), createDecrypter());

        encryptByteArrayAndDecryptStream(encryptionUtil, encryptionUtil, false);

        EncryptionUtil encryptEncryptionUtil = new EncryptionUtil(createEncrypter(), null);
        EncryptionUtil decryptEncryptionUtil = new EncryptionUtil(null, createDecrypter());

        encryptByteArrayAndDecryptStream(encryptEncryptionUtil, decryptEncryptionUtil, true);

    }


    @Test
    public void shouldEncryptAndDecryptBinaryFiles() throws Exception {

        EncryptionUtil encryptionUtil = new EncryptionUtil(createEncrypter(), createDecrypter());

        InputStream is = AbstractEncryptionUtilTest.class.getResourceAsStream("colors.jpg");
        File encryptedFile = File.createTempFile(this.getClass().getCanonicalName(), "colors.jpg");
        encryptedFile.deleteOnExit();
        FileOutputStream out = new FileOutputStream(encryptedFile);

        assertTrue(encryptionUtil.encrypt(is, out));

        is = new FileInputStream(encryptedFile);
        File decryptedFile = File.createTempFile(this.getClass().getCanonicalName(),
                ".decrypted.colors.jpg");
        decryptedFile.deleteOnExit();
        out = new FileOutputStream(decryptedFile);

        assertTrue(encryptionUtil.decrypt(is, out));

        InputStream expected = AbstractEncryptionUtilTest.class.getResourceAsStream("colors.jpg");
        InputStream actual = new FileInputStream(decryptedFile);

        assertStreamsEqual(expected, actual);

    }

    @Test
    public void shouldEncryptAndDecryptMarkupFiles() throws Exception {

        EncryptionUtil encryptionUtil = new EncryptionUtil(createEncrypter(), createDecrypter());

        InputStream is = AbstractEncryptionUtilTest.class.getResourceAsStream("markup.html");
        File encryptedFile = File.createTempFile(this.getClass().getCanonicalName(),
                ".encrypted.markup.html");
        encryptedFile.deleteOnExit();
        FileOutputStream out = new FileOutputStream(encryptedFile);

        assertTrue(encryptionUtil.encrypt(is, out));

        // assert that it is really encrypted, not only base64 encoded
        FileInputStream inputStream = new FileInputStream(encryptedFile);
        StringWriter writer = new StringWriter();
        IOUtils.copy(inputStream, writer, "UTF-8");
        String stringContentEncoded = writer.toString();
        String stringContent = byteToString(Base64.decodeBase64(stringContentEncoded));
        assertFalse(stringContent.contains("<html>"));

        is = new FileInputStream(encryptedFile);
        File decryptedFile = File.createTempFile(this.getClass().getCanonicalName(),
                ".decrypted.markup.html");
        decryptedFile.deleteOnExit();
        out = new FileOutputStream(decryptedFile);

        assertTrue(encryptionUtil.decrypt(is, out));

        InputStream expected = AbstractEncryptionUtilTest.class.getResourceAsStream("markup.html");
        InputStream actual = new FileInputStream(decryptedFile);

        assertStreamsEqual(expected, actual);

    }


    //
    // helper to multiply test cases
    //

    private void encryptAndDecryptStrings(
            EncryptionUtil encrypt,
            EncryptionUtil decrypt,
            boolean force) {
        doEncryptAndDecryptStrings(encrypt, decrypt, "plain text", force);
        // should work multiple times
        for (int i = 0; i < numberOfRepeats; i++) {
            String plain = UUID.randomUUID().toString();
            doEncryptAndDecryptStrings(encrypt, decrypt, plain, force);
        }
    }

    private void encryptStringsAndDecryptStreams(
            EncryptionUtil encrypt,
            EncryptionUtil decrypt,
            boolean force) throws Exception {
        doEncryptStringsAndDecryptStreams(encrypt, decrypt, "plain text", force);
        // should work multiple times
        for (int i = 0; i < numberOfRepeats; i++) {
            String plain = UUID.randomUUID().toString();
            doEncryptStringsAndDecryptStreams(encrypt, decrypt, plain, force);
        }
    }

    private void encryptStringsAndDecryptByteArray(
            EncryptionUtil encrypt,
            EncryptionUtil decrypt,
            boolean force) throws Exception {
        doEncryptStringsAndDecryptByteArray(encrypt, decrypt, "plain text", force);
        // should work multiple times
        for (int i = 0; i < numberOfRepeats; i++) {
            String plain = UUID.randomUUID().toString();
            doEncryptStringsAndDecryptByteArray(encrypt, decrypt, plain, force);
        }
    }

    private void encryptAndDecryptStreams(
            EncryptionUtil encrypt,
            EncryptionUtil decrypt,
            boolean force) throws Exception {
        doEncryptAndDecryptStreams(encrypt, decrypt, "plain text", force);
        // should work multiple times
        for (int i = 0; i < numberOfRepeats; i++) {
            String plain = UUID.randomUUID().toString();
            doEncryptAndDecryptStreams(encrypt, decrypt, plain, force);
        }
    }

    private void encryptStreamAndDecryptString(
            EncryptionUtil encrypt,
            EncryptionUtil decrypt,
            boolean force) throws Exception {
        doEncryptStreamAndDecryptString(encrypt, decrypt, "plain text", force);
        // should work multiple times
        for (int i = 0; i < numberOfRepeats; i++) {
            String plain = UUID.randomUUID().toString();
            doEncryptStreamAndDecryptString(encrypt, decrypt, plain, force);
        }
    }

    private void encryptStreamAndDecryptByteArray(
            EncryptionUtil encrypt,
            EncryptionUtil decrypt,
            boolean force) throws Exception {
        doEncryptStreamAndDecryptByteArray(encrypt, decrypt, "plain text", force);
        // should work multiple times
        for (int i = 0; i < numberOfRepeats; i++) {
            String plain = UUID.randomUUID().toString();
            doEncryptStreamAndDecryptByteArray(encrypt, decrypt, plain, force);
        }
    }

    private void encryptAndDecryptByteArrays(
            EncryptionUtil encrypt,
            EncryptionUtil decrypt,
            boolean force) throws Exception {
        doEncryptAndDecryptByteArrays(encrypt, decrypt, "plain text", force);
        // should work multiple times
        for (int i = 0; i < numberOfRepeats; i++) {
            String plain = UUID.randomUUID().toString();
            doEncryptAndDecryptByteArrays(encrypt, decrypt, plain, force);
        }
    }

    private void encryptByteArrayAndDecryptString(
            EncryptionUtil encrypt,
            EncryptionUtil decrypt,
            boolean force) throws Exception {
        doEncryptByteArrayAndDecryptString(encrypt, decrypt, "plain text", force);
        // should work multiple times
        for (int i = 0; i < numberOfRepeats; i++) {
            String plain = UUID.randomUUID().toString();
            doEncryptByteArrayAndDecryptString(encrypt, decrypt, plain, force);
        }
    }

    private void encryptByteArrayAndDecryptStream(
            EncryptionUtil encrypt,
            EncryptionUtil decrypt,
            boolean force) throws Exception {
        doEncryptByteArrayAndDecryptStream(encrypt, decrypt, "plain text", force);
        // should work multiple times
        for (int i = 0; i < numberOfRepeats; i++) {
            String plain = UUID.randomUUID().toString();
            doEncryptByteArrayAndDecryptStream(encrypt, decrypt, plain, force);
        }
    }


    //
    // helper to perform the test encryption
    //

    private void doEncryptAndDecryptStrings(
            EncryptionUtil encrypt, EncryptionUtil decrypt,
            String plain,
            boolean force) {

        String encrypted;
        if (force) {
            encrypted = encrypt.encrypt(plain, force);
        }
        else {
            encrypted = encrypt.encrypt(plain);
        }

        String decrypted = decrypt.decrypt(encrypted);
        assertEquals(plain, decrypted);

    }

    private void doEncryptStringsAndDecryptStreams(
            EncryptionUtil encrypt, EncryptionUtil decrypt,
            String plain,
            boolean force) throws Exception {

        String encrypted;
        if (force) {
            encrypted = encrypt.encrypt(plain, force);
        }
        else {
            encrypted = encrypt.encrypt(plain);
        }

        ByteArrayInputStream encryptedIn = new ByteArrayInputStream(encrypted.getBytes("UTF-8"));

        ByteArrayOutputStream decrypted = new ByteArrayOutputStream();

        assertTrue(decrypt.decrypt(encryptedIn, decrypted));

        assertEquals(plain, new String(decrypted.toByteArray(), "UTF-8"));

    }

    private void doEncryptStringsAndDecryptByteArray(
            EncryptionUtil encrypt, EncryptionUtil decrypt,
            String plain,
            boolean force) throws Exception {

        String encrypted;
        if (force) {
            encrypted = encrypt.encrypt(plain, force);
        }
        else {
            encrypted = encrypt.encrypt(plain);
        }

        byte[] decrypted =  decrypt.decrypt(encrypted.getBytes("UTF-8"));

        assertEquals(plain, new String(decrypted, "UTF-8"));

    }

    private void doEncryptAndDecryptStreams(
            EncryptionUtil encrypt,
            EncryptionUtil decrypt,
            String input,
            boolean force) throws Exception {

        ByteArrayInputStream plain = new ByteArrayInputStream(input.getBytes("UTF-8"));

        ByteArrayOutputStream encrypted = new ByteArrayOutputStream();

        if (force) {
            assertTrue(encrypt.encrypt(plain, encrypted, force));
        }
        else {
            assertTrue(encrypt.encrypt(plain, encrypted));
        }

        ByteArrayInputStream encryptedIn = new ByteArrayInputStream(encrypted.toByteArray());

        ByteArrayOutputStream decrypted = new ByteArrayOutputStream();

        assertTrue(decrypt.decrypt(encryptedIn, decrypted));

        assertEquals(input, new String(decrypted.toByteArray(), "UTF-8"));

    }

    private void doEncryptStreamAndDecryptString(
            EncryptionUtil encrypt,
            EncryptionUtil decrypt,
            String input,
            boolean force) throws Exception {

        ByteArrayInputStream plain = new ByteArrayInputStream(input.getBytes("UTF-8"));

        ByteArrayOutputStream encrypted = new ByteArrayOutputStream();

        if (force) {
            assertTrue(encrypt.encrypt(plain, encrypted, force));
        }
        else {
            assertTrue(encrypt.encrypt(plain, encrypted));
        }

        String encryptedString = new String(encrypted.toByteArray(), "UTF-8");

        String decrypted = decrypt.decrypt(encryptedString);

        assertEquals(input, decrypted);

    }

    private void doEncryptStreamAndDecryptByteArray(
            EncryptionUtil encrypt,
            EncryptionUtil decrypt,
            String input,
            boolean force) throws Exception {

        ByteArrayInputStream plain = new ByteArrayInputStream(input.getBytes("UTF-8"));

        ByteArrayOutputStream encrypted = new ByteArrayOutputStream();

        if (force) {
            assertTrue(encrypt.encrypt(plain, encrypted, force));
        }
        else {
            assertTrue(encrypt.encrypt(plain, encrypted));
        }

        byte[] encryptedByteArray = encrypted.toByteArray();

        byte[] decrypted = decrypt.decrypt(encryptedByteArray);

        assertArrayEquals(input.getBytes("UTF-8"), decrypted);

    }

    private void doEncryptAndDecryptByteArrays(
            EncryptionUtil encrypt,
            EncryptionUtil decrypt,
            String input,
            boolean force) throws Exception {

        byte[] plain = input.getBytes("UTF-8");
        byte[] encrypted;
        if (force) {
            encrypted = encrypt.encrypt(plain, force);
        }
        else {
            encrypted = encrypt.encrypt(plain);
        }

        byte[] decrypted = decrypt.decrypt(encrypted);
        assertArrayEquals(plain, decrypted);

    }

    private void doEncryptByteArrayAndDecryptString(
            EncryptionUtil encrypt,
            EncryptionUtil decrypt,
            String input,
            boolean force) throws Exception {

        byte[] plain = input.getBytes("UTF-8");
        byte[] encrypted;
        if (force) {
            encrypted = encrypt.encrypt(plain, force);
        }
        else {
            encrypted = encrypt.encrypt(plain);
        }

        String encryptedString = new String(encrypted, "UTF-8");

        String decrypted = decrypt.decrypt(encryptedString);

        assertEquals(input, decrypted);

    }

    private void doEncryptByteArrayAndDecryptStream(
            EncryptionUtil encrypt,
            EncryptionUtil decrypt,
            String input,
            boolean force) throws Exception {

        byte[] plain = input.getBytes("UTF-8");
        byte[] encrypted;
        if (force) {
            encrypted = encrypt.encrypt(plain, force);
        }
        else {
            encrypted = encrypt.encrypt(plain);
        }

        InputStream encryptedStream = new ByteArrayInputStream(encrypted);
        ByteArrayOutputStream decryptedStream = new ByteArrayOutputStream();

        assertTrue(decrypt.decrypt(encryptedStream, decryptedStream));

        assertArrayEquals(plain, decryptedStream.toByteArray());

    }


    //
    // helper for assertions
    //

    /**
     * {@link org.junit.Assert}s that the content of the {@code actual} {@link InputStream} equals
     * the content of the {@code expected} {@link InputStream} on byte level. The test will fail
     * if the {@link InputStream}s are not equal.
     *
     * @param expected the {@link InputStream} with the expected content
     * @param actual the {@link InputStream} with the actual content
     * @throws IOException
     */
    private void assertStreamsEqual(InputStream expected, InputStream actual) throws IOException {

        assertTrue(areStreamsEqual(expected, actual));

    }

    /**
     * Checks if the content of the given {@link InputStream}s {@code isA} and {@code isB} are equal
     * on byte level.
     *
     * @param isA the {@link InputStream} to be compared to {@code isB}
     * @param isB the {@link InputStream} to be compared to {@code isA}
     * @return if {@code isA} and {@code isB} are equal
     * @throws IOException
     */
    private boolean areStreamsEqual(InputStream isA, InputStream isB) throws IOException {

        // compare the file streams byte by byte
        int aValue = isA.read();
        while (aValue != -1) {
            int bValue = isB.read();
            if (aValue != bValue) {
                return false;
            }
            aValue = isA.read();
        }
        return (-1 == isB.read());

    }

}
