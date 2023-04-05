package com.example.app.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UsageApps {
    @JsonProperty("packageName")
    private String packageName;

    @JsonProperty("totalTime")
    private String totalTime;

    @JsonProperty("firstTime")
    private String firstTime;

    @JsonProperty("lastTime")
    private String lastTime;

    public UsageApps(String _packageName, String _totalTime, String _firstTime, String _lastTime){
        this.packageName = _packageName;
        this.totalTime = _totalTime;
        this.firstTime = _firstTime;
        this.lastTime = _lastTime;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }
    public void setFirstTime(String firstTime) {
        this.firstTime = firstTime;
    }
    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }
}
