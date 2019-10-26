package jp.co.jyl.bustime.bean;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jiang on 2015/04/19.
 */
public class BusComingInfo {

    private int companyCD = 0;
    private int busRouteCD = 0;
    private int busStopCD = 0;
    private String busRouteName = null;
    private String busStopName = null;
    private String going;
    private int goingCD;

    private int startStopCD = 0; //路線の起点
    private int endStopCD = 0;   //路線の終点

    private boolean fromStartToEnd = true;

    private List<BusStopInfo> busStopInfoList = new ArrayList<BusStopInfo>();

    private List<BusPlaceInfo> busPlaceInfoList = new ArrayList<>();

    public boolean hasBusComing(){
        return busPlaceInfoList.size() > 0;
    }

    public void addBusStopInfo(BusStopInfo busStopInfo){
        busStopInfoList.add(busStopInfo);
    }
    public void addBusStopInfoFromList(List<BusStopInfo> list){
        busStopInfoList.addAll(list);
    }

    public void clearPlaceInfo(){
        busPlaceInfoList.clear();
    }

    public void addBusPlaceInfo(BusPlaceInfo busPlaceInfo){
        busPlaceInfoList.add(busPlaceInfo);
    }

    public List<BusStopInfo> getBusStopInfoList() {
        return busStopInfoList;
    }
    public List<BusPlaceInfo> getBusPlaceInfoList() {
        return busPlaceInfoList;
    }
    public int getBusStopCD() {
        return busStopCD;
    }

    public void setBusStopCD(int busStopCD) {
        this.busStopCD = busStopCD;
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

    public int getGoingCD() {
        return goingCD;
    }

    public void setGoingCD(int goingCD) {
        this.goingCD = goingCD;
    }

    public int getCompanyCD() {
        return companyCD;
    }

    public void setCompanyCD(int companyCD) {
        this.companyCD = companyCD;
    }

    public String getBusRouteName() {
        return busRouteName;
    }

    public void setBusRouteName(String busRouteName) {
        this.busRouteName = busRouteName;
    }

    public int getBusRouteCD() {
        return busRouteCD;
    }

    public void setBusRouteCD(int busRouteCD) {
        this.busRouteCD = busRouteCD;
    }

    public BusDirection getBusDirection(){
        return fromStartToEnd?BusDirection.START_END:BusDirection.END_START;
    }

    public boolean isTerminalStop(){
        return busStopCD == startStopCD || busStopCD == endStopCD;
    }

    public void adjustBusStopByGoing(){

        int size = busStopInfoList.size();
        if(size <= 1){
            return;
        }

        //始点と終点が同じである場合、DBHelperでfromStartToEndの値を設定した
        if(busStopInfoList.get(0).getBusStopCD() !=
                busStopInfoList.get(size -1 ).getBusStopCD()){
            if(startStopCD == goingCD){
                fromStartToEnd = false;
            }else{
                fromStartToEnd = true;
            }
        }

        if(busStopInfoList.get(0).getBusStopCD() == busStopCD){
            //始点であれば、そのまま。
            //始点と終点が同じである場合でもそのまま
            return;
        }

        List<BusStopInfo> resultList = new LinkedList<>();
        if(fromStartToEnd){
            for(int i = 0; i < size;i++){
                resultList.add(busStopInfoList.get(i));
                if(busStopInfoList.get(i).getBusStopCD() == busStopCD){
                    break;
                }
            }

        }else{
            for(int i = size - 1; i >= 0;i--){
                resultList.add(busStopInfoList.get(i));
                if(busStopInfoList.get(i).getBusStopCD() == busStopCD){
                    break;
                }
            }
        }
        busStopInfoList.clear();
        for(int i = resultList.size() - 1; i >= 0;i--){
            busStopInfoList.add(resultList.get(i));
        }

    }

    public int getEndStopCD() {
        return endStopCD;
    }

    public void setEndStopCD(int endStopCD) {
        this.endStopCD = endStopCD;
    }

    public boolean isFromStartToEnd() {
        return fromStartToEnd;
    }

    public void setFromStartToEnd(boolean fromStartToEnd) {
        this.fromStartToEnd = fromStartToEnd;
    }

    public int getStartStopCD() {
        return startStopCD;
    }

    public void setStartStopCD(int startStopCD) {
        this.startStopCD = startStopCD;
    }
}
