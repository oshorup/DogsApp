package com.app.development.dogsapp.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

// now we have to make DogBreed as an entity(table) for storing data
@Entity
public class DogBreed {

    // ColumnInfo is used for making columns in table

    @ColumnInfo(name = "dog_id")
    @SerializedName("id")
    public String BreedId;

    @ColumnInfo(name = "dog_breed")
    @SerializedName("name")
    public String dogBreed;

    @ColumnInfo(name = "dog_lifespan")
    @SerializedName("life_span")
    public String lifeSpan;

    @ColumnInfo(name = "breed_group")
    @SerializedName("breed_group")
    public String BreedGroup;

    @ColumnInfo(name = "bred_for")
    @SerializedName("bred_for")
    public String BreedFor;

    @SerializedName("temperament")
    public String Temperament;

    @ColumnInfo(name = "dog_image_url")
    @SerializedName("url")
    public String ImageUrl;

    @PrimaryKey(autoGenerate = true) // it will generate unique primary key for each row of table
    public int uuid;

    public DogBreed(String breedId, String dogBreed, String lifeSpan, String breedGroup, String breedFor, String temperament, String imageUrl) {
        this.BreedId = breedId;
        this.dogBreed = dogBreed;
        this.lifeSpan = lifeSpan;
        this.BreedGroup = breedGroup;
        this.BreedFor = breedFor;
        this.Temperament = temperament;
        this.ImageUrl = imageUrl;

    }
    public DogBreed()
    {

    }
}
