package com.wyre.trade.model;

import org.json.JSONObject;

public class ZaboAccount {
    private JSONObject data;

    public void setData(JSONObject data) {
        this.data = data;
    }

    public String getId() {
        return data.optString("account_id");
    }

    public String getAssetsCount() {
        return data.optString("asset_cnt");
    }

    public Double getBalance() {
        return data.optDouble("balance");
    }

    public String getProvider() {
        String provider = data.optString("provider");
        if(provider.equals("null"))
            provider = "";
        return provider;
    }

    public String getProviderLogo() {
        return data.optString("provider_logo");
    }
}
