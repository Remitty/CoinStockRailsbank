package com.wyre.trade.helper;

import java.text.DecimalFormat;

public class PriceFormat {
    Double price = 0.0;
    public PriceFormat(Double value) {
        price = value;
    }

    public String toString() {
        if(price == 0)
            return "$ 0.00";
        return "$ " + new DecimalFormat("#,###.##").format(price);
    }
}
