package com.example.lordone.picturegroups.BaseClasses;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
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

    public static void computeGradient(int xmax, int ymax) {

    double vert, hori;
    double tmpinten;
    int inten, orient;

    GV.shape = new int[xmax + 1][ymax + 1];

    for (int i = 0; i <= xmax; i++) for (int j = 0; j <= ymax; j++) {
        if (i > 0 && i < xmax)
            vert = GV.img[i - 1][j] - GV.img[i + 1][j];
        else if (i == 0)
            vert = GV.img[i][j] - GV.img[i + 1][j];
        else
            vert = GV.img[i - 1][j] - GV.img[i][j];
        if (j > 0 && j < ymax)
            hori = GV.img[i][j + 1] - GV.img[i][j - 1];
        else if (j == 0)
            hori = GV.img[i][j + 1] - GV.img[i][j];
        else
            hori = GV.img[i][j] - GV.img[i][j - 1];

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

    public static void compute_2_hist(Mat mat_img, int lvl) {
        int [][] local_hist_sogi = new int[GV.nshapes + 1][GV.nshapes + 1];
        int [] local_hist_spact = new int[GV.n_bins_1_spact + 1];
        int xres = mat_img.rows();
        int yres = mat_img.cols();
        GV.img = new int[xres][yres];

        int xmin, xmax, ymin, ymax;

        for (int i = 0; i < xres; i++)
            for (int j = 0; j < yres; j++)
                GV.img[i][j] = (int) mat_img.get(i, j)[0];

        computeGradient(xres - 1, yres - 1);

        int to_divide = 1 << lvl;
        int h_range = (xres - 2) / to_divide;
        int w_range = (yres - 2) / to_divide;

        Vector<Integer> h_min_vec = new Vector<>();
        Vector<Integer> h_max_vec = new Vector<>();
        Vector<Integer> w_min_vec = new Vector<>();
        Vector<Integer> w_max_vec = new Vector<>();
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

        int tmp_shape, tmp_shape_2;
        for (int i = 0; i < h_min_vec.size(); i++, GV.mstd_index++) {
            xmin = h_min_vec.get(i);
            xmax = h_max_vec.get(i);
            ymin = w_min_vec.get(i);
            ymax = w_max_vec.get(i);
            int sum_all_sogi = h_range * w_range * 8;
            int sum_all_spact = h_range * w_range;
            for(int[] row : local_hist_sogi)
                Arrays.fill(row, 0);
            Arrays.fill(local_hist_spact, 0);

            int bin = 0;

            for (int u = xmin; u <= xmax; u++)
                for (int v = ymin; v <= ymax; v++) {
                    tmp_shape = GV.shape[u][v];
                    if (tmp_shape == 0) {
                        sum_all_sogi -= 8;
                    }
                    else {
                        for (int k = 0; k < 8; k++) {
                            tmp_shape_2 = GV.shape[u + GV.adjx[k]][v + GV.adjy[k]];
                            if (tmp_shape_2 == 0) {
                                sum_all_sogi--;
                                continue;
                            }
                            local_hist_sogi[tmp_shape][tmp_shape_2]++;
                        }
                    }

                    bin = 0;
                    for (int k = 0; k < 8; k++)
                        if (GV.img[u][v] >= GV.img[u + GV.adjx[k]][v + GV.adjy[k]])
                            bin |= (1 << k);
                    if (bin > 0 && bin <= GV.n_bins_1_spact)
                        local_hist_spact[bin] ++;
                    else
                        sum_all_spact--;

                }

            if (sum_all_sogi > 0)
                for (int p = 1; p <= GV.nshapes; p++)
                    for (int q = 1; q <= GV.nshapes; q++, GV.bin_index++)
                        GV.hist_all[GV.img_index][GV.bin_index] = (double)local_hist_sogi[p][q] / sum_all_sogi;
            else
                GV.bin_index += GV.n_bins_1_sogi;

            if (sum_all_spact > 0)
                for (int p = 1; p <= GV.n_bins_1_spact; p++, GV.bin_index++)
                    GV.hist_all[GV.img_index][GV.bin_index] = (double) local_hist_spact[p] / sum_all_spact;
            else
                GV.bin_index += GV.n_bins_1_spact;

            double tmp_mean = 0;
            for (int u = xmin; u <= xmax; u++)
                for (int v = ymin; v <= ymax; v++) {
                    tmp_mean += GV.img[u][v];
                }
            tmp_mean /= (h_range * w_range);
            tmp_mean /= 255;

            double tmp_std = 0;
            for (int u = xmin; u <= xmax; u++)
                for (int v = ymin; v <= ymax; v++) {
                    tmp_std += (GV.img[u][v] / 255. - tmp_mean) * (GV.img[u][v] / 255. - tmp_mean);
                }
            tmp_std = Math.sqrt(tmp_std) / (h_range * w_range);

            GV.mean_val[GV.img_index][GV.mstd_index] = tmp_mean;
            GV.std_val[GV.img_index][GV.mstd_index] = tmp_std;

        }
    }

    public static void normalize_hist(int nrow, int ncol) {
        double [] mean = new double[ncol];
        double [] dev = new double[ncol];
        for (int j = 0; j < ncol; j++)
            mean[j] = dev[j] = 0;


        for (int i = 0; i < nrow; i++) {
            for (int j = 0; j < ncol; j++)
                mean[j] += GV.hist_all[i][j];
        }

        for (int j = 0; j < ncol; j++)
            mean[j] /= nrow;
        for (int i = 0; i < nrow; i++)
            for (int j = 0; j < ncol; j++)
                GV.hist_all[i][j] -= mean[j];

        for (int i = 0; i < nrow; i++)
            for (int j = 0; j < ncol; j++)
                dev[j] += GV.hist_all[i][j] * GV.hist_all[i][j];
        for (int j = 0; j < ncol; j++)
            dev[j] = Math.sqrt(dev[j]) / nrow;
        for (int i = 0; i < nrow; i++)
            for (int j = 0; j < ncol; j++)
                GV.hist_all[i][j] /= dev[j];

//        FileIO.writeArrayToFile(GV.hist_all, nrow, ncol, "hist.csv");
    }

    public static void doPCA() {
        Mat sample_sogi_to_pca = new Mat(GV.img_index, GV.n_bins_1_sogi, CvType.CV_32F);
        Mat sample_spact_to_pca = new Mat(GV.img_index, GV.n_bins_1_spact, CvType.CV_32F);
        Mat compressed = new Mat(GV.img_index, GV.n_compressor * GV.n_patches * 2, CvType.CV_32F);
        int min_com_col;
        int min_col = 0, max_col = -1;
        for (int ind = 0; ind < GV.n_patches; ind++) {
            min_col = max_col + 1;
            max_col = min_col + GV.n_bins_1_sogi - 1;

            for(int i = 0; i < GV.img_index; i++)
                for(int j = min_col, k = 0; j <= max_col; j++, k++) {
                    sample_sogi_to_pca.put(i, k, GV.hist_all[i][j]);
//                    System.out.print(sample_sogi_to_pca.get(i, k)[0]);
                }

//            int i = 0;
//            for (int cat = 0; cat < GV.mapTarget.length(); cat++) {
//                int sample_from = GV.begin_of_category.get(cat);
//                int sample_upto = sample_from + GV.n_train_pca;
//                for (int dem = sample_from; dem < sample_upto; dem++, i++)
//                    for (int j = min_col, k = 0; j <= max_col; j++, k++) {
//                        sample_sogi_to_pca.put(i, k, GV.hist_all[dem][j]);
//                    }
//            }

//            for (int i = 0; i < GV.img_index; i++) {
//                for (int j = min_col, k = 0; j <= max_col; j++, k++) {
//                    df_sogi.put(i, k, GV.hist_all[i][j]);
//                }
//            }

            Mat pca_mean_sogi = new Mat();
            Mat pca_eigenvectors_sogi = new Mat();

            Core.PCACompute(sample_sogi_to_pca, pca_mean_sogi, pca_eigenvectors_sogi, GV.n_compressor);

            Mat coeffs1 = new Mat(1, GV.n_compressor, CvType.CV_32F);
            for (int i = 0; i < GV.img_index; i++) {
                Mat vec = sample_sogi_to_pca.row(i);
                Core.PCAProject(vec, pca_mean_sogi, pca_eigenvectors_sogi, coeffs1);
                min_com_col = ind * (GV.n_compressor * 2);
                for (int j = 0; j < GV.n_compressor; j++, min_com_col++)
                    compressed.put(i, min_com_col, coeffs1.get(0, j));
            }

            //----------------------

            min_col = max_col + 1;
            max_col = min_col + GV.n_bins_1_spact - 1;

            for(int i = 0; i < GV.img_index; i++)
                for(int j = min_col, k = 0; j <= max_col; j++, k++)
                    sample_spact_to_pca.put(i, k, GV.hist_all[i][j]);

//            i = 0;
//            for (int cat = 0; cat < GV.mapTarget.length(); cat++) {
//                int sample_from = GV.begin_of_category.get(cat);
//                int sample_upto = sample_from + GV.n_train_pca;
//                for (int dem = sample_from; dem < sample_upto; dem++, i++)
//                    for (int j = min_col, k = 0; j <= max_col; j++, k++) {
//                        sample_spact_to_pca.put(i, k, GV.hist_all[dem][j]);
//                    }
//            }
//
//            for (i = 0; i < GV.img_index; i++) {
//                for (int j = min_col, k = 0; j <= max_col; j++, k++) {
//                    df_spact.put(i, k, GV.hist_all[i][j]);
//                }
//            }

            Mat pca_mean_spact = new Mat();
            Mat pca_eigenvectors_spact = new Mat();

            Core.PCACompute(sample_spact_to_pca ,pca_mean_spact, pca_eigenvectors_spact, GV.n_compressor);

            Mat coeffs2 = new Mat(1, GV.n_compressor, CvType.CV_32F);
            for (int i = 0; i < GV.img_index; i++) {
                Mat vec = sample_spact_to_pca.row(i);
                Core.PCAProject(vec, pca_mean_spact, pca_eigenvectors_spact, coeffs2);
                min_com_col = (ind * 2 + 1) * GV.n_compressor;
                for (int j = 0; j < GV.n_compressor; j++, min_com_col++)
                    compressed.put(i, min_com_col, coeffs2.get(0, j));
            }

        }

        FileIO.writeMatToFile(compressed, GV.img_index, GV.n_compressed_col, "ultimate.csv");
        FileIO.writeArrayToFile(GV.mean_val, GV.img_index, GV.mstd_index, "mean_val.csv");
        FileIO.writeArrayToFile(GV.std_val, GV.img_index, GV.mstd_index, "std_val.csv");

        //TODO: combine compressed with mean_val and std_val, train SVM

//        writeUltimate(compressed, row, n_compressed_col);

    }

//    public static void normalizeFeatures_train() {
//        double sumAround;
//        for (int d = 1; d <= GV.maxDist; d++) for (int o = 1; o <= GV.norient; o++) for (int i = 1; i <= GV.ninten; i++) for(int pos = 1; pos <= GV.npos; pos++) {
//            sumAround = 0;
//            for (int u = 1; u <= GV.norient; u++) for (int v = 1; v <= GV.ninten; v++)
//                sumAround += GV.features[d][o][i][u][v][pos];
//            if (sumAround > GV.eps)
//                for (int u = 1; u <= GV.norient; u++) for (int v = 1; v <= GV.ninten; v++)
//                    GV.features[d][o][i][u][v][pos] /= sumAround;
//        }
////        FileIO.writeFileFeatures();
//        int dem = 0;
//        for (int d = 1; d <= GV.maxDist; d++) for (int o1 = 1; o1 <= GV.norient; o1++)
//            for (int i1 = 1; i1 <= GV.ninten; i1++) for (int o2 = 1; o2 <= GV.norient; o2++)
//                for (int i2 = 1; i2 <= GV.ninten; i2++) for(int pos = 1; pos <= GV.npos; pos++){
//                    GV.sogi.put(GV.cnt, dem++, (float) GV.features[d][o1][i1][o2][i2][pos]);
//                }
//    }

//    public static void normalizeFeatures_test() {
//        double sumAround;
//        for (int d = 1; d <= GV.maxDist; d++) for (int o = 1; o <= GV.norient; o++) for (int i = 1; i <= GV.ninten; i++) for(int pos = 1; pos <= GV.npos; pos++) {
//            sumAround = 0;
//            for (int u = 1; u <= GV.norient; u++) for (int v = 1; v <= GV.ninten; v++)
//                sumAround += GV.features[d][o][i][u][v][pos];
//            if (sumAround > GV.eps)
//                for (int u = 1; u <= GV.norient; u++) for (int v = 1; v <= GV.ninten; v++)
//                    GV.features[d][o][i][u][v][pos] /= sumAround;
//        }
//
//        int dem = 0;
//        for (int d = 1; d <= GV.maxDist; d++) for (int o1 = 1; o1 <= GV.norient; o1++)
//            for (int i1 = 1; i1 <= GV.ninten; i1++) for (int o2 = 1; o2 <= GV.norient; o2++)
//                for (int i2 = 1; i2 <= GV.ninten; i2++) for(int pos = 1; pos <= GV.npos; pos++){
//                    GV.testMat.put(0, dem++, (float) GV.features[d][o1][i1][o2][i2][pos]);
//                }
//    }

    public static Bitmap getBitmap(String _image) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap photo = BitmapFactory.decodeFile(_image, options);
        return photo;
    }

//    public static void releaseMemory() {
//        GV.sogi = null;
//        GV.features = null;
//        System.gc();
//    }

}
