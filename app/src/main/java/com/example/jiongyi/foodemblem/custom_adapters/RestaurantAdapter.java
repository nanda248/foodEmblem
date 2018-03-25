package com.example.jiongyi.foodemblem.custom_adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.jiongyi.foodemblem.R;
import com.example.jiongyi.foodemblem.room.CustomerReservation;
import com.example.jiongyi.foodemblem.room.Restaurant;

import java.util.ArrayList;

/**
 * Created by JiongYi on 22/3/2018.
 */

public class RestaurantAdapter extends ArrayAdapter<Restaurant> {
    public RestaurantAdapter(Context context, ArrayList<Restaurant> restaurants) {
        super(context,0,restaurants);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Restaurant restaurant = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.restaurant_grid_item, parent, false);
        }
        // Lookup view for data population
        TextView restName = (TextView) convertView.findViewById(R.id.restName);
        convertView.setId(restaurant.getRestaurantId());
        // Populate the data into the template view using the data object
        try {
            restName.setText(String.valueOf(restaurant.getRestaurantName()));
        }
        catch (Exception ex){
            Log.e("Error" , ex.getMessage());
        }
        // Return the completed view to render on screen
        return convertView;
    }
}
