package com.app.development.dogsapp.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DogDAORoom {

    @Insert
    List<Long> insertAll(DogBreed... dogs);
    // It takes a list of DogBreed and insertAll DogBreed into database
    // and returns a list of primary keys for that table where list of DogBreed is stored
    // i.e., list of uuid is returned by this function

    @Query("SELECT * FROM dogbreed")
    List<DogBreed> getAllDogs();   // it will return list of all DogBreed

    @Query("SELECT * FROM dogbreed WHERE uuid=:dogId")
    DogBreed getDog(int dogId);
    // it will return a particular DogBreed whose uuid is equal to passed value : dogId
    // and by using the DogBreed we can get the details inside that DogBreed and the we will display details on Details screen

    @Query("DELETE FROM dogbreed") // it will basically delete the whole table named dogbreed
    void deleteAllDogs();

}
