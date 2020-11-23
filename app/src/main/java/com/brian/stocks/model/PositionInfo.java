package com.brian.stocks.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

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
        return data.optString("filled_qty");
    }

    public String getAvgPrice() {
        return DoubleFormat(data.optString("avg_entry_price"));
    }

    public String getCurrentPrice(){
        return DoubleFormat(data.optString("current_price"));
    }

    public String getEquity(){
        BigDecimal amount3 = new BigDecimal(getCurrentPrice());
        BigDecimal amount4 = new BigDecimal(getQty());
        return DoubleFormat(""+amount3.multiply(amount4));
    }

    public String getLastDayPrice(){
        return DoubleFormat(data.optString("lastday_price"));
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
        return DoubleFormat(data.optString("change_today"));
    }

    private String DoubleFormat(String number){
        return String.format("%.4f", Double.parseDouble(number));
    }

}

