package jp.co.jyl.bustime.bean;

/**
 * Created by jiang on 2015/04/19.
 */
public class BusRouteStopInfo {

    private int companyCD = 0;

    private int busRouteCD = 0;

    private int busStopCD = 0;

    private int seq = 0;
    //時刻表を取得するためのp値
    private String pToStart = null;
    private String pToEnd = null;

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

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
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
