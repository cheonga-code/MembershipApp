package com.example.membershipapp;

public class CoffeelistDTO {

    String coffeeImgPath;
    String coffeeName;
    int coffeePrice;
    public String uid;
    public String userId;
    public String imgName;

//    public CoffeelistDTO(String coffeeImgPath, String coffeeName, int coffeePrice) {
//        this.coffeeImgPath = coffeeImgPath;
//        this.coffeeName = coffeeName;
//        this.coffeePrice = coffeePrice;
//    }

    public CoffeelistDTO() {
    }

    public String getCoffeeImgPath() {
        return coffeeImgPath;
    }

    public void setCoffeeImgPath(String coffeeImgPath) {
        this.coffeeImgPath = coffeeImgPath;
    }

    public String getCoffeeName() {
        return coffeeName;
    }

    public void setCoffeeName(String coffeeName) {
        this.coffeeName = coffeeName;
    }

    public int getCoffeePrice() {
        return coffeePrice;
    }

    public void setCoffeePrice(int coffeePrice) {
        this.coffeePrice = coffeePrice;
    }
}
