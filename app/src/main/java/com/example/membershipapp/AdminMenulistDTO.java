package com.example.membershipapp;

public class AdminMenulistDTO {

    String menuCategory;    //메뉴 종류 구분!
    String menuImgPath;
    String menuImgName;
    String menuName;
    int menuPrice;
//
//    String uid;     // 삭제 (x 필요없음)
//    String userId;  // 삭제 (x 필요없음)

    public AdminMenulistDTO() {
    }

    public String getMenuCategory() {
        return menuCategory;
    }

    public void setMenuCategory(String menuCategory) {
        this.menuCategory = menuCategory;
    }

    public String getMenuImgPath() {
        return menuImgPath;
    }

    public void setMenuImgPath(String menuImgPath) {
        this.menuImgPath = menuImgPath;
    }

    public String getMenuImgName() {
        return menuImgName;
    }

    public void setMenuImgName(String menuImgName) {
        this.menuImgName = menuImgName;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public int getMenuPrice() {
        return menuPrice;
    }

    public void setMenuPrice(int menuPrice) {
        this.menuPrice = menuPrice;
    }

//    public String getUid() {
//        return uid;
//    }
//
//    public void setUid(String uid) {
//        this.uid = uid;
//    }
//
//    public String getUserId() {
//        return userId;
//    }
//
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }
}
