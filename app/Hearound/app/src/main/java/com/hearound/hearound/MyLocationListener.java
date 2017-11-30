package com.hearound.hearound;

/**
 * Created by Maxwell on 10/23/17.
 */

/*
 * From Ming's SimpleGeoExample
 * https://github.com/tuftsdev/MobileDev/blob/gh-pages/examples/SimpleGeoApp/app/src/main/java/edu/tufts/cs/mchow/simplegeoapp/MyLocationListener.java
 */

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.location.Geocoder;
import android.location.Address;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MyLocationListener implements LocationListener
{
    private MainActivity copyOfMainActivity;

    public MyLocationListener(MainActivity f) {
        copyOfMainActivity = f;
    }

    @Override
    public void onLocationChanged(Location arg0) {
        double latitude = arg0.getLatitude();
        double longitude = arg0.getLongitude();
        String newDispLabel = "";

        // Reverse geocoding of lat and lng
        // Recipe: http://stackoverflow.com/questions/9409195/how-to-get-complete-address-from-latitude-and-longitude
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(copyOfMainActivity, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
            newDispLabel = "You are at " + address + ", " + city + ", " + state;
        }
        catch (IOException e) {
            newDispLabel = "You are at " + latitude + ", " + longitude;
        }
    }

    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub
    }
}
