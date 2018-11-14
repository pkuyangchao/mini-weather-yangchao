package cn.edu.pku.yangchao.bean;

/**
 * Created by YangChao on 2018/10/9.
 */
public class TodayWeather {
    private String city;
    private String updatetime;
    private String wendu;
    private String shidu;
    private String pm25;
    private String quality;
    private String fengxiang;
    private String fengli;
    private String date;
    private String high;
    private String low;
    private String type;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public String getWendu() {
        return wendu;
    }

    public void setWendu(String wendu) {
        this.wendu = wendu;
    }

    public String getShidu() {
        return shidu;
    }

    public void setShidu(String shidu) {
        this.shidu = shidu;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getFengxiang() {
        return fengxiang;
    }

    public void setFengxiang(String fengxiang) {
        this.fengxiang = fengxiang;
    }

    public String getFengli() {
        return fengli;
    }

    public void setFengli(String fengli) {
        this.fengli = fengli;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "TodayWeather{" +
                "city='" + city + '\'' +
                ", updatetime='" + updatetime + '\'' +
                ", wendu='" + wendu + '\'' +
                ", shidu='" + shidu + '\'' +
                ", pm25='" + pm25 + '\'' +
                ", quality='" + quality + '\'' +
                ", fengxiang='" + fengxiang + '\'' +
                ", fengli='" + fengli + '\'' +
                ", date='" + date + '\'' +
                ", high='" + high + '\'' +
                ", low='" + low + '\'' +
                ", type='" + type + '\'' +
                ", week_today1='" + week_today1 + '\'' +
                ", temperatureH1='" + temperatureH1 + '\'' +
                ", getTemperatureL1='" + getTemperatureL1 + '\'' +
                ", climate1='" + climate1 + '\'' +
                ", wind1='" + wind1 + '\'' +
                ", week_today2='" + week_today2 + '\'' +
                ", temperatureH2='" + temperatureH2 + '\'' +
                ", getTemperatureL2='" + getTemperatureL2 + '\'' +
                ", climate2='" + climate2 + '\'' +
                ", wind2='" + wind2 + '\'' +
                ", week_today3='" + week_today3 + '\'' +
                ", temperatureH3='" + temperatureH3 + '\'' +
                ", getTemperatureL3='" + getTemperatureL3 + '\'' +
                ", climate3='" + climate3 + '\'' +
                ", wind3='" + wind3 + '\'' +
                ", week_today4='" + week_today4 + '\'' +
                ", temperatureH4='" + temperatureH4 + '\'' +
                ", getTemperatureL4='" + getTemperatureL4 + '\'' +
                ", climate4='" + climate4 + '\'' +
                ", wind4='" + wind4 + '\'' +
                ", week_today5='" + week_today5 + '\'' +
                ", temperatureH5='" + temperatureH5 + '\'' +
                ", getTemperatureL5='" + getTemperatureL5 + '\'' +
                ", climate5='" + climate5 + '\'' +
                ", wind5='" + wind5 + '\'' +
                ", week_today6='" + week_today6 + '\'' +
                ", temperatureH6='" + temperatureH6 + '\'' +
                ", getTemperatureL6='" + getTemperatureL6 + '\'' +
                ", climate6='" + climate6 + '\'' +
                ", wind6='" + wind6 + '\'' +
                '}';
    }

    //六日天气的日期、风力、温度、天气类型
    private String week_today1,temperatureH1,getTemperatureL1,climate1,wind1;
    private String week_today2,temperatureH2,getTemperatureL2,climate2,wind2;
    private String week_today3,temperatureH3,getTemperatureL3,climate3,wind3;
    private String week_today4,temperatureH4,getTemperatureL4,climate4,wind4;
    private String week_today5,temperatureH5,getTemperatureL5,climate5,wind5;
    private String week_today6,temperatureH6,getTemperatureL6,climate6,wind6;

    public String getWeek_today1() {
        return week_today1;
    }

    public void setWeek_today1(String week_today1) {
        this.week_today1 = week_today1;
    }

    public String getTemperatureH1() {
        return temperatureH1;
    }

    public void setTemperatureH1(String temperatureH1) {
        this.temperatureH1 = temperatureH1;
    }

    public String getGetTemperatureL1() {
        return getTemperatureL1;
    }

    public void setGetTemperatureL1(String getTemperatureL1) {
        this.getTemperatureL1 = getTemperatureL1;
    }

    public String getClimate1() {
        return climate1;
    }

    public void setClimate1(String climate1) {
        this.climate1 = climate1;
    }

    public String getWind1() {
        return wind1;
    }

    public void setWind1(String wind1) {
        this.wind1 = wind1;
    }

    public String getWeek_today2() {
        return week_today2;
    }

    public void setWeek_today2(String week_today2) {
        this.week_today2 = week_today2;
    }

    public String getTemperatureH2() {
        return temperatureH2;
    }

    public void setTemperatureH2(String temperatureH2) {
        this.temperatureH2 = temperatureH2;
    }

    public String getGetTemperatureL2() {
        return getTemperatureL2;
    }

    public void setGetTemperatureL2(String getTemperatureL2) {
        this.getTemperatureL2 = getTemperatureL2;
    }

    public String getClimate2() {
        return climate2;
    }

    public void setClimate2(String climate2) {
        this.climate2 = climate2;
    }

    public String getWind2() {
        return wind2;
    }

    public void setWind2(String wind2) {
        this.wind2 = wind2;
    }

    public String getWeek_today3() {
        return week_today3;
    }

    public void setWeek_today3(String week_today3) {
        this.week_today3 = week_today3;
    }

    public String getTemperatureH3() {
        return temperatureH3;
    }

    public void setTemperatureH3(String temperatureH3) {
        this.temperatureH3 = temperatureH3;
    }

    public String getGetTemperatureL3() {
        return getTemperatureL3;
    }

    public void setGetTemperatureL3(String getTemperatureL3) {
        this.getTemperatureL3 = getTemperatureL3;
    }

    public String getClimate3() {
        return climate3;
    }

    public void setClimate3(String climate3) {
        this.climate3 = climate3;
    }

    public String getWind3() {
        return wind3;
    }

    public void setWind3(String wind3) {
        this.wind3 = wind3;
    }

    public String getWeek_today4() {
        return week_today4;
    }

    public void setWeek_today4(String week_today4) {
        this.week_today4 = week_today4;
    }

    public String getTemperatureH4() {
        return temperatureH4;
    }

    public void setTemperatureH4(String temperatureH4) {
        this.temperatureH4 = temperatureH4;
    }

    public String getGetTemperatureL4() {
        return getTemperatureL4;
    }

    public void setGetTemperatureL4(String getTemperatureL4) {
        this.getTemperatureL4 = getTemperatureL4;
    }

    public String getClimate4() {
        return climate4;
    }

    public void setClimate4(String climate4) {
        this.climate4 = climate4;
    }

    public String getWind4() {
        return wind4;
    }

    public void setWind4(String wind4) {
        this.wind4 = wind4;
    }

    public String getWeek_today5() {
        return week_today5;
    }

    public void setWeek_today5(String week_today5) {
        this.week_today5 = week_today5;
    }

    public String getTemperatureH5() {
        return temperatureH5;
    }

    public void setTemperatureH5(String temperatureH5) {
        this.temperatureH5 = temperatureH5;
    }

    public String getGetTemperatureL5() {
        return getTemperatureL5;
    }

    public void setGetTemperatureL5(String getTemperatureL5) {
        this.getTemperatureL5 = getTemperatureL5;
    }

    public String getClimate5() {
        return climate5;
    }

    public void setClimate5(String climate5) {
        this.climate5 = climate5;
    }

    public String getWind5() {
        return wind5;
    }

    public void setWind5(String wind5) {
        this.wind5 = wind5;
    }

    public String getWeek_today6() {
        return week_today6;
    }

    public void setWeek_today6(String week_today6) {
        this.week_today6 = week_today6;
    }

    public String getTemperatureH6() {
        return temperatureH6;
    }

    public void setTemperatureH6(String temperatureH6) {
        this.temperatureH6 = temperatureH6;
    }

    public String getGetTemperatureL6() {
        return getTemperatureL6;
    }

    public void setGetTemperatureL6(String getTemperatureL6) {
        this.getTemperatureL6 = getTemperatureL6;
    }

    public String getClimate6() {
        return climate6;
    }

    public void setClimate6(String climate6) {
        this.climate6 = climate6;
    }

    public String getWind6() {
        return wind6;
    }

    public void setWind6(String wind6) {
        this.wind6 = wind6;
    }

}
