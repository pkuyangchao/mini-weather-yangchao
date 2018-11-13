package cn.edu.pku.yangchao.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.yangchao.bean.City;
import cn.edu.pku.yangchao.miniweather.R;

/**
 * Created by YangChao on 2018/11/13.
 */
public class SearchAdapter extends BaseAdapter {
    private Context mcontext;
    private List<City> mlistcity;
    private List<City> msearchList;
    private LayoutInflater mInflater;

    public SearchAdapter(Context context,List<City> citylist){
        mcontext=context;
        mlistcity=citylist;
        msearchList=new ArrayList<City>();
        mInflater=LayoutInflater.from(mcontext);//从一个Context中，获得一个布局填充器，这样就可以使用这个填充器来把xml布局文件转为View对象了
    }

    public int getCount(){
        return msearchList.size();
    }

    public Object getItem(int position){
        return  msearchList.get(position);
    }

    public long getItemId(int position){
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView==null){
            convertView=mInflater.inflate(R.layout.select_city_item,null);
        }
        TextView provinceTV=(TextView)convertView.findViewById(R.id.province);
        TextView cityTV=(TextView)convertView.findViewById(R.id.city_name);
        provinceTV.setText(msearchList.get(position).getProvince());
        cityTV.setText(msearchList.get(position).getCity());
        return convertView;
    }

    public Filter getFilter(){
        Filter filter=new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String str=constraint.toString().toUpperCase();
                FilterResults results=new FilterResults();
                ArrayList<City> filterList=new ArrayList<City>();

                if(mlistcity!=null&&mlistcity.size()!=0){
                    for(City city : mlistcity){
                        if(city.getAllFirstPY().indexOf(str)>-1||city.getAllPY().indexOf(str)>-1||city.getCity().indexOf(str)>-1){
                            filterList.add(city);
                        }
                    }
                }

                results.values=filterList;
                results.count=filterList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                msearchList=(ArrayList<City>)results.values;
                if(results.count>0){
                    notifyDataSetChanged();
                }else{
                    notifyDataSetInvalidated();
                }

            }

        };
        return filter;
    }
}
