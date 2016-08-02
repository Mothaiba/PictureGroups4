package com.example.lordone.picturegroups.AsynClasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

import com.example.lordone.picturegroups.AuxiliaryClasses.AlertDia;
import com.example.lordone.picturegroups.BaseClasses.FileIO;
import com.example.lordone.picturegroups.BaseClasses.GV;
import com.example.lordone.picturegroups.BaseClasses.ImageExecutive;
import com.example.lordone.picturegroups.AuxiliaryClasses.DisplayResultActivity;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Lord One on 8/1/2016.
 */
public class Test extends AsyncTask<Void, Integer, Integer> {
    Activity activity;
    int showResult;

    Date beginTime = new Date();
    ProgressDialog progressDialog;
    AlertDialog.Builder builder;
    AlertDialog alert;

    public Test(Activity _activity, int _showResult) {
        activity = _activity;
        showResult = _showResult;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = new ProgressDialog(activity);
        builder = new AlertDialog.Builder(activity);

        progressDialog.setTitle("Images are being categorized!");
        progressDialog.setMessage("0 image categorized");
        progressDialog.show();

        GV.sogi = new double[1][GV.n_patches * GV.n_bins_1_sogi];
        GV.spact = new double[1][GV.n_patches * GV.n_bins_1_spact];
        GV.meanPixel = new double[GV.train_size * GV._uniqueCatNames.length][GV.n_patches];
        GV.stdPixel = new double[GV.train_size * GV._uniqueCatNames.length][GV.n_patches];
        GV.test = new Mat(1, GV.n_patches * (GV.n_bins_1_spact + GV.n_bins_1_sogi + 2),CvType.CV_32F);
    }

    protected Integer doInBackground(Void... voids) {
        int img_index = 0;
        try {
            GV._predictedCats = new ArrayList<>();

            for(String _file : GV._testListDirs) {

                Mat raw_img = Highgui.imread(_file, 0);

                GV.sogi_index = GV.spact_index = GV.mstd_index = 0;
                Mat mat_img = new Mat();
                for (int lvl = 0; lvl <= GV.spm_max; lvl++) {
                    Size size = new Size(raw_img.cols() / (1 << (GV.spm_max - lvl)), raw_img.rows() / (1 << (GV.spm_max - lvl)));
                    Imgproc.resize(raw_img, mat_img, size);
                    int [][] img = ImageExecutive.matToMatrix(mat_img);
                    ImageExecutive.computeSogi(0, img, lvl);
                    ImageExecutive.computeSpact(0, img, lvl);
                    ImageExecutive.computeMeanSTD(0, img, lvl);
                    ImageExecutive.combineToTest();
                }
                ImageExecutive.testSVM();

                img_index++;
                publishProgress(img_index);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return img_index;
    }

    protected void onProgressUpdate(Integer... progress) {
        progressDialog.setMessage(String.valueOf(progress[0] + "/" + GV._testListDirs.size() + " image(s) categorized!"));
    }

    protected void onPostExecute(Integer result) {
        progressDialog.dismiss();

        Date endTime = new Date();
        long featuring_time = (endTime.getTime() - beginTime.getTime()) / 1000;
//        builder.setTitle("Notification");
//        builder.setMessage("Categorize " + result + " images in " + featuring_time + " seconds");
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                alert = builder.create();
//                alert.show();
//            }
//        });
        AlertDia.showAlert(activity, "Categorize " + result + " images in " + featuring_time + " seconds");

        Intent intent = new Intent(activity, DisplayResultActivity.class);
        intent.putExtra("type", showResult);
        activity.startActivity(intent);
    }
}
