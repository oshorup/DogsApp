package com.app.development.dogsapp.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.development.dogsapp.R;
import com.app.development.dogsapp.viewmodel.ListViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Dogs_list_fragments extends Fragment {

    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swiperefresh;
    // Important variables/references
    private ListViewModel listViewModel;
    private AdapterList adapterList = new AdapterList(new ArrayList<>());

    @BindView(R.id.recyclerView_Doglist)
    RecyclerView recyclerViewDoglist;
    @BindView(R.id.errormsg)
    TextView errormsg;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;

    public Dogs_list_fragments() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dogs_list_fragment, container, false);
        ButterKnife.bind(this, view);

        //setting flag for adding menu to this screen. It is important, otherwise menu will not appear on the screen
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // instantiating ViewModel
        listViewModel = ViewModelProviders.of(this).get(ListViewModel.class);
        //and once ViewModel is instantiated, now calling refresh function for showing data
        listViewModel.refresh();

        // now working recycler view for showing data and setting the layout of recycler view
        recyclerViewDoglist.setLayoutManager(new LinearLayoutManager(getContext()));
        // linearLayoutManager simply returns a list arranged linearly, we can also have matrix manager for showing data matrix wise
        recyclerViewDoglist.setAdapter(adapterList); // till now ouw recyclerView is set by an empty arrayList

        //whenever swipe Refresh is pulled down then its onRefreshListener will be activated
        swiperefresh.setOnRefreshListener(() -> {
            recyclerViewDoglist.setVisibility(View.GONE);
            errormsg.setVisibility(View.GONE);
            progressbar.setVisibility(View.VISIBLE);

            //now calling the function for refreshing
            listViewModel.RefreshByPassCahe();

            // now after refreshing hiding the swipeRefreshing
            swiperefresh.setRefreshing(false);
        });



        observeViewModel(); // calling observeViewModel() for observing  MutableLiveDatas of viewModel
    }

    public void observeViewModel() {
        listViewModel.dogs.observe(this, dogBreeds -> {
            if (dogBreeds != null && dogBreeds instanceof List) {
                recyclerViewDoglist.setVisibility(View.VISIBLE); // setting the visibility of recyclerView on receiving new Content from LiveData
                adapterList.updateDogList(dogBreeds); // now passing that new data to adapter to update the dogList
            }

            // same is with error message,observing it
            listViewModel.DogError.observe(this, isError -> {
                if (isError != null && isError instanceof Boolean) {
                    errormsg.setVisibility(isError ? View.VISIBLE : View.GONE);

                }
            });

            listViewModel.loading.observe(this, isloading -> {
                if (isloading != null && isloading instanceof Boolean) {
                    progressbar.setVisibility(isloading ? View.VISIBLE : View.GONE);
                    // also if isLoading is true, then we have to hide the recyclerView and when isLoading is false then we have to show the recyclerView
                    recyclerViewDoglist.setVisibility(isloading ? View.GONE : View.VISIBLE);
                    // and if isloading is true(only) than we have to hide error message
                    if (isloading == true) {
                        errormsg.setVisibility(View.GONE);

                    }
                }
            });
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);

        inflater.inflate(R.menu.list_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting: {
                if (isAdded()) {
                    // now navigating to setting screen
                    Navigation.findNavController(getView()).navigate(Dogs_list_fragmentsDirections.actionSettings());
                }
                break;
            }
        }
        return super.onOptionsItemSelected(item);

    }
}
