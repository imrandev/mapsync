package com.imran.mapsync.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.util.List;
import java.util.Locale;

public class MapUtils {

    private static MapUtils instance;

    public static MapUtils getInstance(){
        if (instance == null){
            instance = new MapUtils();
        }
        return instance;
    }

    public String getAddressByLatLng(Context context, double lat, double lng) {
        String address = "No address returned!";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        if (Geocoder.isPresent()){
            try {
                List<Address> addresses = geocoder.getFromLocation(lat, lng, 5);
                if (addresses != null) {
                    Address returnedAddress = addresses.get(0);
                    StringBuilder addressBuilder = new StringBuilder("");

                    for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                        addressBuilder.append(returnedAddress.getAddressLine(i)).append(" ");
                    }
                    address = addressBuilder.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return address;
    }
}
