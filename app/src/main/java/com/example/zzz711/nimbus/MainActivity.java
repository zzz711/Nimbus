package com.example.zzz711.nimbus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;


public class MainActivity extends Activity {
    CheckBox checkUmbrella, checkCoat, checkSunscreen, checkSnow;
    boolean umbrellaBool = true;
    boolean coatBool, sunscreenBool, snowBool = false; //booleans to

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkUmbrella = (CheckBox) findViewById(R.id.checkUmbrella);
        checkCoat = (CheckBox) findViewById(R.id.checkCoat);
        checkSunscreen = (CheckBox) findViewById(R.id.checkSunscreen);
        checkSnow = (CheckBox) findViewById(R.id.checkSnow);

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

    }

    public void coatClick(View view){
        if(!coatBool){
            coatBool = true;
        }
        else{
            coatBool = false;
        }
    }

    public void sunscreenClick(View view){
        if(!sunscreenBool){
            sunscreenBool = true;
        }
        else{
            sunscreenBool = false;
        }
    }

    public void snowClick(View view){
        if(!snowBool){
            snowBool = true;
        }
        else{
            snowBool = false;
        }

    }
}
