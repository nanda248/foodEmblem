package com.example.jiongyi.foodemblem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.jiongyi.foodemblem.room.CustomerOrder;
import com.example.jiongyi.foodemblem.room.RestaurantDish;

import java.util.ArrayList;
import java.util.List;

public class AddToOrder extends AppCompatActivity {
    TextView qtyLbl;
    SharedPreferences sp;
    Boolean isordering;
    int qty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_order);
        final RestaurantDish dish = (RestaurantDish) getIntent().getExtras().getSerializable("dish");
        TextView dishnameLbl = findViewById(R.id.dishNameLbl);
        TextView dishdescLbl = findViewById(R.id.dishDescLbl);
        sp = getSharedPreferences("FoodEmblem",MODE_PRIVATE);
        isordering = sp.getBoolean("IsOrdering",false);
        final int restaurantId = getIntent().getIntExtra("RestaurantId",0);
        dishnameLbl.setText(dish.getName());
        dishdescLbl.setText(dish.getDesc());
        Button addtoorderbtn = findViewById(R.id.addToOrderBtn);
        qtyLbl = findViewById(R.id.qtydish);
        if (isordering == true){
            //Update qty of item if user visits back
            CustomerOrder customerOrder = (CustomerOrder) getIntent().getExtras().getSerializable("CustomerOrder");
            for (int i = 0; i < customerOrder.getOrders().size(); i++){
                RestaurantDish restaurantDish = customerOrder.getOrders().get(i);
                if (restaurantDish.getDishid() == dish.getDishid()){
                    qtyLbl.setText(String.valueOf(restaurantDish.getQuantity()));
                }
            }
        }
        qty = Integer.parseInt(qtyLbl.getText().toString());
        ImageButton addbtn = findViewById(R.id.addBtn);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add label number
                qty++;
                qtyLbl.setText(Integer.toString(qty));
            }
        });
        ImageButton removebtn = findViewById(R.id.removeBtn);
        removebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Remove label number
                if (qty > 1) {
                    qty--;
                }
                qtyLbl.setText(Integer.toString(qty));
            }
        });
        addtoorderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add to cart
                if (isordering == true){
                    qty = Integer.parseInt(qtyLbl.getText().toString());
                    Boolean newdish = true;
                    //Customer is already ordering, retrieve current array of cart items
                    CustomerOrder customerOrder = (CustomerOrder) getIntent().getExtras().getSerializable("CustomerOrder");
                    for (int i = 0; i < customerOrder.getOrders().size(); i++){
                        RestaurantDish restaurantDish = customerOrder.getOrders().get(i);
                        //Check for same item, if same just update qty
                        if (restaurantDish.getDishid() == dish.getDishid()){
                            restaurantDish.setQuantity(qty);
                            customerOrder.getOrders().remove(i);
                            customerOrder.getOrders().add(i,restaurantDish);
                            newdish = false;
                        }
                    }
                    Bundle bundle = new Bundle();
                    //Unique dish, create new one
                    if (newdish == true){
                        RestaurantDish restaurantDish = dish;
                        restaurantDish.setQuantity(qty);
                        customerOrder.getOrders().add(restaurantDish);
                    }
                    bundle.putSerializable("CustomerOrder",customerOrder);
                    bundle.putInt("RestaurantId",restaurantId);
                    back(bundle);
                }
                else {
                    //User not currently shopping, create new cart and store added dishes
                    qty = Integer.parseInt(qtyLbl.getText().toString());
                    Bundle bundle = new Bundle();
                    RestaurantDish restaurantDish = dish;
                    restaurantDish.setQuantity(qty);
                    List<RestaurantDish>restaurantDishes = new ArrayList<RestaurantDish>();
                    restaurantDishes.add(restaurantDish);
                    CustomerOrder customerOrder = new CustomerOrder(restaurantDishes);
                    bundle.putSerializable("CustomerOrder",customerOrder);
                    bundle.putInt("RestaurantId",restaurantId);
                    sp.edit().putBoolean("IsOrdering",true).apply();
                    sp.edit().putInt("IsOrderingRestId",restaurantId).apply();
                    isordering = true;
                    back(bundle);
                }
            }
        });
    }
    public void back(Bundle bundle){
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
