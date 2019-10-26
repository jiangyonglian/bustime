package jp.co.jyl.bustime.bean;

/**
 * Created by jiang on 2015/05/01.
 */
public enum BusDirection {
    UNKNOWN(0),
    START_END(1),
    END_START(2);

    private int value;

    private BusDirection(int n) {
        this.value = n;
    }

    public int getValue() {
        return this.value;
    }
}
