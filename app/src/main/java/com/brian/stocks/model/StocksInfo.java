package com.brian.stocks.model;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

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

    public String getStockSymbol() {
        try {
            return data.getString("symbol");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getStockName() {
        try {
            String name = data.getString("name");
            if(name.equals("null")) name="";
            return name;
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
        try {
            return new DecimalFormat("#,###").format(data.getDouble("shares"));
        } catch (JSONException e) {
            return "0";
        }
    }

    public String getStocksOrderShares(){
        try {
            return new DecimalFormat("#,###").format(data.getDouble("qty"));
        } catch (JSONException e) {
            return "0";
        }
    }

    public String getStockAvgPrice(){
        try {
            return new DecimalFormat("#.##").format(data.getDouble("filled_avg_price"));
        } catch (JSONException e) {
            return "0.0";
        }
    }

    public String getStocksPrice() {
        try {
            return new DecimalFormat("#,###.##").format(data.getDouble("vw"));
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
            return String.format("%.2f", Double.parseDouble(data.getString("todaysChange")));
        } catch (JSONException e) {
            e.printStackTrace();
            return "0.0";
        }
    }

    public String getStockTodayChangePercent() {
        try {
            return String.format("%.4f", Double.parseDouble(data.getString("todaysChangePerc")));
        } catch (JSONException e) {
            e.printStackTrace();
            return "0.0";
        }
    }

    public String getStockOrderStatus(){
        try {
            return data.getString("status");
        } catch (JSONException e) {
            return "";
        }
    }
    public String getStockOrderLimitPrice() {
        try {
            return String.format("%.2f", Double.parseDouble(data.getString("limit_price")));
        } catch (JSONException e) {
            return "0.0";
        }
    }
    public String getStockOrderSide(){
        try {
            return data.getString("side");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getStockOrderType(){
        try {
            return data.getString("type");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getStockOrderID(){
        try {
            return data.getString("order_id");
        } catch (JSONException e) {
            return  "";
        }
    }

    public String getStockOrderCost(){
        try {
            return new DecimalFormat("#,###.##").format(data.getDouble("est_cost"));
        } catch (JSONException e) {
            return  "";
        }
    }

    public String getStockOrderDate(){
        try {
            return data.getString("created_at").split(" ")[0];
        } catch (JSONException e) {
            return "";
        }
    }

}

