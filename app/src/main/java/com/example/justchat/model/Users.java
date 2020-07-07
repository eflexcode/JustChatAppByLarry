package com.example.justchat.model;

public class Users {

    private String userId;
    private String username;
    private String imageUrl;
    private String coverImageUrl;
    private String dateOfBirth;
    private String aboutUser;
    private String status;

    public Users() {
    }

    public Users(String userId, String username, String imageUrl, String coverImageUrl, String dateOfBirth,String aboutUser,String status) {
        this.userId = userId;
        this.username = username;
        this.imageUrl = imageUrl;
        this.coverImageUrl = coverImageUrl;
        this.dateOfBirth = dateOfBirth;
        this.aboutUser = aboutUser;
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAboutUser() {
        return aboutUser;
    }

    public void setAboutUser(String aboutUser) {
        this.aboutUser = aboutUser;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
