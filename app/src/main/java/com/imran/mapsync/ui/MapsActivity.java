package com.imran.mapsync.ui;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.imran.mapsync.R;
import com.imran.mapsync.adapter.MapAdapter;
import com.imran.mapsync.room.AppDatabase;
import com.imran.mapsync.room.AppExecutors;
import com.imran.mapsync.room.dao.MapDao;
import com.imran.mapsync.room.model.Map;
import com.imran.mapsync.util.LocationUpdater;
import com.imran.mapsync.util.MapUtils;

import java.util.List;

public class MapsActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener, LocationUpdater.OnUpdateLocationListener {

    private static final int LOCATION_REQUEST_CODE = 100;
    private GoogleMap mMap;

    private static final String TAG = "MapsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_maps;
    }

    @Override
    protected void onConnectivity(boolean isConnected) {
        if (isConnected) enableLocation();
        AppExecutors.getInstance().diskIO().execute(() -> {
            MapDao mapDao = AppDatabase.getAppDatabase(getApplicationContext()).postDao();
            if (mapDao.count() > 0){
                load();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerDragListener(this);
        // location permission
        enableLocation();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        save(new Map(marker.getPosition()));
    }

    private void save(Map map) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            MapDao mapDao = AppDatabase.getAppDatabase(getApplicationContext()).postDao();
            mapDao.insert(map);
            load();
        });
    }

    private void load() {
        MapDao mapDao = AppDatabase.getAppDatabase(getApplicationContext()).postDao();
        List<Map> mapList = mapDao.getAll();
        runOnUiThread(() -> {
            if (mapList != null && !mapList.isEmpty()){
                mMap.clear();
                for (Map map : mapList){
                    LatLng latLng = new LatLng(map.getLat(), map.getLng());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(MapUtils.getInstance().getAddressByLatLng(MapsActivity.this, latLng.latitude, latLng.longitude)).snippet(map.getCreatedTime()).draggable(true));
                }
            }
        });
    }

    @Override
    public void onUpdate(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng).title(MapUtils.getInstance().getAddressByLatLng(getApplicationContext(), location.getLatitude(), location.getLongitude())).snippet("Please move the marker if needed.").draggable(true));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        mMap.animateCamera(cameraUpdate);
        save(new Map(latLng));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 && permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                Log.d(TAG, "onRequestPermissionsResult: true");
            } else {
                //Permission was denied. Display an error message.
                enableLocation();
                Log.d(TAG, "onRequestPermissionsResult: false");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_REQUEST_CODE) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    LocationUpdater.getInstance().init(getApplicationContext()).setUpdateLocationListener(this);
                    Log.d(TAG, "onActivityResult: ok");
                    break;
                case Activity.RESULT_CANCELED:
                    Log.d(TAG, "onActivityResult: cancel");
                    break;
                default:
                    break;
            }
        }
        Log.d(TAG, "onActivityResult: ");
    }

    private void enableLocation() {
        boolean hasForegroundLocationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            boolean hasBackgroundLocationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;

            if (!hasBackgroundLocationPermission && !hasForegroundLocationPermission){
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Permission to access the location is missing.
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, LOCATION_REQUEST_CODE);
                    return;
                }
            }
        } else {
            if (!hasForegroundLocationPermission){
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Permission to access the location is missing.
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
                    return;
                }
            }
        }

        if (mMap != null) {
            //Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
            LocationUpdater.getInstance().init(getApplicationContext()).setUpdateLocationListener(this);
            Log.d(TAG, "enableLocation: ");
        }
    }
}
