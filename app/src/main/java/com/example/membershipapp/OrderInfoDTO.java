package com.example.membershipapp;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderInfoDTO {

    String orderDate;               //주문한 날짜+시간
    String orderEmail;              //주문한 유저 이메일
    String orderName;               //주문한 유저 이름
    String orderState;              //주문상태
    String orderTime;               //주문한 시간
    Map<String, Object> orderlistMap = new HashMap<>();
    int totalOrderBeverageQuantity; //주문한 총 음료만 수량
    int totalOrderPrice;            //총결제가격
    int totalOrderQuantity;         //주문한 총 메뉴 수량

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderEmail() {
        return orderEmail;
    }

    public void setOrderEmail(String orderEmail) {
        this.orderEmail = orderEmail;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public Map<String, Object> getOrderlistMap() {
        return orderlistMap;
    }

    public void setOrderlistMap(Map<String, Object> orderlistMap) {
        this.orderlistMap = orderlistMap;
    }

    public int getTotalOrderBeverageQuantity() {
        return totalOrderBeverageQuantity;
    }

    public void setTotalOrderBeverageQuantity(int totalOrderBeverageQuantity) {
        this.totalOrderBeverageQuantity = totalOrderBeverageQuantity;
    }

    public int getTotalOrderPrice() {
        return totalOrderPrice;
    }

    public void setTotalOrderPrice(int totalOrderPrice) {
        this.totalOrderPrice = totalOrderPrice;
    }

    public int getTotalOrderQuantity() {
        return totalOrderQuantity;
    }

    public void setTotalOrderQuantity(int totalOrderQuantity) {
        this.totalOrderQuantity = totalOrderQuantity;
    }

//    public Map<String, Object> toMap(){
//        HashMap<String, Object> result = new HashMap<>();
//        result.put("orderDate", orderDate);
//        result.put("orderId", orderId);
//        result.put("orderState", orderState);
//        result.put("orderlistMap", orderlistMap);
//        result.put("totalOrderBeverageQuantity", totalOrderBeverageQuantity);
//        result.put("totalOrderPrice", totalOrderPrice);
//        result.put("totalOrderQuantity", totalOrderQuantity);
//
//        return  result;
//    }
}
