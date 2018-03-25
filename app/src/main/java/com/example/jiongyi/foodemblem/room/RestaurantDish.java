package com.example.jiongyi.foodemblem.room;

/**
 * Created by JiongYi on 22/3/2018.
 */

public class RestaurantDish {
    private String category;
    private int imagePath;
    private String name;
    private Double price;
    private String desc;

    public RestaurantDish(){

    }
    public RestaurantDish(String category, int imagePath, String name, Double price, String desc){
        this.category = category;
        this.imagePath = imagePath;
        this.name = name;
        this.price = price;
        this.desc = desc;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getImagePath() {
        return imagePath;
    }

    public void setImagePath(int imagePath) {
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
