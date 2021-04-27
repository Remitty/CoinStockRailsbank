package com.wyre.trade.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class StocksInfo {
    private JSONObject data;

    public StocksInfo(JSONObject item) {
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
            return data.getString("avg_price");
//            return new DecimalFormat("#,###.##").format(data.getDouble("avg_price"));
        } catch (JSONException e) {
            return "0.0";
        }
    }

    public String getCurrentPrice(){
        try {
//            return data.getString("current_price");
            return new DecimalFormat("#,###.##").format(data.getDouble("current_price"));
        } catch (JSONException e) {
            return "0.0";
        }
    }

    public String getHolding(){//holding
        try {
            return data.getString("holding");
//            return new DecimalFormat("#,###.##").format(data.getDouble("holding"));
        } catch (JSONException e) {
            return "0.0";
        }
    }


    public String getChangePrice(){

        try {
            return data.getString("change");
//            return new DecimalFormat("#,###.##").format(data.getDouble("change"));
        } catch (JSONException e) {
            return "0.0";
        }
    }

    public String getProfit(){
        try {
            return data.getString("profit");
//            return new DecimalFormat("#,###.##").format(data.getDouble("profit"));
//            return String.format("%.2f", data.getDouble("profit"));
        } catch (JSONException e) {
            return "0.0";
        }
    }

    public String getChangePricePercent(){
        try {
            return data.getString("change_percent");
//            return new DecimalFormat("#,###.####").format(data.getDouble("change_percent"));
        } catch (JSONException e) {
            return "0.0";
        }
    }

}

