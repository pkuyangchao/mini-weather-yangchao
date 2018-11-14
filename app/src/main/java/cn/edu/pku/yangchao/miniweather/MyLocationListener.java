package cn.edu.pku.yangchao.miniweather;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;

import java.util.List;

import cn.edu.pku.yangchao.app.MyApplication;
import cn.edu.pku.yangchao.bean.City;

/**
 * Created by YangChao on 2018/11/14.
 */
public class MyLocationListener extends BDAbstractLocationListener{

    public String recity;
    public String cityCode;

    /**
     * BDLocation为定位结果信息类
     * @param bdLocation
     */
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        String addr = bdLocation.getAddrStr();
        String country = bdLocation.getCountry();
        String province = bdLocation.getProvince();
        String city = bdLocation.getCity();
        String district = bdLocation.getDistrict();
        String street = bdLocation.getStreet();
        recity = city.replace("市","");

        List<City> mCityList;
        MyApplication myApplication;
        myApplication = MyApplication.getInstance();

        mCityList = myApplication.getCityList();

        for (City city1:mCityList){
            if (city1.getCity().equals(recity)){
                cityCode = city1.getNumber();
            }
        }
    }
}
