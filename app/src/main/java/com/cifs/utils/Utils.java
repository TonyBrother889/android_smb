package com.cifs.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class Utils {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * Is the live streaming still available
     * @return is the live streaming is available
     */
    public static boolean isLiveStreamingAvailable() {
        // Todo: Please ask your app server, is the live streaming still available
        return true;
    }

    public static void showToastTips(final Context context, final String tips) {
        Toast.makeText(context, tips, Toast.LENGTH_SHORT).show();
    }


    /**单位转换*/
    //long==> 616.19KB,3.73M
    public static String sizeFormatNum2String(long size) {
        double pers = 1024*1024;
        String s = "";
        if(size>1024*1024)
            s=String.format("%.2f", (double)size/pers)+"M";
        else
            s=String.format("%.2f", (double)size/(1024))+"KB";
        return s;
    }
}
