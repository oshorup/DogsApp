package com.app.development.dogsapp.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.app.development.dogsapp.R;
import com.app.development.dogsapp.model.DogBreed;
import com.app.development.dogsapp.util.ImageGlide;

import java.util.ArrayList;
import java.util.List;

public class AdapterList extends RecyclerView.Adapter<AdapterList.AdapterHolder> {

    ArrayList<DogBreed> Doglist;

    public AdapterList(ArrayList<DogBreed> doglist) {
        this.Doglist = doglist;
    }

    // Data for updating DogList when the data of list changes
    public void updateDogList(List<DogBreed> updateDogList) {
        Doglist.clear();
        Doglist.addAll(updateDogList);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public AdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_dog_recyclerview, parent, false);
        AdapterHolder adapterHolder = new AdapterHolder(view);
        return adapterHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterHolder holder, int position) {
        //getting id of image, dog name, dog life span
        ImageView dogImage = holder.itemView.findViewById(R.id.dog_image);
        TextView dogname = holder.itemView.findViewById(R.id.dog_name);
        TextView doglifespan = holder.itemView.findViewById(R.id.lifespan);

        //setting onclick listener for moving to details screen
        LinearLayout doglayout = holder.itemView.findViewById(R.id.doglayout);
        doglayout.setOnClickListener(v -> {
            Dogs_list_fragmentsDirections.ActionDetails action = Dogs_list_fragmentsDirections.actionDetails();
            action.setDogUid(Doglist.get(position).uuid);
            Navigation.findNavController(doglayout).navigate(action);

        });


        // setting values to TextView parameters
        dogname.setText(Doglist.get(position).dogBreed);
        doglifespan.setText(Doglist.get(position).lifeSpan);
        // setting images to ImageView parameters
        ImageGlide.loadImage(dogImage,Doglist.get(position).ImageUrl,ImageGlide.GetcircularProgressDrawable(dogImage.getContext()));


    }

    @Override
    public int getItemCount() {
        return Doglist.size();
    }

    public class AdapterHolder extends RecyclerView.ViewHolder {

        public View itemView;

        public AdapterHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView; // just saving the address of our ItemView
        }
    }
}
