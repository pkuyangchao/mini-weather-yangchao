package cn.edu.pku.yangchao.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.yangchao.bean.City;

/**
 * CityDB操作类
 * Created by YangChao on 2018/10/16.
 */
public class CityDB {

    public static final String CITY_DB_NAME = "city.bd";
    private static final String CITY_TABLE_NAME = "city";
    /**
     * SQLiteDatabase：本身是一个数据库的操作类，
     * 但是如果想进行数据库的操作，还需要android.database.sqlite.SQLiteOpenHelper类的帮助
     */
    private SQLiteDatabase db;

    public CityDB(Context context,String path){
        /**
         * 通过openOrCreateDatabase来打开或创建一个数据库,返回SQLiteDatabase对象。
         * openOrCreateDatabase(String name,int mode,SQLiteDatabase.CursorFactory factory)
         * name: 数据库名 mode:数据库权限，MODE_PRIVATE为本应用程序私有
         *     MODE_WORLD_READABLE和MODE_WORLD_WRITEABLE分别为全局可读和可写。
         * factory:可以用来实例化一个cusor对象的工厂类
         */
        db = context.openOrCreateDatabase(path,Context.MODE_PRIVATE,null);
    }

    public List<City> getAllCity(){
        List<City> list = new ArrayList<City>();
        //SQLiteDatabase的rawQuery() 用于执行select语句,第一个参数为select语句；第二个参数为select语句中占位符参数的值
        /**
         * Cursor是结果集游标，用于对结果集进行随机访问，与JDBC中的ResultSet作用很相似。
         * 使用moveToNext()方法可以将游标从当前行移动到下一行，如果已经移过了结果集的最后一行，返回结果为false，否则为true
         * moveToPrevious()方法（用于将游标从当前行移动到上一行，如果已经移过了结果集的第一行，返回值为false，否则为true ）
         * moveToFirst()方法（用于将游标移动到结果集的第一行，如果结果集为空，返回值为false，否则为true ）
         * moveToLast()方法（用于将游标移动到结果集的最后一行，如果结果集为空，返回值为false，否则为true)
         */
        Cursor c = db.rawQuery("SELECT * FROM "+CITY_TABLE_NAME,null);
        while (c.moveToNext()){
            String province = c.getString(c.getColumnIndex("province"));
            String city = c.getString(c.getColumnIndex("city"));
            String number = c.getString(c.getColumnIndex("number"));
            String allPY = c.getString(c.getColumnIndex("allpy"));
            String allfirstPY = c.getString(c.getColumnIndex("allfirstpy"));
            String firstPY = c.getString(c.getColumnIndex("firstpy"));
            City item = new City(province,city,number,firstPY,allPY,allfirstPY);
            list.add(item);
        }
        return list;
    }
}
