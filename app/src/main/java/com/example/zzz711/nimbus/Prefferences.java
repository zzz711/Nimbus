package com.example.zzz711.nimbus;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteAbortException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class Prefferences extends Activity implements AdapterView.OnItemSelectedListener{
    private SQLiteDatabase database;
    private NimbusDB nimbusDB;
    Spinner profiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefferences);
        Context context = getApplicationContext();

        nimbusDB = Singleton.getInstance(context).getNimbusDB();
        database = nimbusDB.getWritableDatabase();

        Cursor cursor = database.rawQuery("Select " + nimbusDB.COLUMN_PROFILE_NAME + " from " + nimbusDB.TABLE_PROFILES, null);
        cursor.moveToFirst();

        List<String> profilesList = new ArrayList<String>();
        profilesList.add(cursor.getString(0));
        while(cursor.moveToNext()){
            Log.d("Profile Name", cursor.getString(0));
            profilesList.add(cursor.getString(0));

        }

        ArrayAdapter<String> profileAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, profilesList);
        profileAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        profiles = (Spinner) findViewById(R.id.profileDD);
        profiles.setAdapter(profileAdapter);

        loadSelectedProfile();
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        database = nimbusDB.getWritableDatabase();
        int dbPos = pos + 1;

        Cursor cursor = database.rawQuery("Select * From " + nimbusDB.TABLE_PROFILES + " Where " + dbPos + " = " + nimbusDB.COLUMN_ID, null );
        cursor.moveToFirst();
        EditText editTextProfile = (EditText) findViewById(R.id.editTextProfile);
        editTextProfile.setText(cursor.getString(2));

        EditText editTextUmbrella = (EditText) findViewById(R.id.editTextUmbrella);
        editTextUmbrella.setText(cursor.getInt(3));

        EditText editTextCoat = (EditText) findViewById(R.id.editTextCoat);
        editTextCoat.setText(cursor.getInt(4));

        EditText editTextSunscreen = (EditText) findViewById(R.id.editTextSunscreen);
        editTextSunscreen.setText(cursor.getString(5));

        EditText editTextSnow = (EditText) findViewById(R.id.editTextSnow);
        editTextSnow.setText(cursor.getInt(6));
    }
    /*
    * method required as part of the spinner. Should load the selected profile.
    * @param an AdapterViewer
     */
    public void onNothingSelected(AdapterView<?> parent) {
        database = nimbusDB.getWritableDatabase();

        Cursor cursor = database.rawQuery("Select * From " + nimbusDB.TABLE_PROFILES + " Where " + nimbusDB.COLUMN_SELECTED + "  = 1;", null);
        EditText editTextProfile = (EditText) findViewById(R.id.editTextProfile);
        editTextProfile.setText(cursor.getString(2));

        EditText editTextUmbrella = (EditText) findViewById(R.id.editTextUmbrella);
        editTextUmbrella.setText(cursor.getInt(3));

        EditText editTextCoat = (EditText) findViewById(R.id.editTextCoat);
        editTextCoat.setText(cursor.getInt(4));

        EditText editTextSunscreen = (EditText) findViewById(R.id.editTextSunscreen);
        editTextSunscreen.setText(cursor.getString(5));

        EditText editTextSnow = (EditText) findViewById(R.id.editTextSnow);
        editTextSnow.setText(cursor.getInt(6));

    }

    public void loadSelectedProfile(){
        Cursor cursor = database.rawQuery("Select * From " + nimbusDB.TABLE_PROFILES + " Where " + nimbusDB.COLUMN_SELECTED + "  = 1;", null);
        cursor.moveToFirst();

        if(cursor.getCount() > 0) {
            String profileName = cursor.getString(1);
            int rain = cursor.getInt(2);
            int coatTemp = cursor.getInt(3);
            String sunscreen = cursor.getString(4);
            int snow = cursor.getInt(5);

            EditText editTextProfile = (EditText) findViewById(R.id.editTextProfile);
            EditText editTextUmbrella = (EditText) findViewById(R.id.editTextUmbrella);
            EditText editTextCoat = (EditText) findViewById(R.id.editTextCoat);
            EditText editTextSunscreen = (EditText) findViewById(R.id.editTextSunscreen);
            EditText editTextSnow = (EditText) findViewById(R.id.editTextSnow);

            editTextProfile.setText(profileName);
            editTextUmbrella.setText(Integer.valueOf(rain).toString());
            editTextCoat.setText(Integer.valueOf(coatTemp).toString());
            editTextSunscreen.setText(sunscreen);
            editTextSnow.setText(Integer.valueOf(snow).toString());
        }

    }

    public void buttonSave(View view) {
        //get values from the edit text and write them to the sqlite database
        EditText editTextProfile = (EditText) findViewById(R.id.editTextProfile);
        Context context = getBaseContext();

        nimbusDB = new NimbusDB(context);
        database = nimbusDB.getWritableDatabase();

        String profile = editTextProfile.getText().toString();
        Cursor cursor;

        Log.d("profile", profile);

        if (profile.equals(null)) {
            finish();
        }

        else {
            try {
                cursor = database.rawQuery("Select * From " + nimbusDB.TABLE_PROFILES + " Where " + nimbusDB.COLUMN_PROFILE_NAME + " = '" + profile + "';", null);
                cursor.moveToFirst();
            }
            catch (SQLiteException e){
                cursor = null;
            }
            EditText umbrellaET = (EditText) findViewById(R.id.editTextUmbrella);
            int umbrella = Integer.parseInt(umbrellaET.getText().toString());

            EditText coatET = (EditText) findViewById(R.id.editTextCoat);
            int coat = Integer.parseInt(coatET.getText().toString());

            CheckBox celsiusBox = (CheckBox) findViewById(R.id.celsiusBox);
            if (celsiusBox.isChecked()){
                coat = coat * 9/5 + 32;
            }

            EditText sunscreenET = (EditText) findViewById(R.id.editTextSunscreen);
            String sunScreen = sunscreenET.getText().toString();

            EditText snowET = (EditText) findViewById(R.id.editTextSnow);
            int snow = Integer.parseInt(snowET.getText().toString());

            if(cursor == null || cursor.getCount() == 0){

                database.execSQL("Insert Into " + nimbusDB.TABLE_PROFILES + " (" + nimbusDB.COLUMN_PROFILE_NAME + ", " + nimbusDB.COLUMN_RAIN_CHANCE + ", " + nimbusDB.COLUMN_COAT_TEMP + ", " + nimbusDB.COLUMN_SUNSCREEN_COND + ", "
                        + nimbusDB.COLUMN_SNOW_CHANCE + "," + nimbusDB.COLUMN_SELECTED +")"
                        + " Values( '" + profile + "', " + umbrella + ", " + coat + ", '" + sunScreen + "', " + snow + ", 1);");
            }
            else{
                database.execSQL("Update " + nimbusDB.TABLE_PROFILES + " Set " +nimbusDB.COLUMN_RAIN_CHANCE + " = " + umbrella +", " + nimbusDB.COLUMN_COAT_TEMP + " = "
                + coat + ", " + nimbusDB.COLUMN_SUNSCREEN_COND + " = '" + sunScreen + "', " + nimbusDB.COLUMN_SNOW_CHANCE + " = " + snow + ", " + nimbusDB.COLUMN_SELECTED + " = 1 " +
                 "Where "+ nimbusDB.COLUMN_PROFILE_NAME + " = '" + profile + "';");
            }

            database.execSQL("Update " + nimbusDB.TABLE_PROFILES + " Set " + nimbusDB.COLUMN_SELECTED  +" = 0 Where " + nimbusDB.COLUMN_SELECTED + " = 1 AND " + nimbusDB.COLUMN_PROFILE_NAME + " != '" + profile + "';");

            finish();
        }
    }
}
