package com.javaone.onepet.Alarm.Utils;

public class MyTimeSorter {
    public int mIndex;
    public String mTime;

    public MyTimeSorter(int index,String time){
        this.mIndex = index;
        this.mTime = time;
    }

    public MyTimeSorter(){};

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String dateTime) {
        mTime = dateTime;
    }
}
