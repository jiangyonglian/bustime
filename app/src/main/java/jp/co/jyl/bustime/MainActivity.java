package jp.co.jyl.bustime;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;

import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.ViewGroup;

import jp.co.jyl.bustime.repository.BusRepository;
import jp.co.jyl.bustime.repository.DBHelper;
import jp.co.jyl.bustime.repository.RepositoryFactory;
import jp.co.jyl.bustime.view.fragment.BusStopSearchFragment;
import jp.co.jyl.bustime.view.fragment.SearchHistoryFragment;
import jp.co.jyl.bustime.view.fragment.TimeTableHistoryFragment;


public class MainActivity extends AppCompatActivity {

    public static String SEARCH_HISTORY_CNT_KEY = "SEARCH_HISTORY_CNT_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DBHelper.initializeDB(this.getApplicationContext());
        setupBottomNavigation();
        initFragment();
    }



    @Override
    protected void onDestroy() {
        DBHelper.getInstance().closeDb();
        super.onDestroy();
    }


    private void setupBottomNavigation() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_history:
                        switchFragment(new SearchHistoryFragment());
                        return true;
                    case R.id.navigation_search:
                        switchFragment(new BusStopSearchFragment());
                        return true;
                    case R.id.navigation_timetable:
                        switchFragment(new TimeTableHistoryFragment());
                        return true;
                    case R.id.navigation_help:
                        Intent intent = new Intent(MainActivity.this, HelpActivity.class);
                        startActivity(intent);
                        return true;

                }
                return false;
            }
        });
    }

    private void switchFragment(Fragment newFragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void initFragment() {
        Fragment fragment = null;
        BusRepository repository = RepositoryFactory.i.getBusRepository();
        int historyCnt = repository.getCountOfHistory();
        if (historyCnt > 0){
            fragment = new SearchHistoryFragment();
        }else{
            fragment = new BusStopSearchFragment();
        }
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment_container, fragment);

        transaction.commit();
    }

}



