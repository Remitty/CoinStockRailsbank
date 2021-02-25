package com.brian.stocks.model;

import android.text.format.DateFormat;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;

public class NewsInfo {
    private JSONObject data;
    public NewsInfo(JSONObject item) {
        data = item;
    }

    public JSONObject getData() {
        return data;
    }

    public String getImageURL(){
        return data.optString("imageUrl");
    }

    public String getNewsTitle(){
        return data.optString("headline");
    }

    public String getURL(){
        return data.optString("url");
    }

    public String getSummary(){
        return data.optString("summary");
    }

    public String getDate(){
        Long timestamp = data.optLong("datetime");
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }
}
