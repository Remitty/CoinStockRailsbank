package com.wyre.trade.payment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wyre.trade.R;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.model.Card;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddPaypalActivity extends AppCompatActivity {
    LoadToast loadToast;

    EditText editPaypal;
    Button btnAdd;
    ImageView imgDelete;
    String paypal = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_paypal);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("");

        loadToast = new LoadToast(this);

        initComponents();
        initListeners();

        if(getIntent().getStringExtra("paypal") != null) {
            paypal = getIntent().getStringExtra("paypal");
            editPaypal.setText(paypal);
            btnAdd.setText("Update");
        } else {
            imgDelete.setVisibility(View.GONE);
        }
    }

    private void initComponents() {
        editPaypal = findViewById(R.id.edit_paypal);
        btnAdd = findViewById(R.id.btn_add_paypal);
        imgDelete = findViewById(R.id.img_delete);
    }

    private void initListeners() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editPaypal.getText().toString().isEmpty()) {
                    editPaypal.setError("!");
                    return;
                }
                Pattern p = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b");
                Matcher m = p.matcher(editPaypal.getText().toString());
                if (!m.find()) {
                    editPaypal.setError("Invalid Email");
                    return;
                }

                editPaypal();
            }
        });
        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(AddPaypalActivity.this);
                alert.setIcon(R.mipmap.ic_launcher_round)
                        .setTitle("Confirm")
                        .setMessage("Are you sure delete this paypal?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deletePaypal();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    private void deletePaypal() {
        loadToast.show();
        AndroidNetworking.delete(URLHelper.REQUEST_PAYPAL+"/{paypal}")
                .addPathParameter("paypal", editPaypal.getText().toString())
                .addHeaders("Content-Type", "application/json")
                .addHeaders("accept", "application/json")
                .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getBaseContext(),"access_token"))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadToast.success();

                        Toast.makeText(getBaseContext(), "Deleted successfully", Toast.LENGTH_SHORT).show();
                        editPaypal.setText("");
                        btnAdd.setText("Add Paypal");
                        imgDelete.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(ANError error) {
                        loadToast.error();
                        // handle error
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddPaypalActivity.this);
                        builder.setIcon(R.mipmap.ic_launcher_round)
                                .setTitle("Alert")
                                .setMessage(error.getErrorBody())
                                .setPositiveButton("Yes", null)
                                .show();
                    }
                });
    }

    private void editPaypal() {
        loadToast.show();
        AndroidNetworking.post(URLHelper.REQUEST_PAYPAL)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("accept", "application/json")
                .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getBaseContext(),"access_token"))
                .addBodyParameter("paypal", editPaypal.getText().toString())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadToast.success();
                        Toast.makeText(getBaseContext(), response.optString("message"), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ANError error) {
                        loadToast.error();
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddPaypalActivity.this);
                        builder.setIcon(R.mipmap.ic_launcher_round)
                                .setTitle("Alert")
                                .setMessage(error.getErrorBody())
                                .setPositiveButton("Yes", null)
                                .show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
