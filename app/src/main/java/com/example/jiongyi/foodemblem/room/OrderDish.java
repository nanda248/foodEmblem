package com.example.jiongyi.foodemblem.room;

/**
 * Created by JiongYi on 29/3/2018.
 */

public class OrderDish {
    private int id;
    private int qty;

    public OrderDish(){

    }

    public OrderDish(int id, int qty){
        this.id = id;
        this.qty = qty;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
