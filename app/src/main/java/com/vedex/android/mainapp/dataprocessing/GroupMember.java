package com.vedex.android.mainapp.dataprocessing;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by user on 04.10.2017.
 */

public class GroupMember implements Serializable{

    private int userId;
    private String login;
    private String name;
    private String surname;
    private String email;
    private String photo;
    private String role;

    public static GroupMember parseFromJSON(JSONObject src) {
        try {
            int user_id = src.getInt("user_id");
            String login = src.getString("login");
            String name = src.getString("name");
            String surname = src.getString("surname");
            String email = src.getString("email");
            String photo = src.getString("photo");
            String role = src.getString("role");
            return new GroupMember(user_id, login, name, surname, email, photo, role);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public GroupMember(int userId, String login, String name, String surname, String email,
                       String photo, String role) {
        this.userId = userId;
        this.login = login;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.photo = photo;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
