package com.example.demo.common.utils;

import java.security.SecureRandom;
import java.util.Base64;

public final class Utils {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private Utils() {
        // Prevent instantiation
    }

    public static String generateSecureRandomToken() {
        byte[] randomBytes = new byte[32];
        SECURE_RANDOM.nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }
}