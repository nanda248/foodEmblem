package com.example.jiongyi.foodemblem.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.Random;

/**
 * Created by JiongYi on 28/3/2018.
 */

public class FoodEmblemService extends Service{
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    // Random number generator
    private final Random mGenerator = new Random();

    public class LocalBinder extends Binder {
        FoodEmblemService getService() {
            // Return this instance of LocalService so clients can call public methods
            return FoodEmblemService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        SharedPreferences sp = getSharedPreferences("FoodEmblem",MODE_PRIVATE);
        sp.edit().remove("IsOrdering").apply();
        sp.edit().remove("IsOrderingRestId").apply();
        sp.edit().remove("promoreserve").apply();
//        sp.edit().remove("AtTable").apply();
        sp.edit().remove("FocusScanTable").apply();
        stopSelf();
    }
}
