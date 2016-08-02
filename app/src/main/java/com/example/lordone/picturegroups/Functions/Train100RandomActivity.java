package com.example.lordone.picturegroups.Functions;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.example.lordone.picturegroups.AsynClasses.Train;
import com.example.lordone.picturegroups.BaseClasses.GV;
import com.example.lordone.picturegroups.AuxiliaryClasses.DisplayResultActivity;
import com.example.lordone.picturegroups.R;

/**
 * Created by Lord One on 6/8/2016.
 */

public class Train100RandomActivity extends AppCompatActivity {

    String selectedPath;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OPENCV", "OpenCV loaded successfully");
                    loadImagesDirectories(selectedPath);
                    new Train(Train100RandomActivity.this, true, DisplayResultActivity.ACCURACY_RESULT).execute();

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.normal_test_layout);

        selectedPath = getIntent().getStringExtra("path");

        if (!OpenCVLoader.initDebug()) {
            Log.d("OPENCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, Train100RandomActivity.this, mLoaderCallback);
        } else {
            Log.d("OPENCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

    }

    void loadImagesDirectories(String path) {
        try {
            File dir = new File(path);
            String[] _dirList = dir.list();
            GV._uniqueCatNames = _dirList.clone();
            GV.mapCat = new JSONObject();
            GV.ivMapCat = new JSONObject();
            for (int i = 0; i < GV._uniqueCatNames.length; i++) {
                GV.mapCat.put(GV._uniqueCatNames[i], i);
                GV.ivMapCat.put(String.valueOf(i), GV._uniqueCatNames[i]);
            }
            GV._trainListDirs = new ArrayList<>();
            GV._trainCats = new ArrayList<>();
            GV._testListDirs = new ArrayList<>();
            GV._testCats = new ArrayList<>();

            // _dirList: list of folders that contain images
            if (_dirList != null) {

                for (String _subDir : _dirList) {
                    File subDir = new File(path + File.separator + _subDir);
                    // for each category, read all of its images
                    if (!_subDir.startsWith(".") && subDir.isDirectory()) {

                        String[] _files = subDir.list();

                        List<String> list = new ArrayList<>(Arrays.asList(_files));
                        Collections.shuffle(list);

                        for(int i = 0; i < GV.train_size; i++) {
                            GV._trainListDirs.add(path + File.separator + _subDir + File.separator + list.get(i));
                            GV._trainCats.add(GV.mapCat.getInt(_subDir));
                        }

                        for(int i = GV.train_size; i < list.size(); i++) {
                            GV._testListDirs.add(path + File.separator + _subDir + File.separator + list.get(i));
                            GV._testCats.add(GV.mapCat.getInt(_subDir));
                        }

                    }
                }

            }
            else {
                Toast.makeText(this, "Directory empty", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


}