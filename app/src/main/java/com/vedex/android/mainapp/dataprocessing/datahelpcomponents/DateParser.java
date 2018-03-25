package com.vedex.android.mainapp.dataprocessing.datahelpcomponents;

import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by user on 24.09.2017.
 */

public class DateParser {
    public static String getFormattedDate(String input) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date dt = null;
        try {
            dt = df.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        String output = DateFormat.format("dd.MM.yyyy HH:mm:ss", dt).toString();

        return output;
    }
}
