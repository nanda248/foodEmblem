package com.example.jiongyi.foodemblem.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;
import com.example.jiongyi.foodemblem.HomeActivity;
import com.example.jiongyi.foodemblem.R;
import com.example.jiongyi.foodemblem.StartupPageActivity;

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
 * Created by JiongYi on 2/4/2018.
 */

public class ScanningService extends IntentService {
    private BeaconManager beaconManager;
    private BeaconManager seatBeaconManager;
    private BeaconRegion region;
    private int major;
    private int minor;
    private int rangemajor;
    private int rangeminor;
    private String sensorid;
    private Handler mHandler;
    public ScanningService() {
        super("ScanningService");
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.i("Run","runnable");
                beaconManager = new BeaconManager(getApplicationContext());
                region = new BeaconRegion("ranged region",
                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);

                beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                    @Override
                    public void onServiceReady() {
                        beaconManager.startRanging(region);
                    }
                });
                beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {
                    @Override
                    public void onBeaconsDiscovered(BeaconRegion region, List<Beacon> list) {
                        SharedPreferences sp = getSharedPreferences("FoodEmblem",MODE_PRIVATE);
                        String email = sp.getString("UserEmail","");
                        if (!list.isEmpty()) {
                            if (sp.getBoolean("isloggedin",false) == true) {
                                //Monitoring service
                                if (sp.getBoolean("FocusScanTable",false) == false) {
                                    retrieveReservationSeat(email);
                                    Log.i("retrieveReservationSeat","retrieveReservationSeatCalled");
                                }
                            }
                            Beacon nearestBeacon = list.get(0);
                            //If nearest beacon is still the same, stop sending notification
                            if (rangemajor == nearestBeacon.getMajor() && rangeminor == nearestBeacon.getMinor()){
                                rangemajor = nearestBeacon.getMajor();
                                rangeminor = nearestBeacon.getMinor();
                                Log.i("rangemajor", String.valueOf(rangemajor));
                                Log.i("rangeminor", String.valueOf(rangeminor));
                            }
                            else {
                                //New beacon, retrieve promotion from it if any
                                rangemajor = nearestBeacon.getMajor();
                                rangeminor = nearestBeacon.getMinor();
                                Log.i("nearestmajor", String.valueOf(rangemajor));
                                Log.i("nearestminor", String.valueOf(rangeminor));
                                retrievePromotionsFromBeacon(rangemajor,rangeminor);
                            }

                        }
                    }
                });
            }
        });
    }
    public void showNotification(String title, String message, int restaurantid) {
        SharedPreferences sp = getSharedPreferences("FoodEmblem",MODE_PRIVATE);
        Intent notifyIntent;
        if (sp.getBoolean("isloggedin",false)==true){
            notifyIntent = new Intent(this, HomeActivity.class);
            Log.i("NotificationId", String.valueOf(restaurantid));
        }
        else {
            notifyIntent = new Intent(this, StartupPageActivity.class);
        }
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[] { notifyIntent }, 0);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_promo_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    public void retrievePromotionsFromBeacon(final int major, final int minor){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute()
            {

            }
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    String sensorid = major+"_"+minor;
                    Log.i("Webservice","**** Calling notification web service");
                    URL url = new URL("http://172.25.103.169:3446/FoodEmblemV1-war/Resources/Promotion/retrieveRestaurantPromoFromBeacon/"+sensorid);
                    Log.i("WSurl",url.toString());
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
                    if (!jsonString.equals("{}")){
                        JSONObject jsonObject = new JSONObject(jsonString);
                        JSONObject promo = jsonObject.getJSONObject("promotion");
                        JSONObject restaurant = jsonObject.getJSONObject("restaurant");
                        String restname = restaurant.getString("name");
                        double promopercentage = promo.getDouble("promotionPercentage");
                        String description = promo.getString("description");
                        String title = restname + " promotion!";
                        String message = description;
                        int restaurantid = restaurant.getInt("id");
                        Log.i("message",description);
                        showNotification(title, description, restaurantid);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();
    }

    public void retrieveReservationSeat(final String email) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {

            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    System.err.println("**** Calling rest web service");
                    URL url = new URL("http://172.25.103.169:3446/FoodEmblemV1-war/Resources/CustomerReservation/retrieveReservationSeating/" + email);
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
                    if (!jsonString.equals("{}")) {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        sensorid = jsonObject.getJSONObject("reservationSeating").getString("sensorId");
                        String [] majorminor = sensorid.split("_");
                        major = Integer.parseInt(majorminor[0].toString());
                        minor = Integer.parseInt(majorminor[1].toString());
                        Log.i("tablemajor", String.valueOf(major));
                        Log.i("tableminor", String.valueOf(minor));
                        beaconManager = new BeaconManager(getApplicationContext());
                        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                            @Override
                            public void onServiceReady() {
                                //Focus monitoring on customer reservation's seating's beacon
                                beaconManager.startMonitoring(new BeaconRegion(
                                        "monitored region",
                                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                                        major, minor));
                            }
                        });
                        beaconManager.setBackgroundScanPeriod(2000, 0);
                        beaconManager.setMonitoringListener(new BeaconManager.BeaconMonitoringListener() {
                            @Override
                            public void onEnteredRegion(BeaconRegion region, List<Beacon> beacons) {
                                //Able to order
                                Log.i("Region", "Customer entered region");
                                SharedPreferences sp = getSharedPreferences("FoodEmblem", MODE_PRIVATE);
                                sp.edit().putBoolean("AtTable", true).apply();
                                sp.edit().putBoolean("FocusScanTable", true).apply();
                                int rerid = sp.getInt("activereservation",0);
                                updatePax(rerid,1);
                            }

                            @Override
                            public void onExitedRegion(BeaconRegion region) {
                                //If exit for 1 mins, set status to inactive and attable to false
                                Log.i("Beacon", "User has left table");
                                SharedPreferences sp = getSharedPreferences("FoodEmblem", MODE_PRIVATE);
                                sp.edit().putBoolean("AtTable", false).apply();
                                int rerid = sp.getInt("activereservation",0);
                                updatePax(rerid,-1);
                            }
                        });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }.execute();
    }

    public void updatePax(final int reservationid, final  int pax){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    String sensorid = major+"_"+minor;
                    Log.i("Webservice","**** Calling notification web service");
                    URL url = new URL("http://172.25.103.169:3446/FoodEmblemV1-war/Resources/Restaurant/updateSeatingPax/" + reservationid + "/" + pax);
                    Log.i("WSurl",url.toString());
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
        }.execute();
    }
}