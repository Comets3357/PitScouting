package com.example.pitscoutingapp2023.common;

import lombok.Getter;
import lombok.Setter;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.annotation.NonNull;

@Entity
@Getter
@Setter
public class ActiveEventKey {
    @PrimaryKey
    @ColumnInfo(name = "pk")
    @NonNull
    String pk;

    @ColumnInfo(name = "active_event_key")
    String activeEventKey;
}
