package com.example.lordone.picturegroups.BaseClasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.CvSVM;
import org.opencv.ml.CvSVMParams;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

/**
 * Created by Lord One on 6/26/2016.
 */
public class ImageExecutive {

    public static void resizeImage(String mCurrentPhotoPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, options);

        int oldWidth = bitmap.getWidth();
        int oldHeight = bitmap.getHeight();

        double ratio = Math.sqrt((double) GV.normalArea / (oldHeight * oldWidth));

        int newWidth = (int) (oldWidth * ratio);
        int newHeight = (int) (oldHeight * ratio);
        Bitmap resized = bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

        File fileDir = new File(GV._tmpImageDir);
        if(!fileDir.exists()) {
            fileDir.mkdirs();
        }

        File file = new File(GV._tmpImageDir + GV._tmpImageFile);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            resized.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void rotateImage(String _imageDir) {
        try {
            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(_imageDir, bounds);

            BitmapFactory.Options opts = new BitmapFactory.Options();
            Bitmap bm = BitmapFactory.decodeFile(_imageDir, opts);
            ExifInterface exif = new ExifInterface(_imageDir);
            String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;

            int rotationAngle = 0;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

            Bitmap rotatedBitmap;
            if(rotationAngle != 0) {
                Matrix matrix = new Matrix();
                matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
                rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
            }
            else
                rotatedBitmap = bm;

            File file = new File(_imageDir);
            file.delete();
            FileOutputStream out = new FileOutputStream(file);
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int[][] matToMatrix(Mat mat_img) {
        int xres = mat_img.rows();
        int yres = mat_img.cols();
        int[][] img = new int[xres][yres];
        for (int i = 0; i < xres; i++)
            for (int j = 0; j < yres; j++)
                img[i][j] = (int) mat_img.get(i, j)[0];
        GV.xres = xres;
        GV.yres = yres;
        return img;
    }

    public static void getPatches(Vector h_min_vec, Vector h_max_vec, Vector w_min_vec, Vector w_max_vec, int lvl, int xres, int yres) {
        int to_divide = 1 << lvl;
        int h_range = (xres - 2) / to_divide;
        int w_range = (yres - 2) / to_divide;

        for (int h_min = 1, h_max = h_min + h_range - 1; h_max < xres - 1; h_min = h_max + 1, h_max = h_min + h_range - 1)
            for (int w_min = 1, w_max = w_min + w_range - 1; w_max < yres - 1; w_min = w_max + 1, w_max = w_min + w_range - 1) {
                h_min_vec.add(h_min);
                h_max_vec.add(h_max);
                w_min_vec.add(w_min);
                w_max_vec.add(w_max);
            }
        for (int h_min = h_range / 2, h_max = h_min + h_range - 1; h_max < xres - 1; h_min = h_max + 1, h_max = h_min + h_range - 1)
            for (int w_min = w_range / 2, w_max = w_min + w_range - 1; w_max < yres - 1; w_min = w_max + 1, w_max = w_min + w_range - 1) {
                h_min_vec.add(h_min);
                h_max_vec.add(h_max);
                w_min_vec.add(w_min);
                w_max_vec.add(w_max);
            }
    }

    public static void releaseAuxiliary() {
        GV.sogi = null;
        GV.spact = null;
        GV.shape = null;
        GV.meanPixel = null;
        GV.stdPixel = null;

        System.gc();
    }

    // image is of size [0..xmax][0..ymax]
    public static void computeGradient(int [][] img, int xmax, int ymax) {

        double vert, hori;
        double tmpinten;
        int inten, orient;

        GV.shape = new int[xmax + 1][ymax + 1];

        for (int i = 0; i <= xmax; i++) for (int j = 0; j <= ymax; j++) {
            if (i > 0 && i < xmax)
                vert = img[i - 1][j] - img[i + 1][j];
            else if (i == 0)
                vert = img[i][j] - img[i + 1][j];
            else
                vert = img[i - 1][j] - img[i][j];
            if (j > 0 && j < ymax)
                hori = img[i][j + 1] - img[i][j - 1];
            else if (j == 0)
                hori = img[i][j + 1] - img[i][j];
            else
                hori = img[i][j] - img[i][j - 1];

            if (hori == 0)
                orient = 1;
            else
                orient = (int) ((Math.atan(vert / hori) / GV.pi * 180 + 90) / GV.iorient + 1);
            tmpinten = Math.sqrt(vert * vert + hori * hori);
            if (tmpinten <= GV.eps)
                inten = 0;
            else if (tmpinten <= GV.threshold[1])
                inten = 1;
            else if (tmpinten <= GV.threshold[2])
                inten = 2;
            else
                inten = 3;
            if (inten != 0)
                GV.shape[i][j] = (inten - 1) * GV.norient + orient;
        }
//    FileIO.writeArrayIntToFile(GV.shape, xmax + 1, ymax + 1, "shape.csv");
    }

    public static void computeSogi(int img_index, int[][] img, int lvl) {
        int[][] local_hist = new int[GV.nshapes + 1][GV.nshapes + 1]; // assumed that this is initialized with 0 values
        int xres = GV.xres;
        int yres = GV.yres;

        computeGradient(img, xres - 1, yres - 1);

        Vector<Integer> h_min_vec = new Vector<>();
        Vector<Integer> h_max_vec = new Vector<>();
        Vector<Integer> w_min_vec = new Vector<>();
        Vector<Integer> w_max_vec = new Vector<>();
        getPatches(h_min_vec, h_max_vec, w_min_vec, w_max_vec, lvl, xres, yres);

        for (int patch_index = 0; patch_index < h_min_vec.size(); patch_index++) {
            int xmin = h_min_vec.get(patch_index);
            int xmax = h_max_vec.get(patch_index);
            int ymin = w_min_vec.get(patch_index);
            int ymax = w_max_vec.get(patch_index);

            int sum_all = (xmax - xmin + 1) * (ymax - ymin + 1) * 8;

            int tmp_shape, tmp_shape_2;
            for (int u = xmin; u <= xmax; u++)
                for (int v = ymin; v <= ymax; v++) {
                    tmp_shape = GV.shape[u][v];
                    if (tmp_shape == 0) {
                        sum_all -= 8;
                    } else {
                        for (int k = 0; k < 8; k++) {
                            tmp_shape_2 = GV.shape[u + GV.adjx[k]][v + GV.adjy[k]];
                            if (tmp_shape_2 == 0)
                                sum_all--;
                            else
                                local_hist[tmp_shape][tmp_shape_2]++;
                        }
                    }
                }

            int bin_index = patch_index * GV.n_bins_1_sogi;
            if (sum_all > 0)
                for (int p = 1; p <= GV.nshapes; p++)
                    for (int q = 1; q <= GV.nshapes; q++, bin_index++)
                        GV.sogi[img_index][bin_index] = (double) local_hist[p][q] / sum_all;

        }
    }

    public static void computeSpact(int img_index, int[][] img, int lvl) {
        int[] local_hist = new int[GV.n_bins_1_spact + 1]; // assumed that this is initialized with 0 values
        int xres = GV.xres;
        int yres = GV.yres;
        int bin;

        Vector<Integer> h_min_vec = new Vector<>();
        Vector<Integer> h_max_vec = new Vector<>();
        Vector<Integer> w_min_vec = new Vector<>();
        Vector<Integer> w_max_vec = new Vector<>();
        getPatches(h_min_vec, h_max_vec, w_min_vec, w_max_vec, lvl, xres, yres);

        for (int patch_index = 0; patch_index < h_min_vec.size(); patch_index++) {
            int xmin = h_min_vec.get(patch_index);
            int xmax = h_max_vec.get(patch_index);
            int ymin = w_min_vec.get(patch_index);
            int ymax = w_max_vec.get(patch_index);

            int sum_all = (xmax - xmin + 1) * (ymax - ymin + 1);

            for (int u = xmin; u <= xmax; u++)
                for (int v = ymin; v <= ymax; v++) {
                    bin = 0;
                    for (int k = 0; k < 8; k++)
                        if (img[u][v] >= img[u + GV.adjx[k]][v + GV.adjy[k]])
                            bin |= (1 << k);
                    if (bin > 0 && bin <= GV.n_bins_1_spact)
                        local_hist[bin] ++;
                    else
                        sum_all--;
                }

            int bin_index = patch_index * GV.n_bins_1_spact;
            if (sum_all > 0)
                for (int p = 1; p <= GV.n_bins_1_spact; p++, bin_index++)
                    GV.spact[img_index][bin_index] = (double) local_hist[p] / sum_all;
        }
    }

    public static void computeMeanSTD(int img_index, int[][] img, int lvl) {
        double tmp_mean;
        double tmp_std;
        int xres = GV.xres;
        int yres = GV.yres;

        Vector<Integer> h_min_vec = new Vector<>();
        Vector<Integer> h_max_vec = new Vector<>();
        Vector<Integer> w_min_vec = new Vector<>();
        Vector<Integer> w_max_vec = new Vector<>();
        getPatches(h_min_vec, h_max_vec, w_min_vec, w_max_vec, lvl, xres, yres);

        for (int patch_index = 0; patch_index < h_min_vec.size(); patch_index++) {
            int xmin = h_min_vec.get(patch_index);
            int xmax = h_max_vec.get(patch_index);
            int ymin = w_min_vec.get(patch_index);
            int ymax = w_max_vec.get(patch_index);

            tmp_mean = tmp_std = 0;
            for (int u = xmin; u <= xmax; u++)
                for (int v = ymin; v <= ymax; v++) {
                    tmp_mean += img[u][v];
                }
            tmp_mean /= ((xmax - xmin + 1) * (ymax - ymin + 1));
            tmp_mean /= 255;

            for (int u = xmin; u <= xmax; u++)
                for (int v = ymin; v <= ymax; v++) {
                    tmp_std += (img[u][v] / 255. - tmp_mean) * (img[u][v] / 255. - tmp_mean);
                }
            tmp_std = Math.sqrt(tmp_std) / ((xmax - xmin + 1) * (ymax - ymin + 1));

            GV.meanPixel[img_index][patch_index] = tmp_mean;
            GV.stdPixel[img_index][patch_index] = tmp_std;
        }
    }

    public static void combineToTrain(int n_images) {

        int sogi_bound = GV.n_patches * GV.n_bins_1_sogi;
        int spact_bound = sogi_bound + GV.n_patches * GV.n_bins_1_spact;
        int mean_bound = spact_bound + GV.n_patches;
        int std_bound = mean_bound + GV.n_patches;

        for(int i = 0; i < n_images; i++) {
            for(int j = 0; j < sogi_bound; j++)
                GV.train.put(i, j, GV.sogi[i][j]);

            for(int j = sogi_bound, k = 0; j < spact_bound; j++, k++)
                GV.train.put(i, j, GV.spact[i][k]);

            for(int j = spact_bound, k = 0; j < mean_bound; j++, k++)
                GV.train.put(i, j, GV.meanPixel[i][k]);

            for(int j = mean_bound, k = 0; j < std_bound; j++, k++)
                GV.train.put(i, j, GV.stdPixel[i][k]);

        }
    }

    public static void combineToTest() {
        int sogi_bound = GV.n_patches * GV.n_bins_1_sogi;
        int spact_bound = sogi_bound + GV.n_patches * GV.n_bins_1_spact;
        int mean_bound = spact_bound + GV.n_patches;
        int std_bound = mean_bound + GV.n_patches;

        for(int j = 0; j < sogi_bound; j++)
            GV.test.put(0, j, GV.sogi[0][j]);

        for(int j = sogi_bound, k = 0; j < spact_bound; j++, k++)
            GV.test.put(0, j, GV.spact[0][k]);

        for(int j = spact_bound, k = 0; j < mean_bound; j++, k++)
            GV.test.put(0, j, GV.meanPixel[0][k]);

        for(int j = mean_bound, k = 0; j < std_bound; j++, k++)
            GV.test.put(0, j, GV.stdPixel[0][k]);

    }

    public static void trainSVM() {
        try {
            CvSVMParams params = new CvSVMParams();
            params.set_svm_type(CvSVM.C_SVC);
            params.set_kernel_type(CvSVM.LINEAR);

            GV.svm = new CvSVM();

            Mat label = new Mat(1, GV._trainCats.size(), CvType.CV_32S);
            for (int i = 0; i < GV._trainCats.size(); i++) {
                label.put(0, i, GV._trainCats.get(i));
            }

            GV.svm.train(GV.train, label);
            FileIO.writeSVMToFile(GV.svm);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testSVM() {
        try {
            GV._predictedCats.add((int) GV.svm.predict(GV.test));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getBitmap(String _image) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap photo = BitmapFactory.decodeFile(_image, options);
        return photo;
    }

}
