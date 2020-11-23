package com.brian.stocks.model;

import org.json.JSONArray;
import org.json.JSONObject;

public class NewsInfo {
    private JSONObject data;
    public NewsInfo(JSONObject item) {
        data = item;
    }

    public JSONObject getData() {
        return data;
    }

    public String getImageURL(){
        return data.optString("image");
    }

    public String getNewsTitle(){
        return data.optString("title");
    }

    public String getURL(){
        return data.optString("url");
    }

    public String getSummary(){
        return data.optString("summary");
    }

    public String getDate(){
        String timestamp = data.optString("timestamp");
        return timestamp.split("T")[0];
    }
}
