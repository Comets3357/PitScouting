package com.example.pitscoutingapp2023.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.pitscoutingapp2023.common.Team;

@Dao
public interface TeamDao {
    @Query("SELECT team_name FROM Team WHERE team_number = :teamNumber")
    String getTeamName(String teamNumber);
    @Insert
    void insertAll(Team... teams);

    @Delete
    void delete(Team team);

    @Query("SELECT COUNT(team_name) FROM Team WHERE team_number = :team")
    int teamExists(String team);
}
