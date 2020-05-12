package com.app.development.dogsapp.model;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface APIinterfaceRetrofit {

    @GET("DevTides/DogsApi/master/dogs.json")
    Single<List<DogBreed>> getDogBreed();


}
