package com.vedex.android.mainapp.dataprocessing;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Lesson implements Serializable {
    private int lessonId;
    private String name;
    private String description;
    private String photo;
    private int form;
    private int questionsNumber;
    private double successRate;
    private String subject;

    public Lesson(int lessonId, String name, String description, String photo, int form,
                  int questionsNumber, double successRate, String subject) {
        this.lessonId = lessonId;
        this.name = name;
        this.description = description;
        this.photo = photo;
        this.form = form;
        this.questionsNumber = questionsNumber;
        this.successRate = successRate;
        this.subject = subject;
    }
    public static Lesson getFromJSON(JSONObject lesson, String subject) {
        Lesson result = null;
        try {
            int id = lesson.getInt("lesson_id");
            String nm = lesson.getString("name");
            String desc = lesson.getString("description");
            String photo = lesson.getString("photo");
            int form = lesson.getInt("form");
            int quesNum = lesson.getInt("questions_number");

            String temp = lesson.getString("success_rate");
            double successRate = (temp.equals("null")) ? 0 : Double.parseDouble(temp);

            result = new Lesson(id, nm, desc, photo, form, quesNum, successRate, subject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public int getLessonId() {
        return lessonId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPhoto() {
        return photo;
    }

    public int getForm() {
        return form;
    }

    public int getQuestionsNumber() {
        return questionsNumber;
    }

    public double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(double successRate) {
        this.successRate = successRate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
