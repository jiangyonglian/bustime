package jp.co.jyl.bustime.service;

import android.text.format.DateFormat;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;

import jp.co.jyl.bustime.bean.BusDirection;
import jp.co.jyl.bustime.bean.BusDirectionStopInfo;
import jp.co.jyl.bustime.bean.BusTimeInfo;
import jp.co.jyl.bustime.bean.DayType;
import jp.co.jyl.bustime.exception.NoInternetException;
import jp.co.jyl.bustime.exception.ServerBusyException;
import jp.co.jyl.bustime.repository.BusRepository;
import jp.co.jyl.bustime.repository.RepositoryFactory;

/**
 * Created by jiang on 2015/05/01.
 */
public class TokyoBusTimeSearchImpl implements BusTimeSearch {

    private static final String SPACE_STRING = "\u00A0";

    public boolean getBusTime(BusDirectionStopInfo busStopAroundPoint)
            throws ServerBusyException,NoInternetException {

        if(busStopAroundPoint == null){
            return false;
        }

        String url = getURL(busStopAroundPoint);
        try {
            Document doc = Jsoup.connect(url).get();
            //時刻表〔平日〕
            Elements tableList = doc.select("table.timeTableWkd");
            for(Element oneTable:tableList){
                parseTimeTableOfWorkday(oneTable,busStopAroundPoint, DayType.WORK_DAY);
            }
            //時刻表〔土曜〕
            tableList = doc.select("table.timeTableSat");
            for(Element oneTable:tableList){
                parseTimeTableOfWorkday(oneTable,busStopAroundPoint, DayType.SATURDAY);
            }
            //時刻表〔休日〕
            tableList = doc.select("table.timeTableHld");
            for(Element oneTable:tableList){
                parseTimeTableOfWorkday(oneTable, busStopAroundPoint,DayType.HOLIDAY);
            }
        }catch(SocketTimeoutException ex){
            throw new ServerBusyException(ex);
        }catch (IOException ex) {
            throw new NoInternetException(ex);
        }

        if(busStopAroundPoint.getTimeInfoCount() == 0){
            return false;
        }

        //取得した時刻表をDBに保存
        BusRepository repository = RepositoryFactory.i.getBusRepository();
        repository.insertTimeTables(busStopAroundPoint);


        return true;
    }

    private void parseTimeTableOfWorkday(Element table,BusDirectionStopInfo busStopAroundPoint,
          DayType dayType){
        Elements trs = table.select("tr");
        for(Element oneTr: trs){
            Element th = oneTr.child(0);
            if(th == null || !"th".equalsIgnoreCase(th.tagName())){
                continue;
            }
            String scope = th.attr("scope");
            if(scope == null || !"row".equalsIgnoreCase(scope)){
                continue;
            }
            String hourStr = th.text();
            int hour = 0;
            try{
                hour = Integer.parseInt(hourStr);
            }catch(NumberFormatException ex){
                continue;
            }
            Elements tds = oneTr.children();
            for(Element oneTd: tds){
                if(!"td".equalsIgnoreCase(oneTd.tagName())){
                    continue;
                }
                String minutesStr = oneTd.ownText();
                if(SPACE_STRING.equals(minutesStr)){
                    continue;
                }
                int minute = 0;
                try{
                    minute = Integer.parseInt(minutesStr);
                }catch(NumberFormatException ex){
                    continue;
                }
                BusTimeInfo busTimeInfo = new BusTimeInfo();
                busTimeInfo.setDayType(dayType);
                busTimeInfo.setHour(hour);
                busTimeInfo.setMinute(minute);
                busStopAroundPoint.addButTimeInfo(busTimeInfo);
            }
         }
    }

    private String getURL(BusDirectionStopInfo busStopAroundPoint){

        //old:http://tobus.jp/blsys/navi?LCD=&VCD=SelectDest&ECD=SelectDest&slst=2233&pl=1&RTMCD=57
        //new:http://tobus.jp/blsys/navi?LCD=&VCD=cresultttbl&ECD=show&slst=916&pl=1&RTMCD=57&lrid=2&tgo=1
        final String urlTemplate =
                "https://tobus.jp/blsys/navi?LCD=&VCD=cresultttbl&ECD=show&slst={0}&{1}";

        String url = urlTemplate.replace("{0}",String.valueOf(busStopAroundPoint.getBusStopCD()));
        if(busStopAroundPoint.getDirection() == BusDirection.END_START){
            url = url.replace("{1}",busStopAroundPoint.getpToStart());
        }else if(busStopAroundPoint.getDirection() == BusDirection.START_END){
            url = url.replace("{1}",busStopAroundPoint.getpToEnd());
        }else{
            url = url.replace("{1}",busStopAroundPoint.getpTo());
        }

        Log.i(this.getClass().getSimpleName(),"access url:" + url);
        return url;
    }
}
