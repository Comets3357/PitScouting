package com.example.pitscoutingapp2023;

public enum DrivetrainType {
    NO_ANSWER(0, "Drivetrain Type"),
    SWERVE(2, "Swerve"),
    TANK(1, "Tank"),
    MECANUM(3, "Mecanum"),
    OTHER(4, "Other");
    public int position;
    public String commonName;
    DrivetrainType(int pos, String commonName) {
        this.position = pos;
        this.commonName = commonName;
    }
    public static boolean isDefined(String drivetrain) {
        for (DrivetrainType drivetrainType : DrivetrainType.values()) {
            if (drivetrainType.toString().equals(drivetrain) || drivetrainType.commonName.equals(drivetrain)) {
                return true;
            }
        }
        return false;
    }

    public static DrivetrainType getDrivetrainTypeFromCommonName(String commonName) {
        for (DrivetrainType drivetrainType : DrivetrainType.values()) {
            if (drivetrainType.commonName.equals(commonName)) {
                return drivetrainType;
            }
        }
        return NO_ANSWER;
    }
}
