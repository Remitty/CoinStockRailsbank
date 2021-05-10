package com.wyre.trade.model;

import org.json.JSONObject;

import java.text.DecimalFormat;

public class BankInfo {
    JSONObject data;

    public void setData(JSONObject data) {
        this.data = data;
    }

    public String getId(){return data.optString("id");}
    public String getBankId(){return data.optString("bank_id");}
    public String getBankAlias(){return data.optString("alias");}
    public String getBankType(){return data.optString("type");}
    public String getBalance(){return new DecimalFormat("#,###.####").format(data.optDouble("balance"));}
    public String getUSAccountNo(){return data.optString("us_account_number");}
    public String getUSRoutingNo(){return data.optString("us_routing_number");}
    public String getUKAccount(){return data.optString("uk_account_number");}
    public String getUKSortCode(){return data.optString("uk_sort_code");}
    public String getIban(){
        String iban = "";
        if(!data.optString("iban").equals("null"))
            iban = data.optString("iban");
        return iban;
    }
    public String getSwift(){
        String bic_swift = "";
        if(!data.optString("bic_swift").equals("null"))
            bic_swift = data.optString("bic_swift");
        return bic_swift;
    }
    public String getCurrency(){return data.optJSONObject("currency").optString("currency");}
    public String getCurrencySymbol(){return data.optJSONObject("currency").optString("symbol");}
    public String getCurrencyID(){return data.optJSONObject("currency").optString("id");}
}
