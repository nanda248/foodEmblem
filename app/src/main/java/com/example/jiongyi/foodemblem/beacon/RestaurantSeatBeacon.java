package com.example.jiongyi.foodemblem.beacon;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;
import com.example.jiongyi.foodemblem.R;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.UUID;

/**
 * Created by JiongYi on 30/3/2018.
 */

public class RestaurantSeatBeacon extends PromotionBeacon{
    private BeaconManager beaconManager;
    int major;
    int minor;
    @Override
    public void onCreate() {
        super.onCreate();
        //Call ws to retrieve reservation seat sensor major and minor to monitor
        Log.i("RestaurantSeatBeacon", "started");
        SharedPreferences sp = getSharedPreferences("FoodEmblem",MODE_PRIVATE);
        String email = sp.getString("UserEmail","");
        retrieveReservationSeat(email);
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                Log.i("Service", "monotoring");
                beaconManager.startMonitoring(new BeaconRegion(
                        "monitored region",
                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                        major, minor));
            }
        });
        beaconManager.setBackgroundScanPeriod(1000, 1000);
        beaconManager.setMonitoringListener(new BeaconManager.BeaconMonitoringListener() {
            @Override
            public void onEnteredRegion(BeaconRegion region, List<Beacon> beacons) {
                //Able to order
                SharedPreferences sp = getSharedPreferences("FoodEmblem",MODE_PRIVATE);
                sp.edit().putBoolean("AtTable", true).apply();
            }
            @Override
            public void onExitedRegion(BeaconRegion region) {
                //If exit for 5 mins, set status to inactive and attable to false
                Log.i("Beacon", "User has left table");
            }
        });
    }

    public void retrieveReservationSeat(final String email){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute()
            {

            }
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    System.err.println("**** Calling rest web service");
                    URL url = new URL("http://192.168.43.213:8080/FoodEmblemV1-war/Resources/CustomerReservation/retrieveReservationSeating/" + email);
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
                    major = jsonObject.getInt("major");
                    minor = jsonObject.getInt("minor");
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }.execute();
    }
}
