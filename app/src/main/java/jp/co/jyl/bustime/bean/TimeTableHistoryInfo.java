package jp.co.jyl.bustime.bean;

/**
 * Created by jiang on 2015/04/19.
 */
public class TimeTableHistoryInfo {
    private String searchDT = null;
    private int companyCD = 0;

    private int busStopCD = 0;
    private String busStopName = null;

    private int busRouteCD = 0;
    private String busRouteName = null;
    private String going = null;
    private int goingCD = 0;

    private String pto = null;//時刻表を取得するURLにあるpto文字列

    public String getSearchDT() {
        return searchDT;
    }

    public String getPto() {
        return pto;
    }

    public void setPto(String pto) {
        this.pto = pto;
    }

    public void setSearchDT(String searchDT) {
        this.searchDT = searchDT;
    }

    public int getCompanyCD() {
        return companyCD;
    }

    public void setCompanyCD(int companyCD) {
        this.companyCD = companyCD;
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

    public int getBusRouteCD() {
        return busRouteCD;
    }

    public void setBusRouteCD(int busRouteCD) {
        this.busRouteCD = busRouteCD;
    }

    public String getBusRouteName() {
        return busRouteName;
    }

    public void setBusRouteName(String busRouteName) {
        this.busRouteName = busRouteName;
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
}
