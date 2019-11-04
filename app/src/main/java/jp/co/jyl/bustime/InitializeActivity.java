package jp.co.jyl.bustime;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import jp.co.jyl.bustime.repository.BusRepository;
import jp.co.jyl.bustime.repository.DBHelper;
import jp.co.jyl.bustime.repository.RepositoryFactory;

public class InitializeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialize);
        DBHelper.initializeDB(this.getApplicationContext());
        new InitializeTask().execute();
    }


    class InitializeTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            //DBの初期化
            long startTime = System.currentTimeMillis();

            BusRepository repository = RepositoryFactory.i.getBusRepository();
            //30日前に取得した時刻表を削除
            repository.deleteTimeTables(30);
            int cnt =  repository.getCountOfHistory();
            long endTime = System.currentTimeMillis();
            long delta = endTime - startTime;
            if(delta < 1000){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            return cnt;
        }
        @Override
        protected void onPostExecute(Integer searchHistoryCnt) {
            super.onPostExecute(searchHistoryCnt);

            finish();
            Intent intent = new Intent(InitializeActivity.this, MainActivity.class);
            intent.putExtra(MainActivity.SEARCH_HISTORY_CNT_KEY,searchHistoryCnt);
            startActivity(intent);
        }
    }
    }
