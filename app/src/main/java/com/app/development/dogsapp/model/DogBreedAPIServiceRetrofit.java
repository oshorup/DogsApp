package com.app.development.dogsapp.model;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class DogBreedAPIServiceRetrofit {

    private static final String BASE_URL = "https://raw.githubusercontent.com/";
    private APIinterfaceRetrofit api;

    public DogBreedAPIServiceRetrofit()
    {
        api=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // It's going to convert of  List of DogBreed into a Singles object
                .build().create(APIinterfaceRetrofit.class);
    }

    public Single<List<DogBreed>>getdogBreed()
    {
        return api.getDogBreed();
    }

}
