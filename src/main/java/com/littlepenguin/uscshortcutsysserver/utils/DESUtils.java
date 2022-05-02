package com.littlepenguin.uscshortcutsysserver.utils;


import javax.crypto.*;
import javax.crypto.spec.DESedeKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class DESUtils {
    //Base64解码/编码器
    private static final Base64.Decoder decoder = Base64.getDecoder();
    private static final Base64.Encoder encoder = Base64.getEncoder();
    //密码字节
    private static final byte[] keyBytes ;
    // desKeySpec 指定密钥生成规则
    private static DESedeKeySpec desKeySpec;
    // keyFactory 密钥生成工厂
    private static SecretKeyFactory keyFactory;
    //
    private static SecretKey desKey;
    // Cipher负责完成加密或解密工作
    private static Cipher c;
    // 该字节数组负责保存加密的结果
    private static byte[] cipherByte;
    static {
        Security.addProvider(new com.sun.crypto.provider.SunJCE());

        String tmp = "";
        for (int i = 0; i < 7; i++) {
            tmp+="littlepenguin";
        }
        keyBytes = tmp.getBytes();

        // 指定密钥生成规则由字符串偏移172生成
        try {
            desKeySpec = new DESedeKeySpec(keyBytes, 7);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        //生成密钥工厂
        try {
            keyFactory = SecretKeyFactory.getInstance("DESede");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        //生成密钥
        try {
            desKey = keyFactory.generateSecret(desKeySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        // 生成Cipher对象,指定其支持的DES算法
        try {
            c = Cipher.getInstance("DESede");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 对str进行加密
     * @param str
     * @return 加密后字节数组
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static byte[] Encrytor(String str) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        // 根据密钥，对Cipher对象进行初始化，ENCRYPT_MODE表示加密
        c.init(Cipher.ENCRYPT_MODE, desKey);
        byte[] src = str.getBytes();
        // 加密，后再进行base64编码
        cipherByte = c.doFinal(src);
        return encoder.encode(cipherByte);
    }

    /**
     * 对str进行解密
     * @param buff
     * @return 加密后base64编码的字节数组
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static byte[] Decryptor(byte[] buff) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        // 根据密钥，对Cipher对象进行初始化，DECRYPT_MODE表示解密
        c.init(Cipher.DECRYPT_MODE, desKey);
        //先base64解码再解密
        buff = decoder.decode(buff);
        cipherByte = c.doFinal(buff);
        return cipherByte;
    }
}
