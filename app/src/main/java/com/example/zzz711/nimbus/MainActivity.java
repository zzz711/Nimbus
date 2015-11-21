package com.example.zzz711.nimbus;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;


public class MainActivity extends Activity {
    CheckBox checkUmbrella, checkCoat, checkSunscreen, checkSnow;
    int umbrellaBool = 1;
    int coatBool, sunscreenBool, snowBool = 0; //booleans for the state of the checks boxes
    private SQLiteDatabase database;
    private NimbusDB nimbusDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        checkUmbrella = (CheckBox) findViewById(R.id.checkUmbrella);
        checkCoat = (CheckBox) findViewById(R.id.checkCoat);
        checkSunscreen = (CheckBox) findViewById(R.id.checkSunscreen);
        checkSnow = (CheckBox) findViewById(R.id.checkSnow);

        Context context = getApplicationContext();//not which sure context to get
        Singleton.getInstance(context).setNimbusDB();
        nimbusDB = Singleton.getInstance(context).getNimbusDB();

        database = nimbusDB.getWritableDatabase();

        if(database == null){
            nimbusDB.onCreate(database);
            database = nimbusDB.getWritableDatabase();
        }

        DBRead();


        Intent weatherPing = new Intent(context, WeatherPing.class);
        //weatherPing.putExtra()



        context.startService(weatherPing);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    *
    * onClick Method to handle when the umbrella check box is pressed
    * @param an object of the view class
    *
     */
    public void umbrellaClick(View view){
        if(umbrellaBool == 1){
            umbrellaBool = 0;
        }
        else{
            umbrellaBool = 1;
        }

        DBWrite();
    }

    /*
     * onClick Method to handle when the coat check box is pressed
    * @param an object of the view class
    *
     */
    public void coatClick(View view){
        if(coatBool == 0){
            coatBool = 1;
        //    buildNotification(1);
        }
        else {
            coatBool = 0;
        }

        DBWrite();

    }

    /*
    * onClick Method to handle when the umbrella check box is pressed
    * @param an object of the view class
     */
    public void sunscreenClick(View view){
        if(sunscreenBool == 0){
            sunscreenBool = 1;

        }
        else{
            sunscreenBool = 0;
        }

        DBWrite();
    }

    /*
    * onClick Method to handle when the umbrella check box is pressed
    * @param an object of the view class
     */
    public void snowClick(View view){
        if(snowBool == 0){
            snowBool = 1;
        }
        else{
            snowBool = 0;

        }

        DBWrite();

    }

    /*
    * method to get values from the SQLite database
     */
    private void DBRead(){
        Cursor cursor = database.rawQuery("Select * From " + nimbusDB.TABLE_CHECKBOXES + ", " + nimbusDB.TABLE_PROFILES + " Where " + nimbusDB.TABLE_PROFILES + "." + nimbusDB.COLUMN_SELECTED + " = 1;", null);

        cursor.moveToFirst();
        int umbrella = cursor.getInt(0);
        int coat = cursor.getInt(1);
        int sun = cursor.getInt(2);
        int snow = cursor.getInt(3);

        setBoxes(umbrella, coat, sun, snow);

    }

    /*
    *method to write checkbox values to the database
     */
    private void DBWrite() {
        database.execSQL("Update " + nimbusDB.TABLE_CHECKBOXES + " Set " + nimbusDB.COLUMN_UMBRELLA + " = " + umbrellaBool + ", " + nimbusDB.COLUMN_COAT + " = " + coatBool + ", "
                + nimbusDB.COLUMN_SUNSCREEN + " = " + sunscreenBool + ", " + nimbusDB.COLUMN_SNOW + " = " + snowBool);
    }

    /*
    * method to set the checkboxes with the values that were received from the database
    *
     */
    private void setBoxes(int umbrella, int coat, int sun, int snow){
        if(umbrella == 1){
            checkUmbrella.setChecked(true);
        }
        else{
            checkUmbrella.setChecked(false);
        }

        if (coat == 1) {
            checkCoat.setChecked(true);
        }
        else{
            checkCoat.setChecked(false);
        }

        if(sun == 1){
            checkSunscreen.setChecked(true);
        }
        else{
            checkSunscreen.setChecked(false);
        }

        if(snow == 1){
            checkSnow.setChecked(true);
        }
        else{
            checkSnow.setChecked(false);
        }

    }

    public void profilesClick(View view){
        Intent intent = new Intent(getBaseContext(), Prefferences.class);
        startActivity(intent);
        DBRead();
    }

    //method to create notifications. Not used in final app
   /* public void buildNotification(int number){ //final version will not push notification every time
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
    }*/

}

