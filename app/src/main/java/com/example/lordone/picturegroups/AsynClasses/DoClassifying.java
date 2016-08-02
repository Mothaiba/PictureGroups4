package com.example.lordone.picturegroups.AsynClasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lordone.picturegroups.BaseClasses.FileIO;
import com.example.lordone.picturegroups.BaseClasses.GV;
import com.example.lordone.picturegroups.BaseClasses.ImageExecutive;
import com.example.lordone.picturegroups.R;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.util.Date;

/**
 * Created by Lord One on 7/5/2016.
 */
public class DoClassifying extends AsyncTask<Void, Integer, String> {

    Activity activity;
    Date beginTime;
    ProgressDialog progressDialog;;
    LinearLayout linearLayout;

    public DoClassifying(Activity _activity) {
        activity = _activity;
        progressDialog = new ProgressDialog(activity);
        linearLayout = (LinearLayout) activity.findViewById(R.id.raw_test_Linear_layout);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setTitle("Please wait!");
        progressDialog.setMessage("Loading svm...");
        progressDialog.show();
    }

//    @Override
    protected String doInBackground(Void... voids) {
        String toShow = "";
        boolean isTrue;

//        try {
//            FileIO.readSVMFromFile();
//            beginTime = new Date();
//            int cnt = 0;
//            publishProgress(cnt);
//
//            int in1CategoryTrue = 0;
//            int in1CategoryAll = 0;
//            int nCategories = 1;
//            double sumAccuracy = 0;
//
//            for(String _file : GV._testListDirs) {
//
//                Mat raw_img = Highgui.imread(_file, 0);
//                GV.xres = raw_img.rows() - 1;
//                GV.yres = raw_img.cols() - 1;
//                GV.bin_index = 0;
//                GV.test_hist_all = new double[1][GV.n_patches * (GV.n_bins_1_sogi + GV.n_bins_1_spact + 2) + 5];
//
//                Mat mat_img = new Mat();
//                for (int lvl = 0; lvl <= GV.spm_max; lvl++) {
//                    Size size = new Size(raw_img.cols() / (1 << (GV.spm_max - lvl)), raw_img.rows() / (1 << (GV.spm_max - lvl)));
//                    Imgproc.resize(raw_img, mat_img, size);
//                    ImageExecutive.compute_2_hist(mat_img, lvl, GV.test_hist_all);
//                }
//
//                GV.test_ultimateFeatures = new Mat(1, GV.n_compressed_col, CvType.CV_32F);
//                ImageExecutive.projectPCA(GV.test_hist_all, GV.test_ultimateFeatures);
//
//                isTrue = ((GV._testTarget.get(cnt)).compareTo(GV.ivMapTarget.getString(String.valueOf((int) GV.svm.predict(GV.test_ultimateFeatures)))) == 0);
//
//                if(cnt > 0 && ( GV._testTarget.get(cnt)).compareTo(GV._testTarget.get(cnt - 1)) != 0) {
//                    double thisCategoryAccuracy = 100. * in1CategoryTrue / in1CategoryAll;
//                    toShow += GV._testTarget.get(cnt - 1) + ": " + thisCategoryAccuracy + '\n';
//                    sumAccuracy += thisCategoryAccuracy;
//                    in1CategoryTrue = 0;
//                    in1CategoryAll = 0;
//                    nCategories++;
//                }
//                in1CategoryAll++;
//                if(isTrue){
//                    in1CategoryTrue++;
//                }
//                publishProgress(++cnt);
//
//                cnt++;
//            }
//
//            double thisCategoryAccuracy = 100. * in1CategoryTrue / in1CategoryAll;
//            toShow +=  GV._testTarget.get(cnt - 1) + ": " + thisCategoryAccuracy + '\n';
//            sumAccuracy += thisCategoryAccuracy;
//            double overAllAccuracy = sumAccuracy / nCategories;
//            toShow += '\n' + "Over all: " + overAllAccuracy;
//
            return toShow;
//        }
//        catch (Exception e) {
//            return toShow;
//        }

    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        progressDialog.setMessage(String.valueOf(progress[0] + "/" + GV._testListDirs.size() + " image(s) classified!"));
    }

    @Override
    protected void onPostExecute(String result) {

        TextView textView = new TextView(activity);
        textView.setText(result);
        textView.setTextSize(20);
        textView.setGravity(Gravity.CENTER);
        linearLayout.addView(textView);

        progressDialog.dismiss();

    }
}