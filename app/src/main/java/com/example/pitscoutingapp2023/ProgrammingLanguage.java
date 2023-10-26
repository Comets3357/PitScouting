package com.example.pitscoutingapp2023;

public enum ProgrammingLanguage {
    NO_ANSWER(0, "Programming Language"),
    CPP(1, "C++"),
    JAVA(2, "Java"),
    PYTHON(3, "Python"),
    LABVIEW(4, "LabView"),
    OTHER(5, "Other");
    public int position;
    public String commonName;
    ProgrammingLanguage(int pos, String commonName) {
        this.position = pos;
        this.commonName = commonName;
    }
    public static boolean isDefined(String programmingLanguage) {
        for (ProgrammingLanguage progLangs : ProgrammingLanguage.values()) {
            if (progLangs.toString().equals(programmingLanguage) || progLangs.commonName.equals(programmingLanguage)) {
                return true;
            }
        }
        return false;
    }

    public static ProgrammingLanguage getProgrammingLanguageFromCommonName(String commonName) {
        for (ProgrammingLanguage programming : ProgrammingLanguage.values()) {
            if (programming.commonName.equals(commonName)) {
                return programming;
            }
        }
        return NO_ANSWER;
    }
}
