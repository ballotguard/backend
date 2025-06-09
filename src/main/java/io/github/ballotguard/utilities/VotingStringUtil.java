package io.github.ballotguard.utilities;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

@Component
public class VotingStringUtil {
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int KEY_SIZE = 16; // 128-bit key
    private static final int IV_SIZE = 16;   // 16 bytes for IV

    @Value("${app.votingLinkSecret}") // Inject from properties
    private String privateKey;

    private byte[] key;

    @PostConstruct
    public void init() {
        Objects.requireNonNull(privateKey, "Private key cannot be null");
        this.key = deriveKey(privateKey);
    }

    private byte[] deriveKey(String privateKey) {
        byte[] keyBytes = privateKey.getBytes(StandardCharsets.UTF_8);
        byte[] derivedKey = new byte[KEY_SIZE];
        System.arraycopy(keyBytes, 0, derivedKey, 0, Math.min(keyBytes.length, KEY_SIZE));
        return derivedKey;
    }

    public String encrypt(String str1, String str2) throws Exception {
        // Create combined string with delimiter
        String combined = str1 + "|||" + str2;

        // Generate random IV
        byte[] iv = new byte[IV_SIZE];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Initialize cipher
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        // Encrypt combined string
        byte[] encrypted = cipher.doFinal(combined.getBytes(StandardCharsets.UTF_8));

        // Combine IV + encrypted data
        byte[] result = new byte[IV_SIZE + encrypted.length];
        System.arraycopy(iv, 0, result, 0, IV_SIZE);
        System.arraycopy(encrypted, 0, result, IV_SIZE, encrypted.length);

        return Base64.getEncoder().encodeToString(result);
    }

    public String[] decrypt(String encryptedData) throws Exception {
        // Decode from Base64
        byte[] data = Base64.getDecoder().decode(encryptedData);

        // Extract IV (first 16 bytes)
        byte[] iv = new byte[IV_SIZE];
        System.arraycopy(data, 0, iv, 0, IV_SIZE);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Extract encrypted payload
        byte[] encrypted = new byte[data.length - IV_SIZE];
        System.arraycopy(data, IV_SIZE, encrypted, 0, encrypted.length);

        // Initialize cipher
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        // Decrypt and split strings
        byte[] decrypted = cipher.doFinal(encrypted);
        String combined = new String(decrypted, StandardCharsets.UTF_8);
        return combined.split("\\|\\|\\|", 2);
    }
}