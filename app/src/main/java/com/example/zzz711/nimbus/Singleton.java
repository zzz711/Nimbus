package com.example.zzz711.nimbus;

import android.content.Context;

/**
 * Created by zzz711 on 11/21/15.
 */
//based off of https://gist.github.com/Akayh/5566992
public class Singleton {
    private static Singleton instance = null;

    private NimbusDB nimbusDB;
    private Context context;

    /*
    * constructor to get the context since this class does not have it
    * @param the application context
     */
    private Singleton(Context calledContext){
        context = calledContext;
    }

    /*
    * method to get Singleton instance
    * @param the application
    * @return the instance of Singleton
     */
    public static Singleton getInstance(Context baseContext){
        if(instance == null){
            instance = new Singleton(baseContext);
        }

        return  instance;
    }

    /*
    * method to get the instance of my SQLiteOpenHelper class
    * @return the instance of my SQLiteOpenHelper class
     */
    public NimbusDB getNimbusDB(){
        return this.nimbusDB;
    }

    /*
    * method to set the instance of my SQLiteOpenHelper class
     */
    public void setNimbusDB(){
        nimbusDB = new NimbusDB(context);
    }

}
