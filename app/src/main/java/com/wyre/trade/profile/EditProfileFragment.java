package com.wyre.trade.profile;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.phonenumberui.AppConstant;
import com.phonenumberui.PhoneNumberActivity;
import com.phonenumberui.VerificationCodeActivity;
import com.wyre.trade.R;
import com.wyre.trade.helper.ConfirmAlert;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.home.HomeActivity;
import com.wyre.trade.main.ChangePasswordActivity;
import com.wyre.trade.model.ProfileModel;
import com.phonenumberui.CountryCodeActivity;
import com.phonenumberui.countrycode.Country;
import com.phonenumberui.countrycode.CountryUtils;
import com.phonenumberui.utility.Utility;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

import static android.app.Activity.RESULT_OK;

public class EditProfileFragment extends Fragment{
    private static final int REQUEST_PHONE_VERIFICATION = 1080;
    View mView;
    EditText editTextName, editTextLastName, editTextEmail, editTextAddress1, editTextAddress2, editTextCity, editCountry,
            editTextPostalCode, editTextNational, editTextState, editTextSSN;
    Button btnUpdate;
    TextView textViewChangePwd, editDob;
    private String countrycode, mobile;

    private AppCompatEditText etCountryCode;
    private AppCompatEditText etPhoneNumber;
    private ImageView imgFlag;
    private Country mSelectedCountry;
    private static final int COUNTRYCODE_ACTION = 10001;
    private PhoneNumberUtil mPhoneUtil;
    DatePickerDialog datePicker;

    private LoadToast loadToast;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    public static EditProfileFragment newInstance() {
        EditProfileFragment fragment = new EditProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        loadToast = new LoadToast(getActivity());
        initComponents();
        initListeners();

        getProfile();

        setPhoneUI();

        return mView;
    }


    private void initComponents() {
        editTextName = mView.findViewById(R.id.editTextName);
        editTextLastName = mView.findViewById(R.id.editTextLastName);
        editTextEmail = mView.findViewById(R.id.editTextEmail);
        editTextAddress1 = mView.findViewById(R.id.editTextAddress1);
        editTextAddress2 = mView.findViewById(R.id.editTextAddress2);
        editTextPostalCode = mView.findViewById(R.id.editTextPostalCode);
        editTextNational = mView.findViewById(R.id.editTextNational);
        editCountry = mView.findViewById(R.id.editTextCountry);
        editTextCity = mView.findViewById(R.id.editTextCity);
        editTextState = mView.findViewById(R.id.editTextState);
        editTextSSN = mView.findViewById(R.id.editTextSSN);
        editDob = mView.findViewById(R.id.editTextDOB);

        etCountryCode = mView.findViewById(R.id.etCountryCode);
        etPhoneNumber = mView.findViewById(R.id.etPhoneNumber);
        imgFlag = mView.findViewById(R.id.flag_img);

        textViewChangePwd = mView.findViewById(R.id.textViewChangePwd);

        btnUpdate = mView.findViewById(R.id.btn_update_profile);


    }

    private void initListeners() {


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validation()) {
//                    if(!etCountryCode.getText().toString().equals(countrycode) || !etPhoneNumber.getText().toString().equals(mobile))
//                        verifyPhone();
//                    else
                        sendUpdateProfile();
                }
            }
        });

        textViewChangePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
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
            datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
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


        if (TextUtils.isEmpty(editTextName.getText().toString())) {
            editTextName.setError("!");
            validFlag = false;
        }
        if (TextUtils.isEmpty(editTextLastName.getText().toString())) {
            editTextLastName.setError("!");
            validFlag = false;
        }
        if (TextUtils.isEmpty(editTextEmail.getText().toString())) {
            editTextEmail.setError("!");
            validFlag = false;
        } else {
            Pattern p = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b");
            Matcher m = p.matcher(editTextEmail.getText().toString());
            validFlag = m.matches();
            if(!validFlag)
                editTextEmail.setError("Invalid email format");
        }
        if (TextUtils.isEmpty(editTextAddress1.getText().toString())) {
            editTextAddress1.setError("!");
            validFlag = false;
        }
        if (TextUtils.isEmpty(editTextAddress2.getText().toString())) {
            editTextAddress2.setError("!");
            validFlag = false;
        }
        validFlag = validatePhone();
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
        if (TextUtils.isEmpty(editTextState.getText().toString())) {
            editTextState.setError("!");
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
        if(editCountry.getText().toString().equals("US")) {
            if (TextUtils.isEmpty(editTextSSN.getText().toString())) {
                editTextSSN.setError("!");
                validFlag = false;
            }
            else {
//                Pattern p = Pattern.compile( "^(?!666|000|9\\d{2})\\d{3}" + "-(?!00)\\d{2}-" + "(?!0{4})\\d{4}$" );
                Pattern p = Pattern.compile( "(?!0{4})\\d{4}$" );
                Matcher m = p.matcher(editTextSSN.getText().toString());
                validFlag = m.matches();
                if(!validFlag)
                    editTextSSN.setError("Invalid format");
            }
        }


        return validFlag;
    }

    private void sendUpdateProfile() {
        loadToast.show();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("first_name", editTextName.getText().toString());
            jsonObject.put("last_name", editTextLastName.getText().toString());
            jsonObject.put("email", editTextEmail.getText().toString());
            jsonObject.put("mobile", etPhoneNumber.getText().toString().trim());
            jsonObject.put("country_code", etCountryCode.getText().toString().trim());
            jsonObject.put("address1", editTextAddress1.getText().toString().trim());
            jsonObject.put("address2", editTextAddress2.getText().toString().trim());
            jsonObject.put("postalcode", editTextPostalCode.getText().toString().trim());
            jsonObject.put("city", editTextCity.getText().toString().trim());
            jsonObject.put("state", editTextState.getText().toString().trim());
            jsonObject.put("country", editCountry.getText().toString().trim());
            jsonObject.put("dob", editDob.getText().toString().trim());
            jsonObject.put("region", editTextNational.getText().toString().trim());
            jsonObject.put("ssn", editTextSSN.getText().toString().trim());
            jsonObject.put("user_type", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(getContext() != null)
            AndroidNetworking.post(URLHelper.UseProfileUpdate)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("accept", "application/json")
                    .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getContext(),"access_token"))
                    .addJSONObjectBody(jsonObject)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("response", "" + response);
                            loadToast.success();
                            if(response.optBoolean("success"))
                                startActivity(new Intent(getActivity(), HomeActivity.class));
                            Toast.makeText(getContext(), response.optString("message"), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(ANError error) {
                            loadToast.error();
                            // handle error
                            Toast.makeText(getContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                            Log.d("errorm", "" + error.getMessage());
                        }
                    });
    }

    private void getProfile() {
        loadToast.show();
        JSONObject jsonObject = new JSONObject();
        if(getContext() != null)
            AndroidNetworking.get(URLHelper.UserProfile)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("accept", "application/json")
                    .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(getContext(),"access_token"))
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("info Edit ProfileGet", "" + response);
                            loadToast.success();
//                            if (response.optBoolean("success")) {


                                ProfileModel profile = new ProfileModel();
                                profile.setData(response.optJSONObject("user"));

//                                textViewUserName.setText(profile.getFirstName() + " " + profile.getLastName());

                                editTextName.setText(profile.getFirstName());
                                editTextLastName.setText(profile.getLastName());
                                editTextEmail.setText(profile.getEmail());
                                mobile = profile.getMobile();
                                etPhoneNumber.setText(mobile);
                                countrycode = profile.getCountryCode();
                                etCountryCode.setText(countrycode);
                                editTextPostalCode.setText(profile.getPostalCode());
                                editTextAddress1.setText(profile.getFirstAddress());
                                editTextAddress2.setText(profile.getSecondAddress());
                                editTextCity.setText(profile.getCity());
                                editCountry.setText(profile.getCountry());
                                editTextState.setText(profile.getState());
                                editTextSSN.setText(profile.getSSN());
                                editDob.setText(profile.getDOB());
                                editTextNational.setText(profile.getRegion());

//                            } else {
//                                Toast.makeText(getActivity(), response.optString("message"), Toast.LENGTH_SHORT).show();
//                            }
                        }

                        @Override
                        public void onError(ANError error) {
                            loadToast.error();
                            // handle error
                            Toast.makeText(getContext(), "Please try again. Network error.", Toast.LENGTH_SHORT).show();
                            Log.d("errorm", "" + error.getMessage());
                        }
                    });
    }

    private void setPhoneUI() {
        mPhoneUtil = PhoneNumberUtil.createInstance(getActivity());
        TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(getActivity().TELEPHONY_SERVICE);
        String countryISO = tm.getNetworkCountryIso();
        String countryNumber = "";
        String countryName = "";
        Utility.log(countryISO);

        if(!TextUtils.isEmpty(countryISO))
        {
            for (Country country : CountryUtils.getAllCountries(getActivity())) {
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
                Utility.hideKeyBoardFromView(getActivity());
                etPhoneNumber.setError(null);
                Intent intent = new Intent(getActivity(), CountryCodeActivity.class);
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

    private void verifyPhone() {
        Intent verificationIntent = new Intent(getActivity(), VerificationCodeActivity.class);
        verificationIntent.putExtra("PhoneNumber", etPhoneNumber.getText().toString().trim());
        verificationIntent.putExtra("PhoneCode", etCountryCode.getText().toString());
        verificationIntent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        startActivityForResult(verificationIntent, REQUEST_PHONE_VERIFICATION);
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
        if(requestCode == REQUEST_PHONE_VERIFICATION) {
            if (data != null && data.hasExtra("PHONE_NUMBER") && data.getStringExtra("PHONE_NUMBER") != null) {
                sendUpdateProfile();
            } else {
                // If mobile number is not verified successfully You can hendle according to your requirement.
//                Toast.makeText(getContext(), "Mobile number verification fails",Toast.LENGTH_SHORT).show();
                ConfirmAlert alert = new ConfirmAlert(getActivity());
                alert.error("Mobile number verification fails");
            }
        }

    }

}
