package cn.edu.pku.yangchao.app;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.yangchao.bean.City;
import cn.edu.pku.yangchao.db.CityDB;

/**
 * Android系统自动会为每个程序运行时创建一个Application类的对象且只创建一个（用来存储系统的一些信息），
 * 所以Application可以说是单例（singleton）模式的一个类
 * 自定义Application:创建一个类继承Application并在AndroidManifest.xml文件中的application标签中进行注册
 * 启动Application时，系统会创建一个PID，即进程ID，所有的Activity都会在此进程上运行
 * Application对象的生命周期是整个程序中最长的，它的生命周期就等于这个程序的生命周期。
 * 可以通过Application来进行一些，如：数据传递、数据共享和数据缓存等操作。
 * Created by YangChao on 2018/10/16.
 */
public class MyApplication extends Application{

    private static final String TAG = "MyAPP";

    private static MyApplication myApplication;
    private CityDB mcityDB;

    private List<City> mCityList;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"MyApplication->Oncreate");

        myApplication = this;
        mcityDB = openCityDB();
        initCityList();

    }

    //获取MyApplication实例，保护私有变量
    public static MyApplication getInstance(){
        return myApplication;
    }

//    打开数据库
    private CityDB openCityDB(){
        String path = "/data"
                + Environment.getDataDirectory().getAbsolutePath()
                + File.separator+getPackageName()
                + File.separator+"database1"
                + File.separator
                + CityDB.CITY_DB_NAME;
        File db = new File(path);
        Log.d(TAG,path);
        if (!db.exists()){
            String pathfolder = "/data"
                    + Environment.getDataDirectory().getAbsolutePath()
                    + File.separator+getPackageName()
                    + File.separator+"database1"
                    + File.separator;
            File dirFirstFolder = new File(pathfolder);
            if (!dirFirstFolder.exists()){
                dirFirstFolder.mkdir();
                Log.d("MyAPP","mkdirs");
            }
            Log.d("MyApp","db is not exists");
            try {
                InputStream is = getAssets().open("city.db");
                FileOutputStream fos = new FileOutputStream(db);
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer))!=-1){
                    fos.write(buffer,0,len);
                    fos.flush();
                }
                fos.close();
                is.close();
            }catch (IOException e){
                e.printStackTrace();
                System.exit(0);
            }

        }
        return new CityDB(this,path);
    }

    //初始化城市列表
    private void initCityList(){
        mCityList = new ArrayList<City>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                prepareCityList();
            }
        }).start();
    }

    private boolean prepareCityList(){
        mCityList = mcityDB.getAllCity();
        int i = 0;
        for (City city:mCityList){
            i++;
            String cityName = city.getCity();
            String cityCode = city.getNumber();
            Log.d(TAG,cityCode+":"+cityName);
        }
        Log.d(TAG,"i="+i);
        return true;
    }

    public List<City> getCityList() {
        return mCityList;
    }
}
