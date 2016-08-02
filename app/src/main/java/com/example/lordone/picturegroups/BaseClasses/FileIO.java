package com.example.lordone.picturegroups.BaseClasses;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;

import org.json.JSONObject;
import org.opencv.core.Mat;
import org.opencv.ml.CvSVM;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;

/**
 * Created by Lord One on 6/21/2016.
 */
public class FileIO {

//    public static void writeFileInt(int[][] image, int xres, int yres, String file_name) {
//        try {
//            File fileDir = new File(GV._folderDir);
//            if (!fileDir.exists()) {
//                fileDir.mkdirs();
//            }
//
//            FileWriter writer;
//            File file = new File(fileDir, file_name);
//            writer = new FileWriter(file);
//
//            for(int i = 0; i <= xres; i++) {
//                writer.append(Integer.toString(image[i][0]));
//                for(int j = 1; j <= yres; j++) {
//                    writer.append(',' + Integer.toString(image[i][j]));
//                }
//                writer.append('\n');
//            }
//            writer.flush();
//            writer.close();
//
//        }
//        catch (Exception e) {}
//
//    }

//    public static void writeFileFeatures() {
//        try {
//            File fileDir = new File(GV._folderDir);
//            if (!fileDir.exists()) {
//                fileDir.mkdirs();
//            }
//            String file_name = "features.csv";
//
//            FileWriter writer;
//            File file = new File(fileDir, file_name);
//            writer = new FileWriter(file);
//
//            for(int d = 1; d <= GV.maxDist; d++)
//                for(int ori = 1; ori <= GV.norient; ori++)
//                    for(int in = 1; in <= GV.ninten; in++)
//                        for(int ori2 = 1; ori2 <= GV.norient; ori2++)
//                            for(int in2 = 1; in2 <= GV.ninten; in2++)
//                                for(int po = 1; po <= GV.npos; po++)
//                                    writer.append('\n' + String.format("%1$.8f", GV.features[d][ori][in][ori2][in2][po]));
//            writer.flush();
//            writer.close();
//
//        }
//        catch (Exception e) {}

//    }

    public static boolean writeAuxiliaryToFile() {
        try {
            File fileDir = new File(GV._folderDir);
            if(!fileDir.exists()){
                fileDir.mkdirs();
            }

            FileWriter writer;

            // save category name list
            File targetFile = new File(fileDir, GV._categoryNamesSaveFile);
            writer = new FileWriter(targetFile);

            writer.append(GV._uniqueCatNames[0]);
            for (int i = 1; i < GV._uniqueCatNames.length; i++){
                writer.append("," + GV._uniqueCatNames[i]);
            }

            writer.flush();
            writer.close();

            return true;
        }
        catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeSVMToFile(CvSVM svm) {
        try {
            File fileDir = new File(GV._folderDir);
            if(!fileDir.exists()){
                fileDir.mkdirs();
            }

            svm.save(GV._folderDir + GV._svmSaveFile);
            writeAuxiliaryToFile();

            System.out.print("Write SVM to File successfully!");
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.print("Cannot write SVM to File!");
            return false;
        }
    }

    public static boolean readAuxiliary() {
        if(GV._uniqueCatNames == null) {
            try {
                String csvFile;
                String csvSplitBy = ",";
                BufferedReader br;
                String line = "";

                //=== load targetNames
                csvFile = GV._folderDir + GV._categoryNamesSaveFile;
                br = new BufferedReader(new FileReader(csvFile));
                while ((line = br.readLine()) != null) {
                    String[] targetNames = line.split(csvSplitBy);
                    GV._uniqueCatNames = targetNames.clone();
                }

                //=== pre-process target map
                GV.mapCat = new JSONObject();
                GV.ivMapCat = new JSONObject();
                for (int i = 0; i < GV._uniqueCatNames.length; i++) {
                    GV.mapCat.put(GV._uniqueCatNames[i], i);
                    GV.ivMapCat.put(String.valueOf(i), GV._uniqueCatNames[i]);
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public static void readSVMFromFile() {
        if (GV.svm == null && (new File(GV._folderDir + GV._svmSaveFile).exists())) {
            GV.svm = new CvSVM();
            GV.svm.load(GV._folderDir + GV._svmSaveFile);
            readAuxiliary();
        }
    }

    public static void relocateFile(String _inDir, String _fileName, String _category) {
        File inDir = new File(_inDir);
        if(!inDir.exists())
            inDir.mkdirs();

        File outDir = new File(GV._groupsDir + _category);
        if(!outDir.exists())
            outDir.mkdirs();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(_inDir + File.separator + _fileName, options);

        File outFile = new File(GV._groupsDir + _category + File.separator + _fileName);
        if (outFile.exists())
            outFile.delete();
        try {
            FileOutputStream out = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeMatToFile(Mat data, int nrow, int ncol, String _file_name) {
        try {
            File fileDir = new File(GV._folderDir);
            if(!fileDir.exists()){
                fileDir.mkdirs();
            }

            FileWriter writer;

            File target = new File(fileDir, _file_name);
            writer = new FileWriter(target);

            for(int i = 0; i < nrow; i++){
                writer.append(String.format("%.12f", data.get(i, 0)[0]));
                for(int j = 1; j < ncol; j++)
                    writer.append("," + String.format("%.12f", data.get(i, j)[0]));
                writer.append('\n');
            }

            writer.flush();
            writer.close();

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeArrayToFile(double[][] data, int nrow, int ncol, String _file_name) {
        try {
            File fileDir = new File(GV._folderDir);
            if(!fileDir.exists()){
                fileDir.mkdirs();
            }

            FileWriter writer;

            File target = new File(fileDir, _file_name);
            writer = new FileWriter(target);

            for(int i = 0; i < nrow; i++) {
                writer.append(String.format("%.12f", data[i][0]));
                for(int j = 1; j < ncol; j++)
                    writer.append("," + String.format("%.12f", data[i][j]));
                writer.append('\n');
            }

            writer.flush();
            writer.close();

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeArrayIntToFile(int[][] data, int nrow, int ncol, String _file_name) {
        try {
            File fileDir = new File(GV._folderDir);
            if(!fileDir.exists()){
                fileDir.mkdirs();
            }

            FileWriter writer;

            File target = new File(fileDir, _file_name);
            writer = new FileWriter(target);

            for(int i = 0; i < nrow; i++) {
                writer.append(String.valueOf(data[i][0]));
                for(int j = 1; j < ncol; j++)
                    writer.append("," + String.valueOf(data[i][j]));
                writer.append('\n');
            }

            writer.flush();
            writer.close();

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}
