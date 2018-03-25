package com.vedex.android.mainapp.dataprocessing;


import android.util.Log;
import android.util.Patterns;

public class Validator {

    private static final String SYMBOLS = "!@#$%^&*()_+=-[]{}\';\":}{<>,.?/";

    public static boolean ValidateGroupName(String str) {
        return (str.length() > 0) && (str.trim().length() == str.length());
    }

    public static boolean ValidateName(String str) {
        Log.d("DIMAAAA", str);
        if (str.isEmpty() || str.length() > 50) return false;
        for (char ch : str.toCharArray()) if(!Character.isLetter(ch)) return false;
        return true;
    }

    public static boolean ValidateUsername(String str) {
        if (str.isEmpty() || str.length() > 20) return false;
        for (char ch : str.toCharArray()) {
            if (!(ch >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z' ||
                    ch >= '0' && ch <= '9' || ch == '_')) return false;
        }
        return true;
    }

    public static boolean ValidateEmail(String str) {
        if (str.isEmpty() || str.length() > 50) return false;
        return Patterns.EMAIL_ADDRESS.matcher(str).matches();
    }

    public static boolean ValidatePassword(String str) {
        if (str.length() < 6 || str.length() > 50) return false;
        for (char ch : str.toCharArray()) {
            if (!(ch >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z' ||
                    ch >= '0' && ch <= '9' || str.indexOf(ch) != -1)) return false;
        }
        return true;
    }
}
