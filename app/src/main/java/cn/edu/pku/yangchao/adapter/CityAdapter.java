package cn.edu.pku.yangchao.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cn.edu.pku.yangchao.bean.City;
import cn.edu.pku.yangchao.miniweather.R;

/**
 * Created by YangChao on 2018/11/13.
 */
public class CityAdapter extends ArrayAdapter<City>{

    private int resourceID;
    public CityAdapter(Context context, int textViewResourceID, List<City> objects){
        super(context,textViewResourceID,objects);
        resourceID=textViewResourceID;
    }

    public View getView(int position, View converview, ViewGroup parent){
        City city = getItem(position);
        View view;
        if (converview==null){
            view = LayoutInflater.from(getContext()).inflate(resourceID,parent,false);
        }else{
            view = converview;
        }

        TextView province = (TextView)view.findViewById(R.id.province);
        TextView cityname=(TextView)view.findViewById(R.id.city_name);
        province.setText(city.getProvince());
        cityname.setText(city.getCity());

        return view;

    }
}
