package io.github.ballotguard;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class TestPurpose {

    private static final String secret= "f4FRw42eioeiUOYfrtbvng35s";
    private static final String algo = "AES/CBC/PKCS5Padding";
    private static SecretKeySpec keySpec;

//    @Value("${app.votingLinkSecret}") String secret;
//    @Value("${app.votingLinkAlgo}") String algo;


    @PostConstruct
    private void init() {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(secret.getBytes("UTF-8"));
            this.keySpec = new SecretKeySpec(hash, 0, 16, "AES");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize VotingStringUtil", e);
        }
    }

    public static String encrypt(String s1, String s2) throws Exception {
        byte[] b1 = s1.getBytes("UTF-8");
        byte[] b2 = s2.getBytes("UTF-8");
        ByteBuffer buf = ByteBuffer.allocate(4 + b1.length + b2.length);
        buf.putInt(b1.length);
        buf.put(b1);
        buf.put(b2);
        byte[] plain = buf.array();

        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance(algo);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv));
        byte[] ct = cipher.doFinal(plain);

        byte[] combined = ByteBuffer.allocate(iv.length + ct.length)
                .put(iv)
                .put(ct)
                .array();

        return Base64.getUrlEncoder().withoutPadding().encodeToString(combined);
    }

    public static String[] decrypt(String cipherText) throws Exception {
        byte[] all = Base64.getUrlDecoder().decode(cipherText);
        ByteBuffer buf = ByteBuffer.wrap(all);

        byte[] iv = new byte[16];
        buf.get(iv);

        byte[] ct = new byte[buf.remaining()];
        buf.get(ct);

        Cipher cipher = Cipher.getInstance(algo);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
        byte[] plain = cipher.doFinal(ct);

        ByteBuffer pb = ByteBuffer.wrap(plain);
        int len1 = pb.getInt();
        byte[] b1 = new byte[len1];
        pb.get(b1);
        byte[] b2 = new byte[pb.remaining()];
        pb.get(b2);

        return new String[]{new String(b1, "UTF-8"), new String(b2, "UTF-8")};
    }

    public static void main(String[] args) throws Exception {
        System.out.println(decrypt("wctg7um4e-a13sjxflobuc3die-i9kazazucu6wzuagd4doyvxjlo-yhkpte1t5crnnxzn1dyeqqiykjoedcajooq6qnqpvypvp_7t8hts9lvy9mwlkt6hbvdpc8vpx9"));
    }
}
