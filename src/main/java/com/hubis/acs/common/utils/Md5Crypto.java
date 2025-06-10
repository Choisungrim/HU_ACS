package com.hubis.acs.common.utils;


import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.Security;

public class Md5Crypto {
    private static final String _key = "";

    public static String encrypt(String plaintext) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        MessageDigest md = MessageDigest.getInstance("MD5");

        byte[] bytes = _key.getBytes("UTF-8");
        md.update(bytes);
        byte[] digest = md.digest();

        Cipher c = Cipher.getInstance("DESede/ECB/PKCS7Padding");
        c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(digest, "DESede"));
        byte[] encrypted = c.doFinal(plaintext.getBytes("UTF-8"));
        return new String(Base64.encode(encrypted));
    }

    public static String decrypt(String ciphertext) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        MessageDigest md = MessageDigest.getInstance("MD5");

        byte[] bytes = _key.getBytes("UTF-8");
        md.update(bytes);
        byte[] digest = md.digest();

        Cipher c = Cipher.getInstance("DESede/ECB/PKCS7Padding");
        c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(digest, "DESede"));
        byte[] decrypted = c.doFinal(Base64.decode(ciphertext));
        return new String(decrypted, "UTF-8");
    }
}
