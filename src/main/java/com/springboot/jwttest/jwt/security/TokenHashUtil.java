package com.springboot.jwttest.jwt.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class TokenHashUtil {
    private static final Base64.Encoder B64 = Base64.getEncoder();
    private static final Base64.Decoder B64D = Base64.getDecoder();

    public static String sha256Base64(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(value.getBytes(StandardCharsets.UTF_8));
            return B64.encodeToString(digest); // DB에는 이 Base64 문자열을 저장
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public static boolean matchesSha256Base64(String raw, String storedBase64) {
        String now = sha256Base64(raw);
        // 타이밍 공격 완화 비교
        byte[] a = B64D.decode(now);
        byte[] b = B64D.decode(storedBase64);
        return MessageDigest.isEqual(a, b);
    }
}
