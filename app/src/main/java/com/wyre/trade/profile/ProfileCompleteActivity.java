package com.wyre.trade.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wyre.trade.R;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.main.MainActivity;
import com.phonenumberui.CountryCodeActivity;
import com.phonenumberui.countrycode.Country;
import com.phonenumberui.countrycode.CountryUtils;
import com.phonenumberui.utility.Utility;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.regex.Pattern;

public class ProfileCompleteActivity extends AppCompatActivity {
    EditText editTextAddress1, editTextAddress2, editTextCity, editCountry,
            editTextPostalCode, editTextNational;
    Button btnUpdate;
    TextView editDob, editTextName;

    private AppCompatEditText etCountryCode;
    private AppCompatEditText etPhoneNumber;
    private ImageView imgFlag;
    private Country mSelectedCountry;
    private static final int COUNTRYCODE_ACTION = 10001;
    private PhoneNumberUtil mPhoneUtil;
    DatePickerDialog datePicker;

    private LoadToast loadToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_complete);

        loadToast = new LoadToast(this);
        //loadToast.setBackgroundColor(R.color.colorBlack);

        initComponents();
        initListeners();
        setPhoneUI();
    }

    private void initComponents() {
        editTextName = findViewById(R.id.text_viewName);
        editTextName.setText(SharedHelper.getKey(this, "fullName"));
        editTextAddress1 = findViewById(R.id.editTextAddress1);
        editTextAddress2 = findViewById(R.id.editTextAddress2);
        editTextPostalCode = findViewById(R.id.editTextPostalCode);
        editTextNational = findViewById(R.id.editTextNational);
        editCountry = findViewById(R.id.editTextCountry);
        editTextCity = findViewById(R.id.editTextCity);
        editDob = findViewById(R.id.editTextDOB);

        etCountryCode = findViewById(R.id.etCountryCode);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        imgFlag = findViewById(R.id.flag_img);

        btnUpdate = findViewById(R.id.btn_update_profile);
    }

    private void initListeners() {

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validation())
                    sendUpdateProfile();
            }
        });

        editDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            datePicker = new DatePickerDialog(ProfileCompleteActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    editDob.setText(year + "-"  + (monthOfYear + 1) + "-" + dayOfMonth );
                    datePicker.dismiss();
                }
            }, year, month, day);
        }
    }

    private boolean validation() {
        boolean validFlag = true;
        // Check if all strings are null or not
        Pattern p = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b");

        if (TextUtils.isEmpty(editTextAddress1.getText().toString())) {
            editTextAddress1.setError("!");
            validFlag = false;
        }
        if (TextUtils.isEmpty(editTextAddress2.getText().toString())) {
            editTextAddress2.setError("!");
            validFlag = false;
        }
        if (TextUtils.isEmpty(etPhoneNumber.getText().toString())) {
            etPhoneNumber.setError("!");
            validFlag = false;
        }
        if (TextUtils.isEmpty(editTextPostalCode.getText().toString())) {
            editTextPostalCode.setError("!");
            validFlag = false;
        }
        if (TextUtils.isEmpty(editDob.getText().toString())) {
            editDob.setError("!");
            validFlag = false;
        }
        if (TextUtils.isEmpty(editTextCity.getText().toString())) {
            editTextCity.setError("!");
            validFlag = false;
        }
        if (TextUtils.isEmpty(editCountry.getText().toString())) {
            editCountry.setError("!");
            validFlag = false;
        }
        if (TextUtils.isEmpty(editTextNational.getText().toString())) {
            editTextNational.setError("!");
            validFlag = false;
        }
        validFlag = validatePhone();
        return validFlag;
    }

    private void sendUpdateProfile() {
        loadToast.show();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobile", etPhoneNumber.getText().toString().trim());
            jsonObject.put("country_code", etCountryCode.getText().toString().trim());
            jsonObject.put("address1", editTextAddress1.getText().toString().trim());
            jsonObject.put("address2", editTextAddress2.getText().toString().trim());
            jsonObject.put("postalcode", editTextPostalCode.getText().toString().trim());
            jsonObject.put("city", editTextCity.getText().toString().trim());
            jsonObject.put("country", editCountry.getText().toString().trim());
            jsonObject.put("dob", editDob.getText().toString().trim());
            jsonObject.put("region", editTextNational.getText().toString().trim());
            jsonObject.put("user_type", 2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(getBaseContext() != null)
            AndroidNetworking.post(URLHelper.UseProfileUpdate)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("accept", "application/json")
                    .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getBaseContext(),"access_token"))
                    .addJSONObjectBody(jsonObject)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("response", "" + response);
                            loadToast.success();
                            if(response.optBoolean("success")) {
                                Toast.makeText(getBaseContext(), "Updated successfully", Toast.LENGTH_SHORT).show();

                                SharedHelper.putKey(getBaseContext(), "is_completed", "true");

                                startActivity(new Intent(ProfileCompleteActivity.this, MainActivity.class));
                            }
                            else
                             Toast.makeText(getBaseContext(), response.optString("message"), Toast.LENGTH_SHORT).show();
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


    private void setPhoneUI() {
        mPhoneUtil = PhoneNumberUtil.createInstance(this);
        TelephonyManager tm = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
        String countryISO = tm.getNetworkCountryIso();
        String countryNumber = "";
        String countryName = "";
        Utility.log(countryISO);

        if(!TextUtils.isEmpty(countryISO))
        {
            for (Country country : CountryUtils.getAllCountries(this)) {
                if (countryISO.toLowerCase().equalsIgnoreCase(country.getIso().toLowerCase())) {
                    countryNumber = country.getPhoneCode();
                    countryName = country.getName();
                    break;
                }
            }
            Country country = new Country(countryISO,
                    countryNumber,
                    countryName);
            this.mSelectedCountry = country;
            etCountryCode.setText("+" + country.getPhoneCode() + "");
            imgFlag.setImageResource(CountryUtils.getFlagDrawableResId(country.getIso()));
            Utility.log(countryNumber);
        }
        else {
            Country country = new Country(getString(com.phonenumberui.R.string.country_united_states_code),
                    getString(com.phonenumberui.R.string.country_united_states_number),
                    getString(com.phonenumberui.R.string.country_united_states_name));
            this.mSelectedCountry = country;
            etCountryCode.setText("+" + country.getPhoneCode() + "");
            imgFlag.setImageResource(CountryUtils.getFlagDrawableResId(country.getIso()));
            Utility.log(countryNumber);
        }

        setPhoneNumberHint();
        etCountryCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.hideKeyBoardFromView(ProfileCompleteActivity.this);
                etPhoneNumber.setError(null);
                Intent intent = new Intent(ProfileCompleteActivity.this, CountryCodeActivity.class);
                intent.putExtra("TITLE", getResources().getString(com.phonenumberui.R.string.app_name));
                startActivityForResult(intent, COUNTRYCODE_ACTION);
            }
        });
    }
    private void setPhoneNumberHint() {
        if (mSelectedCountry != null) {
            Phonenumber.PhoneNumber phoneNumber =
                    mPhoneUtil.getExampleNumberForType(mSelectedCountry.getIso().toUpperCase(),
                            PhoneNumberUtil.PhoneNumberType.MOBILE);
            if (phoneNumber != null) {
                String format = mPhoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
                if (format.length() > mSelectedCountry.getPhoneCode().length())
                    etPhoneNumber.setHint(
                            format.substring((mSelectedCountry.getPhoneCode().length() + 1), format.length()));
            }
        }
    }

    private boolean validatePhone() {
        if (TextUtils.isEmpty(etPhoneNumber.getText().toString().trim())) {
            etPhoneNumber.setError("Please enter phone number");
            etPhoneNumber.requestFocus();
            return false;
        } else if (!isValid()) {
            etPhoneNumber.setError("Please enter valid phone number");
            etPhoneNumber.requestFocus();
            return false;
        }
        return true;
    }

    public boolean isValid() {
        Phonenumber.PhoneNumber phoneNumber = getPhoneNumber();
        return phoneNumber != null && mPhoneUtil.isValidNumber(phoneNumber);
    }

    public Phonenumber.PhoneNumber getPhoneNumber() {
        try {
            String iso = null;
            if (mSelectedCountry != null) {
                iso = mSelectedCountry.getIso().toUpperCase();
            }
            return mPhoneUtil.parse(etPhoneNumber.getText().toString().trim(), iso);
        } catch (NumberParseException ignored) {
            ignored.printStackTrace();
            return null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == COUNTRYCODE_ACTION) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    if (data.hasExtra("COUNTRY")) {
                        Country country = (Country) data.getSerializableExtra("COUNTRY");
                        this.mSelectedCountry = country;
                        setPhoneNumberHint();
                        etCountryCode.setText("+" + country.getPhoneCode() + "");
                        imgFlag.setImageResource(CountryUtils.getFlagDrawableResId(country.getIso()));
                    }
                }
            }
        }
    }
}
