package com.hearound.hearound;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    // Constants
    private final int DEFAULT_GPS_MIN_TIME = 1; // in milliseconds
    private final int DEFAULT_GPS_MIN_DISTANCE = 1; // in meters

    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 0;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private LocationManager locManager;
    private MyLocationListener locListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoibXR1cnBpbiIsImEiOiJjajkzbnA3ZWkxY3YxMzNwNmFoYTB0eW9vIn0.95ukjEFdm3gG7lebBY4Eow");
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        Log.d("****** onCreate *****", "map: " + mapView);

        // Set up location detection
        locManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        // Set up my location listener
        locListener = new MyLocationListener(this);

        /* Check permissions
           Without this, you will receiveERROR:  Caused by: java.lang.SecurityException: "gps"
           location provider requires ACCESS_FINE_LOCATION permission.
           More: http://stackoverflow.com/questions/32083913/android-gps-requires-access-fine-location-error-even-though-my-manifest-file
           Documentation: https://developer.android.com/training/permissions/requesting.html*/
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_ACCESS_FINE_LOCATION);
        }

        // Needed so onMapReady gets called
        mapView.getMapAsync(this);
        // Add button click listener
        userLocationFAB();
        newPostFAB();

        Intent postValues = getIntent();

        if (postValues.hasExtra("userID") && postValues.hasExtra("postBody") &&
                postValues.hasExtra("latitude") && postValues.hasExtra("longitude")) {
            String userID = postValues.getStringExtra("userID");
            String postBody = postValues.getStringExtra("postBody");
            double latitude = getIntent().getDoubleExtra("latitude", 0);
            double longitude = getIntent().getDoubleExtra("longitude", 0);
            Log.d("******** intent values", userID);
            Log.d("******** intent values", postBody);
            Log.d("******** intent values", "" + latitude);
            Log.d("******** intent values", "" + longitude);

            //displayPost(new LatLng(latitude, longitude), userID, postBody);
        } else {
            Log.d("******** intent values", "Not all intent values present");
        }
    }


    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        MainActivity.this.mapboxMap = mapboxMap;
        Log.d("****** onMapReady *****", "map: " + mapboxMap);
        mapboxMap.setMyLocationEnabled(true);
        displayPost(new LatLng(42.407095, -71.117974), "Tufts University", "Tufts");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        // Callback on permissions grant or deny screen
        if (requestCode == MY_PERMISSIONS_ACCESS_FINE_LOCATION
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Yay!  Now you can do request for location updates
            try {
                locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        DEFAULT_GPS_MIN_TIME,
                        DEFAULT_GPS_MIN_DISTANCE,
                        locListener);
            } catch (SecurityException e) {
                Log.e("Security exception","request location updates: " + e);
            }
            mapboxMap.setMyLocationEnabled(true);
        }
    }

    // https://github.com/mapbox/mapbox-gl-native/issues/3365
    private void userLocationFAB(){
        FloatingActionButton FAB = (FloatingActionButton) findViewById(R.id.myLocationButton);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapboxMap != null && mapboxMap.getMyLocation() != null) {
                    mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mapboxMap.getMyLocation()), 15));
                }
                else {
                    Log.d("****** FAB *****", "map: " + mapboxMap);
                }
            }
        });
    }

    private void newPostFAB() {
        FloatingActionButton compose = (FloatingActionButton) findViewById(R.id.composeButton);
        compose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), NewPost.class);
                startActivity(intent);
            }
        });
    }

    // Add a post to the map
    private void displayPost(LatLng loc, String user, String body) {
        mapboxMap.addMarker(new MarkerViewOptions().position(loc).title(user).snippet(body));
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
