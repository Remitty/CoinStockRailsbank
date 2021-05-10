package com.wyre.trade.model;

import com.wyre.trade.helper.URLHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class PredictAsset {
    JSONObject data;

    public PredictAsset(JSONObject item) {
        data = item;
    }

    public String getName() {
        try {
            return data.getString("name");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getSymbol() {
        try {
            return data.getString("symbol");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getId() {
        try {
            return data.getString("id");
        } catch (JSONException e) {
            return "0";
        }
    }

    public Double getPrice() {
        try {
            return data.getDouble("price");
        } catch (JSONException e) {
            return 0.0;
        }
    }

    public Double getChange() {
        try {
            return data.getDouble("change");
        } catch (JSONException e) {
            return 0.0;
        }
    }

    public String getIcon() {
        String icon = data.optString("icon");
        if(!icon.startsWith("http"))
            icon = URLHelper.base + icon;

        return icon;
    }
}
