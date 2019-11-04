package jp.co.jyl.bustime;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

import jp.co.jyl.bustime.bean.BusDirection;
import jp.co.jyl.bustime.bean.BusDirectionStopInfo;
import jp.co.jyl.bustime.bean.TimeTableHistoryInfo;
import jp.co.jyl.bustime.exception.NoInternetException;
import jp.co.jyl.bustime.exception.ServerBusyException;
import jp.co.jyl.bustime.repository.BusRepository;
import jp.co.jyl.bustime.repository.DBHelper;
import jp.co.jyl.bustime.repository.RepositoryFactory;
import jp.co.jyl.bustime.service.BusPlaceSearchFactory;
import jp.co.jyl.bustime.view.Helper;
import jp.co.jyl.bustime.view.TimeTableView;


public class TimeTableActivity extends AppCompatActivity {

    public static final String BUS_DIRECTION_STOP_INO =  "BUS_DIRECTION_STOP_INO";

    private TimeTableView timeTableView = null;
    // Instance of the progress view
    private MenuItem miActionProgressItem = null;

    private BusDirectionStopInfo busStopAroundPoint = null;

    private boolean getDataFromDB = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);
        DBHelper.initializeDB(this.getApplicationContext());
        timeTableView = (TimeTableView)findViewById(R.id.viewTimeTable);

        Intent intent = getIntent();
        busStopAroundPoint =(BusDirectionStopInfo)
                intent.getSerializableExtra(BUS_DIRECTION_STOP_INO);
        if(busStopAroundPoint == null){
            return;
        }
        //バス停留所、系統、行き先情報の設定
        Helper.setTextView(this, R.id.textBusStopName, busStopAroundPoint.getBusStopName());
        Helper.setTextView(this,R.id.textBusRouteName,busStopAroundPoint.getBusRouteName());
        Helper.setTextView(this,R.id.textBusGoingName,busStopAroundPoint.getGoing());

    }

    /**
     * onWindowFocusChangedでViewのgetWidthが有効な
     * 値を取得できる。onCreateでは、取得できない。
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollViewOfTimeTable);
        int viewWidth = scrollView.getWidth();
        int viewHeight = scrollView.getHeight();
        timeTableView.setMaxWidth(viewWidth);
        timeTableView.setMaxHeight(viewHeight);
        scrollView.updateViewLayout(timeTableView,timeTableView.getLayoutParams());
        //時刻情報の取得
        //onCreate()で以下を呼び出すと、時刻表の取得時間が短い場合、onWindowFocusChanged()
        //を呼ぶ前に、drawTimeTable()が呼ばれて、スクロールできなくなる。
        new AsyncHttpRequest().execute();
    }

    private void drawTimeTable(){
        timeTableView.setTimeInfoList(busStopAroundPoint.getTimeInfoList());
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollViewOfTimeTable);
        scrollView.updateViewLayout(timeTableView,timeTableView.getLayoutParams());
        timeTableView.invalidate();
    }

    public class AsyncHttpRequest extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {

            int companyCD = busStopAroundPoint.getCompanyCD();
            try{
                //まずDBから検索する
                BusRepository repository = RepositoryFactory.i.getBusRepository();
                int count = repository.getTimeTables(busStopAroundPoint);
                if(count > 0){
                    getDataFromDB = true;
                }else{
                    boolean result = BusPlaceSearchFactory.i.getBusTimeSearcher(companyCD)
                            .getBusTime(busStopAroundPoint);
                    if(!result){
                        return R.string.msg_failget_timetable;
                    }
                }

                //検索履歴の保存
                TimeTableHistoryInfo timeTableHistoryInfo = new TimeTableHistoryInfo();
                timeTableHistoryInfo.setCompanyCD(busStopAroundPoint.getCompanyCD());
                timeTableHistoryInfo.setBusStopCD(busStopAroundPoint.getBusStopCD());
                timeTableHistoryInfo.setBusRouteCD(busStopAroundPoint.getBusRouteCD());
                timeTableHistoryInfo.setGoingCD(busStopAroundPoint.getGoingCD());
                timeTableHistoryInfo.setBusStopName(busStopAroundPoint.getBusStopName());
                timeTableHistoryInfo.setBusRouteName(busStopAroundPoint.getBusRouteName());
                timeTableHistoryInfo.setGoing(busStopAroundPoint.getGoing());
                DateFormat df = new DateFormat();
                CharSequence searchDT = df.format("yyyyMMddhhmmss", new Date());
                timeTableHistoryInfo.setSearchDT(searchDT.toString());
                if(busStopAroundPoint.getDirection() == BusDirection.START_END){
                    timeTableHistoryInfo.setPto(busStopAroundPoint.getpToEnd());
                }else{
                    timeTableHistoryInfo.setPto(busStopAroundPoint.getpToStart());
                }

                repository.insertOrUpdateTimeTableHistory(timeTableHistoryInfo);
                 return 0;
            }catch(ServerBusyException ex){
                return R.string.msg_server_busy;
            }catch(NoInternetException ex){
                return R.string.msg_no_internet;
            }
        }

        @Override
        protected void onPostExecute(Integer msgId) {
            super.onPostExecute(msgId);

            if(msgId == 0){
                drawTimeTable();
            }else{
                Helper.showShortToast(TimeTableActivity.this,msgId);
            }
            if(miActionProgressItem != null){
                miActionProgressItem.setVisible(false);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_table, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        if(getDataFromDB){
            miActionProgressItem.setVisible(false);
        }
        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }
}
