package com.example.jiongyi.foodemblem.room;

import java.io.Serializable;
import java.util.List;

/**
 * Created by JiongYi on 28/3/2018.
 */

public class CustomerOrder implements Serializable {
    private List<RestaurantDish>orders;

    public CustomerOrder(){

    }

    public CustomerOrder(List<RestaurantDish>orders){
        this.orders = orders;
    }

    public List<RestaurantDish> getOrders() {
        return orders;
    }

    public void setOrders(List<RestaurantDish> orders) {
        this.orders = orders;
    }
}
