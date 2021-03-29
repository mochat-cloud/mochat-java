package com.mochat.mochat.common.util;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: yangpengwei
 * @time: 2020/11/13 3:16 下午
 * @description RAS 相关工具
 * @see #encryptByPubKey(String, String) 公钥加密
 * @see #decryptByPriKey(String, String) 私钥解密
 * @see #getRsaKeys() 获取公私钥
 * @see #base64Decode(String) Base64 字符串解密成 byte 数组
 * @see #base64EncodeToString(byte[]) byte 数组加密成 Base64 字符串
 */
public class RSAUtils {

    public static final String KEY_PUBLIC = "publicKeyStr";
    public static final String KEY_PRIVATE = "privateKeyStr";

    private static final String ALGORITHM = "RSA";
    private static final String CHARSET = "UTF-8";
    private static final int encryptLimit = 245;
    private static final int decryptLimit = 256;

    /**
     * 公钥加密
     *
     * @param content 原字符串
     * @return 加密后字符串
     */
    public static String encryptByPubKey(String content, String publicKeyStr) {
        publicKeyStr = publicKeyStr.replaceAll("-----BEGIN PUBLIC KEY-----", "");
        publicKeyStr = publicKeyStr.replaceAll("-----END PUBLIC KEY-----", "");
        publicKeyStr = publicKeyStr.replaceAll("\n", "");
        try {
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(base64Decode(publicKeyStr));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] contentBytes = content.getBytes(CHARSET);
            byte[] encryptedData = cipherDoFinal(cipher, contentBytes, encryptLimit);
            return base64EncodeToString(encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "公钥加密失败";
    }

    /**
     * 私钥解密
     *
     * @param ciphertext 被加密字符串
     * @return 解密后字符串
     */
    public static String decryptByPriKey(String ciphertext, String privateKeyStr) {
        privateKeyStr = privateKeyStr.replaceAll("-----BEGIN PRIVATE KEY-----", "");
        privateKeyStr = privateKeyStr.replaceAll("-----END PRIVATE KEY-----", "");
        privateKeyStr = privateKeyStr.replaceAll("\n", "");
        try {
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(base64Decode(privateKeyStr));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] encryptedData = base64Decode(ciphertext);
            byte[] decryptedData = cipherDoFinal(cipher, encryptedData, decryptLimit);
            return new String(decryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @author: yangpengwei
     * @time: 2020/11/13 3:16 下午
     * @description 分段解密
     */
    private static byte[] cipherDoFinal(Cipher cipher, byte[] bytes, int limit) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int bytesLength = bytes.length;
            int offSet = 0;
            int i = 0;
            byte[] cache;
            // 对数据分段解密
            while (bytesLength - offSet > 0) {
                if (bytesLength - offSet > limit) {
                    cache = cipher.doFinal(bytes, offSet, limit);
                } else {
                    cache = cipher.doFinal(bytes, offSet, bytesLength - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * limit;
            }
            byte[] result = out.toByteArray();
            out.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * RSA 公钥、私钥的生成
     *
     * @return map 公钥 key：publicKeyStr，私钥 key：privateKeyStr
     */
    public static Map<String, String> getRsaKeys() {
        Map<String, String> map = new HashMap<>(2);
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(2048);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            String publicKeyStr = base64EncodeToString(publicKey.getEncoded());
            map.put(KEY_PUBLIC, "-----BEGIN PUBLIC KEY-----" + publicKeyStr + "-----END PUBLIC KEY-----");
            String privateKeyStr = base64EncodeToString(privateKey.getEncoded());
            map.put(KEY_PRIVATE, "-----BEGIN PRIVATE KEY-----" + privateKeyStr + "-----END PRIVATE KEY-----");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * @param bytes 原 Byte 数组
     * @return Base64 字符串
     * @author: yangpengwei
     * @time: 2020/11/13 2:55 下午
     * @description 原 byte 数组加密成 Base64 字符串
     */
    public static String base64EncodeToString(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * @param str Base64 字符串
     * @return 原 byte 数组
     * @author: yangpengwei
     * @time: 2020/11/13 2:55 下午
     * @description 解析 Base64 字符串
     */
    public static byte[] base64Decode(String str) {
        return Base64.getDecoder().decode(str);
    }

}