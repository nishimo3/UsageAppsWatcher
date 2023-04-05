package com.example.app.api;

public class CurrentApp {
    private String time;
    private String vid;
    private String packageName;

    public CurrentApp(String _time, String _vid, String _packageName) {
        this.time = _time;
        this.vid = _vid;
        this.packageName = _packageName;
    }
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public void setVid(String vid) {
        this.vid = vid;
    }
    public String getPackageName() {
        return this.packageName;
    }
    public String getTime() {
        return this.time;
    }
    public String getVid() {
        return this.vid;
    }
}
