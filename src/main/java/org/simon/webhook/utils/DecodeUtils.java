package org.simon.webhook.utils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @program: simon-webhook
 * @description: TODO
 * @author: renBo
 * @create: 2026-01-15 10:27
 **/
public class DecodeUtils {


    public static String decoded(String str) {
        return URLDecoder.decode(str, StandardCharsets.UTF_8);
    }


    public static boolean isEncoded(String str) {
        try {
            String decoded = URLDecoder.decode(str, StandardCharsets.UTF_8);
            String encoded = URLEncoder.encode(decoded, StandardCharsets.UTF_8)
                    .replace("+", "%20"); // 对齐 RFC3986
            return !encoded.equals(str) || looksLikeEncoded(str);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean looksLikeEncoded(String str) {
        if (str == null) return false;
        // %XX 或 +
        return str.matches(".*%[0-9a-fA-F]{2}.*") || str.contains("+");
    }


}
