package com.infernalbitsoft.guardianangel.RecyclerViews;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.infernalbitsoft.guardianangel.R;

import java.util.ArrayList;
import java.util.Map;

import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Log;

public class AppListRecycler extends RecyclerView.Adapter<AppListRecycler.viewholder> {

    private ArrayList<Map<String, Object>> appInfo = new ArrayList<>();
    private Modes appListMode;

    private AppSetLimitListener mAppSetLimitListener;

    private Long maxUsage;
    private String sortedKey;
    private Long totalUsage;

    public enum Modes {APP_LIST, CONTACTS, APP_USAGE}
    private enum Viewtype {HEADER, APP_INFO}

    public AppListRecycler(Map<String, Map<String, Object>> appInfo, Modes mode){
        for(String key: appInfo.keySet())
            this.appInfo.add(appInfo.get(key));
        this.appListMode = mode;
    }

    public AppListRecycler(Map<String, Map<String, Object>> appInfo, Modes mode, Long maxUsage, String sortedKey, Long totalUsage){
        for(String key: appInfo.keySet())
            this.appInfo.add(appInfo.get(key));
        this.appListMode = mode;
        this.maxUsage = maxUsage;
        this.sortedKey = sortedKey;
        this.totalUsage = totalUsage;
    }

    public AppListRecycler(ArrayList<Map<String, Object>> contacts, Modes mode){
        this.appInfo = contacts;
        this.appListMode = mode;
    }

    @Override
    public int getItemViewType(int position) {
        Log("postion", "pos"+position);
        if(position == appInfo.size() && appListMode == Modes.APP_USAGE)
            return Viewtype.HEADER.ordinal();
        else
            return Viewtype.APP_INFO.ordinal();
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        MaterialCardView materialCardView = (MaterialCardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_app_info,parent,false);
//        return new viewholder(materialCardView);

        if(viewType == Viewtype.HEADER.ordinal())
            return new viewholder((TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_app_info_header,parent,false));
        else
            return new viewholder((MaterialCardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_app_info,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {

        Log("CurrentPostion", position + "Pos");

        if(position == appInfo.size() && appListMode == Modes.APP_USAGE){
            holder.totalUsage.setText("Total Screen Time ".concat(calculateTime(totalUsage)));
            return;
        }

        if(appListMode == Modes.APP_LIST || appListMode == Modes.APP_USAGE) {
            holder.appName.setText((String) appInfo.get(position).get("appName"));
            holder.packageName.setText((String) appInfo.get(position).get("packageName"));
            holder.appIcon.setVisibility(View.VISIBLE);
            holder.appIcon.setImageBitmap(baseToBitmap((String) appInfo.get(position).get("icon")));
        }else{
            holder.appName.setText((String) appInfo.get(position).get("name"));
            holder.packageName.setText((String) appInfo.get(position).get("number"));
        }

        if(appListMode == Modes.APP_USAGE){

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0,
                    6,
                    calculatePercent((Long) appInfo.get(position).get(sortedKey))
            );

            Log("UsageMode", "isWorking " + calculatePercent((Long) appInfo.get(position).get(sortedKey)));

            holder.usagePercent.setLayoutParams(params);
            holder.usagePercent.setVisibility(View.VISIBLE);

            holder.timeUsed.setText(calculateTime((Long) appInfo.get(position).get(sortedKey)));
            holder.timeUsed.setVisibility(View.VISIBLE);

            holder.setLimit.setVisibility(View.VISIBLE);
            holder.setLimit.setOnClickListener(v -> {
                mAppSetLimitListener.onAppSetLimit((String) appInfo.get(position).get("appName"), (String) appInfo.get(position).get("packageName"));
            });
        }
    }

    private Bitmap baseToBitmap(String base64){
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    private int calculatePercent(Long usage){
        if(maxUsage == 0)
            return 100;
        return (int) (usage*100/maxUsage);
    }

    private String calculateTime(Long time){

        String usageTime = "";

        if(time >= 3600000){
            usageTime = String.valueOf(time/3600000).concat(" hr");
        }

        long minutes = time%3600000;
        usageTime = usageTime.concat(" ").concat(String.valueOf(minutes/60000)).concat(" m");

        return usageTime;

    }

    @Override
    public int getItemCount() {
        if(appListMode == Modes.APP_USAGE)
            return appInfo.size()+1;
        else return appInfo.size();
    }

    public static class viewholder extends RecyclerView.ViewHolder{

        public TextView totalUsage;
        private Button setLimit;

        public TextView appName;
        public TextView packageName;
        public TextView timeUsed;
        public View usagePercent;
        public ImageView appIcon;

        viewholder(View view){
            super(view);
            totalUsage = view.findViewById(R.id.usage_header);
            setLimit = view.findViewById(R.id.setLimit);
            appName = view.findViewById(R.id.appName);
            packageName = view.findViewById(R.id.packageName);
            appIcon = view.findViewById(R.id.appIcon);
            timeUsed = view.findViewById(R.id.usage_time);
            usagePercent = view.findViewById(R.id.usage_percentage);
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if(recyclerView.getContext() instanceof AppSetLimitListener)
            mAppSetLimitListener = (AppSetLimitListener) recyclerView.getContext();
    }

    public interface AppSetLimitListener {
        void onAppSetLimit(String app, String pack);
    }

}
