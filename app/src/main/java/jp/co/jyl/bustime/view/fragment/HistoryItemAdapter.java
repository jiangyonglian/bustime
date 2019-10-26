package jp.co.jyl.bustime.view.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import jp.co.jyl.bustime.R;
import jp.co.jyl.bustime.repository.BusRepository;
import jp.co.jyl.bustime.repository.RepositoryFactory;

/**
 * Created by jiang on 2015/05/04.
 */
public class HistoryItemAdapter extends SimpleAdapter {

    private LayoutInflater layoutInflater;
    private List<? extends Map<String, ?>> data = null;
    public HistoryItemAdapter(Context context,
                           List<? extends Map<String, ?>> data, int resource, String[] from,
                           int[] to) {
        super(context, data, resource, from, to);
        this.data = data;
    }

    private class ViewHolder {

        TextView textViewBusStopName;
        ImageButton btnDelete;
        TextView textViewBusRouteName1;
        TextView textViewBusRouteName2;
        TextView textViewBusRouteName3;
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getView(final int position,View convertView,final ViewGroup parent){
        layoutInflater = LayoutInflater.from(parent.getContext());
        ViewHolder holder;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.list_history_item,parent,false);
            holder = new ViewHolder();
            holder.textViewBusStopName =(TextView)convertView.findViewById(R.id.textViewBusNameStop);
            holder.btnDelete =(ImageButton)convertView.findViewById(R.id.btn_delete_history);
            holder.textViewBusRouteName1 =(TextView)convertView.findViewById(R.id.textViewBusRouteName1);
            holder.textViewBusRouteName2 =(TextView)convertView.findViewById(R.id.textViewBusRouteName2);
            holder.textViewBusRouteName3 =(TextView)convertView.findViewById(R.id.textViewBusRouteName3);

            convertView.setTag(holder);

        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListView listView = (ListView)parent;
                Map<String,String> data = (Map<String,String>)listView.getItemAtPosition(position);
                if(data == null){
                    return;
                }
                String ids = data.get("historyItemId");
                BusRepository repository = RepositoryFactory.i.getBusRepository();
                repository.deleteSearchHistoryById(Long.parseLong(ids));
                HistoryItemAdapter.this.data.remove(position);
                HistoryItemAdapter.this.notifyDataSetChanged();
            }
        });
        ListView listView = (ListView)parent;
        Map<String,String> data = (Map<String,String>)listView.getItemAtPosition(position);
        if(data == null){
            return convertView;
        }
        holder.textViewBusStopName.setText(data.get("textViewBusStopName"));
        holder.textViewBusRouteName1.setText(data.get("textViewBusRouteName1"));
        holder.textViewBusRouteName2.setText(data.get("textViewBusRouteName2"));
        holder.textViewBusRouteName3.setText(data.get("textViewBusRouteName3"));

        return convertView;
    }
}
