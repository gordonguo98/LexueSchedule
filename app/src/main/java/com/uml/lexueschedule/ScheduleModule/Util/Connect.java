package com.uml.lexueschedule.ScheduleModule.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class Connect {
    public static boolean isConnectIsNomarl(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected())
            {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED)
                {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }
}
