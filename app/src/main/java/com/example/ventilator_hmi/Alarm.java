package com.example.ventilator_hmi;

import androidx.annotation.Nullable;

import java.util.Date;

public class Alarm {
    private String alarmText = "";
    private String priority = "";
    private Date startTime = null;
    private Date endTime = null;

    private boolean cleared = false;

    public Alarm() { }

    public Alarm(String alarmText, @Nullable String priority, @Nullable Date startTime, @Nullable Date endTime) {
        this.alarmText = alarmText != null ? alarmText : "null";
        this.priority = priority != null ? priority : "null";
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void setAlarmText(String alarmText) { this.alarmText = alarmText; }
    public void setPriority(String priority) { this.priority = priority; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public void setCleared(boolean cleared) { this.cleared = cleared; }

    public String getAlarmText() { return alarmText; }
    public String getPriority() { return priority; }
    public Date getStartTime() { return startTime; }
    public Date getEndTime() { return endTime; }
    public boolean isCleared() { return cleared; }

}
