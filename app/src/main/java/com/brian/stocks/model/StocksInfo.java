package com.brian.stocks.model;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StocksInfo {
    private JSONObject data;
    private String StocksName;
    private String StocksShared;
    private String StocksPrice;
    private JSONArray StockAggregate = new JSONArray();

    public StocksInfo(JSONObject item) {
        //            data = item.getJSONObject("ticker");
        data = item;
    }

    public JSONObject getData() {
        return data;
    }

    public String getStocksName() {
        try {
            return data.getString("symbol");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getStocksNameOther() {
        try {
            return data.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getStocksTickerOther() {
        try {

            return data.getString("ticker");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getStocksShares() {
       return data.optString("shares");
    }

    public String getStocksOrderName() {
        try {
            return data.getString("symbol");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getStocksOrderShares(){
        return data.optString("qty");
    }

    public String getStockAvgPrice(){
        return data.optString("filled_avg_price");
    }

    public String getStocksPrice() {
        try {
            return data.getString("vw");
        } catch (JSONException e) {
            e.printStackTrace();
            return "0.0";
        }
    }

    public JSONArray getStockAggregate(){
            return StockAggregate;
    }

    public void setStockAggregate(JSONArray aggregate) {
        StockAggregate = aggregate;
    }

    public String getStockTodayChange() {
        try {
            return data.getString("todaysChange");
        } catch (JSONException e) {
            e.printStackTrace();
            return "0.0";
        }
    }

    public String getStockTodayChangePercent() {
        try {
            return data.getString("todaysChangePerc");
        } catch (JSONException e) {
            e.printStackTrace();
            return "0.0";
        }
    }

    public String getStockOrderStatus(){
        return data.optString("status");
    }
    public String getStockOrderLimitPrice() {return data.optString("limit_price");}
    public String getStockOrderSide(){
        return data.optString("side");
    }

    public String getStockOrderType(){
        return data.optString("type");
    }

    public String getStockOrderID(){
        return data.optString("order_id");
    }

    public String getStockOrderDate(){
        return data.optString("created_at").split(" ")[0];
    }

}

