package com.wyre.trade.model;

import org.json.JSONException;
import org.json.JSONObject;

public class TokenTradePair {
    JSONObject data;

    public void setData(JSONObject data) {
        this.data = data;
    }

    public String getTradeSymbol() {
        try {
            return data.getString("trade_symbol");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getMarketSymbol() {
        try {
            return data.getString("market_symbol");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getPair() {
        try {
            return data.getString("symbol");
        } catch (JSONException e) {
            return "";
        }
    }
}
