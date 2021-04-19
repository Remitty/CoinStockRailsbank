package com.brian.stocks.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class PositionInfo {
    private JSONObject data;

    public PositionInfo(JSONObject item) {
        data = item;
    }

    public JSONObject getData() {
        return data;
    }

    public String getSymbol() {
        return data.optString("symbol");
    }

    public String getName() {
        try {
            return data.getString("name");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getQty() {
        try {
            return data.getString("filled_qty");
        } catch (JSONException e) {
            return "0";
        }
    }

    public String getAvgPrice() {
        try {
            return new DecimalFormat("#,###.##").format(data.getDouble("avg_price"));
        } catch (JSONException e) {
            return "0.0";
        }
    }

    public String getCurrentPrice(){
        try {
            return new DecimalFormat("#,###.##").format(data.getDouble("current_price"));
        } catch (JSONException e) {
            return "0.0";
        }
    }

    public String getEquity(){//holding
        try {
            return new DecimalFormat("#,###.##").format(data.getDouble("holding"));
        } catch (JSONException e) {
            return "0.0";
        }
    }


    public String getChangePrice(){

        try {
            return new DecimalFormat("#,###.##").format(data.getDouble("change"));
        } catch (JSONException e) {
            return "0.0";
        }
    }

    public String getProfit(){
        try {
            return new DecimalFormat("#,###.##").format(data.getDouble("profit"));
        } catch (JSONException e) {
            return "0.0";
        }
    }

    public String getChangePricePercent(){
        try {
            return new DecimalFormat("#,###.####").format(data.getDouble("change_percent"));
        } catch (JSONException e) {
            return "0.0";
        }
    }

}

