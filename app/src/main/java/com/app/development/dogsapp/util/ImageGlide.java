package com.app.development.dogsapp.util;

import android.content.Context;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.app.development.dogsapp.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class ImageGlide {

    public static void loadImage(ImageView imageView, String Url, CircularProgressDrawable circularProgressDrawable) {
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(circularProgressDrawable) // for activating circularProgressDrawable
                .error(R.mipmap.ic_launcher); // for applying default image in case error occurs while loading image
        Glide.with(imageView.getContext())
                .setDefaultRequestOptions(requestOptions)
                .load(Url) // loading the given url
                .into(imageView); // into that given imageView
    }

    // now writing function for getting circularProgressDrawable
    public static CircularProgressDrawable GetcircularProgressDrawable(Context context) {
        CircularProgressDrawable cpd = new CircularProgressDrawable(context);
        cpd.setStrokeWidth(10f); // setting the width of circularProgressDrawable
        cpd.setCenterRadius(50f);// setting the Radius of circularProgressDrawable w.r.t centre of image
        cpd.start(); // starting the circularProgressDrawable

        return cpd;
    }

    @BindingAdapter("android:ImageUrl") // we created an attribute for xml file
    public static void loadimage(ImageView view, String url)
    {
        loadImage(view,url,GetcircularProgressDrawable(view.getContext()));
    }

}
