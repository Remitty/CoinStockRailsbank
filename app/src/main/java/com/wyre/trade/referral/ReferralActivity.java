package com.wyre.trade.referral;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.wyre.trade.R;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.helper.URLHelper;
import com.wyre.trade.main.SignUpActivity;
import com.wyre.trade.model.TransferInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReferralActivity extends AppCompatActivity {

    Button btnInvite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        btnInvite = findViewById(R.id.btn_invite);

        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = SharedHelper.getKey(getBaseContext(), "userId");
                String link = "https://wyretrade.com/?invitedby=" + uid;
                FirebaseDynamicLinks.getInstance()
                        .createDynamicLink()
                        .setLink(Uri.parse(link))
                        .setDomainUriPrefix("https://wyretrade.page.link")
                        .setAndroidParameters(
                                new DynamicLink.AndroidParameters.Builder("com.wyre.trade")
                                        .setMinimumVersion(17)
                                        .build())
//                        .setIosParameters(
//                                new DynamicLink.IosParameters.Builder("com.example.ios")
//                                        .setAppStoreId("123456789")
//                                        .setMinimumVersion("1.1.1")
//                                        .build())
                        .buildShortDynamicLink()
                        .addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
                            @Override
                            public void onSuccess(ShortDynamicLink shortDynamicLink) {
                                Uri mInvitationUrl = shortDynamicLink.getShortLink();
                                String referrerName = SharedHelper.getKey(getBaseContext(), "fullName");
                                String subject = String.format("%s invite you to wyretrade!", referrerName);
                                String invitationLink = mInvitationUrl.toString();
                                String msg = "Please use this very good app! Use my referrer link: "
                                        + invitationLink;
                                String msgHtml = String.format("<p>Please use this very good app! Use my "
                                        + "<a href=\"%s\">referrer link</a>!</p>", invitationLink);

                                Intent intent = new Intent(Intent.ACTION_SENDTO);
                                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                                intent.putExtra(Intent.EXTRA_TEXT, msg);
                                intent.putExtra(Intent.EXTRA_HTML_TEXT, msgHtml);
                                if (intent.resolveActivity(getPackageManager()) != null) {
                                    startActivity(intent);
                                }
                            }

                        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("firebas dynamic link issue: ", e.getMessage());
                    }
                });
            }
        });

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }
                        //
                        // If the user isn't signed in and the pending Dynamic Link is
                        // an invitation, sign in the user anonymously, and record the
                        // referrer's UID.
                        //
                        if(SharedHelper.getKey(getBaseContext(), "userId") == null || SharedHelper.getKey(getBaseContext(), "userId") == "") {
                            String referrerUid = deepLink.getQueryParameter("invitedby");
                            Log.d("invitedby", referrerUid);
                            createAnonymousAccountWithReferrerInfo(referrerUid);
                        }

                    }
                });


    }

    private void createAnonymousAccountWithReferrerInfo(final String referrerUid) {
        SharedHelper.putKey(getBaseContext(), "referred_by", referrerUid);

        startActivity(new Intent(ReferralActivity.this, SignUpActivity.class));
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
