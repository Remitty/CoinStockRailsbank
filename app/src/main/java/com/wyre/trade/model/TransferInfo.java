package com.wyre.trade.model;

import org.json.JSONException;
import org.json.JSONObject;

public class TransferInfo {
    private JSONObject data;
    public TransferInfo(JSONObject item) {
        data = item;
    }

    public JSONObject getData() {
        return data;
    }

    public String getAmount(){
        return data.optString("amount");
    }

    public String getReceived(){
        return data.optString("payable");
    }

    public String getStatus(){
        return data.optString("status");
    }

    public String getUnit() {
        try {
            return  data.getString("coin");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getDate(){
        String timestamp = data.optString("created_at");
        return timestamp.split(" ")[0];
    }
}
