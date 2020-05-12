package com.app.development.dogsapp.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.app.development.dogsapp.R;
import com.app.development.dogsapp.view.MainActivity;

public class NotificationHelper {

    private static final String CHANNEL_ID ="Dogs Channel Id";
    private static final int NOTIFICATION_ID= 1234;

    private static NotificationHelper instance;
    private Context context;

    private NotificationHelper(Context context) {
        this.context = context;

    }

    public static NotificationHelper getInstance(Context context) {
        if (instance == null) {
            instance = new NotificationHelper(context);
        }
        return instance;
    }

    // now writing code for notification building
    public void creteNotification()
    {
        // first of all calling function for creating notification channel
        createNotificationChannel();

        Intent intent = new Intent(context, MainActivity.class); // when user taps on notification than this intent is activated
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // adding FLAG for some speciality to the intent

        //PendingIntent :- a way of packaging our intent
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);

        Bitmap LargeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.largeicon);

        //Now designing our notification window's UI
        Notification notification = new NotificationCompat.Builder(context,CHANNEL_ID)
                // Builder is something that allows us to add designs to our notification Window'UI
                .setSmallIcon(R.drawable.smallicon) // setting smallIcon for notification
                .setLargeIcon(LargeIcon) // this takes a Bitmap type argument, that is why we converted largeicon to Bitmap in line no.52
                .setContentTitle("Dogs Retrieved") // title of notification
                .setContentText("This is a notification to let you know that Dogs information has been retrieved.")// description of notification
                .setStyle( // setStyle() will be executed when user pulls down the notification for seeing the details inside notification otherwise not
                        new NotificationCompat.BigPictureStyle() // it will simply make image larger when user pulls down the notification
                        .bigPicture(LargeIcon)                  // we can also have .BigTextStyle()
                        .bigLargeIcon(null)     // it says that, when user pulls down the notification, then i don't want to see LargeIcon

                )
                .setContentIntent(pendingIntent) // setting intent on this no
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)// setting priority for this notification
                .build();


        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID,notification); // final statement for notifying about notification
                                                                                      // to NotificationManagerCompat
    }

    private void createNotificationChannel()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) // checking whether SDK is greater than 26 or not, b/c Build.VERSION_CODES.O = 26
        {
            //if this if block condition is satisfied then we do need to create channel
            String name =CHANNEL_ID ; // name of channel , it can be anything, not necessary equal to CHANNEL_ID
            String description = "Dogs retrieved notification channel"; // short description about the channel
            int importance = NotificationManager.IMPORTANCE_DEFAULT; // importance tells the system that how much important this notification is

            //Now creating channel
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,name,importance);
            channel.setDescription(description); // setting description about the channel
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE); // taking system service
            notificationManager.createNotificationChannel(channel); // finally channel created

        }

    }


}
