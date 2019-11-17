package com.imran.mapsync.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.imran.mapsync.room.model.Map;

import java.util.List;

@Dao
public interface MapDao {

    @Query("select * from map")
    List<Map> getAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Map map);

    @Delete
    void delete(Map map);

    @Query("select COUNT(*) from map")
    int count();
}
