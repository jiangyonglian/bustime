package jp.co.jyl.bustime.bean;

/**
 * Created by jiang on 2015/04/19.
 */
public class BusStopInfo {
    private int companyCD = 0;

    private int busStopCD = 0;

    private String busStopName = null;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BusStopInfo that = (BusStopInfo) o;

        if (busStopCD != that.busStopCD) return false;
        if (companyCD != that.companyCD) return false;
        if (busStopName != null ? !busStopName.equals(that.busStopName) : that.busStopName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = companyCD;
        result = 31 * result + busStopCD;
        result = 31 * result + (busStopName != null ? busStopName.hashCode() : 0);
        return result;
    }
}
