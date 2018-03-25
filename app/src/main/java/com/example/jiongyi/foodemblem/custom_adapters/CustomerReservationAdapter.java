package com.example.jiongyi.foodemblem.custom_adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.jiongyi.foodemblem.R;
import com.example.jiongyi.foodemblem.room.CustomerReservation;

import java.util.ArrayList;

/**
 * Created by JiongYi on 19/3/2018.
 */

public class CustomerReservationAdapter extends ArrayAdapter<CustomerReservation> {

    public CustomerReservationAdapter(Context context, ArrayList<CustomerReservation>customerReservations) {
       super(context,0,customerReservations);
    }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            CustomerReservation customerReservation = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.customer_list_item, parent, false);
            }
            // Lookup view for data population
            TextView pax = (TextView) convertView.findViewById(R.id.pax);
            TextView reservationDate = (TextView) convertView.findViewById(R.id.reservationDate);
            TextView tableNo = (TextView) convertView.findViewById(R.id.tableNo);
            TextView status = (TextView) convertView.findViewById(R.id.status);
            TextView restaurantName = (TextView) convertView.findViewById(R.id.restName);
            TextView reservationTime = (TextView) convertView.findViewById(R.id.reservationTime);
            // Populate the data into the template view using the data object
            try {
                pax.setText(String.valueOf(customerReservation.getPax()));
                reservationDate.setText(customerReservation.getReservationdate().toString());
                tableNo.setText(customerReservation.getTableNo());
                status.setText(customerReservation.getStatus());
                if (status.getText().equals("Active")){
                    status.setTextColor(Color.parseColor("#00ff00"));
                }
                restaurantName.setText(customerReservation.getRestname());
                reservationTime.setText((customerReservation.getReservationtime().toString()));
            }
            catch (Exception ex){
                Log.e("Error" , ex.getMessage());
            }
            // Return the completed view to render on screen
            return convertView;
        }
}
