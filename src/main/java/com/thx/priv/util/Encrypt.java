package com.thx.priv.util;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class Encrypt {
    private static final int AES_KEY_SIZE = 256;
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    private static final int SALT_LENGTH_BYTE = 16;

    public static void main(String args[]) throws Exception
    {
        String plainText = "This is a plain text which need to be encrypted by Java AES 256 GCM Encryption Algorithm";
        String password = "simple.powerful";

        System.out.println("Original Text : " + plainText);

        Encrypt encryptUtil = new Encrypt();

        String cipherText = encryptUtil.encrypt(plainText, password);
        System.out.println("Encrypted Text : " + cipherText);

        String decryptedText = encryptUtil.decrypt(cipherText, password);
        System.out.println("DeCrypted Text : " + decryptedText);
    }

    private SecretKey getKeyFromPassword(String password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 200001, AES_KEY_SIZE);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        return secret;
    }

    public String encrypt(String plaintext, String password) throws Exception
    {
        SecureRandom secureRandom = new SecureRandom();
        byte[] keySalt = new byte[SALT_LENGTH_BYTE];
        secureRandom.nextBytes(keySalt);

        SecretKey key = getKeyFromPassword(password, keySalt);

        SecureRandom random = SecureRandom.getInstanceStrong();
        byte[] iv = new byte[GCM_IV_LENGTH];
        random.nextBytes(iv);

        // Get Cipher Instance
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        // Create SecretKeySpec
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");

        // Create GCMParameterSpec
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * java.lang.Byte.SIZE, iv);

        // Initialize Cipher for ENCRYPT_MODE
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);

        byte[] cipherText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        // prefix IV and Salt to cipher text
        byte[] cipherTextWithIvSalt = ByteBuffer.allocate(iv.length + keySalt.length + cipherText.length)
                .put(iv)
                .put(keySalt)
                .put(cipherText)
                .array();

        // string representation, base64, send this string to other for decryption.
        return Base64.getEncoder().encodeToString(cipherTextWithIvSalt);
    }

    public String decrypt(String cipherText, String password) {
        byte[] decode = Base64.getDecoder().decode(cipherText.getBytes(StandardCharsets.UTF_8));

        // get back the iv and salt from the cipher text
        ByteBuffer bb = ByteBuffer.wrap(decode);

        byte[] iv = new byte[GCM_IV_LENGTH];
        bb.get(iv);

        byte[] salt = new byte[SALT_LENGTH_BYTE];
        bb.get(salt);

        byte[] cipherTextBody = new byte[bb.remaining()];
        bb.get(cipherTextBody);

        // get back the aes key from the same password and salt
        String decrypted = null;

        try {
            SecretKey secretKey = getKeyFromPassword(password, salt);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_LENGTH * java.lang.Byte.SIZE, iv));

            byte[] plainText = cipher.doFinal(cipherTextBody);

            decrypted = new String(plainText, StandardCharsets.UTF_8);

        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | InvalidKeySpecException | BadPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }

        return decrypted;
    }
}
