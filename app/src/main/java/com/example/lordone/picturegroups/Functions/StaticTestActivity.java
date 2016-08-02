package com.example.lordone.picturegroups.Functions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lordone.picturegroups.AsynClasses.Test;
import com.example.lordone.picturegroups.AuxiliaryClasses.DisplayResultActivity;
import com.example.lordone.picturegroups.BaseClasses.GV;
import com.example.lordone.picturegroups.R;

import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Lord One on 8/1/2016.
 */
public class StaticTestActivity extends AppCompatActivity {

    String selectedPath;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OPENCV", "OpenCV loaded successfully");
                    new Test(StaticTestActivity.this, DisplayResultActivity.PICTURE_RESULT).execute();

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selectedPath = getIntent().getStringExtra("path");

        if(getIntent().hasExtra("file_name")) {
            GV._testListDirs = new ArrayList<>();
            GV._testListDirs.add(getIntent().getStringExtra("file_name"));
        }
        else {
            loadImagesDirectories(selectedPath);
        }

        if (!OpenCVLoader.initDebug()) {
            Log.d("OPENCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, StaticTestActivity.this, mLoaderCallback);
        } else {
            Log.d("OPENCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

    }

    void loadImagesDirectories(String path) {
        try {
            File dir = new File(path);
            String[] _fileList = dir.list();
            GV._testListDirs = new ArrayList<>();

            // _fileList: list of image file names
            if (_fileList != null) {
                for (String _file : _fileList) {
                    File file = new File(path + File.separator + _file);
                    if (!_file.startsWith(".") && file.isFile()) {
                        GV._testListDirs.add(_file);
                    }
                }
            } else {
                Toast.makeText(this, "Directory empty", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
