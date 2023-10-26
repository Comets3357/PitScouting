package com.example.pitscoutingapp2023.common;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import lombok.Getter;
import lombok.Setter;

@Entity(primaryKeys = {"team_number", "event_key"})
@Getter
@Setter
public class TeamPitScout {
    @ColumnInfo(name="team_number")
    @NonNull
    public String teamNumber;
    @ColumnInfo(name="event_key")
    @NonNull
    public String event;
    @ColumnInfo(name = "prog_lang")
    public String programmingLanguage;
    @ColumnInfo(name = "drivetrain_type")
    public String drivetrainType;
    @ColumnInfo(name = "drive_team")
    public Boolean driveteam;
}
