package circuit.crypto;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * =============================================================================
 * CLASS: CryptoManager
 * =============================================================================
 * Implements the SECURITY requirement from the project brief:
 * "Using Java's Built-in Cryptography Architecture (javax.crypto and java.security)"
 *
 * This class provides AES (Advanced Encryption Standard) encryption and
 * decryption for circuit data. It allows users to encrypt/decrypt their
 * circuit parameters (e.g., component values) for secure storage or transfer.
 *
 * ENCRYPTION ALGORITHM: AES-128 in CBC (Cipher Block Chaining) mode
 *   - AES    = Advanced Encryption Standard (industry standard)
 *   - 128    = 128-bit key length (16 bytes)
 *   - CBC    = each block is XOR'd with the previous ciphertext block
 *   - PKCS5  = standard padding to fill incomplete blocks
 *
 * OOP CONCEPTS:
 *   - Encapsulation : Private key and cipher objects
 *   - Abstraction   : Hides crypto complexity behind simple encrypt/decrypt API
 * =============================================================================
 */
public class CryptoManager {

    // -------------------------------------------------------------------------
    // ENCAPSULATION: The secret key is NEVER exposed outside this class
    // -------------------------------------------------------------------------
    private SecretKey secretKey;       // The AES encryption key (private!)
    private byte[]    initVector;      // Initialization Vector for CBC mode
    private String    algorithm;       // The cipher algorithm string

    /** Algorithm configuration constant */
    private static final String ALGORITHM     = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int    KEY_SIZE       = 128; // bits

    /**
     * Constructor — generates a new random AES key automatically.
     * Each CryptoManager instance gets its own unique key.
     *
     * @throws Exception If key generation fails (should not happen normally)
     */
    public CryptoManager() throws Exception {
        this.algorithm = ALGORITHM;

        // Step 1: Create a KeyGenerator for AES
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(KEY_SIZE); // 128-bit key

        // Step 2: Generate the secret key
        this.secretKey = keyGen.generateKey();

        // Step 3: Generate a random Initialization Vector (IV)
        // The IV adds randomness so the same plaintext won't produce the same ciphertext
        this.initVector = new byte[16]; // AES block size = 16 bytes
        new SecureRandom().nextBytes(initVector); // Fill with secure random bytes

        System.out.println("  [CryptoManager] AES-128 CBC key generated successfully.");
    }

    /**
     * ENCRYPTS a plain text string using AES-128 CBC.
     * Returns a Base64-encoded string of the ciphertext.
     *
     * Steps:
     *   1. Get AES/CBC/PKCS5Padding cipher instance
     *   2. Initialize cipher in ENCRYPT_MODE with key and IV
     *   3. Encrypt the byte array
     *   4. Encode as Base64 for safe text transport
     *
     * @param plainText The data to encrypt (e.g., circuit summary)
     * @return Base64-encoded encrypted string
     * @throws Exception If encryption fails
     */
    public String encrypt(String plainText) throws Exception {
        // Create the Cipher object using the Java Cryptography Architecture (JCA)
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);

        // IvParameterSpec wraps the IV bytes for use by the cipher
        IvParameterSpec ivSpec = new IvParameterSpec(initVector);

        // Initialize cipher for ENCRYPTION
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

        // Perform encryption: plaintext bytes → ciphertext bytes
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));

        // Encode as Base64 (converts binary bytes to printable ASCII text)
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * DECRYPTS an AES-encrypted Base64 string back to plain text.
     *
     * Steps:
     *   1. Decode from Base64 back to byte array
     *   2. Initialize cipher in DECRYPT_MODE with same key and IV
     *   3. Decrypt the bytes
     *   4. Return as UTF-8 string
     *
     * @param encryptedBase64 The encrypted Base64 string to decrypt
     * @return The original plain text
     * @throws Exception If decryption fails (wrong key, corrupted data, etc.)
     */
    public String decrypt(String encryptedBase64) throws Exception {
        // Decode from Base64 back to raw bytes
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedBase64);

        // Create the same cipher configuration
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        IvParameterSpec ivSpec = new IvParameterSpec(initVector);

        // Initialize cipher for DECRYPTION
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

        // Perform decryption: ciphertext bytes → plaintext bytes
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes, "UTF-8");
    }

    /**
     * Returns the Base64-encoded representation of the current secret key.
     * IMPORTANT: In a real system, NEVER expose the key like this!
     * This is only for educational demonstration purposes.
     *
     * @return Base64-encoded secret key
     */
    public String getKeyAsBase64() {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    /**
     * Returns a short hash of the key for identification (safe to display).
     * Uses the first 8 characters of the Base64 key.
     *
     * @return First 8 chars of the Base64 key (for identification only)
     */
    public String getKeyFingerprint() {
        String keyB64 = getKeyAsBase64();
        return keyB64.substring(0, Math.min(8, keyB64.length())) + "...";
    }

    /**
     * Encrypts a circuit summary string and prints a demonstration.
     * This shows the full encrypt → display → decrypt cycle.
     *
     * @param circuitData The circuit parameter string to protect
     */
    public void demonstrateEncryption(String circuitData) {
        System.out.println("\n  ╔══════════════════════════════════════════════════════╗");
        System.out.println("  ║        CRYPTOGRAPHIC SECURITY DEMONSTRATION          ║");
        System.out.println("  ╠══════════════════════════════════════════════════════╣");
        System.out.printf("  ║  Algorithm  : %-37s ║%n", TRANSFORMATION);
        System.out.printf("  ║  Key Size   : %-37s ║%n", KEY_SIZE + "-bit AES");
        System.out.printf("  ║  Key ID     : %-37s ║%n", getKeyFingerprint());

        try {
            // Show original data
            System.out.println("  ╠══════════════════════════════════════════════════════╣");
            System.out.printf("  ║  PLAINTEXT  : %-37s ║%n",
                circuitData.length() > 35
                    ? circuitData.substring(0, 32) + "..."
                    : circuitData);

            // Encrypt
            String encrypted = encrypt(circuitData);
            System.out.printf("  ║  ENCRYPTED  : %-37s ║%n",
                encrypted.length() > 35
                    ? encrypted.substring(0, 32) + "..."
                    : encrypted);

            // Decrypt back
            String decrypted = decrypt(encrypted);
            boolean match = decrypted.equals(circuitData);
            System.out.printf("  ║  DECRYPTED  : %-37s ║%n", match ? "[Matches original ✓]" : "[MISMATCH!]");
            System.out.println("  ╠══════════════════════════════════════════════════════╣");
            System.out.printf("  ║  Status: %-43s ║%n",
                match ? "Encryption/Decryption cycle SUCCESS ✓" : "ERROR in crypto cycle ✗");

        } catch (Exception e) {
            System.out.println("  ║  ERROR: " + e.getMessage());
        }

        System.out.println("  ╚══════════════════════════════════════════════════════╝");
    }

    public String getAlgorithm() { return algorithm; }
}
