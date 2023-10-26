package com.example.pitscoutingapp2023.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.pitscoutingapp2023.common.TeamPitScout;

import java.util.List;

@Dao
public interface TeamPitScoutDao {

    @Query("SELECT * FROM TeamPitScout WHERE event_key = :eventKey AND team_number = :teamNumber")
    TeamPitScout getTeamAtEvent(String teamNumber, String eventKey);

    @Query("SELECT * FROM TeamPitScout WHERE event_key = :eventKey")
    List<TeamPitScout> getAllTeams(String eventKey);

    @Query("SELECT COUNT(team_number) FROM TeamPitScout WHERE event_key = :eventKey AND team_number = :teamNumber")
    int getAlreadySubmitted(String teamNumber, String eventKey);

    @Insert
    void insertAll(TeamPitScout... teamPitScouts);
    @Delete
    void delete(TeamPitScout teamPitScout);

    @Query("DELETE FROM TeamPitScout WHERE event_key = :eventKey")
    void deleteEventRecords(String eventKey);

    @Update
    void update(TeamPitScout teamPitScout);
}
