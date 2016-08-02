package com.example.lordone.picturegroups.Functions;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.lordone.picturegroups.AsynClasses.Train;
import com.example.lordone.picturegroups.BaseClasses.GV;

import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Lord One on 8/1/2016.
 */
public class TrainActivity extends AppCompatActivity {

    String selectedPath;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OPENCV", "OpenCV loaded successfully");
                    loadImagesDirectories(selectedPath);
                    new Train(TrainActivity.this, false, -1).execute();

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

        selectedPath = getIntent().getStringExtra("path");

        if (!OpenCVLoader.initDebug()) {
            Log.d("OPENCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, TrainActivity.this, mLoaderCallback);
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

            // _dirList: list of folders that contain images
            if (_dirList != null) {

                for (String _subDir : _dirList) {
                    File subDir = new File(path + File.separator + _subDir);
                    // for each category, read all of its images
                    if (!_subDir.startsWith(".") && subDir.isDirectory()) {

                        String[] _files = subDir.list();

                        for(int i = 0; i < _files.length; i++) {
                            GV._trainListDirs.add(path + File.separator + _subDir + File.separator + _files[i]);
                            GV._trainCats.add(GV.mapCat.getInt(_subDir));
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
