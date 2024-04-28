package edu.redwoods.cis18.scam.remotegardenproject2.db;

import java.util.ArrayList;

public class User implements Observer {
    private static User instance;
    private int userID;
    private String username;
    private String password;
    private ArrayList<Plants> plants;

    private User() {
        // private constructor
    }

    public static synchronized User getInstance() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<Plants> getPlants() {
        return plants;
    }

    public void setPlants(ArrayList<Plants> plants) {
        this.plants = plants;
    }

    @Override
    public void update() {

    }
}

