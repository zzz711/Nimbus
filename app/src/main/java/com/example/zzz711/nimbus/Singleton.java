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


    private Singleton(Context calledContext){
        context = calledContext;
    }

    public static Singleton getInstance(Context baseContext){
        if(instance == null){
            instance = new Singleton(baseContext);
        }

        return  instance;
    }

    public NimbusDB getNimbusDB(){
        return this.nimbusDB;
    }

    public void setNimbusDB(){
        nimbusDB = new NimbusDB(context);
    }

}
