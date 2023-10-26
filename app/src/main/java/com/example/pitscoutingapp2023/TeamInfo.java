package com.example.pitscoutingapp2023;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TeamInfo {
    String teamNumber;
    DrivetrainType drivetrainType;
    ProgrammingLanguage programmingLanguage;
    Boolean driveTeam;
}
