package com.greem4.springmedicines;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String adminHash = encoder.encode("admin");
        String userHash = encoder.encode("user");

        System.out.println("Admin Password Hash: " + adminHash);
        System.out.println("User Password Hash: " + userHash);
    }
}
