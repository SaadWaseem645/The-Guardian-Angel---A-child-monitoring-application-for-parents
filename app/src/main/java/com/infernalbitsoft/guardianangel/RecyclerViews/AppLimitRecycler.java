package com.infernalbitsoft.guardianangel.RecyclerViews;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.infernalbitsoft.guardianangel.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import static android.content.Context.MODE_PRIVATE;

public class AppLimitRecycler extends RecyclerView.Adapter<AppLimitRecycler.viewholder> {

    private ArrayList<String> appLimitPackages;
    private Map<String, Long> appLimits;

    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private SharedPreferences preferences;
    private String username;

    public AppLimitRecycler(Map<String, Long> appLimits) {
        this.appLimits = appLimits;
        appLimitPackages = new ArrayList<>(appLimits.keySet());
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        preferences = parent.getContext().getSharedPreferences(parent.getContext().getString(R.string.app_preferences), MODE_PRIVATE);
        username = preferences.getString(parent.getContext().getString(R.string.sharedpreferences_child_profile_name_key), "");

        MaterialCardView materialCardView = (MaterialCardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_app_limit, parent, false);
        return new viewholder(materialCardView);
    }

    @Override
    public void onBindViewHolder(@NonNull AppLimitRecycler.viewholder holder, int position) {


        String pack = appLimitPackages.get(position);
        Long time = appLimits.get(pack);

        db.child(user.getUid())
                .child(username)
                .child("AppList")
                .child(pack).get().addOnSuccessListener(dataSnapshot -> {

            Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();

            holder.appName.setText((String) map.get("appName"));
            holder.packageName.setText((String) map.get("packageName"));
            holder.appIcon.setVisibility(View.VISIBLE);
            holder.appIcon.setImageBitmap(baseToBitmap((String) map.get("icon")));
            holder.timeLimit.setText(calculateTime(time));
            holder.deleteLimit.setOnClickListener(v -> {
                db.child(user.getUid())
                        .child(username)
                        .child("AppUsageLimit").child(pack).removeValue().addOnSuccessListener(aVoid -> {
                            appLimits.remove(pack);
                            appLimitPackages.remove(pack);
                            notifyDataSetChanged();
                        });
            });
        });

    }

    private Bitmap baseToBitmap(String base64){
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    @Override
    public int getItemCount() {
        return appLimits.size();
    }

    public static class viewholder extends RecyclerView.ViewHolder {


        public TextView appName;
        public TextView packageName;
        public TextView timeLimit;
        public ImageView appIcon;
        public Button deleteLimit;

        viewholder(View view) {
            super(view);
            appName = view.findViewById(R.id.appName);
            packageName = view.findViewById(R.id.packageName);
            appIcon = view.findViewById(R.id.appIcon);
            timeLimit = view.findViewById(R.id.timeLimit);
            deleteLimit = view.findViewById(R.id.limit_delete);
        }
    }

    private String calculateTime(Long time) {

        String usageTime = "";

        if (time >= 3600000) {
            usageTime = String.valueOf(time / 3600000).concat(" hr");
        }

        long minutes = time % 3600000;
        usageTime = usageTime.concat(" ").concat(String.valueOf(minutes / 60000)).concat(" m");

        return usageTime;

    }
}
