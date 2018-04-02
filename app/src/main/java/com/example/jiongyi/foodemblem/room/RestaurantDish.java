package com.example.jiongyi.foodemblem.room;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * Created by JiongYi on 22/3/2018.
 */

public class RestaurantDish implements Serializable {
    private int dishid;
    private int quantity;
    private String category;
    private int imagePath;
    private String name;
    private Double price;
    private String desc;

    public RestaurantDish(){

    }

    public RestaurantDish(int dishid,double price){
        this.dishid = dishid;
        this.price = price;
    }
    public RestaurantDish(String category, int imagePath, String name, Double price, String desc, int dishid){
        this.category = category;
        this.imagePath = imagePath;
        this.name = name;
        this.price = price;
        this.desc = desc;
        this.dishid = dishid;
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
        return  price;
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

    public int getDishid() {
        return dishid;
    }

    public void setDishid(int dishid) {
        this.dishid = dishid;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
