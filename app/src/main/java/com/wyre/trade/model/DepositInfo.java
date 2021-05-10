package com.wyre.trade.model;

import com.wyre.trade.helper.URLHelper;

import org.json.JSONObject;

public class DepositInfo {
    private JSONObject data;

    public DepositInfo(JSONObject item) {
        data = item;
    }

    public JSONObject getData() {
        return data;
    }

    public String getCoinSymbol() {
        return data.optString("coin_name");
    }

    public String getAmount() {
        String amount = data.optString("received_amount");
        if(amount.equals("null"))
            amount = "0";
        return amount;
    }

    public String getDepositDate() {
        return data.optString("date");
    }

    public String getCoinIcon() {
        String path = data.optString("icon");
        if(!path.startsWith("http"))
            path = URLHelper.base + path;
        return path;
    }

    public String getStatus() {
        return data.optString("status_text");
    }
}
