package com.vedex.android.mainapp.dataprocessing;

import android.text.format.DateFormat;
import android.util.Log;

import com.vedex.android.mainapp.dataprocessing.datahelpcomponents.DateParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class LessonResult implements Serializable {
    private String subjectName;
    private int lessonId;
    private String lessonName;
    private String lessonDesc;
    private String lessonPhotoURL;
    private int form;
    private int numberOfQuestions;
    private double successRate;

    private int passageTime;
    private int rightPercentage;
    private String recordDate;

    public LessonResult(String subjectName, int lessonId, String lessonName, String lessonDesc,
                        String lessonPhotoURL, int form, int numberOfQuestions, double successRate,
                        int passageTime, int rightPercentage, String recordDate) {
        this.subjectName = subjectName;
        this.lessonId = lessonId;
        this.lessonName = lessonName;
        this.lessonDesc = lessonDesc;
        this.lessonPhotoURL = lessonPhotoURL;
        this.form = form;
        this.numberOfQuestions = numberOfQuestions;
        this.successRate = successRate;
        this.passageTime = passageTime;
        this.rightPercentage = rightPercentage;
        this.recordDate = recordDate;
    }

    public static LessonResult getFromJSON(JSONObject lessonResult) {
        LessonResult result = null;

        try {
            JSONObject lessonObject = lessonResult.getJSONObject("lesson");

            String subjNme = lessonObject.getString("subject");
            int lsnId = lessonObject.getInt("lesson_id");
            String lsnNme = lessonObject.getString("name");
            String lsnDsc = lessonObject.getString("description");
            String photoURL = lessonObject.getString("photo");
            int frm = lessonObject.getInt("form");
            int questNmbr = lessonObject.getInt("questions_number");
            double sucRate = lessonObject.getDouble("success_rate");

            int psgTme = lessonResult.getInt("passage_time");
            int rightPrc = lessonResult.getInt("right_percent");
            String recDt = DateParser.getFormattedDate(lessonResult.getString("recording_date"));

            result = new LessonResult(subjNme, lsnId, lsnNme, lsnDsc, photoURL, frm, questNmbr,
                    sucRate, psgTme, rightPrc, recDt);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public int getLessonId() {
        return lessonId;
    }

    public String getLessonName() {
        return lessonName;
    }

    public String getLessonDesc() {
        return lessonDesc;
    }

    public String getLessonPhotoURL() {
        return lessonPhotoURL;
    }

    public int getForm() {
        return form;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public double getSuccessRate() {
        return successRate;
    }

    public int getPassageTime() {
        return passageTime;
    }

    public int getRightPercentage() {
        return rightPercentage;
    }

    public String getRecordDate() {
        return recordDate;
    }
}
