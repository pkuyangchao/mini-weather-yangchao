package cn.edu.pku.yangchao.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.yangchao.adapter.ViewPagerAdapter;
import cn.edu.pku.yangchao.bean.TodayWeather;
import cn.edu.pku.yangchao.util.NetUtil;

public class MainActivity extends Activity implements View.OnClickListener, ViewPager.OnPageChangeListener{

    private static final int UPDATE_TODAY_WEATHER = 1;
    private static final int DB = 2;

    //为更新按钮添加单击事件
    private ImageView mUpdateBtn;

    //为选择城市ImageView添加点击事件
    private ImageView mCitySelect;

    //初始化界面控键
    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv
            ,temperatureTv, climateTv, windTv, city_name_Tv;
    private ImageView weatherImg, pmImg;
    private ProgressBar progressBar;

    //用于保存主页面code数据
    private String tempCityCode="101010100";

    //六天天气信息展示，两个展示页
    private ViewPagerAdapter vpAdapter;
    private ViewPager vp;
    private List<View> views;
    private ImageView[] dots;//小圆点
    private int[] ids = {R.id.iv1,R.id.iv2};
    private TextView week_today1,temperature1,climate1,wind1;
    private TextView week_today2,temperature2,climate2,wind2;
    private TextView week_today3,temperature3,climate3,wind3;
    private TextView week_today4,temperature4,climate4,wind4;
    private TextView week_today5,temperature5,climate5,wind5;
    private TextView week_today6,temperature6,climate6,wind6;

    //定位
    private LocationClient mLocationClient;
    private MyLocationListener myLocationListener = new MyLocationListener();
    private ImageView mTitleLocation;//定位按钮

    /**
     * Android中的View不是线程安全的
     *   在Android应用启动时，会自动创建一个线程，即程序的主线程，
     *   主线程负责UI的展示、UI事件消息的派发处理等等，因此主线程也叫做UI线程。
     * Handler：是Android中引入的一种让开发者参与处理线程中消息循环的机制。（实现跨线程跟新UI控件）
     *   每个Hanlder都关联了一个线程，每个线程内部都维护了一个消息队列MessageQueue
     *   这样Handler实际上也就关联了一个消息队列
     *   Handler可以用来在多线程间进行通信。
     *   在执行new Handler()的时候，默认情况下Handler会绑定当前代码执行的线程
     *   两种方法：一：通过post方法（uiHandler.post(runnable)），二：调用sendMessage方法。
     */
    //通过消息机制，将解析的天气对象，通过消息发送给主线程，主线程接收到消息数据后，调用函数，更新UI上的数据
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what){
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        if (savedInstanceState!=null){
            String temp = savedInstanceState.getString("temp_city_code");
            querWeatherCode(temp);
        }
        mUpdateBtn = findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);

        progressBar = (ProgressBar)findViewById(R.id.title_update_progress);

        //检查网络连接状态
        if (NetUtil.getNetWorkState(this) != NetUtil.NETWORN_NONE){
            Log.d("myWeather","网络正常");
            Toast.makeText(MainActivity.this,"网络正常！",Toast.LENGTH_LONG).show();
        }else{
            Log.d("myWeather","网络挂断了");
            Toast.makeText(MainActivity.this,"网络挂断了",Toast.LENGTH_LONG).show();
        }

        //为选择城市控件添加点击事件
        mCitySelect = (ImageView)findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);

        //初始化滑动页面
        initViews();
        //初始化小圆点
        initDots();
        //初始化界面控件
        initView();

        mTitleLocation = (ImageView)findViewById(R.id.title_location);
        mTitleLocation.setOnClickListener(this);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(myLocationListener);
        initLocation();

    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        int span = 1000;
        option.setScanSpan(0);//默认定位一次
        option.setIsNeedAddress(true);//是否需要地址信息，默认不需要
        option.setOpenGps(true);//是否使用GPS
        option.setLocationNotify(true);
        option.setIsNeedLocationDescribe(true);
        option.setIsNeedLocationPoiList(true);
        option.setIgnoreKillProcess(false);
        option.SetIgnoreCacheException(false);
        option.setEnableSimulateGps(false);
        mLocationClient.setLocOption(option);
    }

    @Override
    public void onClick(View v) {
        //选择城市控件的点击事件
        if (v.getId()==R.id.title_city_manager){
            //用于接收返回的信息
            Intent i = new Intent(this,SelectCity.class);
            startActivityForResult(i,1);
        }

        if (v.getId() == R.id.title_update_btn){
            progressBar.setVisibility(View.VISIBLE);
            mUpdateBtn.setVisibility(View.INVISIBLE);

            SharedPreferences sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
            //如果没有数据，就赋值一个缺省值
            String cityCode = sharedPreferences.getString("main_city_code","101010100");
            tempCityCode = cityCode;
            Log.d("myWeather",cityCode);

            if (NetUtil.getNetWorkState(this) != NetUtil.NETWORN_NONE){
                Log.d("myWeather","网络OK");
                //根据城市code获取网络数据
                querWeatherCode(cityCode);
            }else {
                Log.d("myWeather","网络挂了");
                Toast.makeText(MainActivity.this,"网络挂了",Toast.LENGTH_LONG).show();
            }
        }
        if (v.getId()==R.id.title_location){

            if (mLocationClient.isStarted()){
                mLocationClient.stop();
            }
            mLocationClient.start();

            final Handler BDHandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what){
                        case DB:
                            if (msg.obj != null){
                                if (NetUtil.getNetWorkState(MainActivity.this)!=NetUtil.NETWORN_NONE){
                                    Log.d("myWeather","网络ok");
                                    querWeatherCode(myLocationListener.cityCode);
                                }else {
                                    Log.d("myWeather","网卡挂了");
                                    Toast.makeText(MainActivity.this,"网络挂了",Toast.LENGTH_LONG).show();
                                }
                            }
                            myLocationListener.cityCode=null;
                            break;
                        default:
                            break;
                    }
                }
            };

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (myLocationListener.cityCode==null){
                            Thread.sleep(2000);
                        }
                        Message msg = new Message();
                        msg.what = DB;
                        msg.obj = myLocationListener.cityCode;
                        BDHandler.sendMessage(msg);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    //接收城市选择返回的数据
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 1 && resultCode == RESULT_OK){
            //获取数据
            String newCityCode = data.getStringExtra("cityCode");
            tempCityCode = newCityCode;
            Log.d("myWeather","选择的城市代码为："+newCityCode);

            if (NetUtil.getNetWorkState(this)!=NetUtil.NETWORN_NONE){
                Log.d("myWeather","网络OK");
                querWeatherCode(newCityCode);
            }else {
                Log.d("myWeather","网络挂了");
                Toast.makeText(MainActivity.this,"网络挂了",Toast.LENGTH_LONG).show();
            }
        }
    }

    //获取网络数据
    private void querWeatherCode(String cityCode){
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey="+cityCode;
        Log.d("myWeather",address);
        //耗时的过程都放在除主线程外的地方
        new Thread(new Runnable() {
            @Override
            public void run() {
                /**
                 * HttpURLConnection：基于http协议的，支持get，post，put，delete等各种请求方式
                 */
                HttpURLConnection con = null;
                TodayWeather todayWeather = null;
                try{
                    URL url = new URL(address);
                    //得到连接对象
                    con = (HttpURLConnection) url.openConnection();
                    //设置请求方式
                    con.setRequestMethod("GET");

                    /**
                     * 为什么？防止程序运行过程中，因为网络等问题使程序阻塞
                     * ConnectTimeout只有在网络正常的情况下才有效，而当网络不正常时，ReadTimeout才真正的起作用
                     * connect timeout：是建立连接的超时时间；
                     * read timeout：是传递数据的超时时间。
                     * 应当结合使用
                     */
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    //得到响应流，并不是数据，但可以从中读出数据，从流中读取数据的操作必须放在子线程
                    // 从这个流对象中只能读取一次数据，第二次读取时将会得到空数据。。
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuffer response = new StringBuffer();
                    String str;
                    while ((str=reader.readLine())!=null){
                        response.append(str);
                        Log.d("myWeather",str);
                    }
                    String responseStr = response.toString();
                    Log.d("myWeather",responseStr);

                    //获取网络数据后，调用解析函数
                    todayWeather = parseXML(responseStr);
                    if (todayWeather != null){
                        Log.d("myWeather",todayWeather.toString());

                        //给主线程发送消息
                        Message message = new Message();
                        message.what = UPDATE_TODAY_WEATHER;
                        message.obj = todayWeather;
                        mHandler.sendMessage(message);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if (con != null){
                        con.disconnect();
                    }
                }
            }
        }).start();
    }

    //解析网络数据
    private TodayWeather parseXML(String xmldata){
        TodayWeather todayWeather = null;
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;
        try {
            /**
             * Android系统中和创建XML相关的包为org.xmlpull.v1
             * 在这个包中不仅提供了用于创建XML的 XmlSerializer，还提供了用来解析XML的Pull方式解析器 XmlPullParser
             */
            //使用工厂类XmlPullParserFactory的方式
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            // xpp.setInput(is, "utf-8")，声明定义保存xml信息的数据结构
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather","parseXML");

            while (eventType != XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    //判断当前事件是否为文档开始事件，如果是，跳出循环，进入下一个元素
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    //判断当前事件是否是标签元素的开始事件
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("resp")){
                            todayWeather = new TodayWeather();
                        }
                        if (todayWeather != null){
                            if(xmlPullParser.getName().equals("city")){
                                eventType = xmlPullParser.next();
                                Log.d("myWeather","city:    "+xmlPullParser.getText());
                                todayWeather.setCity(xmlPullParser.getText());
                            }else if (xmlPullParser.getName().equals("updatetime")){
                                eventType = xmlPullParser.next();
                                Log.d("myWeather","updatetime:  "+xmlPullParser.getText());
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            }else if (xmlPullParser.getName().equals("shidu")){
                                eventType = xmlPullParser.next();
                                Log.d("myWeather","shidu:  "+xmlPullParser.getText());
                                todayWeather.setShidu(xmlPullParser.getText());
                            }else if (xmlPullParser.getName().equals("wendu")){
                                eventType = xmlPullParser.next();
                                Log.d("myWeather","wendu:  "+xmlPullParser.getText());
                                todayWeather.setWendu(xmlPullParser.getText());
                            }else if (xmlPullParser.getName().equals("pm25")){
                                eventType = xmlPullParser.next();
                                Log.d("myWeather","pm25:  "+xmlPullParser.getText());
                                todayWeather.setPm25(xmlPullParser.getText());
                            }else if (xmlPullParser.getName().equals("quality")){
                                eventType = xmlPullParser.next();
                                Log.d("myWeather","quality:  "+xmlPullParser.getText());
                                todayWeather.setQuality(xmlPullParser.getText());
                            }
                            //fengxiangCount==0，因为只有第一个数据是当天的
                            else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0){
                                eventType = xmlPullParser.next();
                                Log.d("myWeather","fengxiang:  "+xmlPullParser.getText());
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                            }else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0){
                                eventType = xmlPullParser.next();
                                Log.d("myWeather","fengli:  "+xmlPullParser.getText());
                                todayWeather.setFengli(xmlPullParser.getText());
                                todayWeather.setWind1(xmlPullParser.getText());
                                fengliCount++;
                            }else if (xmlPullParser.getName().equals("fengli") && fengliCount == 1){
                                eventType = xmlPullParser.next();
                                todayWeather.setWind2(xmlPullParser.getText());
                                fengliCount++;
                            }else if (xmlPullParser.getName().equals("fengli") && fengliCount == 2){
                                eventType = xmlPullParser.next();
                                todayWeather.setWind3(xmlPullParser.getText());
                                fengliCount++;
                            }else if (xmlPullParser.getName().equals("fengli") && fengliCount == 3){
                                eventType = xmlPullParser.next();
                                todayWeather.setWind4(xmlPullParser.getText());
                                fengliCount++;
                            }else if (xmlPullParser.getName().equals("fengli") && fengliCount == 4){
                                eventType = xmlPullParser.next();
                                todayWeather.setWind5(xmlPullParser.getText());
                                fengliCount++;
                            }else if (xmlPullParser.getName().equals("fengli") && fengliCount == 5){
                                eventType = xmlPullParser.next();
                                todayWeather.setWind6(xmlPullParser.getText());
                                fengliCount++;
                            }else if (xmlPullParser.getName().equals("date") && dateCount == 0){
                                eventType = xmlPullParser.next();
                                Log.d("myWeather","date:  "+xmlPullParser.getText());
                                todayWeather.setDate(xmlPullParser.getText());
                                todayWeather.setWeek_today1(xmlPullParser.getText());
                                dateCount++;
                            }else if (xmlPullParser.getName().equals("date") && dateCount == 1){
                                eventType = xmlPullParser.next();
                                todayWeather.setWeek_today2(xmlPullParser.getText());
                                dateCount++;
                            }else if (xmlPullParser.getName().equals("date") && dateCount == 2){
                                eventType = xmlPullParser.next();
                                todayWeather.setWeek_today3(xmlPullParser.getText());
                                dateCount++;
                            }else if (xmlPullParser.getName().equals("date") && dateCount == 3){
                                eventType = xmlPullParser.next();
                                todayWeather.setWeek_today4(xmlPullParser.getText());
                                dateCount++;
                            }else if (xmlPullParser.getName().equals("date") && dateCount == 4){
                                eventType = xmlPullParser.next();
                                todayWeather.setWeek_today5(xmlPullParser.getText());
                                dateCount++;
                            }else if (xmlPullParser.getName().equals("date") && dateCount == 5){
                                eventType = xmlPullParser.next();
                                todayWeather.setWeek_today6(xmlPullParser.getText());
                                dateCount++;
                            }else if (xmlPullParser.getName().equals("high") && highCount == 0){
                                eventType = xmlPullParser.next();
                                Log.d("myWeather","high:  "+xmlPullParser.getText());
                                todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                                todayWeather.setTemperatureH1(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            }else if (xmlPullParser.getName().equals("high") && highCount == 1){
                                eventType = xmlPullParser.next();
                                todayWeather.setTemperatureH2(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            }else if (xmlPullParser.getName().equals("high") && highCount == 2){
                                eventType = xmlPullParser.next();
                                todayWeather.setTemperatureH3(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            }else if (xmlPullParser.getName().equals("high") && highCount == 3){
                                eventType = xmlPullParser.next();
                                todayWeather.setTemperatureH4(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            }else if (xmlPullParser.getName().equals("high") && highCount == 4){
                                eventType = xmlPullParser.next();
                                todayWeather.setTemperatureH5(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            }else if (xmlPullParser.getName().equals("high") && highCount == 5){
                                eventType = xmlPullParser.next();
                                todayWeather.setTemperatureH6(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            }else if (xmlPullParser.getName().equals("low") && lowCount == 0){
                                eventType = xmlPullParser.next();
                                Log.d("myWeather","low:  "+xmlPullParser.getText());
                                todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                                todayWeather.setGetTemperatureL1(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            }else if (xmlPullParser.getName().equals("low") && lowCount == 1){
                                eventType = xmlPullParser.next();
                                todayWeather.setGetTemperatureL2(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            }else if (xmlPullParser.getName().equals("low") && lowCount == 2){
                                eventType = xmlPullParser.next();
                                todayWeather.setGetTemperatureL3(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            }else if (xmlPullParser.getName().equals("low") && lowCount == 3){
                                eventType = xmlPullParser.next();
                                todayWeather.setGetTemperatureL4(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            }else if (xmlPullParser.getName().equals("low") && lowCount == 4){
                                eventType = xmlPullParser.next();
                                todayWeather.setGetTemperatureL5(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            }else if (xmlPullParser.getName().equals("low") && lowCount == 5){
                                eventType = xmlPullParser.next();
                                todayWeather.setGetTemperatureL6(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            }else if (xmlPullParser.getName().equals("type") && typeCount == 0){
                                eventType = xmlPullParser.next();
                                Log.d("myWeather","type:  "+xmlPullParser.getText());
                                todayWeather.setType(xmlPullParser.getText());
                                todayWeather.setClimate1(xmlPullParser.getText());
                                typeCount++;
                            }else if (xmlPullParser.getName().equals("type") && typeCount == 1){
                                eventType = xmlPullParser.next();
                                todayWeather.setClimate2(xmlPullParser.getText());
                                typeCount++;
                            }else if (xmlPullParser.getName().equals("type") && typeCount == 2){
                                eventType = xmlPullParser.next();
                                todayWeather.setClimate3(xmlPullParser.getText());
                                typeCount++;
                            }else if (xmlPullParser.getName().equals("type") && typeCount == 3){
                                eventType = xmlPullParser.next();
                                todayWeather.setClimate4(xmlPullParser.getText());
                                typeCount++;
                            }else if (xmlPullParser.getName().equals("type") && typeCount == 4){
                                eventType = xmlPullParser.next();
                                todayWeather.setClimate5(xmlPullParser.getText());
                                typeCount++;
                            }else if (xmlPullParser.getName().equals("type") && typeCount == 5){
                                eventType = xmlPullParser.next();
                                todayWeather.setClimate6(xmlPullParser.getText());
                                typeCount++;
                            }
                        }
                        break;
                    //判断当前事件是否是标签元素结束事件
                    case XmlPullParser.END_TAG:
                        break;
                }
                //进入下一个元素并触发相应事件
                eventType = xmlPullParser.next();
            }
        }catch (XmlPullParserException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return todayWeather;
    }

    //初始化控键内容
    void initView(){
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView)findViewById(R.id.city);
        timeTv = (TextView)findViewById(R.id.time);
        humidityTv = (TextView)findViewById(R.id.humidity);
        weekTv = (TextView)findViewById(R.id.week_today);
        pmDataTv = (TextView)findViewById(R.id.pm_data);
        pmQualityTv = (TextView)findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView)findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView)findViewById(R.id.temperature);
        climateTv = (TextView)findViewById(R.id.climate);
        windTv = (TextView)findViewById(R.id.wind);
        weatherImg = (ImageView)findViewById(R.id.weather_img);
        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");

        //六日天气
        week_today1 = views.get(0).findViewById(R.id.week_today1);
        temperature1 = views.get(0).findViewById(R.id.temperature1);
        climate1 = views.get(0).findViewById(R.id.climate1);
        wind1 = views.get(0).findViewById(R.id.wind1);

        week_today2 = views.get(0).findViewById(R.id.week_today2);
        temperature2 = views.get(0).findViewById(R.id.temperature2);
        climate2 = views.get(0).findViewById(R.id.climate2);
        wind2 = views.get(0).findViewById(R.id.wind2);

        week_today3 = views.get(0).findViewById(R.id.week_today3);
        temperature3 = views.get(0).findViewById(R.id.temperature3);
        climate3 = views.get(0).findViewById(R.id.climate3);
        wind3 = views.get(0).findViewById(R.id.wind3);

        week_today4 = views.get(1).findViewById(R.id.week_today1);
        temperature4 = views.get(1).findViewById(R.id.temperature1);
        climate4 = views.get(1).findViewById(R.id.climate1);
        wind4 = views.get(1).findViewById(R.id.wind1);

        week_today5 = views.get(1).findViewById(R.id.week_today2);
        temperature5 = views.get(1).findViewById(R.id.temperature2);
        climate5 = views.get(1).findViewById(R.id.climate2);
        wind5 = views.get(1).findViewById(R.id.wind2);

        week_today6 = views.get(1).findViewById(R.id.week_today3);
        temperature6 = views.get(1).findViewById(R.id.temperature3);
        climate6 = views.get(1).findViewById(R.id.climate3);
        wind6 = views.get(1).findViewById(R.id.wind3);

        week_today2.setText("N/A");
        temperature2.setText("N/A");
        climate2.setText("N/A");
        wind2.setText("N/A");

        week_today3.setText("N/A");
        temperature3.setText("N/A");
        climate3.setText("N/A");
        wind3.setText("N/A");

        week_today4.setText("N/A");
        temperature4.setText("N/A");
        climate4.setText("N/A");
        wind4.setText("N/A");

        week_today5.setText("N/A");
        temperature5.setText("N/A");
        climate5.setText("N/A");
        wind5.setText("N/A");

        week_today6.setText("N/A");
        temperature6.setText("N/A");
        climate6.setText("N/A");
        wind6.setText("N/A");

        week_today1.setText("N/A");
        temperature1.setText("N/A");
        climate1.setText("N/A");
        wind1.setText("N/A");
    }

    //更新TodayWeather函数
    void updateTodayWeather(TodayWeather todayWeather){
        city_name_Tv.setText(todayWeather.getCity()+"天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+"发布");
        humidityTv.setText("湿度:"+todayWeather.getShidu());

        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh()+"-"+todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力"+todayWeather.getFengli());

        //六日天气
        week_today1.setText(todayWeather.getWeek_today1());
        temperature1.setText(todayWeather.getGetTemperatureL1()+"-"+todayWeather.getTemperatureH1());
        climate1.setText(todayWeather.getClimate1());
        wind1.setText(todayWeather.getWind1());

        week_today2.setText(todayWeather.getWeek_today2());
        temperature2.setText(todayWeather.getGetTemperatureL2()+"-"+todayWeather.getTemperatureH2());
        climate2.setText(todayWeather.getClimate2());
        wind2.setText(todayWeather.getWind2());

        week_today3.setText(todayWeather.getWeek_today3());
        temperature3.setText(todayWeather.getGetTemperatureL3()+"-"+todayWeather.getTemperatureH3());
        climate3.setText(todayWeather.getClimate3());
        wind3.setText(todayWeather.getWind3());

        week_today4.setText(todayWeather.getWeek_today4());
        temperature4.setText(todayWeather.getGetTemperatureL4()+"-"+todayWeather.getTemperatureH4());
        climate4.setText(todayWeather.getClimate4());
        wind4.setText(todayWeather.getWind4());

        week_today5.setText(todayWeather.getWeek_today5());
        temperature5.setText(todayWeather.getGetTemperatureL5()+"-"+todayWeather.getTemperatureH5());
        climate5.setText(todayWeather.getClimate5());
        wind5.setText(todayWeather.getWind5());

        week_today6.setText(todayWeather.getWeek_today6());
        temperature6.setText(todayWeather.getGetTemperatureL6()+"-"+todayWeather.getTemperatureH6());
        climate6.setText(todayWeather.getClimate6());
        wind6.setText(todayWeather.getWind6());

        //Toast.makeText的第一个参数是：当前的上下文
        Toast.makeText(MainActivity.this,"更新成功",Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.INVISIBLE);
        mUpdateBtn.setVisibility(View.VISIBLE);
    }


    //解决活动被回收时，临时数据得不到保存的问题
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String temp = tempCityCode;
        outState.putString("temp_city_code",temp);
    }

    void initDots(){
        dots = new ImageView[views.size()];
        for (int i=0;i<views.size();i++){
            dots[i]=(ImageView)findViewById(ids[i]);
        }
    }

    private void initViews(){
        LayoutInflater inflater = LayoutInflater.from(this);
        views = new ArrayList<View>();
        views.add(inflater.inflate(R.layout.sixday1,null));
        views.add(inflater.inflate(R.layout.sixday2,null));
        vpAdapter = new ViewPagerAdapter(views,this);
        vp = (ViewPager)findViewById(R.id.viewpager);
        vp.setAdapter(vpAdapter);
        vp.setOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        for (int a=0;a<ids.length;a++){
            if (a==i){
                dots[a].setImageResource(R.drawable.page_indicator_focuse);
            }else {
                dots[a].setImageResource(R.drawable.page_indicator_unfocuse);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
