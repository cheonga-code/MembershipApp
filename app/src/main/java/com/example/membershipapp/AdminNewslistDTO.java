package com.example.membershipapp;

public class AdminNewslistDTO {

    int newsNumber;
    String newsTitle;

    public AdminNewslistDTO() {
    }

    public int getNewsNumber() {
        return newsNumber;
    }

    public void setNewsNumber(int newsNumber) {
        this.newsNumber = newsNumber;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }
}
