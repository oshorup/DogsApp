package com.app.development.dogsapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.app.development.dogsapp.R;

public class MainActivity extends AppCompatActivity {

    private Fragment fragment;

    //setting back arrow button on top 1BB]
    private NavController navController;

    //Request codes
    private static final int SMS_PERMISSION_REQUEST_CODE = 1231; // This key is used to identify the permission amongst a bunch of permissions

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialising fragment to a certain fragment
        fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);

//        //2BB]
        navController = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupActionBarWithNavController(this, navController);

    }
//    // 3BB] BB stands for back button on top

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, (DrawerLayout) null);
    }

    //1] step
    public void checkSMSPermissions() {
        // check if SEND_SMS permission has not been given
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) // means permission not given
        {
            //here since permission is not granted, so we will ask from system that should i show rationale to user
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                new AlertDialog.Builder(this)
                        .setTitle("Send SMS Permission")
                        .setMessage("This app requires an access to send SMS permission for sending message")
                        .setPositiveButton("Ask me", (dialog, which) -> {
                            // now since user is convinced to give permission, so now asking permission again
                            requestSMSPermissions();
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            // hence user has denied once again
                            // now user is not convinced to give permission, so we will notify the detail fragment that permission is not granted
                            notifyDetailFragment(false);
                        })
                        .show();
            } else {
                // that is if we don't have to show rationale, then i will simply ask for permission
                requestSMSPermissions();
            }

        } else {
            //permission has been granted already
            // Do your desired work, whatever you want
            // Now we simply notify the Detail fragment that permission has been granted and then the Detail fragment will do their work
            notifyDetailFragment(true);
        }

    }


    // now writing codes for requesting permissions
    private void requestSMSPermissions() {
        String[] permissions = {Manifest.permission.SEND_SMS};
        ActivityCompat.requestPermissions(this, permissions, SMS_PERMISSION_REQUEST_CODE);

    }

    //function for storing the result of requested permissions, whether permission was granted or not
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case SMS_PERMISSION_REQUEST_CODE: {
                //now checking the result is positive(i.e., asked permission was granted or not )
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) // means permission granted
                {
                    notifyDetailFragment(true);
                } else {
                    notifyDetailFragment(false);
                }
                break;
            }
        }


    }


    private void notifyDetailFragment(Boolean isPermissionGranted) {
        //while returning the result of asked permission back, we have to check back that the active fragment is detail fragment is active now
        // line numbers=> 117 to 118 is very important for asking permission from Fragment
        Fragment activeFragment = fragment.getChildFragmentManager().getPrimaryNavigationFragment();
        if (activeFragment instanceof Dog_details) {
            ((Dog_details) activeFragment).onPermissionResults(isPermissionGranted);

        }
    }
}
