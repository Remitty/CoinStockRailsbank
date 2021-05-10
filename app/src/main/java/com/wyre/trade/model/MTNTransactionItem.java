package com.wyre.trade.model;

import org.json.JSONException;
import org.json.JSONObject;

public class MTNTransactionItem {
    private JSONObject data;

    public void setData(JSONObject data) {
        this.data = data;
    }

    public String getDate(){
        try {
            return data.getString("created_at").substring(0, 10);
        } catch (JSONException e) {
            return "";
        }
    }

    public String getAmount() {
        try {
            return data.getString("amount");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getType() {
        try {
            return data.getString("type");
        } catch (JSONException e) {
            return "";
        }
    }
    public String getContact() {
        try {
            return data.getString("to_phone");
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

    public String getTransactionId() {
        try {
            return data.getString("transaction_id");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getCurrency() {
        try {
            String currency = data.getString("currency");
            return "XAF";
        } catch (JSONException e) {
            return "";
        }
    }
}
