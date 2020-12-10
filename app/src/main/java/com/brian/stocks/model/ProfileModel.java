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
            return "+1";
        }
    }

    public String getPostalCode(){
        try {
            return data.getJSONObject("profile").getString("postalcode");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getFirstAddress(){
        try {
            return data.getJSONObject("profile").getString("address");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getSecondAddress(){
        try {
            return data.getJSONObject("profile").getString("address2");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getCountry(){
        try {
            return data.getJSONObject("profile").getString("country");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getCity(){
        try {
            return data.getJSONObject("profile").getString("city");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getDOB(){
        try {
            return data.getJSONObject("profile").getString("dob");
        } catch (JSONException e) {
            return "";
        }
    }

    public String getRegion(){
        try {
            return data.getJSONObject("profile").getString("region");
        } catch (JSONException e) {
            return "";
        }
    }

}
