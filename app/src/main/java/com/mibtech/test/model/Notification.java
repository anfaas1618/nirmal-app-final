package com.mibtech.test.model;

public class Notification {

    private String image, title, message;

    public Notification() {

    }

    public Notification(String image, String title, String message) {
        this.image = image;
        this.title = title;
        this.message = message;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
