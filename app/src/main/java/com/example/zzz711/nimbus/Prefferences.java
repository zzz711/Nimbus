package com.example.zzz711.nimbus;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class Prefferences extends Activity implements AdapterView.OnItemSelectedListener{
    private SQLiteDatabase database;
    private NimbusDB nimbusDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefferences);
        Context context = getBaseContext();

        nimbusDB = new NimbusDB(context);
        database = nimbusDB.getWritableDatabase();

        Cursor cursor = database.rawQuery("Select " + nimbusDB.COLUMN_PROFILE_NAME + " from " + nimbusDB.TABLE_PROFILES, null);

        List<String> profilesList = new ArrayList<String>();

        for(int i = 0; i < cursor.getColumnCount(); i++){
            profilesList.add(cursor.getString(i+1));
        }

        ArrayAdapter<String> profileAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, profilesList);
        profileAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        Spinner profiles = (Spinner) findViewById(R.id.profileDD);
        profiles.setAdapter(profileAdapter);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Context context = getBaseContext();

        nimbusDB = new NimbusDB(context);
        database = nimbusDB.getWritableDatabase();
        int dbPos = pos + 1;

        Cursor cursor = database.rawQuery("Select * From " + nimbusDB.TABLE_PROFILES + " Where " + dbPos + " = " + nimbusDB.COLUMN_ID, null );
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

    public void onNothingSelected(AdapterView<?> parent) {
        // may need to put this in onCreate
        Context context = getBaseContext();

        nimbusDB = new NimbusDB(context);
        database = nimbusDB.getWritableDatabase();

        Cursor cursor = database.rawQuery("Select * From " + nimbusDB.TABLE_PROFILES + " Where " + nimbusDB.COLUMN_PROFILE_NAME + " 'Default", null);
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

    public void buttonSave(View view) {
        //get values from the edit text and write them to the sqlite database
        EditText editTextProfile = (EditText) findViewById(R.id.editTextProfile);
        Context context = getBaseContext();

        nimbusDB = new NimbusDB(context);
        database = nimbusDB.getWritableDatabase();

        String profile = editTextProfile.getText().toString();

        if (profile.equals(null) || profile.equals("Default")) {
            finish();
        }

        else {
            Cursor cursor = database.rawQuery("Select * From " + nimbusDB.TABLE_PROFILES + " Where " + nimbusDB.COLUMN_PROFILE_NAME + " = " + profile + ";", null);
            EditText umbrellaET = (EditText) findViewById(R.id.editTextUmbrella);
            int umbrella = Integer.parseInt(umbrellaET.toString());

            int coat = Integer.parseInt(findViewById(R.id.editTextCoat).toString());

            String sunScreen = findViewById(R.id.editTextSunscreen).toString();

            int snow = Integer.parseInt(findViewById(R.id.editTextSnow).toString());


            if(cursor == null){ //may not be the correct


                database.execSQL("Insert Into " + nimbusDB.TABLE_PROFILES + " Values( " + umbrella + ", " + coat + ", " + sunScreen + ", " + snow + ", 1);");
            }
            else{
                database.execSQL("Update " + nimbusDB.TABLE_PROFILES + "Set " +nimbusDB.COLUMN_RAIN_CHANCE + " = " + umbrella +", " + nimbusDB.COLUMN_COAT_TEMP + " = "
                + coat + ", " + nimbusDB.COLUMN_SUNSCREEN_COND + " = " + sunScreen + ", " + nimbusDB.COLUMN_SNOW_CHANCE + " = " + snow + ", " + nimbusDB.COLUMN_SELECTED + " = 1);");
            }

            database.execSQL("Update " + nimbusDB.TABLE_PROFILES + "Set " + nimbusDB.COLUMN_SELECTED  +" = 0 Where " + nimbusDB.COLUMN_SELECTED + " = 1 AND " + nimbusDB.COLUMN_PROFILE_NAME + " != " + profile + ";");

            finish();
        }
    }
}