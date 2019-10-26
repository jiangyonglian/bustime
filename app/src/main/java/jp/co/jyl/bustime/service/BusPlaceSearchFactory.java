package jp.co.jyl.bustime.service;

/**
 * Created by jiang on 2015/04/30.
 */
public class BusPlaceSearchFactory {

    public static BusPlaceSearchFactory i = new BusPlaceSearchFactory();

    private BusPlaceSearchFactory(){}

    public BusPlaceSearch getBusPlaceSearcher(int companyCD) {
        return new TokyoBusPlaceSearchImpl();
    }

    public BusTimeSearch getBusTimeSearcher(int companyCD){
        return new TokyoBusTimeSearchImpl();
    }

}
