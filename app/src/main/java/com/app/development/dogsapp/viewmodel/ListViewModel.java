package com.app.development.dogsapp.viewmodel;

import android.app.Application;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.app.development.dogsapp.model.DogBreed;
import com.app.development.dogsapp.model.DogBreedAPIServiceRetrofit;
import com.app.development.dogsapp.model.DogDAORoom;
import com.app.development.dogsapp.model.DogDatabaseRoom;
import com.app.development.dogsapp.util.NotificationHelper;
import com.app.development.dogsapp.util.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ListViewModel extends AndroidViewModel {


    //Mutable liveData is used because we will need to change LiveData in future
    public MutableLiveData<List<DogBreed>> dogs = new MutableLiveData<List<DogBreed>>();
    public MutableLiveData<Boolean> DogError = new MutableLiveData<Boolean>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<Boolean>();


    public ListViewModel(@NonNull Application application) {
        super(application);
    }

    private DogBreedAPIServiceRetrofit dogBreedAPIServiceRetrofit = new DogBreedAPIServiceRetrofit(); // by this line constructor of service class will get activated
    // and our url will be formed and all other basic stuff will be completed

    private AsyncTask<List<DogBreed>, Void, List<DogBreed>> insertTask;
    private AsyncTask<Void, Void, List<DogBreed>> retrieveTask;

    // for saving memory, we are using disposable
    private CompositeDisposable disposable = new CompositeDisposable();

    //for SharedPreferencesHelper instantiation
    private SharedPreferencesHelper PrefHelper = SharedPreferencesHelper.getInstance(getApplication());

    //this is delta time, that is after every 5 minutes, data will be again taken from backend api and Room database's data will be updated
    private long RefreshTime = 5 * 60 * 1000 * 1000 * 1000L; //(it is equal to 5 minutes converted into nano seconds unit, because system'clock works is in ns)


    public void refresh() {
        //when refresh function will be called then we have to take service from backend API
        // and this is here done by calling the below function

        //checkUpdateRefreshTime() function will be called every time(for checking that whether user has changed refresh time or not) whenever user will refresh the List of dogs,
        checkUpdateRefreshTime();

        long updateTime = PrefHelper.getUpdateTime(); // this is the last time we updated the information
        long currentTime = System.nanoTime(); // this is the current time
        if (updateTime != 0 && currentTime - updateTime < RefreshTime) {//  we have to check for updateTime !=0, b/c by default we are returning 0 for the first time
            fetchFromRoomDatabase();
        } else {

            fetchRemoteService();
        }

    }

    private void checkUpdateRefreshTime()
    {
        String newRefreshTime = PrefHelper.getUsergivenCacheTime();
        if(!newRefreshTime.equals("")) // checking, cache time shouldn't be equal to null
        {
            // then again checking cache time should be integer only
            try{
                int newRefreshTimeINT = Integer.parseInt(newRefreshTime);
                RefreshTime=newRefreshTimeINT * 1000 * 1000 * 1000L; // user given cache time converted in nano second and passed to RefreshTime

            }catch (NumberFormatException e){  // handling when the user puts some random values like , he/she puts some string instead of integer
                e.printStackTrace();
            }
        }
    }


    private void fetchRemoteService() {
        loading.setValue(true); // we will have to showing loading as the command for getting data from backend api will be called

        disposable.add(
                dogBreedAPIServiceRetrofit.getdogBreed() // this will return a Singles values o List of DogBreed type data
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<DogBreed>>() {
                            @Override
                            public void onSuccess(List<DogBreed> dogBreeds) {

                                //calling asyncTask for inserting retrieved data(from backend) to Room Database
                                insertTask = new InsertDogsTask();
                                insertTask.execute(dogBreeds);
                                Toast.makeText(getApplication(),"Dogs retrieved from endpoints",Toast.LENGTH_SHORT).show();

                                NotificationHelper.getInstance(getApplication()).creteNotification();
                            }

                            @Override
                            public void onError(Throwable e) {
                                loading.setValue(false);
                                DogError.setValue(true);
                                e.printStackTrace();

                            }
                        })

        );

    }

    private void fetchFromRoomDatabase() {
        loading.setValue(true);
        retrieveTask = new RetrieveDogTask();
        retrieveTask.execute();
    }


    private void dogsRetrieved(List<DogBreed> doglist) {
        dogs.setValue(doglist);
        DogError.setValue(false);
        loading.setValue(false);

    }

    // whenever user pulls down the screen for refreshing the screen we simply call the fetchRemoteService for loading new data
    public void RefreshByPassCahe() {
        fetchRemoteService();
    }

    @Override
    protected void onCleared() {
        // this function will be called whenever system try to clear or try to stop the app , then we will have to clear all the things.. and by
        // using disposable, we are clearing the memory covered by our app
        super.onCleared();
        disposable.clear();

        // similarly doing memory management for InsertDogTask Class
        if (insertTask != null)
        // that is AsyncTask is getting executed ,but user clears the app all of sudden ,
        // then at that time insertTask will not be null and forcefully we have cancel the execution of that background task
        {
            insertTask.cancel(true);
            insertTask = null;
        }

        // similarly doing memory management for RetrieveDogTask Class
        if (retrieveTask != null) {
            retrieveTask.cancel(true);
            retrieveTask = null;
        }
    }

    // creating a class for working as Async Task for storing data to Room Database
    private class InsertDogsTask extends AsyncTask<List<DogBreed>, Void, List<DogBreed>> {
        @Override
        protected List<DogBreed> doInBackground(List<DogBreed>... lists) {
            List<DogBreed> list = lists[0]; // getting first DosgBreed amongst all list of DogBreeds

            //instantiating
            DogDAORoom dogDAO = DogDatabaseRoom.getInstance(getApplication()).dogDAORoom(); //getApplication() is a context. coming from AndroidViewModel

            // first we want a clean table in our database
            dogDAO.deleteAllDogs();

            //now inserting data to database, a fixed way of inserting
            ArrayList<DogBreed> newList = new ArrayList<>(list);  // first converting list to ArrayList
            List<Long> result = dogDAO.insertAll(newList.toArray(new DogBreed[0]));
            // here then above converted ArrayList is first converted to array and then passed as argument to insertAll function
            // now after insertion we will get a list of unique identifier(primary key) which is stored in result list

            //now updating uuid if each DogBreed
            int i = 0;
            while (i < list.size()) {
                list.get(i).uuid = result.get(i).intValue(); // updating uuid with their corresponding values from result list
                ++i;                           //by using .intValue() function we are converting list to intValue
            }
            return list; // now after updating uuid , return the list of lists
            //lists is list of DogBreed classes
            //list is list of parameters inside a single DogBreed class

        }

        // Now Data Insertion at Database is completed, that is our background task of inserting data at Database is now completed
        // now we have to show data in foreground, therefore calling an inbuilt function for shifting from background thread to foreground thread
        @Override
        protected void onPostExecute(List<DogBreed> dogBreeds) {
            // and now this will call the function for updating mutableLiveData
            dogsRetrieved(dogBreeds);
            // and every time we save/update data to room , we also have to update saveUpdateTime
            PrefHelper.saveUpdateTime(System.nanoTime());
        }
    }


    // AsyncTask for Retrieving data from Room Database whenever we need(we will need to retrieve data Room when there is no internet, so our app continue to work)
    class RetrieveDogTask extends AsyncTask<Void, Void, List<DogBreed>> {
        @Override
        protected List<DogBreed> doInBackground(Void... voids) {
            return DogDatabaseRoom.getInstance(getApplication()).dogDAORoom().getAllDogs();
            //calling getAllDogs() function from @DAO and returning the same whatever getAllDogs() returns
        }

        // now foreground thread
        @Override
        protected void onPostExecute(List<DogBreed> dogBreeds) {
            //once this async task return all dogs  ,
            // then calling dogRetrieved() function for add these values to MutableLiveData
            dogsRetrieved(dogBreeds);
            Toast.makeText(getApplication(),"Dogs retrieved from Room",Toast.LENGTH_SHORT).show();
        }
    }

}
