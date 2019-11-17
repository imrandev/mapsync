package com.imran.mapsync.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class LocationUpdater implements LocationListener {

    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 30000;
    private static final float LOCATION_DISTANCE = 10f;
    private static LocationUpdater instance;
    private static final String TAG = "LocationUpdater";
    private OnUpdateLocationListener onUpdateLocationListener;

    public static LocationUpdater getInstance(){
        if (instance == null){
            instance = new LocationUpdater();
        }
        return instance;
    }

    public LocationUpdater init(Context context) {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        try {
            mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, this);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        startLocationUpdates(context);
        return instance;
    }

    private void startLocationUpdates(Context context) {
        LocationRequest request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(request);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        SettingsClient settingsClient = LocationServices.getSettingsClient(context);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(context);

        try {
            locationClient.requestLocationUpdates(request, new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            Location location = locationResult.getLastLocation();
                            if (location != null) {
                                onLocationChanged(location);
                            } else {
                                getLastLocation(context);
                            }
                        }
                    },
                    null);
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    private void getLastLocation(Context context) {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(context);
        try {
            // GPS location can be null if GPS is switched off
            locationClient.getLastLocation().addOnSuccessListener(this::onLocationChanged).addOnFailureListener(e -> e.printStackTrace());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void setUpdateLocationListener(OnUpdateLocationListener onUpdateLocationListener) {
        this.onUpdateLocationListener = onUpdateLocationListener;
    }

    @Override
    public void onLocationChanged(Location location) {
        onUpdateLocationListener.onUpdate(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public interface OnUpdateLocationListener {
        void onUpdate(Location location);
    }
}
