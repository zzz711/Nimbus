package com.example.zzz711.nimbus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;


public class MainActivity extends Activity {
    CheckBox checkUmbrella, checkCoat, checkSunscreen, checkSnow;
    boolean umbrellaBool = true;
    boolean coatBool, sunscreenBool, snowBool = false; //booleans for the state of the checks boxes
    SharedPreferences.Editor editor;
    public static final String PREFERENCES = "Pref";
    public static final String umbrellaKey = "umbrellaKey";
    public static final String coatKey = "coatKey";
    public static final String sunscreenKey = "sunscreenKey";
    public static final String snowKey = "snowKey";
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);



        checkUmbrella = (CheckBox) findViewById(R.id.checkUmbrella);
        checkCoat = (CheckBox) findViewById(R.id.checkCoat);
        checkSunscreen = (CheckBox) findViewById(R.id.checkSunscreen);
        checkSnow = (CheckBox) findViewById(R.id.checkSnow);



        if (!sharedPref.contains(PREFERENCES)){
            editor = sharedPref.edit();
            editor.putBoolean(umbrellaKey, umbrellaBool);
            editor.putBoolean(coatKey, coatBool);
            editor.putBoolean(sunscreenKey, sunscreenBool);
            editor.putBoolean(snowKey, snowBool);

            editor.apply();
        }

        Context context = getApplicationContext();//not which context to get

        Intent weatherPing = new Intent(context, WeatherPing.class);
        //weatherPing.putExtra()
      //  context.startService(weatherPing);
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


    //TODO save preferences
    public void umbrellaClick(View view){
        if(umbrellaBool){
            umbrellaBool = false;
        }
        else{
            umbrellaBool = true;
        }

        SharedPreferences.Editor editor1 = sharedPref.edit();
        editor1.putBoolean(umbrellaKey, umbrellaBool);
        editor1.apply();
    }

    public void coatClick(View view){
        if(!coatBool){
            coatBool = true;
        }
        else{
            coatBool = false;
        }
        SharedPreferences.Editor editor1 = sharedPref.edit();
        editor1.putBoolean(coatKey, coatBool);
        editor1.apply();
    }

    public void sunscreenClick(View view){
        if(!sunscreenBool){
            sunscreenBool = true;
        }
        else{
            sunscreenBool = false;
        }
        SharedPreferences.Editor editor1 = sharedPref.edit();
        editor1.putBoolean(sunscreenKey, sunscreenBool);
        editor1.apply();
    }

    public void snowClick(View view){
        if(!snowBool){
            snowBool = true;
        }
        else{
            snowBool = false;
        }
        SharedPreferences.Editor editor1 = sharedPref.edit();
        editor1.putBoolean(snowKey, snowBool);
        editor1.apply();
    }
}
