package com.wyre.trade.helper;

import android.app.Activity;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.plaid.link.BuildConfig;
import com.plaid.link.Plaid;
import com.plaid.link.PlaidHandler;
import com.plaid.link.configuration.LinkLogLevel;
import com.plaid.link.configuration.LinkTokenConfiguration;
import com.plaid.link.result.LinkResultHandler;
//import com.wyre.trade.BuildConfig;
import com.wyre.trade.R;
import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONException;
import org.json.JSONObject;

import kotlin.Unit;

public class PlaidConnect {
    private Activity mContext;
    private LoadToast loadToast;
    String linkToken;

    public LinkResultHandler myPlaidResultHandler = new LinkResultHandler(
            linkSuccess -> {
        sendPublicToken(linkSuccess.getPublicToken(), linkSuccess.getMetadata().getAccounts().get(0).getId());

        return Unit.INSTANCE;
    },
    linkExit -> {
        if (linkExit.getError() != null) {
            showAlert(linkExit.getError().getDisplayMessage());
        } else {
            showAlert(linkExit.getMetadata().getStatus() != null ? linkExit.getMetadata()
                    .getStatus()
                    .getJsonValue() : "unknown");
        }
        return Unit.INSTANCE;
    }
        );

    public PlaidConnect(Activity context){
        mContext = context;
        loadToast = new LoadToast(mContext);
    }

    public void openPlaid() {
        setOptionalEventListener();
        getLinkTokenFromServer();
    }

    public static void initPlaid(Activity context){
        new PlaidConnect(context);
    }

    /**
     * Optional, set an <a href="https://plaid.com/docs/link/android/#handling-onevent">event listener</a>.
     */
    private void setOptionalEventListener() {
        Plaid.setLinkEventListener(linkEvent -> {
            Log.i("Plaid Event", linkEvent.toString());
            return Unit.INSTANCE;
        });
    }

    /**
     * For all Link configuration options, have a look at the
     * <a href="https://plaid.com/docs/link/android/#parameter-reference">parameter reference</>
     */
    private void openLink() {
        LinkLogLevel logLevel = BuildConfig.DEBUG ? LinkLogLevel.VERBOSE : LinkLogLevel.ERROR;

        Plaid.create(
                mContext.getApplication(),
                new LinkTokenConfiguration.Builder()
                .token(linkToken)
                .logLevel(logLevel)
                .build()
        ).open(mContext);
    }

    private void getLinkTokenFromServer() {
        loadToast.show();
        AndroidNetworking.get(URLHelper.GET_PLAID_LINK_TOKEN)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("accept", "application/json")
                .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(mContext,"access_token"))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", "" + response);
                        loadToast.success();
                        try {
                            linkToken = response.getString("link_token");
                            openLink();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        loadToast.error();
                        // handle error
                        Toast.makeText(mContext, error.getErrorBody(), Toast.LENGTH_SHORT).show();
                        Log.d("errorm", "" + error.getErrorBody());
                    }
                });
    }

    private void sendPublicToken(String publicToken, String accountId) {
        Log.d("publictoken", publicToken);
        loadToast.show();
        AndroidNetworking.post(URLHelper.SEND_PLAID_CONNECT_BANK)
                .addBodyParameter("pub_token", publicToken)
                .addBodyParameter("account_id", accountId)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("accept", "application/json")
                .addHeaders("Authorization", "Bearer " + SharedHelper.getKey(mContext,"access_token"))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", "" + response);
                        loadToast.success();
                        //                            linkToken = response.getString("link_token");
                        Toast.makeText(mContext, response.optString("message"), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(ANError error) {
                        loadToast.error();
                        showAlert(error.getErrorBody());

                    }
                });
    }

    private void showAlert(String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        alert.setTitle("Alert")
                .setIcon(R.mipmap.ic_launcher_round)
                .setMessage(message)
                .setPositiveButton("Ok", null)
                .show();
    }

}
