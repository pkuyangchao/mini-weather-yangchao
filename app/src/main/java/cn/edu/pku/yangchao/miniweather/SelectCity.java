package cn.edu.pku.yangchao.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LongDef;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.pku.yangchao.adapter.CityAdapter;
import cn.edu.pku.yangchao.adapter.SearchAdapter;
import cn.edu.pku.yangchao.app.MyApplication;
import cn.edu.pku.yangchao.bean.City;

/**
 * 选择城市活动
 * Created by YangChao on 2018/10/16.
 */
public class SelectCity extends Activity implements View.OnClickListener {

    private ImageView mBackBtn;
    private TextView cityselected;

    private CityAdapter cityAdapter;
    private SearchAdapter msearchAdapter;
    private List<City> data = new ArrayList<City>();

    private EditText mEditText;//搜索功能
    private ListView mListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        //为返回上一页按钮添加点击事件
        mBackBtn = (ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);

        cityselected = (TextView) findViewById(R.id.title_name);

        mListView=(ListView)findViewById(R.id.title_list);

        mEditText=(EditText)findViewById(R.id.title_search);

        initCityLists();

        Log.d("data",data.get(0).toString());
        cityAdapter = new CityAdapter(SelectCity.this,R.layout.select_city_item,data);
        mListView.setAdapter(cityAdapter);

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                msearchAdapter = new SearchAdapter(SelectCity.this,data);
                mListView.setTextFilterEnabled(true);
                if (data.size()<1|| TextUtils.isEmpty(s)){
                    mListView.setAdapter(cityAdapter);
                }else{
                    mListView.setAdapter(msearchAdapter);
                    msearchAdapter.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                City city;
                if (msearchAdapter!=null){
                    city = (City)msearchAdapter.getItem(position);
                }else{
                    Toast.makeText(SelectCity.this,"你单击了"+position,Toast.LENGTH_SHORT).show();
                    city = data.get(position);
                }
                cityselected.setText("当前城市："+city.getCity());
                Intent intent = new Intent();
                intent.putExtra("cityCode",city.getNumber());
                setResult(RESULT_OK,intent);

            }
        });

    }


    //初始化城市列表信息
    public void initCityLists(){
       MyApplication myApplication = (MyApplication)getApplication();
       data = myApplication.getCityList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back:

                //Intent：用于组件之间传递数据
//                Intent i = new Intent();
//                i.putExtra("cityCode",);
//                setResult(RESULT_OK,i);
                finish();
                break;
            default:
                break;
        }
    }
}
