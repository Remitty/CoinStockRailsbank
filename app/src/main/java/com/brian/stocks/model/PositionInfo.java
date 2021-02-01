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
            return DoubleFormat(data.getString("avg_entry_price"));
        } catch (JSONException e) {
            return "0.0";
        }
    }

    public String getCurrentPrice(){
        try {
            return DoubleFormat(data.getString("current_price"));
        } catch (JSONException e) {
            return "0.0";
        }
    }

    public String getEquity(){
        BigDecimal amount3 = new BigDecimal(getCurrentPrice());
        BigDecimal amount4 = new BigDecimal(getQty());
//        return DoubleFormat(""+amount3.multiply(amount4));
        return new DecimalFormat("#,###.####").format(amount3.multiply(amount4).doubleValue());
    }

    public String getLastDayPrice(){
        try {
            return DoubleFormat(data.getString("lastday_price"));
        } catch (JSONException e) {
            return "0.0";
        }
    }

    public String getChangePrice(){

        BigDecimal amount3 = new BigDecimal(getCurrentPrice());
        BigDecimal amount4 = new BigDecimal(getLastDayPrice());
        return DoubleFormat(""+amount3.subtract(amount4));
    }

    public String getProfit(){
        BigDecimal amount3 = new BigDecimal(getChangePrice());
        BigDecimal amount4 = new BigDecimal(getQty());
        return DoubleFormat(""+amount3.multiply(amount4));
    }

    public String getChangePricePercent(){
        try {
            return DoubleFormat(data.getString("change_today"));
        } catch (JSONException e) {
            return "0.0";
        }
    }

    private String DoubleFormat(String number){
        return String.format("%.4f", Double.parseDouble(number));
    }

}

