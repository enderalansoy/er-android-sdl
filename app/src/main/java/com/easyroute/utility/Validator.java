package com.easyroute.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    /**
     * Email doğrulama
     *
     * @param email
     * @return
     */
    public static boolean email(String email) {
        String regExpn = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@" + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?" + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\." + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?" + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|" + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";
        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) return true;
        else return false;
    }

    /**
     * İsim doğrulama. İsim alanı min. 2 karakterli 2 kelimeden oluşmalı.
     *
     * @param name
     * @return
     */
    public static boolean name(String name) {
        boolean rtn = false;
        name = name.trim().replace("  ", " ");
        if (name.split(" ").length > 1) {
            String[] names = name.split(" ");
            if (names[0].length() > 1 && names[1].length() > 1) rtn = true;
        }
        return rtn;
    }

    public static boolean username(String username) {
        String pattern = "^[a-z0-9_.]{3,30}$";
        return Pattern.compile(pattern).matcher(username).matches();
    }
}
