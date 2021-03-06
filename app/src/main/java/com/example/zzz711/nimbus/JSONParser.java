package com.example.zzz711.nimbus;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.text.DateFormat;

import java.util.Calendar;
import java.util.Date;

import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zzz711 on 11/13/15.
 */
class JSONParser {
    String weatherUrl = "http://api.worldweatheronline.com/free/v2/weather.ashx?key=";
    private String apiKey = "9cbd89fade0170bb8102ed4296968"; //add, but don't commit key
    private Context context;
    private SQLiteDatabase database;
    private NimbusDB nimbusDB;
    private int rainPercent = 0;
    private int coatTemp = 0;
    private String sunCond = "";
    private int snowPercent = 0;

    private int rainCB = 0;
    private int coatCB = 0;
    private int sunCB = 0;
    private int snowCB = 0;

     JSONParser(){
        nimbusDB = Singleton.getInstance(context).getNimbusDB();
        database = nimbusDB.getReadableDatabase();


    }

    /*
    * method to set latitude and longitude variables
     */
    private void onCall(double latitude, double longitude,Context c){
        context = c;
        String url = weatherUrl + apiKey + "&q="+ String.valueOf(latitude) + "," + String.valueOf(longitude) + "&num_of_days=2&tp=3&format=json";
        Log.d("URL: ", url);

        readDB();

        new HttpAsyncTask(url, this).execute(url); //put real api in
       // run(url);
    }

    /*
    *Method to parse JSON
    * @param a string containing the JSON
     */
    private void setJSON(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            String currHour = getTime(); // use currHour to get closest

            JSONArray weather = jsonObject.getJSONObject("data").getJSONArray("weather").getJSONObject(0).getJSONArray("hourly");
            JSONObject forecast = null;

            Log.d("rainCB:", String.valueOf(coatCB));

            for(int i = 0; i < weather.length(); i++){
                forecast = weather.getJSONObject(i);

                if(forecast.getString("time").equals(currHour) || forecast.getString("time").equals(String.valueOf(Integer.valueOf(currHour) + 100)) ){
                    break;
                }

                else if(forecast.getString("time").equals(String.valueOf(Integer.valueOf(currHour) + 200))){ //no real need for an else if except for readablity
                    break;
                }

            }
            //Log.d("out", forecast.toString());



            if(forecast.getInt("chanceofrain") >= rainPercent && rainCB == 1){

                buildNotification(0);
            }

            if(forecast.getInt("tempF") <= coatTemp && coatCB == 1){
                buildNotification(1);
            }

            if(forecast.getJSONArray("weatherDesc").getJSONObject(0).getString("value").equals(sunCond) && sunCB == 1){
                buildNotification(2);
            }
            if(forecast.getInt("chanceofsnow") >= snowPercent && snowCB == 1) {
                buildNotification(3);
            }
        }
        catch (JSONException e){
            e.printStackTrace();

        }

    }

    /*
    *  method to create and push notifications to the user
    *  @param an integer indicating what notification to send
     */
     private void buildNotification(int number){ //create notification
         NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
         if(Build.VERSION.SDK_INT >= 26){
             switch (number) {
                 case 0: //take an umbrella
                     Notification.Builder nbRain = new Notification.Builder(context.getApplicationContext(), "Nimbus_Rain");

                     Bitmap umbrella = BitmapFactory.decodeResource(context.getResources(), R.drawable.umbrella_icon_large);
                     nbRain.setLargeIcon(umbrella);
                     nbRain.setContentTitle("Rain!");
                     nbRain.setContentText("Rain is near! Be sure to pack an umbrella!");
                     nbRain.setSmallIcon(R.drawable.umbrela_icon_borderless);

                     notificationManager.notify(number, nbRain.build());
                     break;

                 case 1: //put on a jacket
                     Notification.Builder nbTemp = new Notification.Builder(context.getApplicationContext(), "Nimbus_Temp");
                     Bitmap coat = BitmapFactory.decodeResource(context.getResources(), R.drawable.coat_icon_large);
                     nbTemp.setLargeIcon(coat);
                     nbTemp.setContentTitle("Jacket");
                     nbTemp.setContentText("It's chilly outside, so wear a jacket.");
                     nbTemp.setSmallIcon(R.drawable.coat_icon_boarderless);

                     notificationManager.notify(number, nbTemp.build());
                     break;
                 case 2: //put on sunscreen
                     Notification.Builder nbSun = new Notification.Builder(context.getApplicationContext(), "Nimbus_Sun");
                     Bitmap sunscreen = BitmapFactory.decodeResource(context.getResources(), R.drawable.sun_icon_large);
                     nbSun.setLargeIcon(sunscreen);
                     nbSun.setContentTitle("Sunscreen");
                     nbSun.setContentText("Put on sunscreen today!");
                     nbSun.setSmallIcon(R.drawable.sun_icon_borderless);

                     notificationManager.notify(number, nbSun.build());
                     break;

                 case 3: //put on a winter coat
                     Notification.Builder nbSnow = new Notification.Builder(context.getApplicationContext(), "Nimbus_Snow");
                     Bitmap snow = BitmapFactory.decodeResource(context.getResources(), R.drawable.snow_icon_large);
                     nbSnow.setLargeIcon(snow);
                     nbSnow.setContentTitle("Snow");
                     nbSnow.setContentText("It's snowing outside! Wear a winter coat and maybe some gloves."); //TODO: change message
                     nbSnow.setSmallIcon(R.drawable.snow_icon_boarderless);

                     notificationManager.notify(number, nbSnow.build());
                     break;
             }
         }
         else{
             Notification.Builder nb = new Notification.Builder(context); //depricated in O (26)
             switch (number) {
                 case 0: //take an umbrella
                     Bitmap umbrella = BitmapFactory.decodeResource(context.getResources(), R.drawable.umbrella_icon_large);
                     nb.setLargeIcon(umbrella);
                     nb.setContentTitle("Rain!");
                     nb.setContentText("Rain is near! Be sure to pack an umbrella!");
                     break;
                 case 1: //put on a jacket
                     Bitmap coat = BitmapFactory.decodeResource(context.getResources(), R.drawable.coat_icon_large);
                     nb.setLargeIcon(coat);
                     nb.setContentTitle("Jacket");
                     nb.setContentText("It's chilly outside, so wear a jacket.");
                     nb.setSmallIcon(R.drawable.coat_icon_boarderless);
                     break;
                 case 2: //put on sunscreen
                     Bitmap sunscreen = BitmapFactory.decodeResource(context.getResources(), R.drawable.sun_icon_large);
                     nb.setLargeIcon(sunscreen);
                     nb.setContentTitle("Sunscreen");
                     nb.setContentText("Put on sunscreen today!");
                     nb.setSmallIcon(R.drawable.sun_icon_borderless);
                     break;
                 case 3: //put on a winter coat
                     Bitmap snow = BitmapFactory.decodeResource(context.getResources(), R.drawable.snow_icon_large);
                     nb.setLargeIcon(snow);
                     nb.setContentTitle("Snow");
                     nb.setContentText("It's snowing outside! Wear a winter coat and maybe some gloves."); //TODO: change message
                     nb.setSmallIcon(R.drawable.snow_icon_boarderless);
                     break;
             }

             notificationManager.notify(number, nb.build());
         }

    }

    /*
    * method to read from the database
     */
    private void readDB() {
        Cursor profileCursor = database.rawQuery("Select * From " + nimbusDB.TABLE_PROFILES + " Where " + nimbusDB.COLUMN_SELECTED + " = 1;", null);
        Cursor checkCursor = database.rawQuery("Select * From " + nimbusDB.TABLE_CHECKBOXES + ", " + nimbusDB.TABLE_PROFILES + " Where " + nimbusDB.COLUMN_PROFILE + " = " + nimbusDB.TABLE_CHECKBOXES + "." + nimbusDB.COLUMN_ID + " and " + nimbusDB.COLUMN_SELECTED + " = 1;", null); //may need to alter

        profileCursor.moveToFirst();
        checkCursor.moveToFirst();

        rainPercent = profileCursor.getInt(2);
        coatTemp = profileCursor.getInt(3);
        sunCond = profileCursor.getString(4);
        snowPercent = profileCursor.getInt(5);

        rainCB = checkCursor.getInt(1);
        coatCB = checkCursor.getInt(2);
        sunCB = checkCursor.getInt(3);
        snowCB = checkCursor.getInt(4);

        profileCursor.close();
        checkCursor.close();
    }


    private String getTime(){
        Calendar calendar = Calendar.getInstance();
        String tzInfo = calendar.getTimeZone().toString();
        String timeZone = tzInfo.substring(tzInfo.indexOf("id=") + 4 , tzInfo.indexOf(",mRaw") -1);
        DateFormat dateFormat = DateFormat.getTimeInstance();
        dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        String time = dateFormat.format(new Date());

        String hour = time.subSequence(0, time.indexOf(":")).toString();

        if(time.contains("PM")){
            int hourInt = Integer.valueOf(hour) + 12;
            hour = String.valueOf(hourInt) + "00";
        }
        else{
            hour = hour + "00";
        }
        Log.d("YZ", timeZone);

        return hour;
    }

    /*
    * Async class that makes http request
     */
    private class HttpAsyncTask extends AsyncTask<String, Void, String> { //get JSON
        String url;
        JSONParser jsonParser;
        private final OkHttpClient client = new OkHttpClient();

        /*
        * constructor to set class variables
         * @param a string contain the web address for the api and an object of the JSONParser class
         */
        private HttpAsyncTask(String address, JSONParser ping){
            this.url = address;
            this.jsonParser = ping;
        }


        /*
        * method that makes http request. override of AsyncTask class method
        * @param a String with the web url
         */
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            String res = "";
            String newUrl = urls[0];

            try {

//                Request request = new Request.Builder().url(newUrl).build();
//
//                Response response = client.newCall(request).execute();
//                res = response.body().toString();
                URL url = new URL(newUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setUseCaches(false);
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                urlConnection.disconnect();
            }
            catch (Exception e){
                e.printStackTrace();
            }

            return  result.toString();

        }

        /*
        * method that executes once doInBackGround has finished.
        * Makes call back the JSONParser class.
        * @param a String containing the result of doInBackground
         */
        @Override
        protected void onPostExecute(String result) {
            this.jsonParser.setJSON(result);
        }
    }


}

