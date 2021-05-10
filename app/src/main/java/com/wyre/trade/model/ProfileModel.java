package com.wyre.trade.model;

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
            String str = data.getString("mobile");
            if(str.equals("null")) str = "";
            return str;
        } catch (JSONException e) {
            return "";
        }
    }

    public String getCountryCode(){
        try {
            String str = data.getString("country_code");
            if(str.equals("null")) str = "";
            return str;
        } catch (JSONException e) {
            return "+1";
        }
    }

    public String getPostalCode(){
        try {
            String postalcode = data.getJSONObject("profile").getString("postalcode");
            if(postalcode.equals("null")) postalcode = "";
            return postalcode;
        } catch (JSONException e) {
            return "";
        }
    }

    public String getFirstAddress(){
        try {
            String address = data.getJSONObject("profile").getString("address");
            if(address.equals("null")) address = "";
            return address;
        } catch (JSONException e) {
            return "";
        }
    }

    public String getSecondAddress(){
        try {
            String address = data.getJSONObject("profile").getString("address2");
            if(address.equals("null")) address = "";
            return address;
        } catch (JSONException e) {
            return "";
        }
    }

    public String getCountry(){
        try {
            String str = data.getJSONObject("profile").getString("country");
            if(str.equals("null")) str = "";
            return str;
        } catch (JSONException e) {
            return "";
        }
    }

    public String getCity(){
        try {
            String str = data.getJSONObject("profile").getString("city");
            if(str.equals("null")) str = "";
            return str;
        } catch (JSONException e) {
            return "";
        }
    }

    public String getDOB(){
        try {
            String str = data.getJSONObject("profile").getString("dob");
            if(str.equals("null")) str = "";
            return str;
        } catch (JSONException e) {
            return "";
        }
    }

    public String getRegion(){
        try {
            String str = data.getJSONObject("profile").getString("region");
            if(str.equals("null")) str = "";
            return str;
        } catch (JSONException e) {
            return "";
        }
    }

    public String getState(){
        try {
            String str = data.getJSONObject("profile").getString("state");
            if(str.equals("null")) str = "";
            return str;
        } catch (JSONException e) {
            return "";
        }
    }

    public String getSSN(){
        try {
            String str = data.getJSONObject("profile").getString("ssn_last_4");
            if(str.equals("null")) str = "";
            return str;
        } catch (JSONException e) {
            return "";
        }
    }
}
