package com.example.lordone.picturegroups.Functions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lordone.picturegroups.AsynClasses.Test;
import com.example.lordone.picturegroups.AuxiliaryClasses.DisplayResultActivity;
import com.example.lordone.picturegroups.BaseClasses.GV;
import com.example.lordone.picturegroups.MainActivity;
import com.example.lordone.picturegroups.R;

import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Lord One on 8/2/2016.
 */
public class TestAccuracyActivity extends AppCompatActivity {
    private BaseLoaderCallback mLoaderCallback_TestAccuracyActivity = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OPENCV", "OpenCV loaded successfully");
                    new Test(TestAccuracyActivity.this, DisplayResultActivity.ACCURACY_RESULT).execute();

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    Button mainmenu_button;
    Button continue_test_button;
    TextView title;
    String path;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.normal_test_layout);

        title = (TextView) findViewById(R.id.title);
        title.setText("Test Accuracy Result");

        path = getIntent().getStringExtra("path");
        loadImagesDirectories(path);

        if (!OpenCVLoader.initDebug()) {
            Log.d("OPENCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, TestAccuracyActivity.this, mLoaderCallback_TestAccuracyActivity);
        } else {
            Log.d("OPENCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback_TestAccuracyActivity.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

        mainmenu_button = (Button) findViewById(R.id.mainmenu_button);
        mainmenu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestAccuracyActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        continue_test_button = (Button) findViewById(R.id.continue_test_button);
        continue_test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    void loadImagesDirectories(String path) {
        try {
            File dir = new File(path);
            String[] _dirList = dir.list();

            GV._testListDirs = new ArrayList<>();
            GV._testCats = new ArrayList<>();

            // _dirList: list of folders that contain images
            if (_dirList != null) {

                for (String _subDir : _dirList) {
                    File subDir = new File(path + File.separator + _subDir);
                    // for each category, read all of its images
                    if (!_subDir.startsWith(".") && subDir.isDirectory()) {

                        String[] _files = subDir.list();

                        for(int i = 0; i < _files.length; i++) {
                            GV._testListDirs.add(path + File.separator + _subDir + File.separator + _files[i]);
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
