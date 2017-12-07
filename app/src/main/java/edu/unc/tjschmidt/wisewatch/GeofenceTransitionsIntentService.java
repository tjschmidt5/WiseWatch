package edu.unc.tjschmidt.wisewatch;

import android.Manifest;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tjschmidt on 11/29/17.
 */

//A lot of this code was adapted from this tutorial:
// https://code.tutsplus.com/tutorials/how-to-work-with-geofences-on-android--cms-26639
    //as well as the android documentation on geofences


public class GeofenceTransitionsIntentService extends IntentService {

    public GeofenceTransitionsIntentService(){
        super("GeofenceTransitionsIntentService");
    }

//    public GeofenceTransitionsIntentService(String name) {
 //       super(name);
 //   }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e("TAG", "Error with geofence");
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        // Test that the reported transition was of interest.
        if (//geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            //we only want the exit one because that's the only one that matters.

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    triggeringGeofences
            );

            // Send notification and log the transition details.
            sendNotification(geofenceTransitionDetails);
            Log.i("TAG", geofenceTransitionDetails);
        }


    }

    private void sendNotification(String geofenceTransitionDetails) {
        Log.v("TAG", geofenceTransitionDetails);
        Toast.makeText(this, geofenceTransitionDetails, Toast.LENGTH_LONG).show();


        

        new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long l) {
                Log.v("Tag", "Ticking");
            }

            @Override
            public void onFinish() {
              //  String button_text = ob.getText().toString();
              //  if(button_text == "Non-emergency"){
               //     return;
               // }

                Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                phoneIntent.setData(Uri.parse("tel:1-630-706-1425"));
                //check to see if you have the applicable permissions
                boolean permission = checkPermission();
                if(permission){
                    return;
                }

                startActivity(phoneIntent);

            }
        }.start();

       // Toast.makeText(this, geofenceTransitionDetails, Toast.LENGTH_LONG).show();
        //should show a toast image



    }



    private String getGeofenceTransitionDetails(GeofenceTransitionsIntentService geofenceTransitionsIntentService, int geofenceTransition, List<Geofence> triggeringGeofences) {
            ArrayList<String> triggeringGeofencesList = new ArrayList<>();
            for ( Geofence geofence : triggeringGeofences ) {
                triggeringGeofencesList.add( geofence.getRequestId() );
            }

            String status = null;
            if ( geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER )
                status = "Entering ";
            else if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL)
                status = "Dwelling";
            else if ( geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT )
                status = "Exiting ";
            return status + TextUtils.join( ", ", triggeringGeofencesList);


        }

    private boolean checkPermission(){
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;

    }


}

    