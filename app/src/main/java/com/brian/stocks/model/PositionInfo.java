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
            return data.getString("avg_entry_price");
        } catch (JSONException e) {
            return "0.0";
        }
    }

    public String getCurrentPrice(){
        try {
            return data.getString("current_price");
        } catch (JSONException e) {
            return "0.0";
        }
    }

    public String getEquity(){//holding
        BigDecimal amount3 = new BigDecimal(getCurrentPrice());
        BigDecimal amount4 = new BigDecimal(getQty());
//        return doubleFormat(""+amount3.multiply(amount4));
        return new DecimalFormat("#,###.##").format(amount3.multiply(amount4).doubleValue());
    }

    public String getLastDayPrice(){
        try {
            return doubleFormat(data.getString("lastday_price"));
        } catch (JSONException e) {
            return "0.0";
        }
    }

    public String getChangePrice(){

        BigDecimal amount3 = new BigDecimal(getCurrentPrice());
        BigDecimal amount4 = new BigDecimal(getLastDayPrice());
        return doubleFormat(""+amount3.subtract(amount4));
    }

    public String getProfit(){
        BigDecimal amount3 = new BigDecimal(getChangePrice());
        BigDecimal amount4 = new BigDecimal(getQty());
        return doubleFormat(""+amount3.multiply(amount4));
    }

    public String getChangePricePercent(){
        try {
            return data.getString("change_percent");
        } catch (JSONException e) {
            return "0.0";
        }
    }

    private String doubleFormat(String number){
        String str = String.format("%.2f", Double.parseDouble(number));
        if(str.equals(""))
            str = "0.00";
        return str;
    }

}

