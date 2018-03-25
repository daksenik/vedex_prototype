package com.vedex.android.mainapp.dataprocessing;


import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class User implements Serializable {
    private static User user;

    private int id;
    private String name;
    private String surname;
    private String username;
    private String email;
    private String password;
    private String photo;

    public static User getInstance(String username) {
        if (user == null)
            user = new User(username);
        return user;
    }
    public static User getInstance() {
        if(user == null) throw new NullPointerException("Object must be created before it is referenced.");
        return user;
    }

    public static User parseFromJSON(JSONObject src) {
        try {
            int user_id = src.getInt("user_id");
            String login = src.getString("login");
            String name = src.getString("name");
            String surname = src.getString("surname");
            String email = src.getString("email");
            String photo = src.getString("photo");
            return new User(user_id, login, name, surname, email, photo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User() {}
    public User(String username) {this.username = username; }
    public User(String username, String name, String surname, String email, String photo) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.photo = photo;
        this.password = "";
    }

    public User(int id, String username, String name, String surname, String email, String photo) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.photo = photo;
        this.password = "";
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUsername() {
        return username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
