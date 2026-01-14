package com.app.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Utility class for password hashing and validation using PBKDF2WithHmacSHA256.
 * Stores hash as: iterations:salt:hash
 */
public class PasswordUtil {

    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int SALT_SIZE = 16;

    // Hash format: iterations:salt(base64):hash(base64)
    // To distinguish from simple BCrypt which starts with $2a$... we use a custom
    // format or just this.
    // Since user asked for "BCrypt style" or "Professional", PBKDF2 is standard
    // Java.

    /**
     * Hashes a password using PBKDF2.
     * 
     * @param password The clear text password.
     * @return The formatted hash string (iterations:salt:hash).
     */
    public static String hashPassword(String password) {
        char[] chars = password.toCharArray();
        byte[] salt = getSalt();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, ITERATIONS, KEY_LENGTH);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return ITERATIONS + ":" + Base64.getEncoder().encodeToString(salt) + ":"
                    + Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Validates a password against a stored hash.
     * 
     * @param originalPassword The clear text password to check.
     * @param storedHash       The stored hash string (iterations:salt:hash).
     * @return true if valid, false otherwise.
     */
    public static boolean validatePassword(String originalPassword, String storedHash) {
        String[] parts = storedHash.split(":");
        if (parts.length != 3) {
            return false; // Invalid hash format
        }

        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = Base64.getDecoder().decode(parts[1]);
        byte[] hash = Base64.getDecoder().decode(parts[2]);

        PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] testHash = skf.generateSecret(spec).getEncoded();

            int diff = hash.length ^ testHash.length;
            for (int i = 0; i < hash.length && i < testHash.length; i++) {
                diff |= hash[i] ^ testHash[i];
            }
            return diff == 0;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error validating password", e);
        }
    }

    private static byte[] getSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[SALT_SIZE];
        sr.nextBytes(salt);
        return salt;
    }
}
