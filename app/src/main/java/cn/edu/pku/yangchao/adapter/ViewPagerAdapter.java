package cn.edu.pku.yangchao.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by YangChao on 2018/11/14.
 */
public class ViewPagerAdapter extends PagerAdapter {

    private List<View> views;
    private Context context;

    public ViewPagerAdapter(List<View> views,Context context){
        this.views = views;
        this.context = context;
    }

    @Override
    public int getCount() {
        return views.size();
    }

    //用于创建position所在位置的视图
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        container.addView(views.get(position));//添加所在位置的视图
        return views.get(position);
    }

    //删除position位置的视图
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(views.get(position));
    }

    //判断instaniateItem返回的对象是否与当前View代表的是同一个对象
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return (view == o);
    }
}
