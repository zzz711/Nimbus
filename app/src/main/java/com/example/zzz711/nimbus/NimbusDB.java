package com.example.zzz711.nimbus;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.security.PublicKey;

/**
 * Created by zzz711 on 11/3/15.
 */
public class NimbusDB extends SQLiteOpenHelper {
    public static final String TABLE_PROFILES = "Profiles";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PROFILE_NAME = "Profile Name";
    public static final String COLUMN_RAIN_CHANCE = "Rain Percentage";
    public static final String COLUMN_COAT_TEMP = "Coat Temperature";
    public static final String COLUMN_SUNSCREEN_COND = "Sun Condition";
    public static final String COLUMN_SNOW_CHANCE = "Snow Percentage";

    public static final String DATABASE_NAME = "Nimbus.db";
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_CREATE = "create table " + TABLE_PROFILES + " ( "  + COLUMN_ID + " integer primary key autoincrement, " + COLUMN_PROFILE_NAME + " text not null " +
            COLUMN_RAIN_CHANCE + " integer " + COLUMN_COAT_TEMP + " integer " + COLUMN_SUNSCREEN_COND + " text " + COLUMN_SNOW_CHANCE + " intetger);";

    public static final String DEFAULT_PROFILE = "Insert into  " + TABLE_PROFILES + " ( " + COLUMN_PROFILE_NAME + ", " + COLUMN_RAIN_CHANCE + ", " + COLUMN_COAT_TEMP + ", " + COLUMN_SUNSCREEN_COND + ", " + COLUMN_SNOW_CHANCE + ") " +
            " Values ( 'Default', 67, 56, 'Mostly Sunny', 57);";

    public boolean created = false;


    public NimbusDB(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase nimbus){
        nimbus.execSQL(DATABASE_CREATE);
        nimbus.execSQL(DEFAULT_PROFILE); //create default profile
        created = true;

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(NimbusDB.class.getName(), "Upgrading from " + oldVersion + " to " + newVersion + " and deleting all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILES);
        onCreate(db);
    }
}
