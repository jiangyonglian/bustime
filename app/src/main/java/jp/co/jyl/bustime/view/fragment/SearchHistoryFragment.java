package jp.co.jyl.bustime.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
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

import jp.co.jyl.bustime.bean.SearchHistoryInfo;
import jp.co.jyl.bustime.repository.BusRepository;
import jp.co.jyl.bustime.repository.RepositoryFactory;
import jp.co.jyl.bustime.view.Helper;

/**
 * バス停検索履歴フラグメント.
 */
public class SearchHistoryFragment extends Fragment {

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private HistoryItemAdapter mAdapter;

    public static SearchHistoryFragment newInstance() {
        SearchHistoryFragment fragment = new SearchHistoryFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SearchHistoryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void genListAdapter(){
        //検索履歴情報の取得
        BusRepository repository = RepositoryFactory.i.getBusRepository();
        List<SearchHistoryInfo> historyInfoList = repository.getSearchHistoryList();
        if(historyInfoList.size() == 0){
            setEmptyText(getString(R.string.msg_no_history));
            return;
        }
        setEmptyText("");
        List<Map<String, String>> listData = new ArrayList<>();
        for(SearchHistoryInfo historyInfo : historyInfoList){
            Map<String, String> map = new HashMap<>();
            map.put( "historyItemId",String.valueOf(historyInfo.getId()));
            map.put( "refCount",String.valueOf(historyInfo.getReferCount()));
            map.put( "textViewBusStopName",historyInfo.getBusStopName());
            map.put( "textViewBusRouteName1",genBusRouteInfo(historyInfo,1));
            map.put( "textViewBusRouteName2",genBusRouteInfo(historyInfo,2));
            map.put( "textViewBusRouteName3",genBusRouteInfo(historyInfo,3));
            listData.add(map);
        }

        mAdapter = new HistoryItemAdapter(getActivity(), listData,
                R.layout.list_history_item, new String[] { "textViewBusStopName",
                "textViewBusRouteName1","textViewBusRouteName2",
                "textViewBusRouteName3"},
                new int[] { R.id.textViewBusNameStop,R.id.textViewBusRouteName1,
                        R.id.textViewBusRouteName2,R.id.textViewBusRouteName3});
    }

    private String genBusRouteInfo(SearchHistoryInfo historyInfo,int index){

        String routeName = null;
        String going = null;

        if(index == 1){
            routeName = historyInfo.getBusRouteName1();
            going = historyInfo.getGoing1();
        }else if(index == 2){
            routeName = historyInfo.getBusRouteName2();
            going = historyInfo.getGoing2();
        }else{
            routeName = historyInfo.getBusRouteName3();
            going = historyInfo.getGoing3();
        }
        if(routeName != null && !"".equals(routeName)){
            String suffix = getString(R.string.going_text);
            return Helper.genBusRouteGoing(routeName,going,suffix);

        }else{
            return "";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_searchhistoryitem, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(R.id.historyItemList);
        genListAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                int position, long rid) {
                Map<String,String> map = (Map<String,String>)parent.getItemAtPosition(position);
                String ids = map.get("historyItemId");
                String refCountStr = map.get("refCount");
                Log.i(this.getClass().getSimpleName(), "selected id:" + ids);
                BusRepository repository = RepositoryFactory.i.getBusRepository();
                repository.addRefCountOfSearchHistory(Long.parseLong(ids),
                        Long.parseLong(refCountStr));
                Intent intent = new Intent(getActivity(), BusComingActivity.class);
                intent.putExtra(BusComingActivity.SELECTED_HIS_ITEM_ID,ids);
                startActivity(intent);
            }
        });
        return view;
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
