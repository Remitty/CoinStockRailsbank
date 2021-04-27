package com.wyre.trade.helper;

import java.math.BigDecimal;

public class BigDecimalDouble {
    BigDecimalDouble() {

    }

    public static BigDecimalDouble newInstance() {
        BigDecimalDouble fragment = new BigDecimalDouble();
        return fragment;
    }

    public Double multify(String num1, String num2){
        BigDecimal number1 = new BigDecimal(num1);
        BigDecimal number2 = new BigDecimal(num2);
        return number1.multiply(number2).doubleValue();
    }

    public String divide(String num1, String num2){
        BigDecimal number1 = new BigDecimal(num1);
        BigDecimal number2 = new BigDecimal(num2);
        return number1.divide(number2).toString();
    }

    public Double add(String num1, String num2){
        BigDecimal number1 = new BigDecimal(num1);
        BigDecimal number2 = new BigDecimal(num2);
        return number1.add(number2).doubleValue();
    }

    public String sub(String num1, String num2){
        BigDecimal number1 = new BigDecimal(num1);
        BigDecimal number2 = new BigDecimal(num2);
        return number1.subtract(number2).toString();
    }
}
