package com.app.development.dogsapp.model;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Entity;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {DogBreed.class},version = 1)
public abstract class DogDatabaseRoom extends RoomDatabase {

    private static DogDatabaseRoom instance;

    public static DogDatabaseRoom getInstance(Context context) {
        if (instance == null) {
            // instantiating
            instance=Room.databaseBuilder(context.getApplicationContext(),DogDatabaseRoom.class,"dogdatabase").build();
        }

        return instance;
    }
    public abstract DogDAORoom dogDAORoom(); // for getting access to the function of DogDAORoom interface
}

