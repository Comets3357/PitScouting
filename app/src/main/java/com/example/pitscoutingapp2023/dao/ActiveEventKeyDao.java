package com.example.pitscoutingapp2023.dao;

import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface ActiveEventKeyDao {
    @Query("SELECT active_event_key FROM ActiveEventKey WHERE pk = '1'")
    String getActiveEventKey();

    @Query("UPDATE ActiveEventKey SET active_event_key = :activeEventKey WHERE pk = '1'")
    void setActiveEventKey(String activeEventKey);

    @Query("INSERT INTO ActiveEventKey VALUES ('1', '')")
    void initEventKey();

    @Query("SELECT COUNT(active_event_key) FROM ActiveEventKey")
    int countEventKey();
}
