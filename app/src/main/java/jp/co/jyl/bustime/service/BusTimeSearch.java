package jp.co.jyl.bustime.service;

import jp.co.jyl.bustime.bean.BusDirectionStopInfo;
import jp.co.jyl.bustime.exception.NoInternetException;
import jp.co.jyl.bustime.exception.ServerBusyException;

/**
 * Created by jiang on 2015/05/01.
 */
public interface BusTimeSearch {
    public boolean getBusTime(BusDirectionStopInfo busStopAroundPoint)
            throws ServerBusyException,NoInternetException;
}
