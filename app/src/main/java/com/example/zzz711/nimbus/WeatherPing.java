package com.example.zzz711.nimbus;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Margo on 11/15/2014.
 */
public class WeatherPing extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
