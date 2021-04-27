package com.wyre.trade.cash;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wyre.trade.R;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONException;
import org.json.JSONObject;

public class AddBankActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner currencySpinner;
    EditText mAlias, mAddress1, mAddress2, mState, mPostalCode, mUkAccountNo, mUkShortCode, mIban, mSwift, mUsAccountNo, mUsRoutingNo;
    Button btnAdd;
    int selectedcurrency = 0;

    private LoadToast loadToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_bank);

        if(getSupportActionBar() != null) {
             getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        loadToast = new LoadToast(this);
        //loadToast.setBackgroundColor(R.color.colorBlack);

        mAlias = findViewById(R.id.edit_bank_alias);
        mAddress1 = findViewById(R.id.edit_address1);
        mAddress2 = findViewById(R.id.edit_address2);
        mState = findViewById(R.id.edit_state);
        mPostalCode = findViewById(R.id.edit_postal_code);
        mUkAccountNo = findViewById(R.id.edit_uk_account_no);
        mUkShortCode = findViewById(R.id.edit_uk_sort_code);
        mIban = findViewById(R.id.edit_iban);
        mSwift = findViewById(R.id.edit_swift);
        mUsAccountNo = findViewById(R.id.edit_us_account_no);
        mUsRoutingNo = findViewById(R.id.edit_us_routing_no);

        currencySpinner = findViewById(R.id.bank_currency);
        btnAdd = findViewById(R.id.btn_add);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate())
                    addBank();
            }
        });

        String[] ADD_CURRENCIES = {"Add currency", "USD", "EUR", "GBP"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, ADD_CURRENCIES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(adapter);
        currencySpinner.setOnItemSelectedListener(this);
    }

    private boolean validate() {
        boolean validate = true;
        if(selectedcurrency == 0) {
            Toast.makeText(getBaseContext(), "Please select currency", Toast.LENGTH_SHORT).show();
            validate = false;
        }
        if(TextUtils.isEmpty(mAlias.getText().toString())){
            mAlias.setError("!");
            validate = false;
        }
//        if(TextUtils.isEmpty(mAddress1.getText().toString())){
//            mAddress1.setError("!");
//            validate = false;
//        }
//        if(TextUtils.isEmpty(mAddress2.getText().toString())){
//            mAddress2.setError("!");
//            validate = false;
//        }
//        if(TextUtils.isEmpty(mState.getText().toString())){
//            mState.setError("!");
//            validate = false;
//        }
        if(TextUtils.isEmpty(mPostalCode.getText().toString())){
            mPostalCode.setError("!");
            validate = false;
        }
        if(selectedcurrency == 1) { //USD
            if(TextUtils.isEmpty(mUsAccountNo.getText().toString())){
                mUsAccountNo.setError("!");
                validate = false;
            }
            if(TextUtils.isEmpty(mUsRoutingNo.getText().toString())){
                mUsRoutingNo.setError("!");
                validate = false;
            }
        }
        if(selectedcurrency == 2) {//EUR
            if(TextUtils.isEmpty(mIban.getText().toString())){
                mIban.setError("!");
                validate = false;
            }
            if(TextUtils.isEmpty(mSwift.getText().toString())){
                mSwift.setError("!");
                validate = false;
            }
        }
        if(selectedcurrency == 3) {//GBP
            if(TextUtils.isEmpty(mUkAccountNo.getText().toString())){
                mUkAccountNo.setError("!");
                validate = false;
            }
            if(TextUtils.isEmpty(mUkShortCode.getText().toString())){
                mUkShortCode.setError("!");
                validate = false;
            }
        }

        return validate;
    }

    private void addBank() {
        loadToast.show();
        JSONObject object = new JSONObject();
        try {
            object.put("currency", selectedcurrency);
            object.put("usertype", 1);
            object.put("alias", mAlias.getText().toString());
            object.put("address1", mAddress1.getText().toString());
            object.put("address2", mAddress2.getText().toString());
            object.put("state", mState.getText().toString());
            object.put("postal_code", mPostalCode.getText().toString());
            if(selectedcurrency == 1) {
                object.put("us_account_no", mUsAccountNo.getText().toString());
                object.put("us_routing_no", mUsRoutingNo.getText().toString());
            }
            if(selectedcurrency == 2) { // EUR
                object.put("iban", mIban.getText().toString());
                object.put("bic_swift", mSwift.getText().toString());
            }
            if(selectedcurrency == 3) { // GBP
                object.put("uk_account_no", mUkAccountNo.getText().toString());
                object.put("uk_sort_code", mUkShortCode.getText().toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("add friend bank params", object.toString());
        AndroidNetworking.post(URLHelper.REQUEST_ADD_FRIEND_BANK)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("accept", "application/json")
                .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(this,"access_token"))
                .addJSONObjectBody(object)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", "" + response);
                        loadToast.success();
                        Toast.makeText(getBaseContext(), "Added successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ANError error) {
                        loadToast.error();
                        // handle error
                        Toast.makeText(getBaseContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                        Log.d("errorm", "" + error.getMessage());
                    }
                });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedcurrency = position;
        Log.d("selected currency", selectedcurrency+"");
        if(position == 1) { //USD
            mIban.setVisibility(View.GONE);
            mSwift.setVisibility(View.GONE);
            mUkAccountNo.setVisibility(View.GONE);
            mUkShortCode.setVisibility(View.GONE);
            mUsAccountNo.setVisibility(View.VISIBLE);
            mUsRoutingNo.setVisibility(View.VISIBLE);
        }
        if(position == 2) { //EUR
            mIban.setVisibility(View.VISIBLE);
            mSwift.setVisibility(View.VISIBLE);
            mUkAccountNo.setVisibility(View.GONE);
            mUkShortCode.setVisibility(View.GONE);
            mUsAccountNo.setVisibility(View.GONE);
            mUsRoutingNo.setVisibility(View.GONE);
        }
        if(position == 3) { //GBP
            mIban.setVisibility(View.GONE);
            mSwift.setVisibility(View.GONE);
            mUkAccountNo.setVisibility(View.VISIBLE);
            mUkShortCode.setVisibility(View.VISIBLE);
            mUsAccountNo.setVisibility(View.GONE);
            mUsRoutingNo.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
