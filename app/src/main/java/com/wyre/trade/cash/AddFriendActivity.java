package com.wyre.trade.cash;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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

public class AddFriendActivity extends AppCompatActivity {
    Spinner currencySpinner;
    Button btnAdd;
    EditText editName, editAccountId;
    private LoadToast loadToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        if(getSupportActionBar() != null) {
             getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        loadToast = new LoadToast(this);
        //loadToast.setBackgroundColor(R.color.colorBlack);

        editName = findViewById(R.id.edit_name);
        editAccountId = findViewById(R.id.edit_id);
        btnAdd = findViewById(R.id.btn_add);
        currencySpinner = findViewById(R.id.bank_currency);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate())
                    addFriend();
            }
        });

        String[] ADD_CURRENCIES = {"Add currency", "USD", "EUR", "GBP"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, ADD_CURRENCIES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(adapter);

    }

    private void addFriend() {
        loadToast.show();
        JSONObject object = new JSONObject();
        try {
            object.put("account_id", editAccountId.getText().toString());
            object.put("alias", editName.getText().toString());
            object.put("currency", currencySpinner.getSelectedItemPosition());

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

    private boolean validate() {
        boolean validate = true;
        if(currencySpinner.getSelectedItemPosition() == 0){
            Toast.makeText(getBaseContext(), "Please select currency", Toast.LENGTH_SHORT).show();
            validate = false;
        }
        if(TextUtils.isEmpty(editName.getText().toString())){
            editName.setError("!");
            validate = false;
        }
        if(TextUtils.isEmpty(editAccountId.getText().toString())){
            editAccountId.setError("!");
            validate = false;
        }
        return validate;
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
