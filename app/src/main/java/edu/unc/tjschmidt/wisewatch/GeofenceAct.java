package edu.unc.tjschmidt.wisewatch;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class GeofenceAct extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    //https://code.tutsplus.com/tutorials/how-to-work-with-geofences-on-android--cms-26639
    // a lot of this was based on this tutorial ^ and the android documentation


    private TextView currentLocation;
    private TextView lat_long;
    private Button save;
    private GoogleApiClient c;
    private double latitude;
    private double longitude;
    final float RADIUS = 20; //radius of geofence is 20m - it should be 100 for accuracy, but this should also be in a
    //wifi enabled location
    final long duration = 1000 * 60 * 60 * 24 * 365; //a year in milliseconds
    private MapFragment mapFragment;
    private GoogleMap map;
    private Marker locationMarker;


    private PendingIntent gfPendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofence);

        currentLocation = (TextView) findViewById(R.id.location);
        save = (Button) findViewById(R.id.save);
        lat_long = (TextView) findViewById(R.id.latlong);

        if (c == null) {
            c = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public void onMapReady(GoogleMap gMap) {
        Log.d("TAG", "onMapReady()");
        map = gMap;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location loc = LocationServices.FusedLocationApi.getLastLocation(c);
            Log.v("LOC", " " + loc.getLatitude() + ", " + loc.getLongitude());

            latitude = loc.getLatitude();
            longitude = loc.getLongitude();

            Geocoder g = new Geocoder(this, Locale.getDefault());

            try {
                List<Address> la = g.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                Address a = la.get(0);
                String feature = a.getFeatureName();
                String thoroughfare = a.getThoroughfare();
                String locality = a.getLocality();
                String sub_admin = a.getSubAdminArea();
                String admin = a.getAdminArea();
                String zip = a.getPostalCode();
                String country = a.getCountryName();
                String full_address = feature + " " + thoroughfare + " " + locality + " " + sub_admin + " "
                        + admin + " " + zip + " " + country;
                Log.v("Address", full_address);
                currentLocation.setText(full_address);
                //Log.v("Address: ", la.get(0).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }


            //Now let's set a location marker based on current location
            //based on this link: https://code.tutsplus.com/tutorials/how-to-work-with-geofences-on-android--cms-26639
            LatLng lat_lng = new LatLng(latitude, longitude);

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(lat_lng);

            locationMarker = map.addMarker(markerOptions);
            float zoom = 14f;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(lat_lng, zoom);
            map.animateCamera(cameraUpdate);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("TAG", "Connection failed");
    }

    @Override
    protected void onStart() {
        c.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        c.disconnect();
        super.onStop();
    }

    public void setGeofence(View v) {
        Geofence gf = new Geofence.Builder()
                .setRequestId("1")
                .setCircularRegion(latitude, longitude, RADIUS)
                .setExpirationDuration(duration)
                .setLoiteringDelay(2000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                .build(); //loitering delay of 2 seconds

        //don't really know why we do this. Taken from the docs though
        //you have to send some sort of geofencing request
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(gf);
        GeofencingRequest gr = builder.build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        //this should add the geofence
        LocationServices.GeofencingApi.addGeofences(
                c,
                gr,
                getGeofencePendingIntent());




    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (gfPendingIntent != null) {
            return gfPendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        gfPendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return gfPendingIntent;
    }
}
