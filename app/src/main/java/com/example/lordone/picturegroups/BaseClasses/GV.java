package com.example.lordone.picturegroups.BaseClasses;

import android.os.Environment;

import org.json.JSONObject;
import org.opencv.core.Mat;
import org.opencv.ml.CvSVM;

import java.util.ArrayList;

/**
 * Created by Lord One on 6/20/2016.
 */
public class GV {
    public static ArrayList<String> fileDirs;
    public static ArrayList<String> target;
    public static JSONObject mapTarget;
    public static JSONObject ivMapTarget;
    public static String[] targetNames = null;
    public static ArrayList<Integer> begin_of_category;

    public static double eps = 1e-9;
    public static double pi = Math.acos(-1);

    public static double threshold[] = { eps, 15, 35 };

    public static int norient = 6;
    public static double iorient = 180. / norient;
    public static int ninten = 3;
    public static int nshapes = norient * ninten;

    public static int adjx[] = { -1, -1, -1, 0, 0, 1, 1, 1 };
    public static int adjy[] = { -1, 0, 1, -1, 1, -1, 0, 1 };

    public static int n_bins_1_spact = 254;
    public static int n_bins_1_sogi = nshapes * nshapes;

    public static int img_index;
    public static int bin_index;
    public static int mstd_index;

    public static int n_compressor = 3; //TODO: change latter
//    public static int n_train_pca = 2; // TODO: change latter

    public static int spm_max = 0;
    public static int n_patches;
    public static int n_compressed_col;

    public static int [][] img;
    public static int xres;
    public static int yres;

    public static int [][] shape;

    public static Mat ultimateFeatures;
    public static double [][] std_val;
    public static double [][] mean_val;
    public static double [][] hist_all;

    public static int nCol = -1;


    public static String _folderDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/pictureGroups/";
    public static String _svmSaveFile = "svm.xml";
    public static String _ultimateSaveFile = "sogi.csv";
    public static String _categoryNamesSaveFile = "categoryNames.csv";
    public static String _categorySaveFile = "categories.csv";

    public static Mat testMat;
    public static String _testDir;

    public static CvSVM svm = null;

    public static int minArea = 40000;
    public static int normalArea = 75000;
    public static int maxArea = 120000;

    public static String _tmpImageDir = _folderDir + "ImageExecutive/";
    public static String _tmpImageFile = "tmp.jpg";

    public static String _cameraTestDir = _folderDir + "CameraTest/";
    public static String _groupsDir = _folderDir + "Groups/";

    public static int trainSize = 2;
    public static ArrayList<String> _trainListDirs = null;
    public static ArrayList<String> _testListDirs = null;
    public static ArrayList<String> _trainTarget = null;
    public static ArrayList<String> _testTarget = null;

    public static int max_number_images_showed_in_static_test = 20;

    public static void init() {
        n_patches = 0;
        for (int lv = 0; lv <= spm_max; lv++) {
            int two_exp = 1 << lv;
            n_patches += two_exp * two_exp + (two_exp - 1) * (two_exp - 1);
        }
        n_compressed_col = n_patches * n_compressor * 2;

//        for (int i = 0; i < maxImage; i++) {
//            hist_all[i] = new double[(n_bins_1_sogi + n_bins_1_spact) * n_patches + 5];
//            mean_val[i] = new double[n_patches];
//            std_val[i] = new double[n_patches];
//        }
    }
}
