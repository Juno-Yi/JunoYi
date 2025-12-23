package com.junoyi.framework.security.crypto;

import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA + AES 混合加密助手
 * 
 * 响应加密流程（服务端 → 前端）：
 * 1. 生成随机 AES 密钥
 * 2. 使用 AES 加密数据（速度快）
 * 3. 使用 RSA 私钥加密 AES 密钥（前端用公钥解密）
 * 4. 返回：RSA加密的AES密钥 + AES加密的数据
 * 
 * 请求解密流程（前端 → 服务端）：
 * 1. 使用 RSA 私钥解密 AES 密钥（前端用公钥加密）
 * 2. 使用 AES 密钥解密数据
 *
 * @author Fan
 */
@Component
public class RsaCryptoHelper {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(RsaCryptoHelper.class);

    private static final String RSA_ALGORITHM = "RSA";
    private static final String RSA_CIPHER = "RSA/ECB/PKCS1Padding";
    private static final String AES_ALGORITHM = "AES";
    private static final String AES_CIPHER = "AES/GCM/NoPadding";
    private static final int AES_KEY_SIZE = 256;
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct
    public void init() {
        try {
            loadKeys();
            log.info("RsaCryptoInit", "RSA key loaded successfully");
        } catch (Exception e) {
            log.warn("RsaCryptoInit", "RSA key loading failed, API encryption functionality will be unavailable: " + e.getMessage());
        }
    }

    /**
     * 加载 RSA 密钥对
     */
    private void loadKeys() throws Exception {
        // 加载私钥
        ClassPathResource privateResource = new ClassPathResource("keys/private.pem");
        if (privateResource.exists()) {
            try (InputStream is = privateResource.getInputStream()) {
                String privateKeyPem = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                privateKey = loadPrivateKey(privateKeyPem);
            }
        }

        // 加载公钥
        ClassPathResource publicResource = new ClassPathResource("keys/public.pem");
        if (publicResource.exists()) {
            try (InputStream is = publicResource.getInputStream()) {
                String publicKeyPem = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                publicKey = loadPublicKey(publicKeyPem);
            }
        }
    }

    /**
     * 解析 PEM 格式私钥
     */
    private PrivateKey loadPrivateKey(String pem) throws Exception {
        String privateKeyPEM = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 解析 PEM 格式公钥
     */
    private PublicKey loadPublicKey(String pem) throws Exception {
        String publicKeyPEM = pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 加密数据（用于响应加密，服务端 → 前端）
     * 使用 RSA 私钥加密 AES 密钥，前端用公钥解密
     * 
     * @param plainText 明文
     * @return 加密结果（Base64 编码）格式：encryptedAesKey.iv.encryptedData
     */
    public String encrypt(String plainText) {
        if (privateKey == null)
            throw new IllegalStateException("私钥未加载，无法加密");

        try {
            // 生成随机 AES 密钥
            KeyGenerator keyGen = KeyGenerator.getInstance(AES_ALGORITHM);
            keyGen.init(AES_KEY_SIZE);
            SecretKey aesKey = keyGen.generateKey();

            // 生成随机 IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            // 使用 AES-GCM 加密数据
            Cipher aesCipher = Cipher.getInstance(AES_CIPHER);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, gcmSpec);
            byte[] encryptedData = aesCipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // 使用 RSA 私钥加密 AES 密钥（前端用公钥解密）
            Cipher rsaCipher = Cipher.getInstance(RSA_CIPHER);
            rsaCipher.init(Cipher.ENCRYPT_MODE, privateKey);
            byte[] encryptedAesKey = rsaCipher.doFinal(aesKey.getEncoded());

            // 组合结果：encryptedAesKey.iv.encryptedData
            String encryptedAesKeyBase64 = Base64.getEncoder().encodeToString(encryptedAesKey);
            String ivBase64 = Base64.getEncoder().encodeToString(iv);
            String encryptedDataBase64 = Base64.getEncoder().encodeToString(encryptedData);

            return encryptedAesKeyBase64 + "." + ivBase64 + "." + encryptedDataBase64;

        } catch (Exception e) {
            throw new RuntimeException("加密失败", e);
        }
    }

    /**
     * 解密数据（用于请求解密，前端 → 服务端）
     * 前端用公钥加密 AES 密钥，服务端用私钥解密
     * 
     * @param encryptedText 加密文本（格式：encryptedAesKey.iv.encryptedData）
     * @return 解密后的明文
     */
    public String decrypt(String encryptedText) {
        if (privateKey == null)
            throw new IllegalStateException("私钥未加载，无法解密");

        try {
            // 解析加密数据
            String[] parts = encryptedText.split("\\.");
            if (parts.length != 3)
                throw new IllegalArgumentException("加密数据格式错误");

            byte[] encryptedAesKey = Base64.getDecoder().decode(parts[0]);
            byte[] iv = Base64.getDecoder().decode(parts[1]);
            byte[] encryptedData = Base64.getDecoder().decode(parts[2]);

            // 使用 RSA 私钥解密 AES 密钥（前端用公钥加密）
            Cipher rsaCipher = Cipher.getInstance(RSA_CIPHER);
            rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] aesKeyBytes = rsaCipher.doFinal(encryptedAesKey);
            SecretKey aesKey = new SecretKeySpec(aesKeyBytes, AES_ALGORITHM);

            // 使用 AES-GCM 解密数据
            Cipher aesCipher = Cipher.getInstance(AES_CIPHER);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            aesCipher.init(Cipher.DECRYPT_MODE, aesKey, gcmSpec);
            byte[] decryptedData = aesCipher.doFinal(encryptedData);

            return new String(decryptedData, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException("解密失败", e);
        }
    }

    /**
     * 检查是否可用
     */
    public boolean isAvailable() {
        return privateKey != null && publicKey != null;
    }

    /**
     * 获取公钥（Base64 编码，供前端使用）
     */
    public String getPublicKeyBase64() {
        if (publicKey == null)
            return null;
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }
}
