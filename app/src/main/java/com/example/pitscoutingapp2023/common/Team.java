package com.example.pitscoutingapp2023.common;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class Team {
    @PrimaryKey
    @ColumnInfo(name = "team_number")
    @NonNull
    public String teamNumber;
    @ColumnInfo(name = "team_name")
    public String teamName;
}
