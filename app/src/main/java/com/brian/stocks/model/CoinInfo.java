package com.brian.stocks.model;

import com.brian.stocks.helper.URLHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;

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
            return new DecimalFormat("#,###.####").format(data.getDouble("balance"));
        } catch (JSONException e) {
            return "0.00";
        }
    }

    public Double getCoinRate() {
        try {
            return data.getDouble("coin_rate");
        } catch (JSONException e) {
            e.printStackTrace();
            return 0.0;
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
        String balance = "0.0";
        try {
            balance = new DecimalFormat("#,###.##").format(data.getDouble("est_usdc"));
        } catch (JSONException e) {
        }
        return balance;
    }

    public String getWithdrawalFee() {
        try {
            return new DecimalFormat("#.######").format(data.getDouble("withdrawal_fee"));
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

