package jp.co.jyl.bustime.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiang on 2015/04/19.
 */
public class BusDirectionStopInfo implements Serializable{

    private int companyCD = 0;
    private int busRouteCD = 0;
    private int busStopCD = 0;
    private BusDirection direction = BusDirection.UNKNOWN;
    private String busRouteName = null;
    private String busStopName = null;
    private String going;
    private int goingCD = 0;
    //時刻表を取得するためのp値
    private String pToStart = null;
    private String pToEnd = null;
    //directionが分からない時に使う
    private String pTo = null;

    public static BusDirectionStopInfo fromTimeTableHistory(TimeTableHistoryInfo tthInfo){
        BusDirectionStopInfo me = new BusDirectionStopInfo();
        me.companyCD = tthInfo.getCompanyCD();
        me.busRouteCD = tthInfo.getBusRouteCD();
        me.busStopCD = tthInfo.getBusStopCD();
        me.busRouteName = tthInfo.getBusRouteName();
        me.busStopName = tthInfo.getBusStopName();
        me.goingCD = tthInfo.getGoingCD();
        me.going = tthInfo.getGoing();
        me.pTo = tthInfo.getPto();
        return me;
    }

    //時刻表の描画に使う、使うときのみ設定される
    private List<BusTimeInfo> timeInfoList = new ArrayList<>();


    public String getpTo() {
        return pTo;
    }

    public void setpTo(String pTo) {
        this.pTo = pTo;
    }
    public String getpToStart() {
        return pToStart;
    }

    public void setpToStart(String pToStart) {
        this.pToStart = pToStart;
    }

    public String getpToEnd() {
        return pToEnd;
    }


    public void setpToEnd(String pToEnd) {
        this.pToEnd = pToEnd;
    }

    public void clearTimeInfo(){
        timeInfoList.clear();
    }

    public void addButTimeInfo(BusTimeInfo busTimeInfo){
        timeInfoList.add(busTimeInfo);
    }
    public List<BusTimeInfo> getTimeInfoList() {
        return timeInfoList;
    }
    public int getTimeInfoCount(){
        return timeInfoList.size();
    }
    public int getGoingCD() {
        return goingCD;
    }

    public void setGoingCD(int goingCD) {
        this.goingCD = goingCD;
    }
    public String getBusRouteName() {
        return busRouteName;
    }

    public void setBusRouteName(String busRouteName) {
        this.busRouteName = busRouteName;
    }

    public String getBusStopName() {
        return busStopName;
    }

    public void setBusStopName(String busStopName) {
        this.busStopName = busStopName;
    }

    public String getGoing() {
        return going;
    }

    public void setGoing(String going) {
        this.going = going;
    }

    public BusDirection getDirection() {
        return direction;
    }

    public void setDirection(BusDirection direction) {
        this.direction = direction;
    }

    public int getCompanyCD() {
        return companyCD;
    }

    public void setCompanyCD(int companyCD) {
        this.companyCD = companyCD;
    }

    public int getBusRouteCD() {
        return busRouteCD;
    }

    public void setBusRouteCD(int busRouteCD) {
        this.busRouteCD = busRouteCD;
    }

    public int getBusStopCD() {
        return busStopCD;
    }

    public void setBusStopCD(int busStopCD) {
        this.busStopCD = busStopCD;
    }
}
