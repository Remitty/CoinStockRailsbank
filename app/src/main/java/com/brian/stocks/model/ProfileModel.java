package com.brian.stocks.model;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileModel {
    private JSONObject data;

    public void setData(JSONObject data){this.data = data;}

    public String getFirstName(){
        try {
            return data.getString("first_name");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getLastName(){
        try {
            return data.getString("last_name");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getEmail(){
        try {
            return data.getString("email");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getMobile(){
        try {
            return data.getString("mobile");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getCountryCode(){
        try {
            return data.getString("country_code");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getPostalCode(){
        try {
            return data.getJSONObject("payment").getString("postalcode");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getFirstAddress(){
        try {
            return data.getJSONObject("payment").getString("address");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getSecondAddress(){
        try {
            return data.getJSONObject("payment").getString("address2");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getCountry(){
        try {
            return data.getJSONObject("payment").getString("country");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getState(){
        try {
            return data.getJSONObject("payment").getString("state");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getCity(){
        try {
            return data.getJSONObject("payment").getString("city");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getDOB(){
        try {
            return data.getJSONObject("payment").getString("dob");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getRegion(){
        try {
            return data.getJSONObject("payment").getString("region");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getAccountNumber(){
        try {
            String account = data.getJSONObject("payment").getString("account_number");
            if( account.equals("null")) return "";
            return data.getJSONObject("payment").getString("account_number");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getBankName(){
        try {
            String bank = data.getJSONObject("payment").getString("bank_name");
            if(bank.equals("null")) return "";
            return bank;
        } catch (JSONException e) {
            return "";
        }
    }

    public String getAcoountHolderName(){
        try {
            String holder = data.getJSONObject("payment").getString("account_holder_name");
            if(holder.equals("null")) return "";
            return holder;
        } catch (JSONException e) {
            return "";
        }
    }

    public String getAccountRoutingNumber(){
        try {
            String routing = data.getJSONObject("payment").getString("account_routing_number");
            if(routing.equals("null")) return "";
            return routing;
        } catch (JSONException e) {
            return "";
        }
    }
}
