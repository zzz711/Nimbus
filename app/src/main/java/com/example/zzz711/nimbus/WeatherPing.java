package com.example.zzz711.nimbus;


import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;


import android.os.Bundle;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * @author Margo
 * @version 1
 * Weather Ping is a service class that runs in the background of Main Activity. It's primary job
 * is to send notifications based on current location data.
 */
public class WeatherPing extends Service implements LocationListener {
    Intent serviceIntent;
    protected LocationManager locationManager;
    private final Context currContext;
    private static final long MIN_TIME_BW_UPDATES = 12600000; //  3.5 hours
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10000; //10000 meter change
    protected Location location;
    double latitude;
    double longitude;
    Context context;
    JSONParser parser;



    // constructor for WeatherPing service
    public WeatherPing(Context context) {
        // grab the current context
        //currContext = getApplicationContext();
        this.currContext = context;
       // getLocation();

        //todo: initialize JSONPARSER object
        parser = new JSONParser();

        //getLocation();
    }


    // files necessary for LocationListener; they do nothing.
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onProviderDisabled(String provider) {
    }



    /*
    * method to start the location listener service
    * @param an Intent from the class where the class was called from
     */
    @Override
    public ComponentName startService(Intent intent) {
        //onHandleIntent(intent);


        if (currContext == null) {
           // Log.d("@@@", " Intent is not the problem");
        }

        serviceIntent = intent;
        onStartCommand(intent, 0, 0);
        ComponentName cn = new ComponentName(currContext, "WeatherPing");
        //ComponentName cn = new ComponentName(context, "WeatherPing");
        return cn;
    }

    /*
    * override of default on create method
    *
     */
//    @Override
//    public void onCreate() {
//        //onStartCommand(serviceIntent, 0, 0); //start id may not be 0
//        context = getApplicationContext();
//        Intent weatherPing = new Intent(context, WeatherPing.class);
//        startService(weatherPing);
//        parser = new JSONParser();
//        parser.onCall(latitude, longitude, context);
//
//
//    }
//
//    /*
//    * override of default onStartCommand
//    * @params an Intent, an integer to represent flags, an integer for the startID
//     */
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (isConnected()) {
//            getLocation();
//            //GET();
//        }
//        return Service.START_NOT_STICKY;
//    }



    /*
    *method to check if there is an internet connection
    * @return true, if there is a connection. False otherwise
     */
    public boolean isConnected() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Service.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    /*
    * override of default onLocationChanged
     * @param an object of the location class
     */
    @Override
    public void onLocationChanged(Location location) {
        getLocation();
       // parser.onCall(latitude, longitude, currContext);
        parser.onCall(latitude, longitude, currContext);
    }


    /*
    * method to get the user's location
    * @return an object of the the location class containing the user's present location
    *
     */
    public Location getLocation() {
//        Log.d("::", currContext.toString());
        locationManager = (LocationManager) currContext.getSystemService(LOCATION_SERVICE);
       // locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        boolean GPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!GPSEnabled) {
            try {
                Criteria criteria = new Criteria();
                String provider = locationManager.getBestProvider(criteria, false);
                location = locationManager.getLastKnownLocation(provider);
            }
            catch (SecurityException e){
                Toast.makeText(getApplicationContext(), "In order for this app to function you must give it location access", Toast.LENGTH_LONG).show();
            }

        } else {
            try {
                //not working because network provider doesn't exist. Try GPS
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES,
                        this);
                Log.d("Network", "Network");
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    setLatLong();
                }
                if (location == null) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            this);
                    Log.d("GPS Enabled", "GPS Enabled");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        setLatLong();
                    }
                }
            } catch (SecurityException e) {
                Toast.makeText(getApplicationContext(), "In order for this app to function you must give it location access", Toast.LENGTH_LONG).show(); //too long?
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return location;
    }

    /*
    *method to set the class variables for latitude and longitude
     */
    public void setLatLong() {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            Log.d(String.valueOf(latitude), String.valueOf(longitude));
        }
    }

    //TODO add public boolean stopService(Intent name)

    /*
    * override of default onBind
    * @param an intent
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



}


