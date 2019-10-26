package jp.co.jyl.bustime;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import jp.co.jyl.bustime.bean.BusComingInfo;
import jp.co.jyl.bustime.bean.BusDirection;
import jp.co.jyl.bustime.bean.BusDirectionStopInfo;
import jp.co.jyl.bustime.exception.NoInternetException;
import jp.co.jyl.bustime.exception.ServerBusyException;
import jp.co.jyl.bustime.repository.BusRepository;
import jp.co.jyl.bustime.repository.DBHelper;
import jp.co.jyl.bustime.repository.RepositoryFactory;
import jp.co.jyl.bustime.service.BusPlaceSearchFactory;
import jp.co.jyl.bustime.view.AccessDrawView;
import jp.co.jyl.bustime.view.Helper;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.MobileAds;

public class BusComingActivity extends Activity  {
    public static final String SELECTED_HIS_ITEM_ID = "SELECTED_HIS_ITEM_ID";

    // Instance of the progress action-view
    private MenuItem miActionProgressItem = null;

    private AccessDrawView accesDrawView = null;
    private List<BusComingInfo> busComingInfoList = null;
    private Timer  elapsedTimer   = null;
    private String searchID = null;
    /**
     * タイマー更新用Handler
     */
    private Handler asyncHandler = new Handler();

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(SELECTED_HIS_ITEM_ID,searchID);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_coming);
        DBHelper.initializeDB(this.getApplicationContext());
//        MobileAds.initialize(getApplicationContext(), "ca-app-pub-9099051044472779~7716540441");
//        AdView mAdView = (AdView) findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

        Intent intent = getIntent();
        //履歴ID
        searchID = intent.getStringExtra(SELECTED_HIS_ITEM_ID);
        if(searchID != null){
            Helper.saveStringToPreference(this,SELECTED_HIS_ITEM_ID,searchID);
        }else{
            searchID = Helper.getStringFromPreference(this,SELECTED_HIS_ITEM_ID);
        }

        //時刻表Activityから戻るときに、savedInstanceStateから検索履歴IDを取得する
        if(searchID == null && savedInstanceState != null){
            searchID = savedInstanceState.getString(SELECTED_HIS_ITEM_ID);
        }

        //対象ルート情報の取得
        BusRepository repository = RepositoryFactory.i.getBusRepository();

        busComingInfoList= repository.getBusComingInfoList(Long.parseLong(searchID));
        accesDrawView = (AccessDrawView) findViewById(R.id.busview);
        accesDrawView.setBusComingInfoList(busComingInfoList);

        //Timerの初期化
        initialTimer();

        //touch event
        accesDrawView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //return onTouchEvent(event);
                return touchEventForDrawView(event);
            }
        });

    }

    private void initialTimer(){
        elapsedTimer = new Timer();
        //1秒ごとに起動
        elapsedTimer.schedule(new SearchTask(), 0, 60000);
    }

      /**
     * タイマーを更新
     * @author jiang
     *
     */
    class SearchTask extends TimerTask {
        @Override
        public void run() {

            asyncHandler.post(new Runnable() {
                public void run() {
                    new AsyncHttpRequest().execute();
                }
            });
        }
    }

    public class AsyncHttpRequest extends AsyncTask<String, Void, Integer> {
        @Override
        protected void onPreExecute (){
            if(miActionProgressItem != null){
                miActionProgressItem.setVisible(true);
            }
        }

        @Override
        protected Integer doInBackground(String... params) {

            if(busComingInfoList == null || busComingInfoList.size() == 0){
                return 0;
            }

            int companyCD = busComingInfoList.get(0).getCompanyCD();
            try{
                boolean result = BusPlaceSearchFactory.i.getBusPlaceSearcher(companyCD)
                        .getBusPlaceInfoData(busComingInfoList);
                if(!result){
                    Log.i(BusComingActivity.class.getSimpleName(),"There is no bus coming");
                    return  R.string.msg_nobus_coming;
                }else{
                    return 0;
                }
            }catch(ServerBusyException ex){
                Log.i(BusComingActivity.class.getSimpleName(),"Server is busy");
                return R.string.msg_server_busy;
            }catch(NoInternetException ex){
                Log.i(BusComingActivity.class.getSimpleName(),"I/O exception");
                return R.string.msg_no_internet;
            }
        }

        @Override
        protected void onPostExecute(Integer msgId) {
            super.onPostExecute(msgId);
            if(msgId == 0){
                accesDrawView.invalidate();
            }else {
                Helper.showShortToast(BusComingActivity.this,msgId);
            }
            if(miActionProgressItem != null){
                miActionProgressItem.setVisible(false);
            }
        }
    }

    /**
     * タッチイベント
     */
    private boolean touchEventForDrawView(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                BusDirectionStopInfo busStopAroundPoint = accesDrawView.busStopAroundPoint(
                        (int) (event.getX()), (int) (event.getY()));
                if(busStopAroundPoint != null){

                    Log.i(BusComingActivity.class.getSimpleName(),
                            "route=" + busStopAroundPoint.getBusRouteCD() +
                            ",stop=" + busStopAroundPoint.getBusStopCD());
                    if(!hasTimeTable(busStopAroundPoint)){
                        Helper.showShortToast(this,R.string.msg_no_timetable);
                        return true;
                    }
                    Intent intent = new Intent(this, TimeTableActivity.class);
                    intent.putExtra(TimeTableActivity.BUS_DIRECTION_STOP_INO,busStopAroundPoint);
                    startActivity(intent);
                }
                break;
        }
        return true;
    }

    private boolean hasTimeTable(BusDirectionStopInfo busStopAroundPoint){
        if(busStopAroundPoint.getDirection() == BusDirection.END_START){
            return !"0".equals(busStopAroundPoint.getpToStart());
        }else{
            return !"0".equals(busStopAroundPoint.getpToEnd());
        }
    }


    /**
     * onWindowFocusChangedでViewのgetWidthが有効な
     * 値を取得できる。onCreateでは、取得できない。
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        initialAccessDrawView();
    }

    private void initialAccessDrawView(){
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        int viewWidth = scrollView.getWidth();
        int viewHeight = scrollView.getHeight();
        //widthを設定して、最大Heightを計算する
        accesDrawView.setMaxWidth(viewWidth);
        int drawHeight = accesDrawView.initAndGetDrawMaxHeight();

        accesDrawView.setMaxHeight(viewHeight < drawHeight?drawHeight:viewHeight);
        //スクロール範囲が変わったので、updateViewLayoutを呼ばないと、スクロール範囲は、変わらない
        scrollView.updateViewLayout(accesDrawView,accesDrawView.getLayoutParams());
    }


    @Override
    protected void onDestroy(){
         if(elapsedTimer != null){
            elapsedTimer.cancel();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bus_coming, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.miActionHelp) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle(getString(R.string.action_help));
            alertDialog.setMessage(getString(R.string.guide_bus_coming));
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.show();
            return true;
        }else if(id == R.id.miActionRefresh){
            if(elapsedTimer != null){
                elapsedTimer.cancel();
            }
            initialTimer();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);

        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }
}
