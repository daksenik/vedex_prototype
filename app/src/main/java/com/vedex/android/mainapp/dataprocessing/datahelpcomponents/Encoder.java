package com.vedex.android.mainapp.dataprocessing.datahelpcomponents;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by user on 10.10.2017.
 */

public class Encoder {
    public static String encode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return s;
    }
}
