import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Test {
    private static final String ALGORITHM = "AES";
    private static final byte[] KEY = "1234567890123456".getBytes();

    public static void main(String[] args) {
        try {
            String plainText = "Amaravathi@1209";

            // Generate the secret key
            SecretKey secretKey = new SecretKeySpec(KEY, ALGORITHM);

            // Initialize the cipher in encryption mode
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            // Perform encryption
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());

            // Encode to Base64 and print the result
            String encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);
            System.out.println("Encrypted Text: " + encryptedText);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}