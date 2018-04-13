package com.example.jiongyi.foodemblem;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.jiongyi.foodemblem.fragment.ReservationDialogFragment;
import com.facebook.share.Share;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReserveSeat extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_seat);
        TextView textView = (TextView)findViewById(R.id.restaurantNameLbl);
        textView.setText(getIntent().getStringExtra("RestaurantName"));
        final int restid = getIntent().getIntExtra("RestaurantId",0);
        final ProgressDialog dialog = new ProgressDialog(ReserveSeat.this);
        final EditText paxinput = (EditText)findViewById(R.id.paxInput);
        final Button reserveBtn = (Button) findViewById(R.id.reserveBtn);
        reserveBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                createReservation(dialog,reserveBtn.getId(), Integer.parseInt(paxinput.getText().toString()));
            }
        });
        paxinput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView textView = (TextView)findViewById(R.id.tableLbl);
                if (s.length() != 0 ) {
                    int pax = Integer.parseInt(s.toString());
                    if (pax != 0) {
                        loadSuitableSeats(reserveBtn,dialog, restid, pax);
                    }
                    else {
                        textView.setText("Enter 1 or more!");
                        reserveBtn.setEnabled(false);
                    }
                }
                else {
                    textView.setText("");
                    reserveBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void loadSuitableSeats(final Button reserveBtn ,final ProgressDialog dialog, final int restid, final int pax){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute()
            {
                dialog.setMessage("Finding a seat");
                dialog.show();
            }
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    System.err.println("**** Calling rest web service");
                    URL url = new URL("http://172.25.103.169:3446/FoodEmblemV1-war/Resources/CustomerReservation/retrieveAllocatedSeat/" + restid + "/" + pax);
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
                TextView textView = (TextView)findViewById(R.id.tableLbl);
                try {
                    if (jsonString.length() == 0){
                        textView.setText("No Available Seats!");
                        reserveBtn.setEnabled(false);
                    }
                    else {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        String tableno = jsonObject.getJSONObject("restaurantSeating").getString("tableNo");
                        int tableid = jsonObject.getJSONObject("restaurantSeating").getInt("id");
                        reserveBtn.setId(tableid);
                        textView.setText(tableno);
                        reserveBtn.setEnabled(true);
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }.execute();
    }

    public void createReservation(final ProgressDialog dialog, final int seatid, final int pax){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute()
            {
                dialog.setMessage("Reserving your seat");
                dialog.show();
            }
            @Override
            protected String doInBackground(Void... voids) {
                String data = "";
                try {
                    System.err.println("**** Calling rest web service");
                    URL url = new URL("http://172.25.103.169:3446/FoodEmblemV1-war/Resources/CustomerReservation");
                    // http://localhost:3446/FoodEmblemV1-war/Resources/Sensor/getFridgesByRestaurantId/1
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("PUT");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.setRequestProperty("Accept", "application/json");
                    JSONObject reservationReq = new JSONObject();
                    SharedPreferences sp = getSharedPreferences("FoodEmblem",MODE_PRIVATE);
                    String email = sp.getString("UserEmail","");
                    reservationReq.put("email",email);
                    reservationReq.put("noOfPax",pax);
                    reservationReq.put("seatId",seatid);
                    DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                    wr.writeBytes(reservationReq.toString());
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
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONObject activereservation = jsonObject.getJSONObject("reservation");
                    int rerid = activereservation.getInt("id");
                    SharedPreferences sp = getSharedPreferences("FoodEmblem",MODE_PRIVATE);
                    sp.edit().putInt("activereservation",rerid).apply();
                    DialogFragment dialogFragment = new ReservationDialogFragment();
                    dialogFragment.show(getFragmentManager(),"ReservationDialog");
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();
    }
}
