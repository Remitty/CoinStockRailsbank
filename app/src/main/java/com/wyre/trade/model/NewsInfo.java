package com.wyre.trade.model;

import org.json.JSONException;
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
        return data.optString("text");
    }

    public String getDate(){
//        Long timestamp = data.optLong("publishedDate");
//        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
//        cal.setTimeInMillis(timestamp);
//        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        String date = null;
        try {
            date = data.getString("publishedDate");
            date = date.substring(0, 10);
        } catch (JSONException e) {
            date = "01-01-1970";
        }
        return date;
    }
}
