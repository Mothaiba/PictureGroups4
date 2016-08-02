package com.example.lordone.picturegroups.BaseClasses;

import android.os.Environment;

import org.json.JSONObject;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.ml.CvSVM;

import java.util.ArrayList;

/**
 * Created by Lord One on 6/20/2016.
 */
public class GV {

    public static String[] _uniqueCatNames = null;
    public static JSONObject mapCat;
    public static JSONObject ivMapCat;

    // const of sogi
    public static double eps = 1e-9;
    public static double pi = Math.acos(-1);
    public static double threshold[] = { eps, 15, 35 };
    public static int norient = 6;
    public static double iorient = 180. / norient;
    public static int ninten = 3;
    public static int nshapes = norient * ninten;
    public static int n_bins_1_sogi = nshapes * nshapes;
    public static int [][] shape;
    public static double[][] sogi;

    // const of spact
    public static int n_bins_1_spact = 254;
    public static double[][] spact;

    public static Mat train;
    public static Mat test;

    // adjacent cells
    public static int adjx[] = { -1, -1, -1, 0, 0, 1, 1, 1 };
    public static int adjy[] = { -1, 0, 1, -1, 1, -1, 0, 1 };

    //const of spatial pyramid
    public static int spm_max = 1;
    public static int n_patches;

    //mean and standard deviation
    public static double [][] meanPixel;
    public static double [][] stdPixel;

    // const of file IO
    public static String _folderDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/pictureGroups/";
    public static String _svmSaveFile = "svm.xml";
    public static String _categoryNamesSaveFile = "categoryNames.csv";

    public static CvSVM svm = null;

    // const to resize image
    public static int minArea = 40000;
    public static int normalArea = 75000;
    public static int maxArea = 120000;

    //const to pre-process image
    public static String _tmpImageDir = _folderDir + "ImageExecutive/";
    public static String _tmpImageFile = "tmp.jpg";

    public static String _cameraTestDir = _folderDir + "CameraTest/";
    public static String _groupsDir = _folderDir + "Groups/";

    public static int train_size = 100;

    public static int xres, yres;
    public static int sogi_index;
    public static int spact_index;
    public static int mstd_index;

    public static ArrayList<String> _trainListDirs = null;
    public static ArrayList<String> _testListDirs = null;
    public static ArrayList<Integer> _trainCats = null;
    public static ArrayList<Integer> _testCats = null;
    public static ArrayList<Integer> _predictedCats = null;


    public static int max_number_images_showed_in_static_test = 20;

    public static void init() {
        n_patches = 0;
        for (int lv = 0; lv <= spm_max; lv++) {
            int two_exp = 1 << lv;
            n_patches += two_exp * two_exp + (two_exp - 1) * (two_exp - 1);
        }

    }

}
