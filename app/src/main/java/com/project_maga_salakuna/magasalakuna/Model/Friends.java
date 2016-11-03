package com.project_maga_salakuna.magasalakuna.Model;

/**
 * Created by lakshan on 11/4/16.
 */
public class Friends {
    String friend1;
    String friend2;
    int accepted;

    public Friends(String friend1, String friend2, int accepted) {
        this.friend1 = friend1;
        this.friend2 = friend2;
        this.accepted = accepted;
    }

    public String getFriend1() {
        return friend1;
    }

    public void setFriend1(String friend1) {
        this.friend1 = friend1;
    }

    public String getFriend2() {
        return friend2;
    }

    public void setFriend2(String friend2) {
        this.friend2 = friend2;
    }

    public int getAccepted() {
        return accepted;
    }

    public void setAccepted(int accepted) {
        this.accepted = accepted;
    }
}
