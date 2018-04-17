package com.example.jiongyi.foodemblem;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jiongyi.foodemblem.custom_adapters.CheckOutAdapter;
import com.example.jiongyi.foodemblem.fragment.CustomDialogFragment;
import com.example.jiongyi.foodemblem.fragment.ReservationDialogFragment;
import com.example.jiongyi.foodemblem.room.CustomerOrder;
import com.example.jiongyi.foodemblem.room.OrderDish;
import com.example.jiongyi.foodemblem.room.Restaurant;
import com.example.jiongyi.foodemblem.room.RestaurantDish;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CheckOutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        SharedPreferences sp = getSharedPreferences("FoodEmblem",MODE_PRIVATE);
        int restid = sp.getInt("IsOrderingRestId",0);
        loadPromotion(restid);
    }
    public void addCustomerOrder(final ProgressDialog dialog, final List<OrderDish>orderDishes , final double total){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute()
            {
                if(!isFinishing()) {
                    dialog.setMessage("Sending your order to the kitchen");
                    dialog.show();
                }
            }
            @Override
            protected String doInBackground(Void... voids) {
                String data = "";
                try {
                    System.err.println("**** Calling rest web service");
                    URL url = new URL("http://192.168.137.1:8080/FoodEmblemV1-war/Resources/Customer/AddCustomerOrder");
                    // http://localhost:3446/FoodEmblemV1-war/Resources/Sensor/getFridgesByRestaurantId/1
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.setRequestProperty("Accept", "application/json");
                    JSONObject customerOrderReq = new JSONObject();
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < orderDishes.size(); i ++){
                        OrderDish dish = orderDishes.get(i);
                        Gson gson = new Gson();
                        String toJson = gson.toJson(dish);
                        JSONObject jsonObject = new JSONObject(toJson);
                        jsonArray.put(jsonObject);
                    }
                    SharedPreferences sp = getSharedPreferences("FoodEmblem",MODE_PRIVATE);
                    String email = sp.getString("UserEmail","");
                    customerOrderReq.put("email",email);
                    customerOrderReq.put("orderdishes", jsonArray);
                    customerOrderReq.put("total",total);
                    DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                    wr.writeBytes(customerOrderReq.toString());
                    wr.flush();
                    wr.close();
                    InputStream in = httpURLConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(in);

                    int inputStreamData = inputStreamReader.read();
                    while (inputStreamData != -1) {
                        char current = (char) inputStreamData;
                        inputStreamData = inputStreamReader.read();
                        data += current;
                    }
                } catch (Exception ex) {

                    System.out.println("error calling API");
                    //Toast.makeText(getApplicationContext(), "Error calling REST web service", Toast.LENGTH_LONG).show();

                    ex.printStackTrace();
                }
                return data;
            }

            @Override
            protected void onPostExecute(String jsonString) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                try {
                    if(!isFinishing()){
                        DialogFragment dialogFragment = new CustomDialogFragment();
                        dialogFragment.show(getFragmentManager(),"CustomDialog");
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();
    }

    public void loadPromotion(final int restid){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute()
            {
            }
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    System.err.println("**** Calling rest web service");
                    URL url = new URL("http://192.168.137.1:8080/FoodEmblemV1-war/Resources/Promotion/getPromotionByRestaurantId/" + restid);
                    // http://localhost:3446/FoodEmblemV1-war/Resources/Sensor/getFridgesByRestaurantId/1
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();

                    String line = null;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    return stringBuilder.toString();

                } catch (Exception ex) {

                    System.out.println("error calling API");
                    //Toast.makeText(getApplicationContext(), "Error calling REST web service", Toast.LENGTH_LONG).show();

                    ex.printStackTrace();
                }
                return "";
            }

            @Override
            protected void onPostExecute(String jsonString) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONArray promoarray = jsonObject.getJSONArray("promotions");
                    JSONObject promojson = promoarray.getJSONObject(0);
                    double promopercentage = promojson.getDouble("promotionPercentage");
                    if (promopercentage > 0){
                        double promo = 0.00;
                        double subtotal = 0.00;
                        double total = 0.00;
                        CustomerOrder customerOrder = (CustomerOrder) getIntent().getExtras().getSerializable("CustomerOrder");
                        List<String>orders = new ArrayList<String>();
                        TextView totalamt = (TextView) findViewById(R.id.total);
                        TextView promoamt = (TextView) findViewById(R.id.promo);
                        TextView subtotalamt = (TextView) findViewById(R.id.subtotal);
                        List<OrderDish>orderDishes = new ArrayList<OrderDish>();
                        for (int i = 0; i < customerOrder.getOrders().size(); i++){
                            OrderDish orderDish = new OrderDish();
                            RestaurantDish dish = customerOrder.getOrders().get(i);
                            String order = dish.getQuantity() + " X  " + dish.getName();
                            orders.add(order);
                            subtotal += (dish.getQuantity() * dish.getPrice());
                            orderDish.setId(dish.getDishid());
                            orderDish.setQty(dish.getQuantity());
                            orderDishes.add(orderDish);
                        }
                        double discountedamt = (promopercentage/100) * subtotal;
                        total = subtotal - discountedamt;
                        subtotalamt.setText("$" + String.format("%.2f",subtotal));
                        totalamt.setText("$" + String.format("%.2f",total));
                        promoamt.setText("- $" + String.format("%.2f",discountedamt));
                        CheckOutAdapter adapter = new CheckOutAdapter(getApplicationContext(), customerOrder.getOrders());
                        ListView listView = findViewById(R.id.orderList);
                        listView.setAdapter(adapter);
                        final ProgressDialog dialog = new ProgressDialog(CheckOutActivity.this);
                        Button placeOrderBtn = findViewById(R.id.placeOrderBtn);
                        final List<OrderDish>wsorderDishList = orderDishes;
                        final double wstotal = total;
                        placeOrderBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Triger webservice to add to kitchen orders, remove sharedpreferences
                                //Format : quantity,dishid
                                SharedPreferences sp = getSharedPreferences("FoodEmblem",MODE_PRIVATE);
                                sp.edit().remove("IsOrdering").apply();
                                sp.edit().remove("IsOrderingRestId").apply();
                                addCustomerOrder(dialog,wsorderDishList,wstotal);
                            }
                        });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();
    }
}
