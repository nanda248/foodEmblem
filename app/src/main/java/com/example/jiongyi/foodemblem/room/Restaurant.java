package com.example.jiongyi.foodemblem.room;

/**
 * Created by JiongYi on 22/3/2018.
 */

public class Restaurant {
    private String restaurantName;
    private String restaurantAddress;
    private String contactNo;
    private String imagePath;
    private int restaurantId;

public Restaurant(){

}

public Restaurant(int restaurantId,String restaurantName, String restaurantAddress, String contactNo, String imagePath){
    this.restaurantId = restaurantId;
    this.restaurantName = restaurantName;
    this.restaurantAddress = restaurantAddress;
    this.contactNo = contactNo;
    this.imagePath = imagePath;
}

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }
}
