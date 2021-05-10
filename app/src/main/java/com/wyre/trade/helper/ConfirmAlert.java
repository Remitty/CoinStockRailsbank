package com.wyre.trade.helper;

import android.app.Activity;
import android.graphics.Color;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ConfirmAlert {
    Activity mContext;
    SweetAlertDialog pDialog;

    public ConfirmAlert(Activity context) {

        mContext = context;
//        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
//        pDialog.setCancelable(false);
    }

    public void alert(String text) {
        pDialog = new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE);
        pDialog.setContentText(text)
                .setConfirmText("Ok")
                .setTitleText("Alert")
                .show();
//                .changeAlertType(SweetAlertDialog.WARNING_TYPE);
//        return pDialog;
    }

    public SweetAlertDialog confirm(String text) {
        pDialog = new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE);
        pDialog.setContentText(text)
                .setConfirmText("Yes")
                .setCancelText("No")
                .setTitleText("Are you sure?")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                });
//                .changeAlertType(SweetAlertDialog.WARNING_TYPE);
        return pDialog;
    }

    public void delete() {
        pDialog.setTitleText("Deleting")
                .setContentText("Please wait")
                .showCancelButton(false)
                .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
    }

    public void process() {
        pDialog.setTitleText("Processing")
                .setContentText("Please wait")
                .showCancelButton(false)
                .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
    }

    public void success(String message) {
        pDialog.setConfirmText("Ok")
                .setTitleText("Success")
                .setContentText(message)
                .setConfirmClickListener(null)
                .showCancelButton(false)
                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
    }

    public void error(String error) {
        if(pDialog == null) {
            pDialog = new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE);
            pDialog.show();
        }
        pDialog.setConfirmText("Ok")
                .setTitleText("Error")
                .setContentText(error)
                .setConfirmClickListener(null)
                .showCancelButton(false)
                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
    }

    public void new_error(String error) {
            pDialog = new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE);
        pDialog.setConfirmText("Ok")
                .setTitleText("Error")
                .setContentText(error)
                .setConfirmClickListener(null)
                .showCancelButton(false)
                .show();
    }

    public void dismissWithAnimation() {
        pDialog.dismissWithAnimation();
    }
}
