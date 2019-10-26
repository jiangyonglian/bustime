package jp.co.jyl.bustime.view.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.co.jyl.bustime.R;
import jp.co.jyl.bustime.TimeTableActivity;
import jp.co.jyl.bustime.bean.BusDirectionStopInfo;
import jp.co.jyl.bustime.bean.BusStopInfo;
import jp.co.jyl.bustime.repository.BusRepository;
import jp.co.jyl.bustime.repository.RepositoryFactory;

/**
 * Created by jiang on 2015/05/04.
 */
public class SearchResultItemAdapter extends SimpleAdapter {
    public static final String KEY_BUS_DIRECT_STOP_INFO  = "busDirectionStopInfo";
    public static final String KEY_TXT_VIEW_ROUTE_NM = "textViewResultRouteName";
    public static final String KEY_TXT_VIEW_STOP_NM = "textViewResultStopName";
    public static final String KEY_CHK_RESULT_SEL = "checkResultSelect";

    private LayoutInflater layoutInflater;
    private List<Map<String, Object>> data = null;
    Context parentContext = null;
    public SearchResultItemAdapter(Context context,
                                   List<Map<String, Object>> data, int resource, String[] from,
                                   int[] to) {
        super(context, data, resource, from, to);
        parentContext = context;
        this.data = data;
    }

    public void clearData(){
        if(data != null){
            data.clear();
        }
    }

    public List<Map<String, Object>> getSelectedData(){
        List<Map<String, Object>> selectedList = new ArrayList<>();
        for(Map<String,Object> map : data){
            if(Boolean.TRUE.equals(map.get("checkResultSelect"))){
                selectedList.add(map);
            }
        }

        return selectedList;
    }

    private class ViewHolder {
        CheckBox checkResultSelect;
        TextView textViewBusStopName;
        TextView textViewBusRouteName;
        ImageButton btnShowRoute;
        ImageButton btnShowTimeTable;
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getView(final int position,View convertView,final ViewGroup parent){
        layoutInflater = LayoutInflater.from(parent.getContext());
        ViewHolder holder;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.list_result_item,parent,false);
            holder = new ViewHolder();
            holder.checkResultSelect = (CheckBox)convertView.findViewById(
                    R.id.checkResultSelect);
            holder.textViewBusStopName =(TextView)convertView.findViewById(
                    R.id.textViewResultStopName);
            holder.textViewBusRouteName =(TextView)convertView.findViewById(
                    R.id.textViewResultRouteName);
            holder.btnShowRoute = (ImageButton)convertView.findViewById(
                    R.id.btn_show_route);
            holder.btnShowTimeTable = (ImageButton)convertView.findViewById(
                    R.id.btn_show_timetable);
            convertView.setTag(holder);

        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.checkResultSelect.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        ListView listView = (ListView)parent;
                        Map<String,Object> data = (Map<String,Object>)listView.getItemAtPosition(position);
                        if(data == null){
                            return;
                        }
                        if (isChecked) {
                            data.put(KEY_CHK_RESULT_SEL,Boolean.TRUE);
                        } else {
                            data.put(KEY_CHK_RESULT_SEL, Boolean.FALSE);
                        }
                    }
                }
        );

        holder.btnShowRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListView listView = (ListView)parent;
                Map<String,Object> data = (Map<String,Object>)listView.getItemAtPosition(position);
                if(data == null){
                    return;
                }
                BusDirectionStopInfo busDirectionStopInfo = (BusDirectionStopInfo)
                        data.get(KEY_BUS_DIRECT_STOP_INFO);
                String showRouteName = (String)data.get(KEY_TXT_VIEW_ROUTE_NM);
                showBusRouteInfoDlg(showRouteName,busDirectionStopInfo);
            }
        });
        holder.btnShowTimeTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListView listView = (ListView)parent;
                Map<String,Object> data = (Map<String,Object>)listView.getItemAtPosition(position);
                if(data == null){
                    return;
                }
                BusDirectionStopInfo busDirectionStopInfo = (BusDirectionStopInfo)
                        data.get(KEY_BUS_DIRECT_STOP_INFO);
                Intent intent = new Intent(parentContext, TimeTableActivity.class);
                intent.putExtra(TimeTableActivity.BUS_DIRECTION_STOP_INO,busDirectionStopInfo);
                parentContext.startActivity(intent);

            }
        });

        ListView listView = (ListView)parent;
        Map<String,Object> data = (Map<String,Object>)listView.getItemAtPosition(position);
        if(data == null){
            return convertView;
        }
        holder.textViewBusStopName.setText((String)data.get(KEY_TXT_VIEW_STOP_NM));
        holder.textViewBusRouteName.setText((String)data.get(KEY_TXT_VIEW_ROUTE_NM));
        holder.checkResultSelect.setChecked(Boolean.TRUE.equals(data.get(KEY_CHK_RESULT_SEL)));
        return convertView;
    }

    private void showBusRouteInfoDlg(String showRouteName,BusDirectionStopInfo busDirectionStopInfo){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(parentContext);
        builderSingle.setTitle(showRouteName);
        BusRepository repository = RepositoryFactory.i.getBusRepository();
        List<BusStopInfo> busStopList= repository.getBusStopsInRoute(busDirectionStopInfo.getCompanyCD(),
                busDirectionStopInfo.getBusRouteCD(),busDirectionStopInfo.getDirection());

        String[] mStrings = new String[busStopList.size()];
        for(int i = 0; i < busStopList.size();i++){
            mStrings[i] = busStopList.get(i).getBusStopName();
        }
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                parentContext,
                android.R.layout.simple_list_item_1,mStrings);
        ListView myList = new ListView(parentContext);
        myList.setAdapter(arrayAdapter);
        builderSingle.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builderSingle.setView(myList);

        builderSingle.show();
    }
}
