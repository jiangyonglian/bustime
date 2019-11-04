package jp.co.jyl.bustime.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.jyl.bustime.BusComingActivity;
import jp.co.jyl.bustime.R;
import jp.co.jyl.bustime.bean.BusDirectionStopInfo;
import jp.co.jyl.bustime.bean.SearchHistoryInfo;
import jp.co.jyl.bustime.exception.TooManyResultException;
import jp.co.jyl.bustime.repository.BusRepository;
import jp.co.jyl.bustime.repository.RepositoryFactory;
import jp.co.jyl.bustime.view.Helper;

/**
 * バス停検索フラグメント
 */
public class BusStopSearchFragment extends Fragment {

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView with
     * Views.
     */
    private SearchResultItemAdapter mAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BusStopSearchFragment.
     */
    public static BusStopSearchFragment newInstance() {
        BusStopSearchFragment fragment = new BusStopSearchFragment();
        return fragment;
    }

    public BusStopSearchFragment() {
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
        final View view = inflater.inflate(R.layout.fragment_busstop_search, container, false);
        final Button btnShowBusComing = (Button) view.findViewById(R.id.btnShowBusComing);
        btnShowBusComing.setEnabled(false);
        btnShowBusComing.setOnClickListener(new View.OnClickListener() {

             @Override
             public void onClick(View v) {
                 doShowBusComing(view);
             }
         });

        ImageButton btnSearch = (ImageButton) view.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText editStopName = (EditText)view.findViewById(R.id.txtSearchBusStopName);
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editStopName.getWindowToken(), 0);
                doSearch(view,editStopName.getText().toString());
            }
        });

        ImageButton btnClear = (ImageButton) view.findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText editStopName = (EditText)view.findViewById(R.id.txtSearchBusStopName);
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editStopName.getWindowToken(), 0);
                editStopName.setText("");
                btnShowBusComing.setEnabled(false);
                clearResultList(view);
            }
        });
        return view;
    }

    private void clearResultList(final View view){
        if(mAdapter != null){
            mAdapter.clearData();
            mAdapter.notifyDataSetChanged();
            mAdapter = null;
        }

        TextView emptyView = (TextView)view.findViewById(R.id.searchResultEmpty);
        emptyView.setText("");
        final Button btnShowBusComing = (Button) view.findViewById(R.id.btnShowBusComing);
        btnShowBusComing.setEnabled(false);
    }

    private void doSearch( final View view ,String busStopName){
        clearResultList(view);

        if(busStopName == null || "".equals(busStopName)){
            return;
        }
        //バス停名検索
        BusRepository repository = RepositoryFactory.i.getBusRepository();
        List<BusDirectionStopInfo> resultList = null;
        try {
            resultList = repository.searchBusStop(busStopName);
        } catch (TooManyResultException e) {
            TextView emptyView = (TextView)view.findViewById(R.id.searchResultEmpty);
            emptyView.setText(getText(R.string.msg_toomany_result));
            return;
        }
        if(resultList == null || resultList.size() == 0){
            TextView emptyView = (TextView)view.findViewById(R.id.searchResultEmpty);
            emptyView.setText(getText(R.string.msg_no_result));
            return;
        }

        mAdapter = genResultListAdapter(resultList);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(R.id.searchResultList);
        mListView.setAdapter(mAdapter);

        final Button btnShowBusComing = (Button) view.findViewById(R.id.btnShowBusComing);
        btnShowBusComing.setEnabled(true);
    }

    private SearchResultItemAdapter genResultListAdapter( List<BusDirectionStopInfo> resultList){

        List<Map<String, Object>> listData = new ArrayList<>();
        for(BusDirectionStopInfo busDirectionStopInfo:resultList){
            Map<String, Object> map = new HashMap<>();
            map.put( SearchResultItemAdapter.KEY_TXT_VIEW_STOP_NM,
                    busDirectionStopInfo.getBusStopName());
            map.put( SearchResultItemAdapter.KEY_CHK_RESULT_SEL,Boolean.FALSE);

            String routeName = Helper.genBusRouteGoing(busDirectionStopInfo.getBusRouteName(),
                    busDirectionStopInfo.getGoing(), getString(R.string.going_text));
            map.put( SearchResultItemAdapter.KEY_TXT_VIEW_ROUTE_NM,routeName);
            map.put( SearchResultItemAdapter.KEY_BUS_DIRECT_STOP_INFO,busDirectionStopInfo);
            listData.add(map);
        }

        return new SearchResultItemAdapter(getActivity(), listData,
                R.layout.list_result_item, new String[] {SearchResultItemAdapter.KEY_TXT_VIEW_STOP_NM,
                SearchResultItemAdapter.KEY_TXT_VIEW_ROUTE_NM},
                new int[] { R.id.textViewResultStopName,R.id.textViewResultRouteName});
    }

    private void doShowBusComing(final View view){
        if(mAdapter == null){
            return;
        }
        List<Map<String, Object>> selectedList = mAdapter.getSelectedData();
        int size = selectedList.size();
        if(size == 0){
            Helper.showShortToast(getActivity(),R.string.msg_no_selected);
            return;
        }
        if(size > 3){
            Helper.showShortToast(getActivity(),R.string.msg_4_selected);
            return;
        }

        BusRepository repository = RepositoryFactory.i.getBusRepository();

        BusDirectionStopInfo busDirectionStopInfo = (BusDirectionStopInfo)
                selectedList.get(0).get("busDirectionStopInfo");

        SearchHistoryInfo shi = new SearchHistoryInfo();
        long id = System.currentTimeMillis();
        shi.setId(id);
        DateFormat df = new DateFormat();
        CharSequence searchDT = df.format("yyyyMMddhhmmss", new Date());
        shi.setSearchDT(searchDT.toString());
        shi.setReferCount(1);

        int busStopCD = busDirectionStopInfo.getBusStopCD();
        shi.setCompanyCD(busDirectionStopInfo.getCompanyCD());
        shi.setBusStopCD(busStopCD);
        shi.setBusStopName(busDirectionStopInfo.getBusStopName());

        shi.setBusRouteCD1(busDirectionStopInfo.getBusRouteCD());
        shi.setBusRouteName1(busDirectionStopInfo.getBusRouteName());
        shi.setGoingCD1(busDirectionStopInfo.getGoingCD());
        shi.setGoing1(busDirectionStopInfo.getGoing());
        if(size > 1){
            BusDirectionStopInfo busDirectionStopInfo2 = (BusDirectionStopInfo)
                    selectedList.get(1).get("busDirectionStopInfo");
            if(busStopCD != busDirectionStopInfo2.getBusStopCD()){
                Helper.showShortToast(getActivity(),R.string.msg_notsame_busstop);
                return;
            }
            shi.setBusRouteCD2(busDirectionStopInfo2.getBusRouteCD());
            shi.setBusRouteName2(busDirectionStopInfo2.getBusRouteName());
            shi.setGoingCD2(busDirectionStopInfo2.getGoingCD());
            shi.setGoing2(busDirectionStopInfo2.getGoing());
        }

        if(size > 2){
            BusDirectionStopInfo busDirectionStopInfo3 = (BusDirectionStopInfo)
                    selectedList.get(2).get("busDirectionStopInfo");
            if(busStopCD != busDirectionStopInfo3.getBusStopCD()){
                Helper.showShortToast(getActivity(),R.string.msg_notsame_busstop);
                return;
            }
            shi.setBusRouteCD3(busDirectionStopInfo3.getBusRouteCD());
            shi.setBusRouteName3(busDirectionStopInfo3.getBusRouteName());
            shi.setGoingCD3(busDirectionStopInfo3.getGoingCD());
            shi.setGoing3(busDirectionStopInfo3.getGoing());
        }
        repository.insertSearchHistory(shi);

        clearResultList(view);
        Intent intent = new Intent(getActivity(), BusComingActivity.class);
        intent.putExtra(BusComingActivity.SELECTED_HIS_ITEM_ID,String.valueOf(id));
        startActivity(intent);
    }

}
