package com.example.lordone.picturegroups.AsynClasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.example.lordone.picturegroups.AuxiliaryClasses.AlertDia;
import com.example.lordone.picturegroups.BaseClasses.FileIO;
import com.example.lordone.picturegroups.BaseClasses.GV;
import com.example.lordone.picturegroups.BaseClasses.ImageExecutive;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.util.Date;

/**
 * Created by Lord One on 8/1/2016.
 */
public class Train extends AsyncTask<Void, Integer, Integer> {

    Activity activity;
    boolean testOn;
    int showResult;

    Date beginTime = new Date();
    ProgressDialog progressDialog;
    AlertDialog.Builder builder;
    AlertDialog alert;

    public Train(Activity _activity, boolean _testOn, int _showResult) {
        activity = _activity;
        testOn = _testOn;
        showResult = _showResult;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        GV.sogi = new double[GV._trainListDirs.size()][GV.n_patches * GV.n_bins_1_sogi];
        GV.spact = new double[GV._trainListDirs.size()][GV.n_patches * GV.n_bins_1_spact];
        GV.meanPixel = new double[GV._trainListDirs.size()][GV.n_patches];
        GV.stdPixel = new double[GV._trainListDirs.size()][GV.n_patches];
        GV.train = new Mat(GV._trainListDirs.size(), GV.n_patches * (GV.n_bins_1_spact + GV.n_bins_1_sogi + 2), CvType.CV_32F);

        progressDialog = new ProgressDialog(activity);
        builder = new AlertDialog.Builder(activity);

        progressDialog.setTitle("Training");
        progressDialog.setMessage("0 image featured");
        progressDialog.show();
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        int img_index = 0;
        try {
            for(String _file : GV._trainListDirs) {

                Mat raw_img = Highgui.imread(_file, 0);

                GV.sogi_index = GV.spact_index = GV.mstd_index = 0;
                Mat mat_img = new Mat();
                for (int lvl = 0; lvl <= GV.spm_max; lvl++) {
                    Size size = new Size(raw_img.cols() / (1 << (GV.spm_max - lvl)), raw_img.rows() / (1 << (GV.spm_max - lvl)));
                    Imgproc.resize(raw_img, mat_img, size);
                    int [][] img = ImageExecutive.matToMatrix(mat_img);
                    ImageExecutive.computeSogi(img_index, img, lvl);
                    ImageExecutive.computeSpact(img_index, img, lvl);
                    ImageExecutive.computeMeanSTD(img_index, img, lvl);
                }
                img_index++;
                publishProgress(img_index);
            }

            ImageExecutive.combineToTrain(img_index);
            ImageExecutive.releaseAuxiliary();
            ImageExecutive.trainSVM();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return img_index;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        progressDialog.setMessage(String.valueOf(progress[0] + "/" + GV._trainListDirs.size() + " image(s) featured!"));
    }

    @Override
    protected void onPostExecute(Integer result) {
        progressDialog.dismiss();

        Date endTime = new Date();
        long featuring_time = (endTime.getTime() - beginTime.getTime()) / 1000;
//        builder.setTitle("Notification");
//        builder.setMessage("Extracted features of " + result + " images in " + featuring_time + " seconds");
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                alert = builder.create();
//                alert.show();
//            }
//        });
        AlertDia.showAlert(activity, "Extracted features of " + result + " images in " + featuring_time + " seconds");

        if(testOn)
            new Test(activity, showResult).execute();
    }

}
