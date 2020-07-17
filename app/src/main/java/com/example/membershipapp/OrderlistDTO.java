package com.example.membershipapp;

import java.io.Serializable;

public class OrderlistDTO implements Serializable {

//    String orderDate;               //주문한 날짜와 시간
//    int totalOrderPrice;            //총결제가격
//    int totalOrderQuantity;         //주문한 총 메뉴 수량
//    int totalOrderBeverageQuantity; //주문한 총 음료만 수량

    String orderMenuItemCategory;   //주문한 아이템 종류
    String orderMenuItemName;   //주문한 아이템 이름
    int orderMenuItemPrice;     //주문한 아이템 가격
    int orderMenuItemQuantity;  //주문한 아이템 수량



    public OrderlistDTO() {
    }

//    public String getOrderDate() {
//        return orderDate;
//    }
//
//    public void setOrderDate(String orderDate) {
//        this.orderDate = orderDate;
//    }
//
//    public int getTotalOrderPrice() {
//        return totalOrderPrice;
//    }
//
//    public void setTotalOrderPrice(int totalOrderPrice) {
//        this.totalOrderPrice = totalOrderPrice;
//    }
//
//    public int getTotalOrderQuantity() {
//        return totalOrderQuantity;
//    }
//
//    public void setTotalOrderQuantity(int totalOrderQuantity) {
//        this.totalOrderQuantity = totalOrderQuantity;
//    }
//
//    public int getTotalOrderBeverageQuantity() {
//        return totalOrderBeverageQuantity;
//    }
//
//    public void setTotalOrderBeverageQuantity(int totalOrderBeverageQuantity) {
//        this.totalOrderBeverageQuantity = totalOrderBeverageQuantity;
//    }

    public String getOrderMenuItemCategory() {
        return orderMenuItemCategory;
    }

    public void setOrderMenuItemCategory(String orderMenuItemCategory) {
        this.orderMenuItemCategory = orderMenuItemCategory;
    }

    public String getOrderMenuItemName() {
        return orderMenuItemName;
    }

    public void setOrderMenuItemName(String orderMenuItemName) {
        this.orderMenuItemName = orderMenuItemName;
    }

    public int getOrderMenuItemPrice() {
        return orderMenuItemPrice;
    }

    public void setOrderMenuItemPrice(int orderMenuItemPrice) {
        this.orderMenuItemPrice = orderMenuItemPrice;
    }

    public int getOrderMenuItemQuantity() {
        return orderMenuItemQuantity;
    }

    public void setOrderMenuItemQuantity(int orderMenuItemQuantity) {
        this.orderMenuItemQuantity = orderMenuItemQuantity;
    }

}
