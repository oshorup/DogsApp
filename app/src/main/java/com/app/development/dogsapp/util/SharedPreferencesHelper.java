package com.app.development.dogsapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class SharedPreferencesHelper {
    //this class will allow us to store and retrieve data(information regarding time) from SharedPreferences

    //this is the caching time
    private static final String PREF_TIME= "pref_time";

    //and this class is also a singleton class
    //b/c at a time there should be only one instantiated variable of this class
    // that is, writing and reading time of Room database shouldn't be equal, and this time is control by this SharedPreferencesHelper class
    private static SharedPreferencesHelper instance;
    private SharedPreferences pref; //this is the object that will allow us to read and write

    private SharedPreferencesHelper(Context context)
    {
        pref= PreferenceManager.getDefaultSharedPreferences(context);
        //here PreferenceManager from AndroidX should be selected b/c we are working on androidX
    }

    public static SharedPreferencesHelper getInstance(Context context)
    {
        if(instance==null){
            instance=new SharedPreferencesHelper(context);
        }
        return instance;
    }

    // function for storing updateTime , this will change every time when we get data from backend api and store to Room Database
    public void saveUpdateTime(Long time)
    {
        pref.edit().putLong(PREF_TIME,time).apply();
    }
    //function for returning retrieveTime
    public Long getUpdateTime()
    {
        return pref.getLong(PREF_TIME,0);
    }


    public String getUsergivenCacheTime()
    {
        return pref.getString("pref_cache_duration",""); // default value is null, when user doesn't give any input
        //getting the value stored inside key ="pref_cache_duration" while taking input from user and returning that value
    }
}
