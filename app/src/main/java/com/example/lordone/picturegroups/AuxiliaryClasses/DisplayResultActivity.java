package com.example.lordone.picturegroups.AuxiliaryClasses;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lordone.picturegroups.BaseClasses.FileIO;
import com.example.lordone.picturegroups.BaseClasses.GV;
import com.example.lordone.picturegroups.BaseClasses.ImageExecutive;
import com.example.lordone.picturegroups.Functions.CameraTestActivity;
import com.example.lordone.picturegroups.MainActivity;
import com.example.lordone.picturegroups.R;

import java.io.File;

/**
 * Created by Lord One on 8/1/2016.
 */
public class DisplayResultActivity extends AppCompatActivity {

    public static int ACCURACY_RESULT = 0,
                      PICTURE_RESULT = 1,
                      CAMERA_RESULT = 2,
                      GROUPS = 3;

    Button mainmenu_button;
    Button continue_test_button;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int result_type = getIntent().getIntExtra("type", 0);

        if(result_type == ACCURACY_RESULT) {
            setContentView(R.layout.normal_test_layout);

            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.raw_test_Linear_layout);

            String _result = calculateAccuracy();

            TextView textView = new TextView(this);
            textView.setText(_result);
            textView.setTextSize(20);
            textView.setGravity(Gravity.CENTER);
            linearLayout.addView(textView);

            mainmenu_button = (Button) findViewById(R.id.mainmenu_button);
            mainmenu_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DisplayResultActivity.this, MainActivity.class);
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

        if(result_type == PICTURE_RESULT) {
            setContentView(R.layout.normal_test_layout);

            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.raw_test_Linear_layout);

            try {
                for (int i = 0; i < GV._testListDirs.size(); i++) {

                    ImageView imageView = new ImageView(this);
                    TextView textView = new TextView(this);

                    Bitmap photo = ImageExecutive.getBitmap(GV._testListDirs.get(i));
                    imageView.setImageBitmap(photo);
                    imageView.setMaxHeight(400);
                    imageView.setAdjustViewBounds(true);

                    textView.setText("predict: " + GV.ivMapCat.getString(String.valueOf(GV._predictedCats.get(i))));
                    textView.setGravity(Gravity.CENTER);

                    linearLayout.addView(imageView);
                    linearLayout.addView(textView);
                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }

            mainmenu_button = (Button) findViewById(R.id.mainmenu_button);
            mainmenu_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DisplayResultActivity.this, MainActivity.class);
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

        if(result_type == CAMERA_RESULT) {
            try {
                setContentView(R.layout.camera_test_layout);

                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.camera_test_Linear_layout);

                Button backButton = (Button) findViewById(R.id.back_button);
                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });

                Button shootAgainButton = (Button) findViewById(R.id.shoot_again_button);
                shootAgainButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(DisplayResultActivity.this, CameraTestActivity.class);
                        startActivity(intent);
                    }
                });



                Bitmap photo = ImageExecutive.getBitmap(GV._testListDirs.get(0));
                ImageView imageView = new ImageView(this);
                imageView.setImageBitmap(photo);
                imageView.setMaxHeight(888);
                imageView.setAdjustViewBounds(true);

                TextView resultTextView = new TextView(this);
                resultTextView.setText("predict: " + GV.ivMapCat.getString(String.valueOf(GV._predictedCats.get(0))));
                resultTextView.setGravity(Gravity.CENTER);
                resultTextView.setTextSize(20);

                linearLayout.addView(imageView);
                linearLayout.addView(resultTextView);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(result_type == GROUPS) {
            try {
                for (int i = 0; i < GV._predictedCats.size(); i++) {
                    FileIO.relocateFile(GV._testListDirs.get(i), GV.ivMapCat.getString(String.valueOf(GV._predictedCats.get(i))));
                }

                int [] n_predicted = new int[GV._uniqueCatNames.length];
                String result = "";
                for(int i = 0; i < GV._predictedCats.size(); i++) {
                    n_predicted[GV._predictedCats.get(i)] ++;
                }

                for(int i = 0; i < GV._uniqueCatNames.length; i++)
                    if(n_predicted[i] > 0) {
                        result += n_predicted[i] + " picture(s) copied to folder " + GV.ivMapCat.getString(String.valueOf(i)) + '\n';
                    }

                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Grouping result");
                builder.setMessage(result);
                builder.setPositiveButton("To Folder", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO
                    }
                });
                builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                });
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    String calculateAccuracy() {
        int [] n_real = new int[GV._uniqueCatNames.length];
        int [] n_predich_correct = new int[GV._uniqueCatNames.length];

        for(int i = 0; i < GV._testCats.size(); i++) {

            int real = GV._testCats.get(i);
            int predict = GV._predictedCats.get(i);

            if(real == predict)
                n_predich_correct[real]++;
            n_real[real]++;
        }

        String result = "";
        double average = 0;
        int n_categories = 0;
        for(int i = 0; i < GV._uniqueCatNames.length; i++)
            if(n_real[i] > 0) {
                double accuracy = (double) n_predich_correct[i] / n_real[i] * 100;
                result += GV._uniqueCatNames[i] + ": " +
                        String.format("%.2f", accuracy) +
                        "% (" + n_predich_correct[i] + "/" + n_real[i] + " images)" + '\n';
                average += accuracy;
                n_categories++;
            }

        if(n_categories > 0)
            result += "Overall accuracy: " + String.format("%.2f", average / n_categories) + "%";

        return result;
    }
}
