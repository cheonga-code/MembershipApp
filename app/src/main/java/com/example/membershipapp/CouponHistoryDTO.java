package com.example.membershipapp;

public class CouponHistoryDTO {

    int couponUseImgPath;
    String couponUseName;
    String couponUseDeadline;
    String couponUseDate;

    public CouponHistoryDTO() {

    }

    public int getCouponUseImgPath() {
        return couponUseImgPath;
    }

    public void setCouponUseImgPath(int couponUseImgPath) {
        this.couponUseImgPath = couponUseImgPath;
    }

    public String getCouponUseName() {
        return couponUseName;
    }

    public void setCouponUseName(String couponUseName) {
        this.couponUseName = couponUseName;
    }

    public String getCouponUseDeadline() {
        return couponUseDeadline;
    }

    public void setCouponUseDeadline(String couponUseDeadline) {
        this.couponUseDeadline = couponUseDeadline;
    }

    public String getCouponUseDate() {
        return couponUseDate;
    }

    public void setCouponUseDate(String couponUseDate) {
        this.couponUseDate = couponUseDate;
    }
}
