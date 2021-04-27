package com.wyre.trade.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class StocksOrderModel {
    private JSONObject data;

    public StocksOrderModel(JSONObject item) {
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

    public String getStocksOrderShares(){
        try {
            return new DecimalFormat("#,###.####").format(data.getDouble("qty"));
        } catch (JSONException e) {
            return "0";
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

    public Double getStockOrderCost(){
        try {
//            return new DecimalFormat("#,###.##").format(data.getDouble("est_cost"));
            return data.getDouble("est_cost");
        } catch (JSONException e) {
            return  0.0;
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

