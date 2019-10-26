package jp.co.jyl.bustime.view.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.jyl.bustime.BusComingActivity;
import jp.co.jyl.bustime.R;
import jp.co.jyl.bustime.TimeTableActivity;
import jp.co.jyl.bustime.bean.BusDirectionStopInfo;
import jp.co.jyl.bustime.bean.SearchHistoryInfo;
import jp.co.jyl.bustime.bean.TimeTableHistoryInfo;
import jp.co.jyl.bustime.repository.BusRepository;
import jp.co.jyl.bustime.repository.RepositoryFactory;
import jp.co.jyl.bustime.view.Helper;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class TimeTableHistoryFragment extends Fragment {

    private TimeTableHisItemAdapter mAdapter;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    public TimeTableHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_time_table_history, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(R.id.timeTableHistoryItemList);
        genListAdapter();
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long rid) {
                Map<String,Object> map = (Map<String,Object>)parent.getItemAtPosition(position);
                TimeTableHistoryInfo historyInfo =
                        (TimeTableHistoryInfo)map.get(TimeTableHisItemAdapter.KEY_TIMETABLE_HIS_INFO);

                BusDirectionStopInfo busDirectionStopInfo = BusDirectionStopInfo.fromTimeTableHistory(historyInfo);

                Intent intent = new Intent(getActivity(), TimeTableActivity.class);
                intent.putExtra(TimeTableActivity.BUS_DIRECTION_STOP_INO,busDirectionStopInfo);
                startActivity(intent);
             }
        });

        return view;
    }

    private void genListAdapter(){
        //時刻表検索履歴情報の取得
        BusRepository repository = RepositoryFactory.i.getBusRepository();
        List<TimeTableHistoryInfo> historyInfoList = repository.getTimeTableHistoryList();
        if(historyInfoList.size() == 0){
            setEmptyText(getString(R.string.msg_no_history));
            return;
        }
        setEmptyText("");
        List<Map<String, Object>> listData = new ArrayList<>();
        for(TimeTableHistoryInfo historyInfo : historyInfoList){
            Map<String, Object> map = new HashMap<>();
            map.put(TimeTableHisItemAdapter.KEY_TIMETABLE_HIS_INFO,historyInfo);
            map.put(TimeTableHisItemAdapter.KEY_TXT_VIEW_STOP_NM,historyInfo.getBusStopName());
            String routeName = Helper.genBusRouteGoing(historyInfo.getBusRouteName(),
                    historyInfo.getGoing(), getString(R.string.going_text));
            map.put(TimeTableHisItemAdapter.KEY_TXT_VIEW_ROUTE_NM,routeName);

            listData.add(map);
        }

        mAdapter = new TimeTableHisItemAdapter(getActivity(), listData,
                R.layout.list_timetablehis_item, new String[] {
                TimeTableHisItemAdapter.KEY_TXT_VIEW_STOP_NM,
                TimeTableHisItemAdapter.KEY_TXT_VIEW_ROUTE_NM},
                new int[] {  R.id.textViewTTHStopName,R.id.textViewTTHRouteName});
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }
}
