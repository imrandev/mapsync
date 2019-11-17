package com.imran.mapsync.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.imran.mapsync.R;
import com.imran.mapsync.room.model.Map;
import com.imran.mapsync.util.MapUtils;

import java.util.List;

public class MapAdapter extends ArrayAdapter<Map> {

    private Context context;

    public MapAdapter(@NonNull Context context, List<Map> mapList) {
        super(context, 0, mapList);
        this.context = context;
    }

    public void update(List<Map> mapList){
        clear();
        addAll(mapList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        Map map = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_map, parent, false);
        // Lookup view for data population
        TextView addressView = convertView.findViewById(R.id.tvAddress);
        // Populate the data into the template view using the data object
        String address = MapUtils.getInstance().getAddressByLatLng(context, map.getLat(), map.getLng());
        addressView.setText(String.format("%s %s", address, map.getCreatedTime()));
        // Return the completed view to render on screen
        return convertView;
    }
}
