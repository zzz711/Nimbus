package com.example.zzz711.nimbus;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.security.auth.callback.Callback;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

/**
 * @author Margo
 * @version 11/15/2014
 * Weather Ping is a service class that runs in the background of Main Activity. It's primary job
 * is to send notifications based on current location data.
 */
public class WeatherPing extends Service implements LocationListener{
    Intent serviceIntent;
    //String weatherUrl = "api.worldweatheronline.com/free/v2/weather.ashx?q=45219&format=json&num_of_days=1&date=2014-11-17&key=9cbd89fade0170bb8102ed4296968";
    protected LocationManager locationManager;
    private final Context currContext = this.getBaseContext();
    private String apiKey = ""; //TODO: add, but don't commit key
    private static final long MIN_TIME_BW_UPDATES = 12600000; //  3.5 hours
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; //no min change
    protected Location location;
    double latitude;
    double longitude;
    Context context;

    // constructor for WeatherPing service
    public WeatherPing(){
        // grab the current context
        //currContext = getApplicationContext();
    }



    // files necessary for LocationListener; they do nothing.
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    public void onProviderEnabled(String provider) {}
    public void onProviderDisabled(String provider) {}
    public void onLocationChanged(Location location) {}



    @Override
    public ComponentName startService(Intent intent){
        //onHandleIntent(intent);


        if(currContext == null){
            Log.d("@@@", " Intent is not the problem");
        }

        serviceIntent = intent;
        onStartCommand(intent, 0, 0);
        //ComponentName cn = new ComponentName(currContext, "WeatherPing");
        ComponentName cn = new ComponentName(context, "WeatherPing");
        return cn;
    }

    @Override
    public void onCreate(){
        //onStartCommand(serviceIntent, 0, 0); //start id may not be 0
        context = getApplicationContext();
        Intent weatherPing = new Intent(context, WeatherPing.class);
        startService(weatherPing);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(isConnected()){
            getLocation();
            new HttpAsyncTask().execute("http://api.worldweatheronline.com/free/v2/weather.ashx?q=45219&format=json&num_of_days=1&date=today");
            //GET();
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
//        Log.d("::", currContext.toString());
        //locationManager = (LocationManager) currContext.getSystemService(LOCATION_SERVICE);
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

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

            Log.d(String.valueOf(latitude), String.valueOf(longitude) );
        }
    }

    public String GET(String url){
        try {
        HttpClient httpClient = new DefaultHttpClient();
            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            BasicClientCookie netscapeCookie = new BasicClientCookie("name", "value");
            netscapeCookie.setVersion(0);
            netscapeCookie.setDomain(".mycompany.com");
            netscapeCookie.setPath("/");


            HttpResponse httpResponse = httpClient.execute(new HttpGet(url));

      InputStream inputStream = httpResponse.getEntity().getContent();

            convertInputStreamToString(inputStream);


           // JSONObject obj = new JSONObject("api.worldweatheronline.com/free/v2/weather.ashx?q=45219&format=json&num_of_days=1&date=today&key=9cbd89fade0170bb8102ed4296968");

           // String tempF = obj.getString("FeelsLikeF");
           // Log.d("", tempF);


       /* RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint("api.worldweatheronline.com/")
                .build();

        OnlineWeatherRetriever onlineWeatherRetriever = adapter.create(OnlineWeatherRetriever.class);

        onlineWeatherRetriever.getWeather(location.toString(), 1, "9cbd89fade0170bb8102ed4296968");

        String result = "";
        try {
        URL url = new URL("http://api.worldweatheronline.com/free/v2/weather.ashx?q=45219&format=json&num_of_days=1&date=2014-11-17&key=9cbd89fade0170bb8102ed4296968");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();


            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            result =  convertInputToString(inputStream);

           HttpClient httpClient = new DefaultHttpClient();

            HttpResponse httpResponse = httpClient.execute(new HttpGet(url));

            inputStream = httpResponse.getEntity().getContent();


            if(inputStream != null){
                result = convertInputToString(inputStream);
                Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
            }

            else {
                Log.d(":", "Input is null");
                //TODO add handler for when there is no information
            }
            urlConnection.disconnect();

        }
        catch (Exception e){
            Log.d("InputStream", e.getLocalizedMessage());
        }


        return  result;*/
        }
        catch (IOException e){
        }

        return " ";
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";

        Log.d("!!", result);
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    //TODO add public boolean stopService(Intent name)

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void buildNotification(int number){
        Notification.Builder nb = new Notification.Builder(this);
        switch(number){
            case 0: //take an umbrella
                Bitmap umbrella = BitmapFactory.decodeResource(getResources(), R.drawable.umbrella_icon_large);
                nb.setLargeIcon(umbrella);
                nb.setContentTitle("Umbrella");
                nb.setContentText("Be sure to pack an umbrella!");
                nb.setSmallIcon(R.drawable.umbrella_icon);
                break;
            case 1: //put on a jacket
                Bitmap coat = BitmapFactory.decodeResource(getResources(), R.drawable.coat_icon_large);
                nb.setLargeIcon(coat);
                nb.setContentTitle("Jacket");
                nb.setContentText("It's chilly outside, so wear a jacket.");
                nb.setSmallIcon(R.drawable.coat_icon);
                break;
            case 2: //put on sunscreen
                Bitmap sunscreen = BitmapFactory.decodeResource(getResources(), R.drawable.sun_icon_large);
                nb.setLargeIcon(sunscreen);
                nb.setContentTitle("Sunscreen");
                nb.setContentText("Put on sunscreen today!");
                nb.setSmallIcon(R.drawable.sun_icon);
                break;
            case 3: //put on a winter coat
                Bitmap snow = BitmapFactory.decodeResource(getResources(), R.drawable.snow_icon_large);
                nb.setLargeIcon(snow);
                nb.setContentTitle("Coat");
                nb.setContentText("It's cold outside! Wear a winter coat and maybe some gloves.");
                nb.setSmallIcon(R.drawable.snow_icon);
                break;
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(number, nb.build());
    }


    private class HttpAsyncTask extends AsyncTask <String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
           // Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
            try {
                JSONObject obj = new JSONObject(result);

                //obj.getString()
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
