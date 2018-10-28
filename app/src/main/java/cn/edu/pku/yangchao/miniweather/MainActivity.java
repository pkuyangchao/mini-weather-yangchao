package cn.edu.pku.yangchao.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import cn.edu.pku.yangchao.bean.TodayWeather;
import cn.edu.pku.yangchao.util.NetUtil;

public class MainActivity extends Activity implements View.OnClickListener{

    private static final int UPDATE_TODAY_WEATHER = 1;

    //为更新按钮添加单击事件
    private ImageView mUpdateBtn;

    //为选择城市ImageView添加点击事件
    private ImageView mCitySelect;

    //初始化界面控键
    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv
            ,temperatureTv, climateTv, windTv, city_name_Tv;
    private ImageView weatherImg, pmImg;

    //用于保存主页面code数据
    private String tempCityCode="101010100";
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

        initView();
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
                                fengliCount++;
                            }else if (xmlPullParser.getName().equals("date") && dateCount == 0){
                                eventType = xmlPullParser.next();
                                Log.d("myWeather","date:  "+xmlPullParser.getText());
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                            }else if (xmlPullParser.getName().equals("high") && highCount == 0){
                                eventType = xmlPullParser.next();
                                Log.d("myWeather","high:  "+xmlPullParser.getText());
                                todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            }else if (xmlPullParser.getName().equals("low") && lowCount == 0){
                                eventType = xmlPullParser.next();
                                Log.d("myWeather","low:  "+xmlPullParser.getText());
                                todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            }else if (xmlPullParser.getName().equals("type") && typeCount == 0){
                                eventType = xmlPullParser.next();
                                Log.d("myWeather","type:  "+xmlPullParser.getText());
                                todayWeather.setType(xmlPullParser.getText());
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
        //Toast.makeText的第一个参数是：当前的上下文
        Toast.makeText(MainActivity.this,"更新成功",Toast.LENGTH_LONG).show();
    }


    //解决活动被回收时，临时数据得不到保存的问题
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String temp = tempCityCode;
        outState.putString("temp_city_code",temp);
    }
}
