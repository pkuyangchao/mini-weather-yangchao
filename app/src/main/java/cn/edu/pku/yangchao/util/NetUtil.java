package cn.edu.pku.yangchao.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by YangChao on 2018/10/9.
 */
public class NetUtil {
    public static final int NETWORN_NONE = 0;
    public static final int NETWORD_WIFI = 1;
    public static final int NETWORN_MOBILE = 2;

    /**
     * ConnectivityManager主要管理和网络连接相关的操作，
     * 相关的TelephonyManager则管理和手机、运营商等的相关信息；WifiManager则管理和wifi相关的信息。 
     * 想访问网络状态，首先得添加权限<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
     * @param context
     * @return
     */
    public static int getNetWorkState(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null){
            return NETWORN_NONE;
        }

        int ntype = networkInfo.getType();
        if (ntype == ConnectivityManager.TYPE_MOBILE){
            return NETWORN_MOBILE;
        }else if (ntype == ConnectivityManager.TYPE_WIFI){
            return NETWORD_WIFI;
        }
        return NETWORN_NONE;
    }


}
