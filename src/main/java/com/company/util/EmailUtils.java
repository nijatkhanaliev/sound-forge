package com.company.util;

public class EmailUtils {

    public static String maskEmail(String email) {
        if (email == null) return null;

        return email.replaceAll("[a-zA-Z1-9]", "*");
    }

}
