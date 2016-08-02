package com.example.lordone.picturegroups.AuxiliaryClasses;

import android.app.Activity;
import android.app.ProgressDialog;

/**
 * Created by Champ on 02/08/2016.
 */
public class ProgressDia {
    static Activity activity;
    static ProgressDialog progressDialog = null;

    public static void showDialog(Activity _activity, String message) {
        activity = _activity;

        if(progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setTitle("Please wait!");
            progressDialog.setMessage(message);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.show();
                }
            });
        }
    }

    public static void dismissDialog() {
        if(progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
