package com.wyre.trade.model;

import org.json.JSONException;
import org.json.JSONObject;

public class ContactUser {
    JSONObject data = new JSONObject();

    public void setData(JSONObject data) {
        this.data = data;
    }

    public String getName() {
        try {
            return data.getString("contact_name");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
    public String getEmail() {
        try {
            return data.getString("contact_email");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getId() {
        try {
            return data.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
            return "0";
        }
    }

}
