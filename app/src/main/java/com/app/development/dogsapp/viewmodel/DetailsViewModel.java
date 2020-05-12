package com.app.development.dogsapp.viewmodel;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.app.development.dogsapp.model.DogBreed;
import com.app.development.dogsapp.model.DogDatabaseRoom;

import java.util.List;

public class DetailsViewModel extends AndroidViewModel {

    public MutableLiveData<DogBreed> dogbreed = new MutableLiveData<DogBreed>();

    private int DogUuid;
    private AsyncTask<Void, Void, DogBreed> retrieveDogTask;

    public DetailsViewModel(@NonNull Application application) {
        super(application);
    }

    public void onRefresh(int DogUid) {
        this.DogUuid = DogUid;

        retrieveDogTask = new retrieveDogDetails();
        retrieveDogTask.execute();

    }

    private void dogDetailsRetrieved(DogBreed dogBreed) {
        dogbreed.setValue(dogBreed);
    }

    //handling memory management event
    @Override
    protected void onCleared() {
        super.onCleared();
        if (retrieveDogTask != null) {
            retrieveDogTask.cancel(true);
            retrieveDogTask = null;
        }
    }

    // retrieving data from Room Database can't be done in main thread, therefor using an async task for working in background thread
    class retrieveDogDetails extends AsyncTask<Void, Void, DogBreed> {
        @Override
        protected DogBreed doInBackground(Void... voids) {
            return DogDatabaseRoom.getInstance(getApplication()).dogDAORoom().getDog(DogUuid);
        }
        @Override
        protected void onPostExecute(DogBreed dogBreed) {
            dogDetailsRetrieved(dogBreed);
        }
    }

}
