package com.example.membershipapp;

public class AdminNewslistDTO {

//    int newsNumber;
    String newsTitle;
    String newsContent;

    public AdminNewslistDTO() {
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsContent() {
        return newsContent;
    }

    public void setNewsContent(String newsContent) {
        this.newsContent = newsContent;
    }
}
