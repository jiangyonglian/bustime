package jp.co.jyl.bustime.bean;

/**
 * Created by jiang on 2015/04/30.
 */
public class BusPlaceInfo {
    private int currentPos = 0;
    private String description = null;

    public int getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
