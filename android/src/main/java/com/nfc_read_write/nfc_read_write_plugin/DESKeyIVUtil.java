package com.nfc_read_write.nfc_read_write_plugin;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class DESKeyIVUtil {
    private static String charsetName = "GBK";
    private static byte[] desKey;

    static {
        try {
            desKey = "AutoSoft".getBytes(charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static byte[] desIV;

    static {
        try {
            desIV = "ZeroSoft".getBytes(charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解密数据
     *
     * @param message
     * @return
     * @throws Exception
     */
    public static String decrypt(String message, byte[] arrKey, byte[] arrIV) throws Exception {
        byte[] inputByteArray = convertHexString(message);
        if (inputByteArray == null)
            return "";

        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        DESKeySpec desKeySpec = new DESKeySpec(arrKey);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        IvParameterSpec iv = new IvParameterSpec(arrIV);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] retByte = cipher.doFinal(inputByteArray);
        return new String(retByte,"UTF-8");
    }

    /**
     * 加密数据
     *
     * @param message
     * @return
     * @throws Exception
     */
    public static String encrypt(String message, byte[] arrKey, byte[] arrIV)
            throws Exception {
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        DESKeySpec desKeySpec = new DESKeySpec(arrKey);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        IvParameterSpec iv = new IvParameterSpec(arrIV);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        return toHexString(cipher.doFinal(message.getBytes(charsetName)));
    }

    public static byte[] convertHexString(String src) {
        if (src.length() % 2 == 1)
            return null;
        byte digest[] = new byte[src.length() / 2];
        for (int i = 0; i < digest.length; i++) {
            String byteString = src.substring(2 * i, 2 * i + 2);
            int byteValue = Integer.parseInt(byteString, 16);
            digest[i] = (byte) byteValue;
        }

        return digest;
    }

    public static String toHexString(byte b[]) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            String plainText = Integer.toHexString(0xff & b[i]);
            if (plainText.length() < 2)
                plainText = "0" + plainText;
            hexString.append(plainText);
        }

        return hexString.toString();
    }
}