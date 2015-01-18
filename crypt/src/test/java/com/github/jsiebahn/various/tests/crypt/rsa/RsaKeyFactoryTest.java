package com.github.jsiebahn.various.tests.crypt.rsa;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
/**
 * Tests {@link RsaKeyFactory}
 *
 * @author jsiebahn
 * @since 02.11.14 09:17
 */
public class RsaKeyFactoryTest {

    @Test
    public void testCreateKeys() throws Exception {

        ByteArrayOutputStream publicKey = new ByteArrayOutputStream();
        ByteArrayOutputStream privateKey = new ByteArrayOutputStream();

        RsaKeyFactory keyFactory = new RsaKeyFactory();

        RsaKeySpecPair keys = keyFactory.createKeys(publicKey, privateKey);
        assertNotNull(keys);

        assertNotNull(keys.getPublicKeySpec());
        assertNotNull(keys.getPublicKeySpec().getModulus());
        assertNotNull(keys.getPublicKeySpec().getPublicExponent());
        assertNotNull(keys.getPrivateKeySpec());
        assertNotNull(keys.getPrivateKeySpec().getModulus());
        assertNotNull(keys.getPrivateKeySpec().getPrivateExponent());

        BigInteger zero = new BigInteger("0", 10);
        assertThat(zero, lessThan(keys.getPublicKeySpec().getModulus()));
        assertThat(zero, lessThan(keys.getPublicKeySpec().getPublicExponent()));
        assertThat(zero, lessThan(keys.getPrivateKeySpec().getModulus()));
        assertThat(zero, lessThan(keys.getPrivateKeySpec().getPrivateExponent()));

        assertEquals(keys.getPublicKeySpec().getModulus(), keys.getPrivateKeySpec().getModulus());

    }

    @Test
    public void testReadPrivateKey() throws Exception{

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Base64OutputStream outBase64 = new Base64OutputStream(out);
        ObjectOutputStream objects = new ObjectOutputStream(outBase64);


        objects.writeObject(new BigInteger("31", 10));
        objects.writeObject(new BigInteger("101", 10));

        objects.flush();
        outBase64.flush();
        out.flush();
        objects.close();
        outBase64.close();
        out.close();

        InputStream bytes = new ByteArrayInputStream(out.toByteArray());

        RsaKeyFactory factory = new RsaKeyFactory();

        RSAPrivateKeySpec privateKeySpec = factory.readPrivateKey(bytes);

        assertEquals(new BigInteger("31", 10), privateKeySpec.getModulus());
        assertEquals(new BigInteger("101", 10), privateKeySpec.getPrivateExponent());
    }

    @Test
    public void testReadPublicKey() throws Exception{

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Base64OutputStream outBase64 = new Base64OutputStream(out);
        ObjectOutputStream objects = new ObjectOutputStream(outBase64);


        objects.writeObject(new BigInteger("11", 10));
        objects.writeObject(new BigInteger("13", 10));

        objects.flush();
        outBase64.flush();
        out.flush();
        objects.close();
        outBase64.close();
        out.close();

        InputStream bytes = new ByteArrayInputStream(out.toByteArray());

        RsaKeyFactory factory = new RsaKeyFactory();

        RSAPublicKeySpec publicKeySpec = factory.readPublicKey(bytes);

        assertEquals(new BigInteger("11", 10), publicKeySpec.getModulus());
        assertEquals(new BigInteger("13", 10), publicKeySpec.getPublicExponent());
    }

    @Test
    public void testCreateAesKey() {

        assertCreateAesKey(2048, 245, 25);
        assertCreateAesKey(1024, 117, 20);
        assertCreateAesKey(512, 53, 10);

        byte b = Byte.parseByte("00000000", 2);
        int i = (int) b;

        byte b2 = (byte) i;
        assertEquals(b, b2);

    }

    private void assertCreateAesKey(int rsaKeyLength, int expectedAesKeyLength, int expectedDifferentRandomBytes) {

        RsaKeyFactory factory = new RsaKeyFactory();

        byte[] aesKey = factory.createAesKey(rsaKeyLength);

        assertEquals(expectedAesKeyLength, aesKey.length);
        // assert that it's not just a new byte array and that it seems to be random
        Map<Byte, Integer> counts = new HashMap<>();
        for (byte b : aesKey) {
            if (!counts.containsKey(b)) {
                counts.put(b, 0);
            }
            counts.put(b, counts.get(b) + 1);
        }
        assertThat(counts.keySet().size(), is(greaterThan(expectedDifferentRandomBytes)));


    }
}
