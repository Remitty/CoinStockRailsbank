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
        String path = "";
        try {
            path = data.getString("coin_icon");
            if(path.equals("") || path.equals("null")) {
                path = data.getString("icon");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            try {
                path = data.getString("icon");
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
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

        try {
            return String.format("%.6f", Double.parseDouble(data.getString("balance")));
        } catch (JSONException e) {
            return "0.0";
        }
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
        try {
            return data.getString("exchange_rate");
        } catch (JSONException e) {
            return "0.0";
        }
    }

    public String getCoinUsdc() {
        BigDecimal balance = null;
        try {
            balance = new BigDecimal(data.getString("est_usdc")).setScale(4, BigDecimal.ROUND_HALF_UP);
        } catch (JSONException e) {
            return "0.0";
        }
        return balance.toString();
    }

    public String getWithdrawalFee() {
        try {
            return String.format("%.6f", Double.parseDouble(data.getString("withdrawal_fee")));
        } catch (JSONException e) {
            return "";
        }
    }

    public String getCoinEffect() {
        BigDecimal balance = null;
        try {
            balance = new BigDecimal(data.getString("change_rate"));
        } catch (JSONException e) {
            return "0.0";
        }
        return balance.toString();
    }

    public Boolean getTradable() {
        try {
            return data.getBoolean("tradable");
        } catch (JSONException e) {
            return false;
        }
    }

    public int getBuyNowOption() {
        try {
            return data.getInt("buy_now");
        } catch (JSONException e) {
            return 0;
        }
    }

}

