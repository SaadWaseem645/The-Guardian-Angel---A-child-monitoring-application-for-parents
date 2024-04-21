package com.infernalbitsoft.guardianangel.RecyclerViews;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.infernalbitsoft.guardianangel.Activities.MirrorFullActivity;
import com.infernalbitsoft.guardianangel.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

public class MirrorRecycler extends RecyclerView.Adapter<MirrorRecycler.viewholder> {

    private final ArrayList<Map<String, Object>> mirrors = new ArrayList<>();

    public MirrorRecycler(Map<String, Map<String, Object>> mirrors){
        if(mirrors != null)
            for(String key: mirrors.keySet())
                this.mirrors.add(mirrors.get(key));
    }

    public void addMirror(Map<String, Object> mirror){
        this.mirrors.add(mirror);
    }

    @NonNull
    @Override
    public MirrorRecycler.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MaterialCardView materialCardView = (MaterialCardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_mirror,parent,false);
        return new MirrorRecycler.viewholder(materialCardView);
    }

    @Override
    public void onBindViewHolder(@NonNull MirrorRecycler.viewholder holder, int position) {
        String url = (String) mirrors.get(position).get("url");
        Long timestamp = (Long) mirrors.get(position).get("timestamp");
        String appName = (String) mirrors.get(position).get("appName");

        if(position%2 == 0)
            holder.parentRelative.setBackgroundColor(holder.view.getContext().getColor(R.color.theme_blue));
        else
            holder.parentRelative.setBackgroundColor(holder.view.getContext().getColor(R.color.theme_dark_blue));

        Glide.with(holder.image.getContext()).load(url).timeout(30 * 1000).into(holder.image);

        Date date = new Date(timestamp);
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy hh.mm aa");
        format.setTimeZone(TimeZone.getDefault());
        String formatted = format.format(date);

        if(!appName.equals("")  && !appName.equals("NULL"))
            formatted = appName.concat(" - ").concat(formatted);

        holder.timestamp.setText(formatted);

        String formattedDate = formatted;

        holder.view.setOnClickListener(v -> {
            Intent intent = new Intent(holder.view.getContext(),MirrorFullActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("timestamp", formattedDate);
            holder.view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mirrors.size();
    }

    public static class viewholder extends RecyclerView.ViewHolder{

        public RelativeLayout parentRelative;
        public ImageView image;
        public TextView timestamp;
        public View view;

        viewholder(View view){
            super(view);
            image = view.findViewById(R.id.mirror_imageview);
            timestamp = view.findViewById(R.id.mirror_textview);
            parentRelative = view.findViewById(R.id.parentRelative);
            this.view = view;
        }

    }

}
