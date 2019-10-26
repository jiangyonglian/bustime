package jp.co.jyl.bustime.bean;

import jp.co.jyl.bustime.bean.DayType;

/**
 * Created by jiang on 2015/05/01.
 */
public class BusTimeInfo {
    private DayType dayType;
    private int hour;
    private int minute;

    public DayType getDayType() {
        return dayType;
    }

    public void setDayType(DayType dayType) {
        this.dayType = dayType;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}
