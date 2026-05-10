// PasswordUtil.java
package com.xsy.dailymate.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {
    static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String encode(String rawPwd) {
        return encoder.encode(rawPwd);
    }

    public static boolean match(String rawPwd, String hashPwd) {
        return encoder.matches(rawPwd, hashPwd);
    }
}
