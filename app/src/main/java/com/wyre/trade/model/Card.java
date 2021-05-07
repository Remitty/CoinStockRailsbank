package com.wyre.trade.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Card {
    JSONObject data;

    public Card(JSONObject data) {
        this.data = data;
    }

    public String getCardId() {
        try {
            return data.getString("card_id");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getLastFour() {
        try {
            return data.getString("last_four");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getBrand() {
        try {
            return data.getString("brand");
        } catch (JSONException e) {
            return "Visa";
        }
    }
}
