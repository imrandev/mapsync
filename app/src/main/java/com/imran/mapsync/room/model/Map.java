package com.imran.mapsync.room.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

@Entity(tableName = "map")
public class Map {

    @PrimaryKey
    @SerializedName("id")
    @NonNull
    private String id = UUID.randomUUID().toString();

    @SerializedName("lat")
    @ColumnInfo(name = "lat")
    private double lat;

    @SerializedName("lng")
    @ColumnInfo(name = "lng")
    private double lng;

    @SerializedName("created_time")
    @ColumnInfo(name = "created_time")
    private String createdTime;

    public Map(){

    }

    @Ignore
    public Map(LatLng latLng) {
        this.setLat(latLng.latitude);
        this.setLng(latLng.longitude);
        this.setCreatedTime(new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()));
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }
}
