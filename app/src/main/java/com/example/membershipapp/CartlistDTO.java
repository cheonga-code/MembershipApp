package com.example.membershipapp;

import java.io.Serializable;

public class CartlistDTO implements Serializable {

    String cartMenuCategory;
    String cartMenuImgPath;
    String cartMenuName;
    int cartMenuPrice;
    int cartMenuQuantity;

    public CartlistDTO() {
    }

    public String getCartMenuCategory() {
        return cartMenuCategory;
    }

    public void setCartMenuCategory(String cartMenuCategory) {
        this.cartMenuCategory = cartMenuCategory;
    }

    public String getCartMenuImgPath() {
        return cartMenuImgPath;
    }

    public void setCartMenuImgPath(String cartMenuImgPath) {
        this.cartMenuImgPath = cartMenuImgPath;
    }

    public String getCartMenuName() {
        return cartMenuName;
    }

    public void setCartMenuName(String cartMenuName) {
        this.cartMenuName = cartMenuName;
    }

    public int getCartMenuPrice() {
        return cartMenuPrice;
    }

    public void setCartMenuPrice(int cartMenuPrice) {
        this.cartMenuPrice = cartMenuPrice;
    }

    public int getCartMenuQuantity() {
        return cartMenuQuantity;
    }

    public void setCartMenuQuantity(int cartMenuQuantity) {
        this.cartMenuQuantity = cartMenuQuantity;
    }
}
