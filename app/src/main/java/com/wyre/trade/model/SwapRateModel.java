package com.wyre.trade.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class SwapRateModel {
    JSONObject data;

    public void setData(JSONObject data) {
        this.data = data;
    }

    public String getSendMax() {
        try {
            return  new DecimalFormat("#.###").format(data.getDouble("max"));
        } catch (JSONException e) {
            return "";
        }
    }

    public String getSendMin() {
        try {
            return  new DecimalFormat("#.###").format(data.getDouble("min"));
        } catch (JSONException e) {
            return "";
        }
    }

    public Double getRate() {
        try {
            return data.getDouble("rate");
        } catch (JSONException e) {
            return 0.0;
        }
    }

    public Double getFee() {
        try {
            return data.getDouble("receiveFee");
        } catch (JSONException e) {
            return 0.0;
        }
    }

    public Double getSendFee() {
        try {
            return data.getDouble("sendFee");
        } catch (JSONException e) {
            return 0.0;
        }
    }
}
