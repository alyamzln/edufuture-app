package com.example.quizsection;

public class LevelModel {

    private String name;
    private String noOfSub;

    public LevelModel(String name, String noOfSub) {
        this.name = name;
        this.noOfSub = noOfSub;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNoOfSub() {
        return noOfSub;
    }

    public void setNoOfSub(String noOfSub) {
        this.noOfSub = noOfSub;
    }
}
