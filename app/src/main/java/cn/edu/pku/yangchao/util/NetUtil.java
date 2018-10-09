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
