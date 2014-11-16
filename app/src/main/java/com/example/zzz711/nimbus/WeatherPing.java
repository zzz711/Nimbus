package com.example.zzz711.nimbus;

import android.app.IntentService;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.os.Bundle;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Margo
 * @version 11/15/2014
 * Weather Ping is a service class that runs in the background of Main Activity. It's primary job
 * is to send notifications based on current location data.
 */
public class WeatherPing extends Service implements LocationListener{
    Intent serviceIntent;
    String weatherUrl;
    protected LocationManager locationManager;
    private final Context currContext;
    private String apiKey = ""; //TODO: add, but don't commit key
    private static final long MIN_TIME_BW_UPDATES = 12600000; //  3.5 hours
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; //no min change
    protected Location location;
    double latitude;
    double longitude;

    // constructor for WeatherPing service
    public WeatherPing(){
        // grab the current context
        currContext = getApplicationContext();
    }



    // files necessary for LocationListener; they do nothing.
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    public void onProviderEnabled(String provider) {}
    public void onProviderDisabled(String provider) {}
    public void onLocationChanged(Location location) {}



    @Override
    public ComponentName startService(Intent intent){
        //onHandleIntent(intent);
        serviceIntent = intent;
        onCreate();
        ComponentName cn = new ComponentName(currContext, "WeatherPing");
        return cn;
    }

    @Override
    public void onCreate(){
        onStartCommand(serviceIntent, 0, 0); //start id may not be 0
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(isConnected()){
            getLocation();
            //TODO: something here
        }
        return Service.START_NOT_STICKY;
    }

    public String convertInputToString(InputStream inputStream){
        String result = "";
        try{
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
            String line = "";


            while((line = bufferedReader.readLine()) != null){
                result += line;

            }

            inputStream.close();

        }
        catch (Exception e){
            Log.d("convertInput", e.getLocalizedMessage());
        }
        return result;
    }

    public boolean isConnected(){
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Service.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conMgr.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()){
            return true;
        }
        else{
            return false;
        }
    }

    public Location getLocation() {
        locationManager = (LocationManager) currContext.getSystemService(LOCATION_SERVICE);
        boolean GPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!GPSEnabled) {
            // TODO: what if they don't have GPS or internet?
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES,
                    this);
            Log.d("Network", "Network");
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
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
        }
        return location;
    }

    public void setLatLong(){
        if(location != null){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    public String getJSONData(String url){
        InputStream inputStream = null;
        String result = "";

        try{
            HttpClient httpClient = new DefaultHttpClient();

            HttpResponse httpResponse = httpClient.execute(new HttpGet(weatherUrl));

            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null){
                result = convertInputToString(inputStream);
            }

            else {
                //TODO add handler for when there is no information
            }
        }
        catch (Exception e){
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return  result;
    }

    //TODO add public boolean stopService(Intent name)

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
