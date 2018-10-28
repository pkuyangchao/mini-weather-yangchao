package cn.edu.pku.yangchao.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import cn.edu.pku.yangchao.app.MyApplication;
import cn.edu.pku.yangchao.bean.City;

/**
 * 选择城市活动
 * Created by YangChao on 2018/10/16.
 */
public class SelectCity extends Activity implements View.OnClickListener {

    private ImageView mBackBtn;

    private ListView mlistView;

    private List<City> cityList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        //为返回上一页按钮添加点击事件
        mBackBtn = (ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);

        initCityLists();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back:
                //Intent：用于组件之间传递数据
                Intent i = new Intent();
                i.putExtra("cityCode","101160101");
                setResult(RESULT_OK,i);
                finish();
                break;
            default:
                break;
        }
    }

    public void initCityLists(){
        mlistView = (ListView)findViewById(R.id.list_view);
        MyApplication myApplication = (MyApplication) getApplication();
        cityList = myApplication.getCityList();
//        Log.d("cityList",cityList.toString());

        int n = cityList.size();
        String[] data  = new String[n];
        final String[] code = new String[n];
        int i=0;
        for (City city:cityList){
            data[i] = city.getCity();
            code[i] = city.getNumber();
            Log.d("city",data[i]);
            i += 1;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                SelectCity.this,android.R.layout.simple_list_item_1,data
        );
        mlistView.setAdapter(adapter);
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SelectCity.this,"你单击了："+position,Toast.LENGTH_SHORT).show();

                Intent i = new Intent();
                i.putExtra("cityCode",code[position]);
                setResult(RESULT_OK,i);
                finish();
            }
        });
    }
}
