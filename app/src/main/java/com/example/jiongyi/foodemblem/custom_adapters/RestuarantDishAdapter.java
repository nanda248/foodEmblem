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
import com.example.jiongyi.foodemblem.room.Restaurant;
import com.example.jiongyi.foodemblem.room.RestaurantDish;

import java.util.ArrayList;

/**
 * Created by JiongYi on 22/3/2018.
 */

public class RestuarantDishAdapter extends ArrayAdapter<RestaurantDish> {
    public RestuarantDishAdapter(Context context, ArrayList<RestaurantDish> restaurantDishes) {
        super(context,0,restaurantDishes);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        RestaurantDish restaurantDish = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.restaurantdish_list_item, parent, false);
        }
        // Lookup view for data population
        TextView dishName = (TextView) convertView.findViewById(R.id.dishNameLbl);
        TextView dishDesc = (TextView) convertView.findViewById(R.id.dishDescLbl);
        TextView dishPrice = (TextView) convertView.findViewById(R.id.dishPriceLbl);
        ImageView img = (ImageView) convertView.findViewById(R.id.dishImg);
        convertView.setId(restaurantDish.getDishid());
        // Populate the data into the template view using the data object
        try {
            dishName.setText(String.valueOf(restaurantDish.getName()));
            dishDesc.setText(restaurantDish.getDesc());
            dishPrice.setText("$"+ String.format("%.2f" ,restaurantDish.getPrice()));
            img.setImageResource(restaurantDish.getImagePath());
        }
        catch (Exception ex){
            Log.e("Error" , ex.getMessage());
        }
        // Return the completed view to render on screen
        return convertView;
    }
}
