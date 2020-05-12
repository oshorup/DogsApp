package com.app.development.dogsapp.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.development.dogsapp.R;
public class Settings extends PreferenceFragmentCompat {

    public Settings() {

    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        //Attaching the XML file with this fragment
        setPreferencesFromResource(R.xml.settings,rootKey);

    }
}
