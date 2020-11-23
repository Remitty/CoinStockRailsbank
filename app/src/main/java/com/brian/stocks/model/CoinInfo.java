package com.brian.stocks.model;

import com.brian.stocks.helper.URLHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class CoinInfo {
    private JSONObject data;

    public CoinInfo(JSONObject item) {
        data = item;
    }

    public JSONObject getData() {
        return data;
    }

    public String getCoinName() {
        return data.optString("coin_name");
    }

    public String getCoinOtherName() {
        return data.optString("coin_other_name");
    }

    public String getCoinId() {
        return data.optString("id");
    }

    public String getCoinIcon() {
        String path = data.optString("icon");
        if(!path.startsWith("http"))
            path = URLHelper.base + path;
        return path;
    }

    public String getCoinSymbol() {
        String symbol;
        symbol = data.optString("coin_symbol");
        if(data.optString("coin_symbol").equals("XBT"))
            symbol = "BTC";

        return symbol;
    }

    public String getCoinBalance() {

        return data.optString("balance");
    }

    public String getCoinRate() {
        try {
            return data.getString("coin_rate");
        } catch (JSONException e) {
            e.printStackTrace();
            return "0.00";
        }
    }

    public String getCoinExchangeRate() {
        return data.optString("exchange_rate");
    }

    public String getCoinUsdc() {
        BigDecimal balance = new BigDecimal(data.optString("est_usdc")).setScale(4, BigDecimal.ROUND_HALF_UP);
        return balance.toString();
    }

    public String getWithdrawalFee() {
        try {
            return data.getString("withdrawal_fee");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getCoinEffect() {
        BigDecimal balance = new BigDecimal(data.optString("change_rate"));
        return balance.toString();
    }

    public Boolean getTradable() {
        try {
            return data.getBoolean("tradable");
        } catch (JSONException e) {
            return false;
        }
    }

}

