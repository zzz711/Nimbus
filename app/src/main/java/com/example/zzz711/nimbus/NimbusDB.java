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
    public  final String TABLE_PROFILES = "Profiles";
    public  final String COLUMN_ID = "_id";
    public  final String COLUMN_PROFILE_NAME = "ProfileName";
    public  final String COLUMN_RAIN_CHANCE = "RainPercentage";
    public  final String COLUMN_COAT_TEMP = "CoatTemperature";
    public  final String COLUMN_SUNSCREEN_COND = "SunCondition";
    public  final String COLUMN_SNOW_CHANCE = "SnowPercentage";
    public  final String COLUMN_SELECTED = "Selected";

    public final String TABLE_CHECKBOXES = "CheckBoxes";
    public final String COLUMN_IDCB = "_id";
    public final String COLUMN_UMBRELLA = "Umbrella";
    public final String COLUMN_COAT = "Coat";
    public final String COLUMN_SUNSCREEN = "Sunscreen";
    public final String COLUMN_SNOW = "Snow";
    public final String COLUMN_PROFILE = "FK_ProfileID";

    public static final String DATABASE_NAME = "Nimbus.db";
    public static final int DATABASE_VERSION = 1;

    public  final String PROFILES_CREATE = "Create Table " + TABLE_PROFILES + " ( "  + COLUMN_ID + " integer primary key autoincrement, " + COLUMN_PROFILE_NAME + " text not null, " +
            COLUMN_RAIN_CHANCE + " integer, " + COLUMN_COAT_TEMP + " integer, " + COLUMN_SUNSCREEN_COND + " text, " + COLUMN_SNOW_CHANCE + " integer, " + COLUMN_SELECTED
            + " integer);";

    public final String CHECKBOX_CREATE = "Create Table " + TABLE_CHECKBOXES + " ( " + COLUMN_IDCB + " integer primary key autoincrement, " + COLUMN_UMBRELLA + " integer, " + COLUMN_COAT + " integer, " + COLUMN_SUNSCREEN +
            " integer, " + COLUMN_SNOW + " integer, " + COLUMN_PROFILE + " integer);";

    public  final String DEFAULT_PROFILE = "Insert Into " + TABLE_PROFILES + " ( " + COLUMN_PROFILE_NAME + ", " + COLUMN_RAIN_CHANCE + ", " + COLUMN_COAT_TEMP
            + ", " + COLUMN_SUNSCREEN_COND + ", " + COLUMN_SNOW_CHANCE + ",  " + COLUMN_SELECTED + " )" +
            " Values ( 'Default', 67, 56, 'Mostly Sunny', 57, 1);";

    public final String DEFAULT_BOXES = "Insert Into " + TABLE_CHECKBOXES + " ( " + COLUMN_UMBRELLA + ", " +COLUMN_COAT + ", " + COLUMN_SUNSCREEN  + ", " + COLUMN_SNOW  + ", " + COLUMN_PROFILE +
            ") Values( 1, 0, 0, 0, 1);"; //While I don't like hard coding the default profile ID into the table it is ok for now since the user has no way to delete profiles

    //public final String DATABASE_CREATE = PROFILES_CREATE + " " + CHECKBOX_CREATE;

    public boolean created = false;


    public NimbusDB(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase nimbus){
        nimbus.execSQL(PROFILES_CREATE);
        nimbus.execSQL(CHECKBOX_CREATE);
        nimbus.execSQL(DEFAULT_PROFILE); //create default profile
        nimbus.execSQL(DEFAULT_BOXES);
        created = true;

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(NimbusDB.class.getName(), "Upgrading from " + oldVersion + " to " + newVersion + " and deleting all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILES);
        onCreate(db);
    }


}
