package com.app.development.dogsapp.view;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.palette.graphics.Palette;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.app.development.dogsapp.R;
import com.app.development.dogsapp.databinding.FragmentDogDetailsBinding;
//import com.app.development.dogsapp.databinding.SendSmsBinding;
import com.app.development.dogsapp.databinding.SendSmsDialogBinding;
import com.app.development.dogsapp.model.DogBreed;
import com.app.development.dogsapp.model.DogPalette;
import com.app.development.dogsapp.model.SendSMS;
import com.app.development.dogsapp.util.ImageGlide;
import com.app.development.dogsapp.viewmodel.DetailsViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Dog_details extends Fragment {

    public DetailsViewModel detailsViewModel;

    private DogBreed currentDogBreed;
    private boolean FLAG_SMS_PERMISSION = false; // a flag that will allow the user to send SMS one at a time
                                                // it simply says that, if it is true, it means sending SMS is under process

    private int DogUid;

    public Dog_details() {

    }

    private FragmentDogDetailsBinding binding;
    private int decider =0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //for dataBinding
        FragmentDogDetailsBinding binding = DataBindingUtil.inflate(inflater,R.layout.fragment_dog_details,container,false);
        this.binding=binding;

        setHasOptionsMenu(true); // for setting menu in this fragment

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            // if arguments is not equal to null then getting value from bundle
            DogUid = Dog_detailsArgs.fromBundle(getArguments()).getDogUid();
        }

           detailsViewModel = ViewModelProviders.of(this).get(DetailsViewModel.class);
           detailsViewModel.onRefresh(DogUid);
           ObserveViewModel();
    }

    public void ObserveViewModel() {
        detailsViewModel.dogbreed.observe(this, dogBreed -> {
            if(dogBreed!=null && dogBreed instanceof DogBreed)
            {
                // storing the value of current DogBreed , because we will need it in getting information
                currentDogBreed=dogBreed;

                //now binding the DogBreed with layout's
                 binding.setDogImage(dogBreed);

                // now if image url is not equal to null then calling palette for background color
                if(dogBreed.ImageUrl!=null) {
                    setBackgroundColorByusingPalette(dogBreed.ImageUrl);
                }
            }

        });
   }

   public void setBackgroundColorByusingPalette(String url)
   {
       // Palette needs mip-map resources, so converting url to Bit-map resources using GLIDE
       //method of converting an imageURL to Bitmap
       Glide.with(this)
               .asBitmap()
               .load(url)
               .into(new CustomTarget<Bitmap>() {
                   @Override
                   public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                       //now here resource is the required Bitmap data
                       //now extracting the color from palette using resource Bitmap
                       Palette.from(resource)
                               .generate(palette -> {// new PaletteAsyncListener() =>then lambda operator
                                   int color = palette.getLightMutedSwatch().getRgb();
                                   DogPalette mypalette = new DogPalette(color);
                                   binding.setPalette(mypalette);

                               });
                   }

                   @Override
                   public void onLoadCleared(@Nullable Drawable placeholder) {

                   }
               });

   }


   // for attaching the menu to this fragment
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.details_menu,menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_send_SMS:{
                if(!FLAG_SMS_PERMISSION)
                {
                    FLAG_SMS_PERMISSION=true;
                    ((MainActivity)getActivity()).checkSMSPermissions();
                }
                break; // after message is sent, break
            }
            case R.id.action_share_image:{
                // sharing some content of our app with some other apps available in our phone
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");

                intent.putExtra(Intent.EXTRA_SUBJECT,"Check out about this Dog Breed"); // subject(topic of message) of content
                intent.putExtra(Intent.EXTRA_TEXT,currentDogBreed.dogBreed+" is bred for "+currentDogBreed.BreedFor); // Details of content
                intent.putExtra(Intent.EXTRA_STREAM,currentDogBreed.ImageUrl); // link for surfing internet

                // Now for this intent, giving the option to choose the app to which user wants to share the contents
                startActivity(Intent.createChooser(intent,"Share with")); // "Share with" is the message that will be shown while selecting
                                                                                // the app for sharing
                break; // after sharing is finished, break
            }

        }
        return super.onOptionsItemSelected(item);
    }

    public void onPermissionResults(Boolean isPermissionGranted){
        // isAdded() function is used to check whether the (Demanded)layout screen is added with with this java file or not
        if(isAdded() && FLAG_SMS_PERMISSION && isPermissionGranted)
        {
            // now since we have been given permission, so sending all the data for sending SMS to SendSMS Model class
            SendSMS sendSMS = new SendSMS("",currentDogBreed.dogBreed+" is bred for "+currentDogBreed.BreedFor,currentDogBreed.ImageUrl);

            // initialise binding1
            SendSmsDialogBinding binding1 = DataBindingUtil.inflate(
                    LayoutInflater.from(getContext()),
                    R.layout.send_sms_dialog,
                    null,
                    false
            );// by now send_sms_dialog is bind

           // now showing dialog
            new AlertDialog.Builder(getContext())
                    .setView(binding1.getRoot())
                    .setPositiveButton("Send SMS", (dialog, which) -> {

                        // first checking To(destination) parameter is not empty
                        if(!binding1.sendTo.getText().toString().isEmpty())
                        {
                            sendSMS.to=binding1.sendTo.getText().toString();
                            // now calling a sendMessage() function for sending the message
                            sendMessage(sendSMS);
                        }

                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {

                    })
                    .show();
            FLAG_SMS_PERMISSION=false; // setting it false, because by now SMS will have been send, so that now  we can send another SMS
            binding1.setSMSInfo(sendSMS); // binding layout
        }



    }

    private void sendMessage(SendSMS sendSMS)
    {
        Intent intent = new Intent(getContext(),MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(getContext(),0,intent,0);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(sendSMS.to,null,sendSMS.text,pi,null);
        Toast.makeText(getContext(),"Message sent to "+sendSMS.to,Toast.LENGTH_SHORT).show();

    }
}
