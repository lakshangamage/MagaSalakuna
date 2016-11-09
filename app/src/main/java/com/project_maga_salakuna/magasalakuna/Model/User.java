package com.project_maga_salakuna.magasalakuna.Model;

/**
 * Created by Lakshan on 2016-06-12.
 */
public class User {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String picture;
    private double lslongitude;
    private double lslattitude;
    private long time;

    public User() {
    }

    public User(String firstName, String lastName, String email, String phone, String picture) {
        this.picture = picture;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    public User(String id, String firstName, String lastName, String email, String phone, String picture) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.picture = picture;
    }

    public User(String id, String firstName, String lastName, String email, String phone, String picture, double lslongitude, double lslattitude, long time) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.picture = picture;
        this.lslongitude = lslongitude;
        this.lslattitude = lslattitude;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public double getLslongitude() {
        return lslongitude;
    }

    public void setLslongitude(double lslongitude) {
        this.lslongitude = lslongitude;
    }

    public double getLslattitude() {
        return lslattitude;
    }

    public void setLslattitude(double lslattitude) {
        this.lslattitude = lslattitude;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
