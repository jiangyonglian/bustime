package jp.co.jyl.bustime.view.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.co.jyl.bustime.R;
import jp.co.jyl.bustime.bean.TimeTableHistoryInfo;
import jp.co.jyl.bustime.repository.BusRepository;
import jp.co.jyl.bustime.repository.RepositoryFactory;

/**
 * Created by jiang on 2015/05/04.
 */
public class TimeTableHisItemAdapter extends SimpleAdapter {
    public static final String KEY_TIMETABLE_HIS_INFO  = "timeTable_History_Info";
    public static final String KEY_TXT_VIEW_ROUTE_NM = "textViewResultRouteName";
    public static final String KEY_TXT_VIEW_STOP_NM = "textViewResultStopName";

    private LayoutInflater layoutInflater;
    private List<Map<String, Object>> data = null;
    Context parentContext = null;
    public TimeTableHisItemAdapter(Context context,
                                   List<Map<String, Object>> data, int resource, String[] from,
                                   int[] to) {
        super(context, data, resource, from, to);
        parentContext = context;
        this.data = data;
    }


    private class ViewHolder {
        TextView textViewBusStopName;
        TextView textViewBusRouteName;
        ImageButton btnDeleteTimeTableHis;
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getView(final int position,View convertView,final ViewGroup parent){
        layoutInflater = LayoutInflater.from(parent.getContext());
        ViewHolder holder;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.list_timetablehis_item,parent,false);
            holder = new ViewHolder();
            holder.textViewBusStopName =(TextView)convertView.findViewById(
                    R.id.textViewTTHStopName);
            holder.textViewBusRouteName =(TextView)convertView.findViewById(
                    R.id.textViewTTHRouteName);
            holder.btnDeleteTimeTableHis = (ImageButton)convertView.findViewById(
                    R.id.btn_delete_timetable_history);
            convertView.setTag(holder);

        }else{
            holder = (ViewHolder) convertView.getTag();
        }


        holder.btnDeleteTimeTableHis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListView listView = (ListView)parent;
                Map<String,Object> data = (Map<String,Object>)listView.getItemAtPosition(position);
                if(data == null){
                    return;
                }
                TimeTableHistoryInfo historyInfo = (TimeTableHistoryInfo)data.get(KEY_TIMETABLE_HIS_INFO);
                BusRepository repository = RepositoryFactory.i.getBusRepository();
                repository.deleteTimeTableHistory(historyInfo);
                TimeTableHisItemAdapter.this.data.remove(position);
                TimeTableHisItemAdapter.this.notifyDataSetChanged();
            }
        });


        ListView listView = (ListView)parent;
        Map<String,Object> data = (Map<String,Object>)listView.getItemAtPosition(position);
        if(data == null){
            return convertView;
        }
        holder.textViewBusStopName.setText((String)data.get(KEY_TXT_VIEW_STOP_NM));
        holder.textViewBusRouteName.setText((String)data.get(KEY_TXT_VIEW_ROUTE_NM));
        return convertView;
    }
}
