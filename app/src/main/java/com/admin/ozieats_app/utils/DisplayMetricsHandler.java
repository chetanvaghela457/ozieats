package com.admin.ozieats_app.utils;

import android.content.res.Resources;

/**
 * Created by Naresh on 12-10-2016.
 */
public class DisplayMetricsHandler {

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }


    public static float getDensity() {
        return Resources.getSystem().getDisplayMetrics().density;
    }

    public static int getDensityDPI() {
        return Resources.getSystem().getDisplayMetrics().densityDpi;
    }

    public static int imageWidthCalc() {

        float density = Resources.getSystem().getDisplayMetrics().density;
        int width = 0;
        if (density == 0.75) {
            width = 43;
            //Log.e("LDPI ", "0.75");
        } else if (density == 1.0) {
            width = 57;
            //Log.e("MDPI ", "1.0");
        } else if (density == 1.5) {
            width = 86;
            //Log.e("HDPI ", "1.5");
        } else if (density == 2.0) {
            width = 150;
            //Log.e("xhdpi ", "2.0");
        } else if (density == 3.0) {
            width = 195;
            //Log.e("xxhdpi", "3.0");
        } else if (density == 4.0) {
            //Log.e("xxxhdpi", "4.0");
        } else {
            width = 100;
        }
        return width;
    }

    public static String getDPI() {

        float density = Resources.getSystem().getDisplayMetrics().density;
        if (density == 0.75) {
            return "LDPI";
            //Log.e("LDPI ", "0.75");
        } else if (density == 1.0) {
            return "MDPI";
            //Log.e("MDPI ", "1.0");
        } else if (density == 1.5) {
            return "HDPI";
            //Log.e("HDPI ", "1.5");
        } else if (density == 2.0) {
            return "XHDPI";
            //Log.e("xhdpi ", "2.0");
        } else if (density == 3.0) {
            return "XXHDPI";
            //Log.e("xxhdpi", "3.0");
        } else if (density == 4.0) {
            return "XXXHDPI";
            //Log.e("xxxhdpi", "4.0");
        }
        return "Unknown";
    }

}
