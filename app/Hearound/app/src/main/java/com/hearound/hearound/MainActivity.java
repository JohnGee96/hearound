package com.hearound.hearound;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import io.fabric.sdk.android.Fabric;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.mapbox.mapboxsdk.style.layers.Filter.all;
import static com.mapbox.mapboxsdk.style.layers.Filter.gte;
import static com.mapbox.mapboxsdk.style.layers.Filter.lt;
import static com.mapbox.mapboxsdk.style.layers.Filter.neq;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleBlur;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleRadius;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    // Constants
    private final int DEFAULT_GPS_MIN_TIME = 1; // in milliseconds
    private final int DEFAULT_GPS_MIN_DISTANCE = 1; // in meters
    // TODO: set url
    private final String API_URL = "http://52.15.170.154/api";

    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 0;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private LocationManager locManager;
    private MyLocationListener locListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        Mapbox.getInstance(this, "pk.eyJ1IjoibXR1cnBpbiIsImEiOiJjajkzbnA3ZWkxY3YxMzNwNmFoYTB0eW9vIn0.95ukjEFdm3gG7lebBY4Eow");
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

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

        // Add button click listeners
        userLocationFAB();
        newPostFAB();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Location loc = mapboxMap.getMyLocation();
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.putExtra("lat", loc.getLatitude());
                intent.putExtra("lng", loc.getLongitude());
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        MainActivity.this.mapboxMap = mapboxMap;
        mapboxMap.setMyLocationEnabled(true);

        if (prefs.getBoolean("enable_heatmap", false)) {
            addClusteredGeoJsonSource(mapboxMap);

        }
        else {
            Location loc = mapboxMap.getMyLocation();
            addNearbyPosts(loc.getLatitude(), loc.getLongitude(), Integer.parseInt(prefs.getString("radius", "5")));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        // Callback on permissions grant or deny screen
        if (requestCode == MY_PERMISSIONS_ACCESS_FINE_LOCATION
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            try {
                locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        DEFAULT_GPS_MIN_TIME,
                        DEFAULT_GPS_MIN_DISTANCE,
                        locListener);
            } catch (SecurityException e) {
                Log.e("**** onPermissions ****","request location failed: " + e);
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
                    Log.d("**** FAB ****", "map: " + mapboxMap);
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

    private void addNearbyPosts(double lat, double lng, int radius) {
        APIConnection api = new APIConnection();

        try {
            JSONObject params = new JSONObject();
            params.put("lat", lat);
            params.put("lng", lng);
            params.put("radius", radius);

            api.post(API_URL + "/nearby_posts", params, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    final String error = e.toString();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("**** post ****", error);
                    }
                });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String res = response.body().string();
                    parseResponse(res);
                }
            });
        } catch (IOException e1) {
            Log.e("**** addNearbyPosts ***", "Error with POST: " + e1);
        } catch (JSONException e2) {
            Log.e("**** addNearbyPosts ***", "Error making JSON" + e2);
        } catch (Exception e) {
            Log.e("**** addNearbyPosts ***", e.toString());
        }
    }

    private void parseResponse(String res) {
        try {
            JSONArray allPosts = new JSONArray(res);

            for(int i = 0; i < allPosts.length(); i++) {
                JSONObject postData = allPosts.getJSONObject(i);
                LatLng loc = new LatLng(postData.getDouble("lat"), postData.getDouble("lng"));

                displayPost(loc, postData.getString("title"), postData.getString("body"));
            }
        } catch (Exception e) {
            Log.e("**** parseResponse ****", "error parsing response data: " + e);
        }

    }

    // Add a post to the map
    private void displayPost(LatLng loc, String user, String body) {
        try {
            mapboxMap.addMarker(new MarkerViewOptions().position(loc).title(user).snippet(body));
        } catch (Exception e) {
            Log.e("**** displayPost ****", "error adding marker: " + e);
        }
    }

    // Copied from Mapbox demo app
    // https://github.com/mapbox/mapbox-android-demo/blob/master/MapboxAndroidDemo/src/main/java/com/mapbox/mapboxandroiddemo/examples/dds/CreateHotspotsActivity.java
    private void addClusteredGeoJsonSource(MapboxMap mapboxMap) {

        // Add a new source from our GeoJSON data and set the 'cluster' option to true.
        try {
            mapboxMap.addSource(
                    // Point to GeoJSON data. This example visualizes all M1.0+ earthquakes from
                    // 12/22/15 to 1/21/16 as logged by USGS' Earthquake hazards program.
                    new GeoJsonSource("posts",
                            new URL(API_URL + "/nearby_post_locations"),
                            new GeoJsonOptions()
                                    .withCluster(true)
                                    .withClusterMaxZoom(15) // Max zoom to cluster points on
                                    .withClusterRadius(20) // Use small cluster radius for the hotspots look
                    )
            );
        } catch (MalformedURLException malformedUrlException) {
            Log.e("**** CreateHotspots", "Check the URL " + malformedUrlException.getMessage());
        }

        // Use the earthquakes source to create four layers:
        // three for each cluster category, and one for unclustered points

        // Each point range gets a different fill color.
        final int[][] layers = new int[][]{
                new int[]{150, Color.parseColor("#E55E5E")},
                new int[]{20, Color.parseColor("#F9886C")},
                new int[]{0, Color.parseColor("#FBB03B")}
        };

        CircleLayer unclustered = new CircleLayer("unclustered-points", "posts");
        unclustered.setProperties(
                circleColor(Color.parseColor("#FBB03B")),
                circleRadius(20f),
                circleBlur(1f));
        unclustered.setFilter(
                neq("cluster", true)
        );

        mapboxMap.addLayerBelow(unclustered, "building");

        for (int i = 0; i < layers.length; i++) {
            CircleLayer circles = new CircleLayer("cluster-" + i, "posts");
            circles.setProperties(
                    circleColor(layers[i][1]),
                    circleRadius(70f),
                    circleBlur(1f)
            );
            circles.setFilter(
                    i == 0
                            ? gte("point_count", layers[i][0]) :
                            all(gte("point_count", layers[i][0]), lt("point_count", layers[i - 1][0]))
            );
            mapboxMap.addLayerBelow(circles, "building");
        }
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
