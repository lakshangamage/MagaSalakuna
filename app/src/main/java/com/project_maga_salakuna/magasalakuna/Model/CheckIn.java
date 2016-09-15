package com.project_maga_salakuna.magasalakuna.Model;

public class CheckIn {
    private String Uid;
    private String checkinId;
    private String status;
    private String timestamp;
    private String at;
    private double longitude;
    private double lattitude;

    private User user;

    public CheckIn(String uid, String checkinId, String status, String timestamp, String at, double longitude, double lattitude, User user) {
        Uid = uid;
        this.checkinId = checkinId;
        this.status = status;
        this.timestamp = timestamp;
        this.at = at;
        this.longitude = longitude;
        this.lattitude = lattitude;
        this.user = user;
    }

    public CheckIn(String uid, String checkinId, String status, String timestamp, String at, double longitude, double lattitude) {
        Uid = uid;
        this.checkinId = checkinId;
        this.status = status;
        this.timestamp = timestamp;
        this.at = at;
        this.longitude = longitude;
        this.lattitude = lattitude;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getCheckinId() {
        return checkinId;
    }

    public void setCheckinId(String checkinId) {
        this.checkinId = checkinId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAt() {
        return at;
    }

    public void setAt(String at) {
        this.at = at;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLattitude() {
        return lattitude;
    }

    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
    }
}
