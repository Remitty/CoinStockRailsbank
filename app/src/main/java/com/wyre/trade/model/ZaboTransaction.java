package com.wyre.trade.model;

import org.json.JSONException;
import org.json.JSONObject;

public class ZaboTransaction {
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

    public Double getAmount() {
        try {
            return data.getDouble("amount");
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

    public String getType() {
        try {
            return data.getString("type");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getStatus() {
        try {
            return data.getString("status");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getFees() {
        try {
            JSONObject fee = data.getJSONObject("fees");
            return fee.getString("amount");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getPairedAsset() {
        try {
            return data.getString("paired_currency");
        } catch (JSONException e) {
            return "";
        }
    }

    public Double getPairedAmount() {
        try {
            return data.getDouble("paired_amount");
        } catch (JSONException e) {
            return 0.0;
        }
    }
}
