package jp.co.jyl.bustime.bean;

/**
 * Created by jiang on 2015/04/19.
 */
public class BusRouteInfo {
    private int companyCD = 0;

    private int busRouteCD = 0;

    private String busRouteName = null;

    private int startStopCD = 0;

    private int endStopCD = 0;

    private String startStopName = null;
    private String endStopName = null;


    public String getStartStopName() {
        return startStopName;
    }

    public void setStartStopName(String startStopName) {
        this.startStopName = startStopName;
    }

    public String getEndStopName() {
        return endStopName;
    }

    public void setEndStopName(String endStopName) {
        this.endStopName = endStopName;
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

    public String getBusRouteName() {
        return busRouteName;
    }

    public void setBusRouteName(String busRouteName) {
        this.busRouteName = busRouteName;
    }

    public int getStartStopCD() {
        return startStopCD;
    }

    public void setStartStopCD(int startStopCD) {
        this.startStopCD = startStopCD;
    }

    public int getEndStopCD() {
        return endStopCD;
    }

    public void setEndStopCD(int endStopCD) {
        this.endStopCD = endStopCD;
    }
}
