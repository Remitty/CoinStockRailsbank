package com.wyre.trade.model;

import org.json.JSONObject;

import java.text.DecimalFormat;

public class PredictionModel {
    private JSONObject data;

    public PredictionModel(JSONObject data) {
        this.data = data;
    }

    public String getContent() {
        return data.optString("content");
    }

    public String getSymbol() {
        return data.optString("symbol");
    }

    public long getDayLeft() {
        return data.optLong("day_left");
    }

    public String getStatus() {
        return data.optString("status");
    }

    public String getPrice() {
        String price;
        String status = data.optString("status");
        if(status.equals("Created") || status.equals("Processing")) {
            price = new DecimalFormat("#,###.##").format(data.optDouble("cu_price"));
        } else {
            price = new DecimalFormat("#,###.##").format(data.optDouble("result"));
        }

        return price;
    }

    public String getPayout() {
        return data.optString("bet_price") + "USDC";
    }
     public String getWinner() {
        return data.optString("win");
     }

     public String getBidder() {
        return data.optString("bidder");
     }

     public String getId() {
        return data.optString("id");
     }
}
