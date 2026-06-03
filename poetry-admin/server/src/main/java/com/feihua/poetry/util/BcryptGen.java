package com.feihua.poetry.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/** Run once to generate BCrypt hash for seed-data.sql */
public class BcryptGen {
    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder().encode("admin123"));
    }
}
