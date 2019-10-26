package jp.co.jyl.bustime.repository;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

import jp.co.jyl.bustime.R;
import jp.co.jyl.bustime.bean.BusComingInfo;
import jp.co.jyl.bustime.bean.BusDirection;
import jp.co.jyl.bustime.bean.BusDirectionStopInfo;
import jp.co.jyl.bustime.bean.BusRouteInfo;
import jp.co.jyl.bustime.bean.BusRouteStopInfo;
import jp.co.jyl.bustime.bean.BusStopInfo;
import jp.co.jyl.bustime.bean.BusTimeInfo;
import jp.co.jyl.bustime.bean.DayType;
import jp.co.jyl.bustime.bean.SearchHistoryInfo;
import jp.co.jyl.bustime.bean.TimeTableHistoryInfo;
import jp.co.jyl.bustime.exception.TooManyResultException;

/**
 * Created by jiang on 2015/04/19.
 */
public class DBHelper extends SQLiteOpenHelper implements BusRepository {

    /**
     * バージョン番号：
     *  ver2  時刻表検索履歴テーブルを追加
     *  ver3  路線データ更新(2018/03/03)
     *  ver4  路線データ更新(2019/10/12)
     */
    private static final int DATABASE_VERSION = 4;

    /**
     * 検索結果最大件数
     */
    private static final int MAX_RESULT_CNT = 100;

    /**
     * データベース名
     */
    public static final String DATABASE_NAME = "jyl_bustime_db";

    /**
     * シングルトン
     */
    private static DBHelper instance = null ;

    /**
     * バス停テーブル名
     */
    private static final String TABLE_BUS_STOP_NAME = "bus_stop";

    /**
     * バス停テーブル生成SQL
     */
    private static final String BUS_STOP_TABLE_CREATE = "CREATE TABLE "
            + TABLE_BUS_STOP_NAME + " (" + "company_cd" + " INTEGER, "
            + "bus_stop_cd" + " INTEGER, " + "bus_stop_name" + " TEXT, "
            + "PRIMARY KEY(company_cd,bus_stop_cd));";

    /**
     * バス系統テーブル名
     */
    private static final String TABLE_BUS_ROUTE_NAME = "bus_route";

    /**
     * バス系統テーブル生成SQL
     */
    private static final String BUS_ROUTE_TABLE_CREATE = "CREATE TABLE "
            + TABLE_BUS_ROUTE_NAME + " (" + "company_cd" + " INTEGER, "
            + "bus_route_cd" + " INTEGER, " + "bus_route_name" + " TEXT, "
            + "start_stop_cd" + " INTEGER, " + "end_stop_cd" + " INTEGER, "
            + "start_stop_name" + " TEXT, " + "end_stop_name" + " TEXT, "
            + "PRIMARY KEY(company_cd,bus_route_cd));";

    /**
     * バス系統_バス停_テーブル名
     */
    private static final String TABLE_BUS_ROUTE_STOP_NAME = "bus_route_stop";

    /**
     * バス停_バス系統テーブル生成SQL
     */
    private static final String BUS_ROUTE_STOP_TABLE_CREATE = "CREATE TABLE "
            + TABLE_BUS_ROUTE_STOP_NAME + " (" + "company_cd" + " INTEGER, "
            + "bus_route_cd" + " INTEGER, " + "bus_stop_cd" + " INTEGER, "
            + "seq" + " INTEGER, " + "pto_start" + " TEXT, " + "pto_end" + " TEXT, "
            + "PRIMARY KEY(company_cd,bus_route_cd,seq));";

    /**
     * 検索履歴_テーブル名
     */
    private static final String TABLE_SEARCH_HISTORY_NAME = "search_his";

    /**
     * 検索履歴テーブル生成SQL
     */
    private static final String SEARCH_HISTORY_TABLE_CREATE = "CREATE TABLE "
            + TABLE_SEARCH_HISTORY_NAME + " (" + "id" + " INTEGER, "
            + "company_cd" + " INTEGER, " + "bus_stop_cd" + " INTEGER, "
            + "search_dt" + " TEXT, "  + "refer_count" + " INTEGER, "
            + "bus_route_cd1" + " INTEGER, "  + "bus_route_cd2" + " INTEGER, "
            + "bus_route_cd3" + " INTEGER, "  + "bus_stop_name" + " TEXT, "
            + "bus_route_name1" + " TEXT, " + "bus_route_name2" + " TEXT, "
            + "bus_route_name3" + " TEXT, " + "going1" + " TEXT, "
            + "going2" + " TEXT, "  + "going3" + " TEXT, " + "goingCD1" + " INTEGER, "
            + "goingCD2" + " INTEGER, " + "goingCD3" + " INTEGER, "
            + "PRIMARY KEY(id));";

    /**
     * 時刻表テーブル名
     */
    private static final String TABLE_TIME_TABLE_NAME = "time_table";

    /**
     * 時刻表テーブル生成SQL
     */
    private static final String TIME_TABLE_CREATE = "CREATE TABLE "
            + TABLE_TIME_TABLE_NAME + " (" + "company_cd" + " INTEGER, "
            + "bus_route_cd" + " INTEGER, "
            + "bus_stop_cd" + " INTEGER, " + "going_cd" + " INTEGER, "
            + "day_kind" + " INTEGER, " + "hour" + " INTEGER, "
            + "minute" + " INTEGER, " + "register_dt" + " TEXT); ";

    /**
     * 時刻表履歴_テーブル名
     */
    private static final String TABLE_TIMETABLE_HISTORY_NAME = "timetable_his";

    /**
     * 時刻表履歴テーブル生成SQL
     */
    private static final String TIMETABLE_HISTORY_TABLE_CREATE = "CREATE TABLE "
            + TABLE_TIMETABLE_HISTORY_NAME + " ( "
            + "company_cd" + " INTEGER, " + "bus_stop_cd" + " INTEGER, "
            + "search_dt" + " TEXT, "  + "refer_count" + " INTEGER, "
            + "bus_route_cd" + " INTEGER, " + "bus_stop_name" + " TEXT, "
            + "bus_route_name" + " TEXT, "  + "going" + " TEXT, "
            + "goingCD" + " INTEGER, " + "pto" + " TEXT, "
            + "PRIMARY KEY(company_cd,bus_stop_cd,bus_route_cd,goingCD));";

    private Resources resources = null;


    /**
     * コンストラクタ
     * @param context　APコンテキスト
     */
    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        resources = context.getResources();
    }

    public synchronized static void initializeDB(Context context){
        if(instance == null){
            instance = new DBHelper(context);
        }
    }

    /**
     * DBヘルパーインスタンスの取得
     * @return　DBヘルパーインスタンス
     */
    public synchronized static DBHelper getInstance(){
        if(instance == null){
            throw new IllegalStateException("DB is not initialized.");
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //バス停テーブルの生成
        db.execSQL(BUS_STOP_TABLE_CREATE);

        //バス系統テーブルの生成
        db.execSQL(BUS_ROUTE_TABLE_CREATE);

        //バス停_系統テーブルの生成
        db.execSQL(BUS_ROUTE_STOP_TABLE_CREATE);

        //検索履歴テーブルの生成
        db.execSQL(SEARCH_HISTORY_TABLE_CREATE);


        //時刻表テーブルの生成
        db.execSQL(TIME_TABLE_CREATE);

        //時刻表検索履歴テーブルの生成
        db.execSQL(TIMETABLE_HISTORY_TABLE_CREATE);

        //テーブルデータの読み込み
        initTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion >= 2 && oldVersion == 1){
            //時刻表検索履歴テーブルの生成
            db.execSQL(TIMETABLE_HISTORY_TABLE_CREATE);
        }

        // 既存データを削除
        deleteOldData(db);
        //テーブルデータの読み込み
         initTables(db);

    }

    /**
     * 路線データ更新前に、既存データを削除
     * @param db
     */
    private void deleteOldData(SQLiteDatabase db){
        db.execSQL("DELETE FROM " + TABLE_BUS_STOP_NAME);
        db.execSQL("DELETE FROM " + TABLE_BUS_ROUTE_NAME);
        db.execSQL("DELETE FROM " + TABLE_BUS_ROUTE_STOP_NAME);

//        db.execSQL("DELETE FROM " + TABLE_SEARCH_HISTORY_NAME);
//        db.execSQL("DELETE FROM " + TABLE_TIME_TABLE_NAME);
//        db.execSQL("DELETE FROM " + TABLE_TIMETABLE_HISTORY_NAME);


    }

    /**
     * raw/bus_route.csvからデータを読み込み、DBにInsertする
     * @param db
     */
    private void initTables(SQLiteDatabase db){

        InputStream csvStream = resources.openRawResource(R.raw.bus_route);
        Map<BusStopInfo,Boolean> busStopMap = new HashMap<BusStopInfo,Boolean>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    csvStream, "UTF-8"));
            String line = null;
            while ((line = reader.readLine()) != null) {
                String temp = line.trim();
               if(temp.length() == 0){
                   continue;
               }
               Scanner s = new Scanner(temp).useDelimiter(",");
               BusRouteInfo busRouteInfo = new BusRouteInfo();
               //会社コード
               int companyCD = s.nextInt();
               busRouteInfo.setCompanyCD(companyCD);
               //路線コード
               int busRouteCD = s.nextInt();
               Log.d("DBHelper","read route :" + busRouteCD);
               busRouteInfo.setBusRouteCD(busRouteCD);
               //路線名
               String busRouteName = s.next().trim();
               busRouteInfo.setBusRouteName(busRouteName);

               List<BusStopInfo> busStopList = getBusStops(companyCD,s);
               int busStopCount = busStopList.size();
               busRouteInfo.setStartStopCD(busStopList.get(0).getBusStopCD());
               busRouteInfo.setStartStopName(busStopList.get(0).getBusStopName());
               busRouteInfo.setEndStopCD(busStopList.get(busStopCount - 1).getBusStopCD());
               busRouteInfo.setEndStopName(busStopList.get(busStopCount - 1).getBusStopName());

               //DBにデータを挿入する
               insertBusRoute(busRouteInfo, db);
               int seq = 0;
               for(BusStopInfo oneBusStop:busStopList){
                   if(busStopMap.get(oneBusStop) == null){
                       busStopMap.put(oneBusStop,Boolean.TRUE);
                       insertBusStop(oneBusStop,db);
                   }

                   BusRouteStopInfo routeStop = new BusRouteStopInfo();
                   routeStop.setCompanyCD(companyCD);
                   routeStop.setBusRouteCD(busRouteCD);
                   routeStop.setBusStopCD(oneBusStop.getBusStopCD());
                   routeStop.setpToStart(oneBusStop.getpToStart());
                   routeStop.setpToEnd(oneBusStop.getpToEnd());
                   routeStop.setSeq(seq++);
                   insertBusRouteStop(routeStop,db);
               }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read csv file",e);
        } finally{
            try {
                csvStream.close();
            } catch (IOException e) {
                throw new RuntimeException("Failed to close csv file",e);
            }
        }
    }

    private List<BusStopInfo> getBusStops(int companyCD,Scanner s){
        List<BusStopInfo> list = new ArrayList<BusStopInfo>();
        while(s.hasNext()){
            BusStopInfo busStopInfo = new BusStopInfo();
            busStopInfo.setCompanyCD(companyCD);
            busStopInfo.setBusStopCD(s.nextInt());
            busStopInfo.setBusStopName(s.next().trim());
            busStopInfo.setpToStart(s.next().trim());
            busStopInfo.setpToEnd(s.next().trim());
            list.add(busStopInfo);
        }
        return list;
    }


    public void closeDb(){
        //according to javadoc,getWritableDatabase() will return the same object
        //returned by getReadableDatabase()
        this.getWritableDatabase().close();
    }

    public void insertBusStop(BusStopInfo busStopInfo){

        if(busStopInfo == null){
            throw new IllegalArgumentException("busStopInfo is null");
        }
        // Opens the database object in "write" mode.
        SQLiteDatabase sdb = this.getWritableDatabase();
        insertBusStop(busStopInfo,sdb);
    }

    private void insertBusStop(BusStopInfo busStopInfo,SQLiteDatabase sdb){

        ContentValues values = new ContentValues();
        values.put("company_cd", busStopInfo.getCompanyCD());
        values.put("bus_stop_cd",busStopInfo.getBusStopCD());
        values.put("bus_stop_name", busStopInfo.getBusStopName());
        sdb.insert(TABLE_BUS_STOP_NAME, null, values);
    }

    public void insertBusRoute(BusRouteInfo busRouteInfo){

        if(busRouteInfo == null){
            throw new IllegalArgumentException("busRouteInfo is null");
        }
        // Opens the database object in "write" mode.
        SQLiteDatabase sdb = this.getWritableDatabase();
        insertBusRoute(busRouteInfo, sdb);
    }

    private void insertBusRoute(BusRouteInfo busRouteInfo,SQLiteDatabase sdb){

        ContentValues values = new ContentValues();
        values.put("company_cd", busRouteInfo.getCompanyCD());
        values.put("bus_route_cd",busRouteInfo.getBusRouteCD());
        values.put("bus_route_name", busRouteInfo.getBusRouteName());
        values.put("start_stop_cd",busRouteInfo.getStartStopCD());
        values.put("end_stop_cd",busRouteInfo.getEndStopCD());
        values.put("start_stop_name",busRouteInfo.getStartStopName());
        values.put("end_stop_name",busRouteInfo.getEndStopName());
        sdb.insert(TABLE_BUS_ROUTE_NAME, null, values);
    }

    public void insertBusRouteStop(BusRouteStopInfo busRouteStopInfo){

        if(busRouteStopInfo == null){
            throw new IllegalArgumentException("busRouteStopInfo is null");
        }
        // Opens the database object in "write" mode.
        SQLiteDatabase sdb = this.getWritableDatabase();
        insertBusRouteStop(busRouteStopInfo, sdb);
    }

    private void insertBusRouteStop(BusRouteStopInfo busRouteStopInfo,SQLiteDatabase sdb){

        ContentValues values = new ContentValues();
        values.put("company_cd", busRouteStopInfo.getCompanyCD());
        values.put("bus_route_cd",busRouteStopInfo.getBusRouteCD());
        values.put("bus_stop_cd",busRouteStopInfo.getBusStopCD());
        values.put("seq",busRouteStopInfo.getSeq());
        values.put("pto_start",busRouteStopInfo.getpToStart());
        values.put("pto_end",busRouteStopInfo.getpToEnd());
        sdb.insert(TABLE_BUS_ROUTE_STOP_NAME, null, values);
    }

    public void deleteTimeTables(int saveDays){
        Calendar ca = Calendar.getInstance();
        ca.setTime(new Date());
        ca.add(Calendar.DAY_OF_MONTH,0 - saveDays);
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");
        String register_dt = sd.format(ca.getTime());
        SQLiteDatabase sdb = this.getWritableDatabase();
        sdb.delete(TABLE_TIME_TABLE_NAME,"register_dt < ?",new String[]{register_dt});
    }

    public void insertTimeTables(BusDirectionStopInfo busDirectionStoInfo){
        if(busDirectionStoInfo == null){
            throw new IllegalArgumentException("busDirectionStoInfo is null");
        }
        SQLiteDatabase sdb = this.getWritableDatabase();
        List<BusTimeInfo> timeInfoList = busDirectionStoInfo.getTimeInfoList();
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");
        String register_dt = sd.format(new Date());
        for(BusTimeInfo busTimeInfo : timeInfoList){
            ContentValues values = new ContentValues();
            values.put("company_cd", busDirectionStoInfo.getCompanyCD());
            values.put("bus_route_cd",busDirectionStoInfo.getBusRouteCD());
            values.put("bus_stop_cd",busDirectionStoInfo.getBusStopCD());
            values.put("going_cd",busDirectionStoInfo.getGoingCD());
            values.put("day_kind",busTimeInfo.getDayType().getValue());
            values.put("hour",busTimeInfo.getHour());
            values.put("minute",busTimeInfo.getMinute());
            values.put("register_dt",register_dt);
            sdb.insert(TABLE_TIME_TABLE_NAME, null, values);
        }
    }

    public void insertOrUpdateTimeTableHistory(TimeTableHistoryInfo timeTableHistoryInfo){
        if(timeTableHistoryInfo == null){
            throw new IllegalArgumentException("timeTableHistoryInfo is null");
        }
        // Opens the database object in "write" mode.
        SQLiteDatabase sdb = this.getWritableDatabase();
        long refCount = searchTimetableHistory(timeTableHistoryInfo,sdb);
        if(refCount > 0){
            addRefCountOfTimeTableHistory(refCount,timeTableHistoryInfo,sdb);
        }else{
            insertTimeTableHistory(timeTableHistoryInfo,sdb);
        }
      }

    private void addRefCountOfTimeTableHistory(long refCount,
        TimeTableHistoryInfo timeTableHistoryInfo,SQLiteDatabase sdb){

        ContentValues values = new ContentValues();
        values.put("refer_count", refCount + 1);

        sdb.update(TABLE_TIMETABLE_HISTORY_NAME, values,"company_cd=? and bus_stop_cd = ?" +
                " and bus_route_cd = ? and goingCD = ?",
                new String[]{String.valueOf(timeTableHistoryInfo.getCompanyCD()),
                        String.valueOf(timeTableHistoryInfo.getBusStopCD()),
                        String.valueOf(timeTableHistoryInfo.getBusRouteCD()),
                        String.valueOf(timeTableHistoryInfo.getGoingCD())});
    }

    private void insertTimeTableHistory(TimeTableHistoryInfo timeTableHistoryInfo,SQLiteDatabase sdb){

        ContentValues values = new ContentValues();

        values.put("search_dt", timeTableHistoryInfo.getSearchDT());
        values.put("refer_count", 1);

        values.put("company_cd", timeTableHistoryInfo.getCompanyCD());
        values.put("bus_stop_cd",timeTableHistoryInfo.getBusStopCD());
        values.put("bus_stop_name",timeTableHistoryInfo.getBusStopName());

        values.put("bus_route_cd",timeTableHistoryInfo.getBusRouteCD());
        values.put("bus_route_name",timeTableHistoryInfo.getBusRouteName());
        values.put("going",timeTableHistoryInfo.getGoing());
        values.put("goingCD",timeTableHistoryInfo.getGoingCD());
        values.put("pto",timeTableHistoryInfo.getPto());

        sdb.insert(TABLE_TIMETABLE_HISTORY_NAME, null, values);
    }

    //戻り値は、refer_count。該当ない場合、0を返す
    private long searchTimetableHistory(TimeTableHistoryInfo timeTableHistoryInfo,
                                           SQLiteDatabase sdb) {

        String sqlSql = "SELECT refer_count " +
                " from " + TABLE_TIMETABLE_HISTORY_NAME +
                " WHERE company_cd = " + timeTableHistoryInfo.getCompanyCD() +
                " and bus_stop_cd=" + timeTableHistoryInfo.getBusStopCD() +
                " and bus_route_cd=" + timeTableHistoryInfo.getBusRouteCD() +
                " and goingCD=" + timeTableHistoryInfo.getGoingCD();

        Cursor c = sdb.rawQuery(sqlSql,new String[]{});

        long refCount = 0;
        try{
            if(c.moveToFirst()) {
                refCount  =  c.getLong(c.getColumnIndex("refer_count"));
                return refCount;
            }else{
                return refCount;
            }
        }finally {
            c.close();
        }
     }

    public void deleteTimeTableHistory(TimeTableHistoryInfo timeTableHistoryInfo){
        SQLiteDatabase sdb = this.getWritableDatabase();
        sdb.delete(TABLE_TIMETABLE_HISTORY_NAME, "company_cd = ? and bus_stop_cd = ? " +
                " and bus_route_cd= ? and goingCD= ?",
                new String[]{String.valueOf(timeTableHistoryInfo.getCompanyCD()),
                        String.valueOf(timeTableHistoryInfo.getBusStopCD()),
                        String.valueOf(timeTableHistoryInfo.getBusRouteCD()),
                        String.valueOf(timeTableHistoryInfo.getGoingCD()),
                });
    }


    public void insertSearchHistory(SearchHistoryInfo searchHisInfo){

        if(searchHisInfo == null){
            throw new IllegalArgumentException("searchHisInfo is null");
        }
        // Opens the database object in "write" mode.
        SQLiteDatabase sdb = this.getWritableDatabase();
        insertSearchHistory(searchHisInfo, sdb);
    }

    private void insertSearchHistory(SearchHistoryInfo searchHisInfo,SQLiteDatabase sdb){

        ContentValues values = new ContentValues();
        values.put("id", searchHisInfo.getId());
        values.put("search_dt", searchHisInfo.getSearchDT());
        values.put("refer_count", searchHisInfo.getReferCount());

        values.put("company_cd", searchHisInfo.getCompanyCD());
        values.put("bus_stop_cd",searchHisInfo.getBusStopCD());
        values.put("bus_stop_name",searchHisInfo.getBusStopName());

        values.put("bus_route_cd1",searchHisInfo.getBusRouteCD1());
        values.put("bus_route_cd2",searchHisInfo.getBusRouteCD2());
        values.put("bus_route_cd3",searchHisInfo.getBusRouteCD3());
        values.put("bus_route_name1",searchHisInfo.getBusRouteName1());
        values.put("bus_route_name2",searchHisInfo.getBusRouteName2());
        values.put("bus_route_name3",searchHisInfo.getBusRouteName3());

        values.put("going1",searchHisInfo.getGoing1());
        values.put("going2",searchHisInfo.getGoing2());
        values.put("going3",searchHisInfo.getGoing3());

        values.put("goingCD1",searchHisInfo.getGoingCD1());
        values.put("goingCD2",searchHisInfo.getGoingCD2());
        values.put("goingCD3",searchHisInfo.getGoingCD3());

        sdb.insert(TABLE_SEARCH_HISTORY_NAME, null, values);
    }

    public List<TimeTableHistoryInfo> getTimeTableHistoryList(){
        List<TimeTableHistoryInfo> list = new ArrayList<>();
        // Open the database object in "write" mode, since  writes  may be done
        SQLiteDatabase sdb = this.getWritableDatabase();

        final String[] columns = new String[]{"company_cd","bus_stop_cd","bus_stop_name",
                "search_dt","bus_route_cd", "bus_route_name","going","goingCD","pto"};

        Cursor c = sdb.query(TABLE_TIMETABLE_HISTORY_NAME, columns, null, null,
                null, null, "refer_count desc,search_dt desc");
        try {
            if (c.moveToFirst()) {
                while(true){
                    TimeTableHistoryInfo historyInfo = new TimeTableHistoryInfo();
                    list.add(historyInfo);
                    String searchDT = c.getString(c.getColumnIndex("search_dt"));
                    int companyCD = c.getInt(c.getColumnIndex("company_cd"));
                    int busTopCD = c.getInt(c.getColumnIndex("bus_stop_cd"));
                    String busStopName = c.getString(c.getColumnIndex("bus_stop_name"));
                    int busRouteCD = c.getInt(c.getColumnIndex("bus_route_cd"));
                    String busRouteName = c.getString(c.getColumnIndex("bus_route_name"));
                    int goingCD = c.getInt(c.getColumnIndex("goingCD"));
                    String going = c.getString(c.getColumnIndex("going"));
                    String pto = c.getString(c.getColumnIndex("pto"));

                    historyInfo.setSearchDT(searchDT);
                    historyInfo.setCompanyCD(companyCD);
                    historyInfo.setBusStopCD(busTopCD);
                    historyInfo.setBusStopName(busStopName);
                    historyInfo.setBusRouteCD(busRouteCD);
                    historyInfo.setBusRouteName(busRouteName);
                    historyInfo.setGoing(going);
                    historyInfo.setGoingCD(goingCD);
                    historyInfo.setPto(pto);
                    if(!c.moveToNext()){
                        break;
                    }
                }
            }
        }finally {
            c.close();
        }
        return list;
    }

    public List<SearchHistoryInfo> getSearchHistoryList(){
        List<SearchHistoryInfo> list = new ArrayList<>();
        // Open the database object in "write" mode, since  writes  may be done
        SQLiteDatabase sdb = this.getWritableDatabase();

        final String[] columns = new String[]{"id","company_cd","bus_stop_cd","bus_stop_name",
                "search_dt","refer_count","bus_route_cd1","bus_route_cd2","bus_route_cd3",
                "bus_route_name1","bus_route_name2","bus_route_name3",
                "going1","going2","going3","goingCD1","goingCD2","goingCD3"
        };
        Cursor c = sdb.query(TABLE_SEARCH_HISTORY_NAME, columns, null, null,
                null, null, "refer_count desc,search_dt desc");
        try {
            if (c.moveToFirst()) {
                while(true){
                    SearchHistoryInfo historyInfo = new SearchHistoryInfo();
                    list.add(historyInfo);
                    long id = c.getLong(c.getColumnIndex("id"));
                    long refCnt = c.getLong(c.getColumnIndex("refer_count"));
                    String searchDT = c.getString(c.getColumnIndex("search_dt"));
                    int companyCD = c.getInt(c.getColumnIndex("company_cd"));
                    int busTopCD = c.getInt(c.getColumnIndex("bus_stop_cd"));
                    String busStopName = c.getString(c.getColumnIndex("bus_stop_name"));
                    int busRouteCD1 = c.getInt(c.getColumnIndex("bus_route_cd1"));
                    int busRouteCD2 = c.getInt(c.getColumnIndex("bus_route_cd2"));
                    int busRouteCD3 = c.getInt(c.getColumnIndex("bus_route_cd3"));
                    String busRouteName1 = c.getString(c.getColumnIndex("bus_route_name1"));
                    String busRouteName2 = c.getString(c.getColumnIndex("bus_route_name2"));
                    String busRouteName3 = c.getString(c.getColumnIndex("bus_route_name3"));
                    int goingCD1 = c.getInt(c.getColumnIndex("goingCD1"));
                    int goingCD2 = c.getInt(c.getColumnIndex("goingCD2"));
                    int goingCD3 = c.getInt(c.getColumnIndex("goingCD3"));
                    String going1 = c.getString(c.getColumnIndex("going1"));
                    String going2 = c.getString(c.getColumnIndex("going2"));
                    String going3 = c.getString(c.getColumnIndex("going3"));

                    historyInfo.setId(id);
                    historyInfo.setReferCount(refCnt);
                    historyInfo.setSearchDT(searchDT);
                    historyInfo.setCompanyCD(companyCD);
                    historyInfo.setBusStopCD(busTopCD);
                    historyInfo.setBusStopName(busStopName);
                    historyInfo.setBusRouteCD1(busRouteCD1);
                    historyInfo.setBusRouteCD2(busRouteCD2);
                    historyInfo.setBusRouteCD3(busRouteCD3);
                    historyInfo.setBusRouteName1(busRouteName1);
                    historyInfo.setBusRouteName2(busRouteName2);
                    historyInfo.setBusRouteName3(busRouteName3);
                    historyInfo.setGoing1(going1);
                    historyInfo.setGoing2(going2);
                    historyInfo.setGoing3(going3);
                    historyInfo.setGoingCD1(goingCD1);
                    historyInfo.setGoingCD2(goingCD2);
                    historyInfo.setGoingCD3(goingCD3);
                    if(!c.moveToNext()){
                        break;
                    }
                }
            }
        }finally {
            c.close();
        }
        return list;
    }

    public int getTimeTables(BusDirectionStopInfo busDirectionStop){
        SQLiteDatabase sdb = this.getWritableDatabase();
        int count = 0;
        final String[] columns = new String[]{"day_kind","hour","minute"};
        String where = "company_cd = ? and bus_route_cd = ? and bus_stop_cd = ? " +
                "and going_cd=?";
        String companyCDParam = String.valueOf(busDirectionStop.getCompanyCD());
        String busRouteCDParam = String.valueOf(busDirectionStop.getBusRouteCD());
        String busStopCDParam = String.valueOf(busDirectionStop.getBusStopCD());
        String goingCDParam = String.valueOf(busDirectionStop.getGoingCD());

        busDirectionStop.clearTimeInfo();
        Cursor c = sdb.query(TABLE_TIME_TABLE_NAME, columns, where,
                new String[]{companyCDParam,busRouteCDParam,busStopCDParam,goingCDParam},
                null, null, null);

        try {
            if (c.moveToFirst()) {
                while (true) {
                    count++;
                    BusTimeInfo busTimeInfo = new BusTimeInfo();
                    int dayKind = c.getInt(c.getColumnIndex("day_kind"));
                    busTimeInfo.setDayType(DayType.fromInteger(dayKind));
                    busTimeInfo.setHour(c.getInt(c.getColumnIndex("hour")));
                    busTimeInfo.setMinute(c.getInt(c.getColumnIndex("minute")));
                    busDirectionStop.addButTimeInfo(busTimeInfo);

                    if(!c.moveToNext()){
                        break;
                    }
                }
            }
        }finally {
            c.close();
        }
        return count;
    }

    public void addRefCountOfSearchHistory(long id,long refCount){

        ContentValues values = new ContentValues();

        values.put("refer_count", refCount + 1);

        // Opens the database object in "write" mode.
        SQLiteDatabase sdb = this.getWritableDatabase();
        sdb.update(TABLE_SEARCH_HISTORY_NAME, values,"id=?",new String[]{String.valueOf(id)});
    }

    public void deleteSearchHistoryById(long id){
        // Opens the database object in "write" mode.
        SQLiteDatabase sdb = this.getWritableDatabase();
        sdb.delete(TABLE_SEARCH_HISTORY_NAME, "id=?",new String[]{String.valueOf(id)});
    }

    public List<BusComingInfo> getBusComingInfoList(long searchHisID){
        List<BusComingInfo> list = new ArrayList<BusComingInfo>();

        // Open the database object in "write" mode, since  writes  may be done
        SQLiteDatabase sdb = this.getWritableDatabase();

        final String[] columns = new String[]{"company_cd","bus_stop_cd","bus_stop_name",
                "bus_route_cd1","bus_route_cd2","bus_route_cd3",
                "bus_route_name1","bus_route_name2","bus_route_name3",
                "going1","going2","going3","goingCD1","goingCD2","goingCD3"
        };
        String where = "id = ?";
        String param1 = String.valueOf(searchHisID);
        Cursor c = sdb.query(TABLE_SEARCH_HISTORY_NAME, columns, where, new String[]{param1},
                null, null, null);
        try{
            if(c.moveToFirst()){
               int companyCD = c.getInt(c.getColumnIndex("company_cd"));
               int busTopCD = c.getInt(c.getColumnIndex("bus_stop_cd"));
               String busStopName = c.getString(c.getColumnIndex("bus_stop_name"));
               for(int i = 1; i <=3; i++){
                   int busRouteCD = c.getInt(c.getColumnIndex("bus_route_cd" + i));
                   if(busRouteCD > 0){
                       String busRouteName = c.getString(c.getColumnIndex("bus_route_name" + i));
                       int goingCD = c.getInt(c.getColumnIndex("goingCD" + i));
                       String going = c.getString(c.getColumnIndex("going" + i));
                       BusComingInfo busComingInfo = new BusComingInfo();
                       list.add(busComingInfo);

                       busComingInfo.setCompanyCD(companyCD);
                       busComingInfo.setBusRouteCD(busRouteCD);
                       busComingInfo.setBusRouteName(busRouteName);
                       busComingInfo.setBusStopCD(busTopCD);
                       busComingInfo.setBusStopName(busStopName);

                       busComingInfo.setGoing(going);
                       busComingInfo.setGoingCD(goingCD);
                       if(going.endsWith(resources.getString(R.string.going_start_end))){
                           busComingInfo.setFromStartToEnd(true);
                       }else  if(going.endsWith(resources.getString(R.string.going_end_start))){
                           busComingInfo.setFromStartToEnd(false);
                       }
                   }
               }
            }
        }finally {
            c.close();
        }

        for( BusComingInfo busComingInfo:list){
            getBusStopInfoList(busComingInfo,sdb);
        }
        return list;
    }

    public int getCountOfHistory(){
        SQLiteDatabase sdb = this.getWritableDatabase();
        String sql = "SELECT count(1) as cnt from " + TABLE_SEARCH_HISTORY_NAME;
        Cursor c = sdb.rawQuery(sql,new String[]{});
        try{
            if(c.moveToFirst()) {
                return c.getInt(c.getColumnIndex("cnt"));
            }else{
                return 0;
            }
        }finally {
            c.close();
        }
    }

    public  List<BusDirectionStopInfo> searchBusStop(String searchName)
            throws TooManyResultException {
        SQLiteDatabase sdb = this.getWritableDatabase();
        searchName = searchName.replaceAll("%","$%");
        searchName = searchName.replaceAll("'","''");
        String sqlSearchBusStopByName = "SELECT company_cd,bus_stop_cd," +
                " bus_stop_name from " + TABLE_BUS_STOP_NAME +
                " WHERE bus_stop_name like '%" + searchName +
                "%' ESCAPE '$'";

        List<BusStopInfo> stopInfoList = new ArrayList<>();
        Cursor c = sdb.rawQuery(sqlSearchBusStopByName,new String[]{});
        int count = 0;
        try{
            if(c.moveToFirst()) {
                while (true) {
                    count++;
                    if(count > MAX_RESULT_CNT){
                        throw new TooManyResultException();
                    }
                    BusStopInfo busStopInfo = new BusStopInfo();
                    int companyCD = c.getInt(c.getColumnIndex("company_cd"));
                    busStopInfo.setCompanyCD(companyCD);
                    int busStopCD = c.getInt(c.getColumnIndex("bus_stop_cd"));
                    busStopInfo.setBusStopCD(busStopCD);
                    String busStopName = c.getString(c.getColumnIndex("bus_stop_name"));
                    busStopInfo.setBusStopName(busStopName);
                    stopInfoList.add(busStopInfo);
                    if(!c.moveToNext()){
                        break;
                    }
                }
            }
        }finally {
            c.close();
        }

        List<BusDirectionStopInfo> searchResultList = new ArrayList<>();
        for(BusStopInfo busStopInfo:stopInfoList){
            addBusRouteInfo(busStopInfo, sdb, searchResultList);
        }
        return searchResultList;
    }

    //バス停に上り、下りの線路情報を付加する。
    //始点の場合、下りのみ、終点の場合、上りのみ
    private void addBusRouteInfo(BusStopInfo busStopInfo,SQLiteDatabase sdb,
                                 List<BusDirectionStopInfo> searchResultList){
        String sql = "SELECT B.bus_route_cd,B.bus_route_name," +
                " B.start_stop_cd,B.end_stop_cd," +
                " B.start_stop_name,B.end_stop_name, " +
                " A.pto_start,A.pto_end " +
                " FROM bus_route_stop A,bus_route B " +
                " WHERE A.company_cd = B.company_cd  " +
                " AND A.bus_route_cd = B.bus_route_cd "+
                " AND A.company_cd = ? " +
                " AND A.bus_stop_cd = ? ";
        String companyCDStr = String.valueOf(busStopInfo.getCompanyCD());
        String busStopCDStr = String.valueOf(busStopInfo.getBusStopCD());
        Cursor c = sdb.rawQuery(sql,new String[]{companyCDStr,busStopCDStr});
        try{
            if(c.moveToFirst()){
                while(true){
                    if(busStopInfo.getBusStopCD() != c.getInt(c.getColumnIndex("end_stop_cd"))){
                        //下り
                        BusDirectionStopInfo busRouteInfo = addInfoToBusDirectionStop(busStopInfo,c);

                        String endStopName = c.getString(c.getColumnIndex("end_stop_name"));
                        //始点と終点が同じ
                        if(c.getInt(c.getColumnIndex("end_stop_cd")) ==
                                c.getInt(c.getColumnIndex("start_stop_cd"))){
                            endStopName = endStopName + resources.getString(R.string.going_start_end) ;
                        }
                        busRouteInfo.setGoing(endStopName);
                        busRouteInfo.setGoingCD(c.getInt(c.getColumnIndex("end_stop_cd")));
                        busRouteInfo.setDirection(BusDirection.START_END);
                        searchResultList.add(busRouteInfo);
                    }

                    if(busStopInfo.getBusStopCD() != c.getInt(c.getColumnIndex("start_stop_cd"))){
                        //上り
                        BusDirectionStopInfo busRouteInfo = addInfoToBusDirectionStop(busStopInfo,c);
                        String startStopName = c.getString(c.getColumnIndex("start_stop_name"));
                        //始点と終点が同じ
                        if(c.getInt(c.getColumnIndex("end_stop_cd")) ==
                                c.getInt(c.getColumnIndex("start_stop_cd"))){
                            startStopName = startStopName + resources.getString(R.string.going_end_start) ;
                        }
                        busRouteInfo.setGoing(startStopName);
                        busRouteInfo.setGoingCD(c.getInt(c.getColumnIndex("start_stop_cd")));
                        busRouteInfo.setDirection(BusDirection.END_START);
                        searchResultList.add(busRouteInfo);
                    }

                    if(!c.moveToNext()){
                        break;
                    }
                }
            }
        }finally {
            c.close();
        }
    }

    private BusDirectionStopInfo addInfoToBusDirectionStop(BusStopInfo busStopInfo, Cursor c){
        BusDirectionStopInfo busRouteInfo = new BusDirectionStopInfo();
        busRouteInfo.setCompanyCD(busStopInfo.getCompanyCD());
        busRouteInfo.setBusStopName(busStopInfo.getBusStopName());
        busRouteInfo.setBusStopCD(busStopInfo.getBusStopCD());
        busRouteInfo.setBusRouteCD(c.getInt(c.getColumnIndex("bus_route_cd")));
        busRouteInfo.setBusRouteName(c.getString(c.getColumnIndex("bus_route_name")));
        busRouteInfo.setpToStart((c.getString(c.getColumnIndex("pto_start"))));
        busRouteInfo.setpToEnd((c.getString(c.getColumnIndex("pto_end"))));

        return busRouteInfo;
    }

    public List<BusStopInfo> getBusStopsInRoute(int companyCD,int busRouteCD,
                                                BusDirection direction){
        SQLiteDatabase sdb = this.getWritableDatabase();
        List<BusStopInfo> busStopInfoList = getBusStopsInRoute(companyCD,
                busRouteCD, direction,sdb);
        return busStopInfoList;
    }

    private List<BusStopInfo> getBusStopsInRoute(int companyCD,int busRouteCD,
             BusDirection direction,SQLiteDatabase sdb){
        List<BusStopInfo> busStopInfoList = new ArrayList<>();

        String sql = "SELECT B.bus_stop_cd,B.bus_stop_name,A.pto_start,A.pto_end " +
                "FROM bus_route_stop A,bus_stop B " +
                "WHERE A.company_cd = B.company_cd  " +
                "AND A.bus_stop_cd = B.bus_stop_cd "+
                "AND A.company_cd = ? " +
                "AND A.bus_route_cd = ? " +
                "ORDER BY A.seq";
        String companyCDStr = String.valueOf(companyCD);
        String busRouteCDStr = String.valueOf(busRouteCD);
        if(direction == BusDirection.END_START){
            sql += " DESC";
        }
        Cursor c = sdb.rawQuery(sql,new String[]{companyCDStr,busRouteCDStr});

        try{
            if(c.moveToFirst()){
                while(true){
                    BusStopInfo busStopInfo = new BusStopInfo();
                    busStopInfo.setCompanyCD(companyCD);

                    int busStopCD = c.getInt(c.getColumnIndex("bus_stop_cd"));
                    busStopInfo.setBusStopCD(busStopCD);
                    String busStopName = c.getString(c.getColumnIndex("bus_stop_name"));
                    busStopInfo.setBusStopName(busStopName);
                    busStopInfo.setpToStart( c.getString(c.getColumnIndex("pto_start")));
                    busStopInfo.setpToEnd(c.getString(c.getColumnIndex("pto_end")));
                    busStopInfoList.add(busStopInfo);

                    if(!c.moveToNext()){
                        break;
                    }
                }
            }
        }finally {
            c.close();
        }

        return busStopInfoList;
    }

    private  void getBusStopInfoList(BusComingInfo busComingInfo,SQLiteDatabase sdb){

        //ここでは、ＢｕｓＲｏｕｔｅの始点、終点情報が分からないため、固定のBusDirection.START_ENDで
        //検索を行う
        List<BusStopInfo> busStopInfoList = getBusStopsInRoute(busComingInfo.getCompanyCD(),
                busComingInfo.getBusRouteCD(), BusDirection.START_END,sdb);
        int size = busStopInfoList.size();
        if(size > 1){
            busComingInfo.setStartStopCD(busStopInfoList.get(0).getBusStopCD());
            busComingInfo.setEndStopCD(busStopInfoList.get(size - 1).getBusStopCD());
        }
       /*
        if(size > 1 && busStopInfoList.get(0).getBusStopCD() != busStopInfoList.get(size -1).getBusStopCD()
                && busComingInfo.getGoingCD() == busStopInfoList.get(0).getBusStopCD()){
            //逆にする
            Collections.reverse(busStopInfoList);
        }
        */
        busComingInfo.addBusStopInfoFromList(busStopInfoList);

        //対象バス停から終点までのリストを生成する
        busComingInfo.adjustBusStopByGoing();
    }
}
