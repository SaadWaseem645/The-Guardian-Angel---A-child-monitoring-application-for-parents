package com.infernalbitsoft.guardianangel.RecyclerViews;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.infernalbitsoft.guardianangel.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

public class GeofenceHistoryRecycler extends RecyclerView.Adapter<GeofenceHistoryRecycler.viewholder> {

    private ArrayList<Map<String, Object>> geofenceEvent = new ArrayList<>();

    public GeofenceHistoryRecycler(Map<String, Map<String, Object>> geofenceEvent) {
        for(String key: geofenceEvent.keySet())
            this.geofenceEvent.add(geofenceEvent.get(key));
    }

    @NonNull
    @Override
    public GeofenceHistoryRecycler.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MaterialCardView materialCardView = (MaterialCardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_geofence_event, parent, false);
        return new GeofenceHistoryRecycler.viewholder(materialCardView);
    }

    @Override
    public void onBindViewHolder(@NonNull GeofenceHistoryRecycler.viewholder holder, int position) {
        holder.name.setText((String) geofenceEvent.get(position).get("name"));
        holder.event.setText((String) geofenceEvent.get(position).get("event"));

        Date date = new Date((long) geofenceEvent.get(position).get("timestamp"));
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy hh.mm aa");
        format.setTimeZone(TimeZone.getDefault());
        String formatted = format.format(date);
        holder.time.setText(formatted);

    }

    @Override
    public int getItemCount() {
        return geofenceEvent.size();
    }

    public static class viewholder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView event;
        public TextView time;

        viewholder(View view) {
            super(view);
            name = view.findViewById(R.id.eventName);
            event = view.findViewById(R.id.eventType);
            time = view.findViewById(R.id.eventTime);
        }

    }
}
