package jp.co.jyl.bustime.bean;

/**
 * Created by jiang on 2015/05/01.
 */
public enum DayType {
    WORK_DAY(1),
    SATURDAY(2),
    HOLIDAY(3);

    private int value;

    private DayType(int n) {
        this.value = n;
    }

    public int getValue() {
        return this.value;
    }

    public static DayType fromInteger(int x) {
        switch(x) {
            case 1:
                return WORK_DAY;
            case 2:
                return SATURDAY;
            case 3:
                return HOLIDAY;
        }
        return null;
    }
}
