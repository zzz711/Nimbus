package com.example.zzz711.nimbus;

import android.app.IntentService;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Margo on 11/15/2014.
 */
public class WeatherPing extends Service {
    Intent serviceIntent;
    String weatherUrl;

    //TODO add public boolean stopService(Intent name)

    private String apiKey = "";
    /*public WeatherPing(){
        super("WeatherPing");
    }

    @Override
    protected abstract void onHandleIntent(Intent intent){}*/

    @Override
  /*  public ComponentName startService(Intent intent){
        //need return statement
        //onHandleIntent(intent);
        serviceIntent = intent;
        onCreate();

    }*/

    //@Override
    public void onCreate(){
        onStartCommand(serviceIntent, 0, 0); //start id may not be 0

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
        if(isConnected()){

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

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
