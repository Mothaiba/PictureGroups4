package com.example.lordone.picturegroups.AuxiliaryClasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * Created by Lord One on 8/1/2016.
 */
public class AlertDia {
    static private Activity activity;
    static private AlertDialog.Builder builder;
    static private AlertDialog alertDialog;

    public static void showAlert(Activity _activity, String message) {
        activity = _activity;

        builder = new AlertDialog.Builder(activity);
        builder.setTitle("Notification");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog = builder.create();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.show();
            }
        });
    }

    public static void showFinalAlert(Activity _activity, String message) {
        activity = _activity;

        builder = new AlertDialog.Builder(activity);
        builder.setTitle("Notification");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                activity.finish();
            }
        });
        alertDialog = builder.create();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.show();
            }
        });
    }
}
