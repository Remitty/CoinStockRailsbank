package com.wyre.trade.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class TopStocks {

    JSONObject data;

    public TopStocks(JSONObject jsonObject) {
        data = jsonObject;
    }

    public String getSymbol() {
        try {
            return data.getString("ticker");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getCompanyName() {
        try {
            return data.getString("companyName");
        } catch (JSONException e) {
            return "";
        }
    }

    public Double getPrice() {
        try {
            return data.getDouble("price");
        } catch (JSONException e) {
            return 0.0;
        }
    }

    public String getChanges() {
        try {
            String str = data.getString("changes");
            if(Double.parseDouble(str) > 0)
                str = "+" + str;
            return str;
        } catch (JSONException e) {
            return "";
        }
    }

    public String getChangesPercentage() {
        try {
            return data.getString("changesPercentage");
        } catch (JSONException e) {
            return "";
        }
    }
}
