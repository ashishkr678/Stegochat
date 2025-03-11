package stegochat.stegochat.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptionUtil {

    // ✅ Generate a secure base encryption key (for new users)
    public static String generateBaseEncryptionKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (Exception e) {
            throw new RuntimeException("Error generating encryption key", e);
        }
    }

    // ✅ Generate a unique key for each friend
    public static String generateFriendEncryptionKey(String baseKey, String friendUsername) {
        try {
            SecureRandom secureRandom = new SecureRandom((baseKey + friendUsername).getBytes());
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256, secureRandom);
            SecretKey friendKey = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(friendKey.getEncoded());
        } catch (Exception e) {
            throw new RuntimeException("Error generating friend encryption key", e);
        }
    }

    // ✅ Encrypt message
    public static String encrypt(String data, String encryptionKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            SecretKey key = getKeyFromBase64(encryptionKey);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Encryption error", e);
        }
    }

    // ✅ Decrypt message
    public static String decrypt(String encryptedData, String encryptionKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            SecretKey key = getKeyFromBase64(encryptionKey);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedData)));
        } catch (Exception e) {
            throw new RuntimeException("Decryption error", e);
        }
    }

    // ✅ Convert base64 key to SecretKey
    private static SecretKey getKeyFromBase64(String key) {
        byte[] decodedKey = Base64.getDecoder().decode(key);
        return new javax.crypto.spec.SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }
}
