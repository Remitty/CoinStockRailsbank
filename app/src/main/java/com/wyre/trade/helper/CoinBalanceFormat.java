package com.wyre.trade.helper;

import androidx.annotation.NonNull;

import java.text.DecimalFormat;

public class CoinBalanceFormat {
    Double balance = 0.0;
    public CoinBalanceFormat(Double value) {
        balance = value;
    }

    public String toString() {
        if(balance == 0)
            return "0.0000";
        return new DecimalFormat("#,###.####").format(balance);
    }
}
