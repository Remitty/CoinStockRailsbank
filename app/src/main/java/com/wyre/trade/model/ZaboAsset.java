package com.wyre.trade.model;

import org.json.JSONException;
import org.json.JSONObject;

public class ZaboAsset {
    private JSONObject data;

    public void setData(JSONObject data) {
        this.data = data;
    }

    public String getSymbol() {
        try {
            return data.getString("currency");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getName() {
        try {
            return data.getString("name");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getIcon() {
        try {
            String icon = data.getString("logo");
            if(icon.equals("null"))
                icon = "";
            return icon;
        } catch (JSONException e) {
            return "";
        }
    }

    public Double getBalance() {
        try {
            return data.getDouble("balance");
        } catch (JSONException e) {
            return 0.0;
        }
    }

    public Double getFiatValue() {
        try {
            return data.getDouble("fiat_value");
        } catch (JSONException e) {
            return 0.0;
        }
    }
}
