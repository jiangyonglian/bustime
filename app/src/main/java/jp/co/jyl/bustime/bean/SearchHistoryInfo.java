package jp.co.jyl.bustime.bean;

/**
 * Created by jiang on 2015/04/19.
 */
public class SearchHistoryInfo {

    private long id;
    private long referCount = 0;
    private String searchDT = null;

    private int companyCD = 0;

    private int busStopCD = 0;
    private String busStopName = null;

    private int busRouteCD1 = 0;
    private int busRouteCD2 = 0;
    private int busRouteCD3 = 0;

    private String busRouteName1 = null;
    private String busRouteName2 = null;
    private String busRouteName3 = null;

    private String going1 = null;
    private String going2 = null;
    private String going3 = null;

    private int goingCD1 = 0;
    private int goingCD2 = 0;
    private int goingCD3 = 0;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getReferCount() {
        return referCount;
    }

    public void setReferCount(long referCount) {
        this.referCount = referCount;
    }

    public String getSearchDT() {
        return searchDT;
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

    public int getBusRouteCD1() {
        return busRouteCD1;
    }

    public void setBusRouteCD1(int busRouteCD1) {
        this.busRouteCD1 = busRouteCD1;
    }

    public int getBusRouteCD2() {
        return busRouteCD2;
    }

    public void setBusRouteCD2(int busRouteCD2) {
        this.busRouteCD2 = busRouteCD2;
    }

    public int getBusRouteCD3() {
        return busRouteCD3;
    }

    public void setBusRouteCD3(int busRouteCD3) {
        this.busRouteCD3 = busRouteCD3;
    }

    public String getBusRouteName1() {
        return busRouteName1;
    }

    public void setBusRouteName1(String busRouteName1) {
        this.busRouteName1 = busRouteName1;
    }

    public String getBusRouteName2() {
        return busRouteName2;
    }

    public void setBusRouteName2(String busRouteName2) {
        this.busRouteName2 = busRouteName2;
    }

    public String getBusRouteName3() {
        return busRouteName3;
    }

    public void setBusRouteName3(String busRouteName3) {
        this.busRouteName3 = busRouteName3;
    }

    public String getGoing1() {
        return going1;
    }

    public void setGoing1(String going1) {
        this.going1 = going1;
    }

    public String getGoing2() {
        return going2;
    }

    public void setGoing2(String going2) {
        this.going2 = going2;
    }

    public String getGoing3() {
        return going3;
    }

    public void setGoing3(String going3) {
        this.going3 = going3;
    }

    public int getGoingCD1() {
        return goingCD1;
    }

    public void setGoingCD1(int goingCD1) {
        this.goingCD1 = goingCD1;
    }

    public int getGoingCD2() {
        return goingCD2;
    }

    public void setGoingCD2(int goingCD2) {
        this.goingCD2 = goingCD2;
    }

    public int getGoingCD3() {
        return goingCD3;
    }

    public void setGoingCD3(int goingCD3) {
        this.goingCD3 = goingCD3;
    }
}
