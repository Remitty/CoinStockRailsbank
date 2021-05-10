package com.wyre.trade.model;

import org.json.JSONObject;

public class BankTransaction {
    JSONObject data = new JSONObject();

    public void setData(JSONObject data) {
        this.data = data;
    }

    public String getCurrency(){return data.optString("currency");}
    public String getGetterName(){
        String getter = data.optJSONObject("bank").optString("alias");
        if(getter == null || getter.equals("null")) getter = "";
        return getter;
    }
    public String getGetterId(){return data.optString("getter_id");}
    public String getAmount(){return data.optString("amount");}
    public String getType(){return data.optString("type");}
    public String getStatus(){return data.optString("status");}
    public String getDate(){return data.optString("created_at").substring(0, 10);}
}
