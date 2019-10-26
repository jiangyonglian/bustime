package jp.co.jyl.bustime.service;

import java.util.List;

import jp.co.jyl.bustime.bean.BusComingInfo;
import jp.co.jyl.bustime.exception.NoInternetException;
import jp.co.jyl.bustime.exception.ServerBusyException;

/**
 * Created by jiang on 2015/04/30.
 */
public interface BusPlaceSearch {

    public boolean getBusPlaceInfoData(List<BusComingInfo> busComingInfoList)
            throws ServerBusyException,NoInternetException;
}
