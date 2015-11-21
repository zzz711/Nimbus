package com.example.zzz711.nimbus;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by zzz711 on 11/13/15.
 */
public class JSONParser {
    String weatherUrl = "http://api.worldweatheronline.com/free/v2/weather.ashx?key=";
    private String apiKey = ""; //add, but don't commit key
    Context context;
    private SQLiteDatabase database;
    private NimbusDB nimbusDB;
    int rainPercent = 0;
    int coatTemp = 0;
    String sunCond = "";
    int snowPercent = 0;

    int rainCB = 0;
    int coatCB = 0;
    int sunCB = 0;
    int snowCB = 0;


    /*
    * method to set latitude and longitude variables
     */
    public void onCall(double latitude, double longitude,Context c){
        context = c;
        String url = weatherUrl + apiKey + "&q="+ String.valueOf(latitude) + "," + String.valueOf(longitude) + "&num_of_days=2&tp=3&format=json";

        nimbusDB = Singleton.getInstance(context).getNimbusDB();
        database = nimbusDB.getReadableDatabase();

        readDB();

        new HttpAsyncTask(url, this).execute(url); //put real api in
    }

    /*
    *Method to parse JSON
    * @param a string containing the JSON
     */
    public void setJSON(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject forecast = jsonObject.getJSONArray("weather").getJSONObject(0);

            if(forecast.getInt("chanceofrain") > rainPercent && rainCB == 1){
                buildNotification(0);
            }

            if(forecast.getInt("tempF") < coatTemp && coatCB == 1){
                buildNotification(1);
            }

            if(forecast.getJSONArray("weatherDesc").getJSONObject(0).getString("value").equals(sunCond) && sunCB == 1){
                buildNotification(2);
            }
            if(forecast.getInt("chanceofsnow") > snowPercent && snowCB == 1) {
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
    public void buildNotification(int number){ //create notification
        Notification.Builder nb = new Notification.Builder(context);
        switch(number){
            case 0: //take an umbrella
                Bitmap umbrella = BitmapFactory.decodeResource(context.getResources(), R.drawable.umbrella_icon_large);
                nb.setLargeIcon(umbrella);
                nb.setContentTitle("Umbrella");
                nb.setContentText("Be sure to pack an umbrella!");
                nb.setSmallIcon(R.drawable.umbrella_icon);
                break;
            case 1: //put on a jacket
                Bitmap coat = BitmapFactory.decodeResource(context.getResources(), R.drawable.coat_icon_large);
                nb.setLargeIcon(coat);
                nb.setContentTitle("Jacket");
                nb.setContentText("It's chilly outside, so wear a jacket.");
                nb.setSmallIcon(R.drawable.coat_icon);
                break;
            case 2: //put on sunscreen
                Bitmap sunscreen = BitmapFactory.decodeResource(context.getResources(), R.drawable.sun_icon_large);
                nb.setLargeIcon(sunscreen);
                nb.setContentTitle("Sunscreen");
                nb.setContentText("Put on sunscreen today!");
                nb.setSmallIcon(R.drawable.sun_icon);
                break;
            case 3: //put on a winter coat
                Bitmap snow = BitmapFactory.decodeResource(context.getResources(), R.drawable.snow_icon_large);
                nb.setLargeIcon(snow);
                nb.setContentTitle("Coat");
                nb.setContentText("It's cold outside! Wear a winter coat and maybe some gloves."); //TODO: change message
                nb.setSmallIcon(R.drawable.snow_icon);
                break;
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(number, nb.build());
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
        snowCB = profileCursor.getInt(5);

        rainCB = checkCursor.getInt(1);
        coatCB = checkCursor.getInt(2);
        sunCB = checkCursor.getInt(3);
        snowCB = checkCursor.getInt(4);
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> { //get JSON
        String url;
        JSONParser jsonParser;

        /*
        * constructor to set class variables
         * @param a string contain the web address for the api and an object of the JSONParser class
         */
        public HttpAsyncTask(String address, JSONParser ping){
            this.url = address;
            this.jsonParser = ping;
        }


        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();


            try {

                URL url = new URL(urls.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
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

        @Override
        protected void onPostExecute(String result) {
            this.jsonParser.setJSON(result);
        }
    }


}

