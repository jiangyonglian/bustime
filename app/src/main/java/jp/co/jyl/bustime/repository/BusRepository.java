package jp.co.jyl.bustime.repository;

import java.util.List;

import jp.co.jyl.bustime.bean.BusComingInfo;
import jp.co.jyl.bustime.bean.BusDirection;
import jp.co.jyl.bustime.bean.BusDirectionStopInfo;
import jp.co.jyl.bustime.bean.BusStopInfo;
import jp.co.jyl.bustime.bean.SearchHistoryInfo;
import jp.co.jyl.bustime.bean.TimeTableHistoryInfo;
import jp.co.jyl.bustime.exception.TooManyResultException;

/**
 * Created by jiang on 2015/04/19.
 */
public interface BusRepository {

    List<BusComingInfo> getBusComingInfoList(long searchHisID);

    List<SearchHistoryInfo> getSearchHistoryList();

    List<TimeTableHistoryInfo> getTimeTableHistoryList();
    void deleteTimeTableHistory(TimeTableHistoryInfo timeTableHistoryInfo);

    List<BusDirectionStopInfo> searchBusStop(String searchName) throws TooManyResultException;

    List<BusStopInfo> getBusStopsInRoute(int companyCD,int busRouteCD,BusDirection direction);

    void insertSearchHistory(SearchHistoryInfo searchHisInfo);

    void insertOrUpdateTimeTableHistory(TimeTableHistoryInfo timeTableHistoryInfo);

    void addRefCountOfSearchHistory(long id,long refCount);

    void deleteSearchHistoryById(long id);

    int getCountOfHistory();

    void insertTimeTables(BusDirectionStopInfo busDirectionStoInfo);

    int getTimeTables(BusDirectionStopInfo busDirectionStop);

    void deleteTimeTables(int saveDays);
}
