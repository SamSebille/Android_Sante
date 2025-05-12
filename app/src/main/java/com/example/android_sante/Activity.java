package com.example.android_sante; // Assurez-vous que le package correspond à votre projet

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Activity {
    private String activityEntryId;
    private String userId;
    private String activityName;
    private int durationInMinutes;
    private String entryDate;

    public Activity(String userId, String activityName, int durationInMinutes) {
        this.activityEntryId = UUID.randomUUID().toString();
        this.userId = userId;
        this.activityName = activityName;
        this.durationInMinutes = durationInMinutes;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
        this.entryDate = sdf.format(new Date());
    }

    public String getActivityEntryId() {
        return activityEntryId;
    }

    public String getUserId() {
        return userId;
    }

    public String getActivityName() {
        return activityName;
    }

    public int getDurationInMinutes() {
        return durationInMinutes;
    }

    public String getEntryDate() {
        return entryDate;
    }

    @Override
    public String toString() {
        return "ActivityEntry{" +
                "activityEntryId='" + activityEntryId + '\'' +
                ", userId='" + userId + '\'' +
                ", activityName='" + activityName + '\'' +
                ", durationInMinutes=" + durationInMinutes +
                ", entryDate='" + entryDate + '\'' +
                '}';
    }
}