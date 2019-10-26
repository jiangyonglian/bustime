package jp.co.jyl.bustime.service;

import android.util.Log;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import jp.co.jyl.bustime.bean.BusComingInfo;
import jp.co.jyl.bustime.bean.BusPlaceInfo;
import jp.co.jyl.bustime.exception.NoInternetException;
import jp.co.jyl.bustime.exception.ServerBusyException;

/**
 * Created by jiang on 2015/04/30.
 */
public class TokyoBusPlaceSearchImpl implements BusPlaceSearch {
    private static final String SPACE_STRING = "\u00A0";

    public boolean  getBusPlaceInfoData(List<BusComingInfo> busComingInfoList)
            throws ServerBusyException,NoInternetException {
        boolean hasBusComing = false;
        boolean ret = genBusComingInfoData(busComingInfoList);
        if(ret){
            hasBusComing = true;
        }

        //路線始点/終点の場合、系統運行情報を取得する
        for(BusComingInfo busComingInfo:busComingInfoList){
            if(busComingInfo.isTerminalStop()){
                ret = genRouteStatusData(busComingInfo);
                if(ret){
                    hasBusComing = true;
                }
            }
        }
        return hasBusComing;
    }

    //バス接近情報を取得する。
    private  boolean  genBusComingInfoData(List<BusComingInfo> busComingInfoList)
            throws ServerBusyException,NoInternetException{
        if(busComingInfoList == null || busComingInfoList.size() == 0){
            return false;
        }
        int busStopCD = busComingInfoList.get(0).getBusStopCD();
        String url = getBusComingInfoURL(busStopCD);
        boolean hasBusComing = false;
        try {
            Document doc  = Jsoup.connect(url).get();
            Elements ddList = doc.select("dd.stopName");
            for (Element oneDD : ddList) {
                String goingText = oneDD.text();
                int pos = goingText.indexOf(SPACE_STRING);
                if(pos < 0){
                    continue;
                }
                String going = goingText.substring(0,pos);
                //find route name
                String routeName = null;
                Element sibling = oneDD.firstElementSibling();
                while(sibling != null){
                    if("dt".equalsIgnoreCase(sibling.tagName())){
                        routeName = sibling.text();
                        break;
                    }
                    sibling = sibling.nextElementSibling();
                }
                if(routeName != null){
                    BusComingInfo busComingInfo = findBusComingInfo(busComingInfoList,
                            routeName,going);
                    if(busComingInfo == null){
                        continue;
                    }
                    sibling = oneDD.parent().nextElementSibling();
                    Element appListTbl = null;
                    while(sibling != null){
                        if("table".equalsIgnoreCase(sibling.tagName())){
                            if("車両接近情報詳細".equals(sibling.attr("summary"))){
                                appListTbl = sibling;
                                break;
                            }

                        }
                        sibling = sibling.nextElementSibling();
                    }
                    if(appListTbl == null){
                        continue;
                    }

                    boolean ret = parseComingInfo(appListTbl,busComingInfo);
                    if(ret){
                        hasBusComing = true;
                    }
                }
            }
        } catch(SocketTimeoutException ex){
            throw new ServerBusyException(ex);
        } catch(IOException ex){
            throw new NoInternetException(ex);
        }
        return hasBusComing;
    }

    private boolean parseComingInfo(Element appListTbl,BusComingInfo busComingInfo){
        busComingInfo.clearPlaceInfo();
        Elements tdLIst = appListTbl.select("td.busLabel");
        int cnt = 0;
        boolean hasBusComing = false;
        for(Element oneTd : tdLIst){
            String text = oneTd.text();
            if(!SPACE_STRING.equals(text)){
                BusPlaceInfo busPlaceInfo = new BusPlaceInfo();
                busPlaceInfo.setCurrentPos(cnt);
                busPlaceInfo.setDescription(text);
                busComingInfo.addBusPlaceInfo(busPlaceInfo);
                hasBusComing = true;
            }
            cnt++;
        }

        return hasBusComing;
    }

    //バス系統運行情報を取得
    private boolean genRouteStatusData(BusComingInfo busComingInfo)
            throws ServerBusyException,NoInternetException{

        String url = getBusRouteStatusURL(busComingInfo.getBusRouteCD());
        boolean hasBusComing = false;
        try {
            Document doc = Jsoup.connect(url).get();
            Elements tableList = doc.select("table.routeListTbl01");
            for (Element oneTable : tableList) {
                Elements tdLIst = oneTable.select("td.tdBalloonL");
                int cnt = 0;
                for(Element oneTd : tdLIst){
                    Elements carLIst = oneTd.select("p.carlabel");
                    if(carLIst != null && carLIst.size() > 0){
                        String text = carLIst.get(0).text();
                        BusPlaceInfo busPlaceInfo = new BusPlaceInfo();
                        //tdBalloonLのあるセルがBusStopと間隔に両方あるため
                        busPlaceInfo.setCurrentPos(cnt / 2);
                        busPlaceInfo.setDescription(text);
                        busComingInfo.addBusPlaceInfo(busPlaceInfo);
                        hasBusComing = true;
                    }
                    cnt++;
                }
            }
        }catch(SocketTimeoutException ex){
            throw new ServerBusyException(ex);
        } catch(IOException ex){
            throw new NoInternetException(ex);
        }
        return hasBusComing;
    }



    private BusComingInfo findBusComingInfo(List<BusComingInfo> busComingInfoList,
         String routeName,String going){
        for(BusComingInfo one:busComingInfoList){
            if(one.getBusRouteName().equals(routeName) &&
                    one.getGoing().equals(going)){
                return one;
            }
        }
        return null;
    }

    private String getBusComingInfoURL(int busStopCD){
        final String urlTemplate =
                "http://tobus.jp/blsys/navi?LCD=&VCD=cresultrsi&ECD=aprslt&slst={0}";
        String url = urlTemplate.replace("{0}",String.valueOf(busStopCD));
        Log.i(this.getClass().getSimpleName(), "access url:" + url);
        return url;
    }

    private String getBusRouteStatusURL(int busRouteCD){
        final String urlTemplate =
                "http://tobus.jp/blsys/navi?VCD=cresultapr&ECD=rsirslt&LCD=&RTMCD={0}";
        String url = urlTemplate.replace("{0}",String.valueOf(busRouteCD));
        Log.i(this.getClass().getSimpleName(),"access url:" + url);
        return url;
    }
}
