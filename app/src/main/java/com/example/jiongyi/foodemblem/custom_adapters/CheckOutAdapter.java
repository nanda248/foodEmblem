package com.example.jiongyi.foodemblem.custom_adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jiongyi.foodemblem.R;
import com.example.jiongyi.foodemblem.room.RestaurantDish;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JiongYi on 29/3/2018.
 */

public class CheckOutAdapter extends ArrayAdapter<RestaurantDish> {
    public CheckOutAdapter(Context context, List<RestaurantDish> checkoutlist) {
        super(context,0,checkoutlist);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        RestaurantDish restaurantDish = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.checkout_list_item, parent, false);
        }
        // Lookup view for data population
        TextView qtydish = (TextView) convertView.findViewById(R.id.qtydish);
        TextView dishprice = (TextView) convertView.findViewById(R.id.dishPrice);
        try {
            qtydish.setText(restaurantDish.getQuantity() + " X  " + restaurantDish.getName());
            dishprice.setText("$"+ String.format("%.2f" ,restaurantDish.getPrice() * restaurantDish.getQuantity()));
        }
        catch (Exception ex){
            Log.e("Error" , ex.getMessage());
        }
        // Return the completed view to render on screen
        return convertView;
    }
}
