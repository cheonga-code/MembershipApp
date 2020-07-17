package com.example.membershipapp;

public class CouponlistDTO {

    int couponImgPath;
    String couponCategory;
    String couponName;
    String couponMakeDate;
//    String couponUserDate;
    String couponDeadline;
    boolean couponUseState;

    public CouponlistDTO() {
    }

    public int getCouponImgPath() {
        return couponImgPath;
    }

    public void setCouponImgPath(int couponImgPath) {
        this.couponImgPath = couponImgPath;
    }

    public String getCouponCategory() {
        return couponCategory;
    }

    public void setCouponCategory(String couponCategory) {
        this.couponCategory = couponCategory;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public String getCouponMakeDate() {
        return couponMakeDate;
    }

    public void setCouponMakeDate(String couponMakeDate) {
        this.couponMakeDate = couponMakeDate;
    }

    public String getCouponDeadline() {
        return couponDeadline;
    }

    public void setCouponDeadline(String couponDeadline) {
        this.couponDeadline = couponDeadline;
    }

    public boolean isCouponUseState() {
        return couponUseState;
    }

    public void setCouponUseState(boolean couponUseState) {
        this.couponUseState = couponUseState;
    }
}
