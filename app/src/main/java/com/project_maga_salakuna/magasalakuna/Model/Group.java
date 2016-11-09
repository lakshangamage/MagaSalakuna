package com.project_maga_salakuna.magasalakuna.Model;

import java.util.ArrayList;

/**
 * Created by lakshan on 11/8/16.
 */
public class Group {
    private String name;
    private String id;
    private String picture;
    private ArrayList<User> members;

    public Group(String name, String id, String picture, ArrayList<User> members) {
        this.name = name;
        this.id = id;
        this.picture = picture;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public ArrayList<User> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<User> members) {
        this.members = members;
    }
}
