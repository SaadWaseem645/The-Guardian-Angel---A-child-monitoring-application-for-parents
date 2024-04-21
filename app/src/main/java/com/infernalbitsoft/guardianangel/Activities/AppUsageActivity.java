package com.infernalbitsoft.guardianangel.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.infernalbitsoft.guardianangel.BottomSheets.LimitBottomSheet;
import com.infernalbitsoft.guardianangel.R;
import com.infernalbitsoft.guardianangel.RecyclerViews.AppListRecycler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Log;

public class AppUsageActivity extends AppCompatActivity implements AppListRecycler.AppSetLimitListener, LimitBottomSheet.ItemClickListener {

    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private SharedPreferences preferences;
    private RecyclerView recyclerView;
    private TextView lastUpdate;

    private ProgressBar bar;

//    private Long maxUsageDaily = 0L;
//    private Long maxUsageWeekly = 0L;
    private Long maxUsage = 0L;
    private Long totalUsage = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_usage);

        TextView headerTitle = findViewById(R.id.header_title);
        headerTitle.setText(getString(R.string.option6));
        ImageView back = findViewById(R.id.header_back);
        back.setOnClickListener(v -> finish());
        TextView utitliyyHeader = findViewById(R.id.header_utility);
        utitliyyHeader.setText(getText(R.string.fa_schedule));
        utitliyyHeader.setVisibility(View.VISIBLE);

        utitliyyHeader.setOnClickListener(v -> {
            Intent intent = new Intent(this, AppLimitsActivity.class);
            startActivity(intent);
        });

        lastUpdate = findViewById(R.id.last_updated);
        RadioGroup radioGroup = findViewById(R.id.usageRadio);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.todayRadio)
                loadUsage("daily");
            else
                loadUsage("weekly");
        });



        preferences = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);
        bar = findViewById(R.id.installed_app_progress_bar);

        recyclerView = findViewById(R.id.installed_apps_recycler_view);

        loadUsage("daily");
    }

    private void loadUsage(String sortedKey){

        bar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        maxUsage = 0L;
        totalUsage = 0L;

        if (user != null)
            db.child(user.getUid())
                    .child(preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), ""))
                    .child("AppList").get().addOnSuccessListener(dataSnapshot -> {

                Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) dataSnapshot.getValue();

                if(map == null)
                    return;

                db.child(user.getUid())
                        .child(preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), ""))
                        .child("AppUsage").get().addOnSuccessListener(dataSnapshot1 -> {
                    Map<String, Object> fullUsageMap = (Map<String, Object>) dataSnapshot1.getValue();

                    long timestamp = (long) fullUsageMap.get("timestamp");
                    Date date = new Date(timestamp);
                    DateFormat format = new SimpleDateFormat("hh:mm aa dd MMMM yyyy");
                    format.setTimeZone(TimeZone.getDefault());
                    String formatted = format.format(date);
                    lastUpdate.setText(getString(R.string.lastUpdate).concat(formatted));

                    fullUsageMap.remove("timestamp");

                    Map<String, Map<String, Long>> usageMap = new HashMap<>();
                    for (Map.Entry<String, Object> entry : fullUsageMap.entrySet()) {
                        usageMap.put(entry.getKey(), (Map<String, Long>) entry.getValue());
                    }

                    for (String pack : map.keySet()) {
                        if (usageMap.containsKey(pack)) {
                            Log("PackageUsage", pack + " " + usageMap.get(pack).get("daily"));
//                            map.get(pack).put("daily", usageMap.get(pack).get("daily"));
//                            map.get(pack).put("weekly", usageMap.get(pack).get("weekly"));

                            map.get(pack).put(sortedKey,usageMap.get(pack).get(sortedKey));
                            totalUsage += usageMap.get(pack).get(sortedKey);

                            if(maxUsage < usageMap.get(pack).get(sortedKey))
                                maxUsage = usageMap.get(pack).get(sortedKey);



//                            if (maxUsageDaily < usageMap.get(pack).get("daily"))
//                                maxUsageDaily = usageMap.get(pack).get("daily");
//                            if (maxUsageWeekly < usageMap.get(pack).get("weekly"))
//                                maxUsageWeekly = usageMap.get(pack).get("weekly");
                        } else {
                            map.get(pack).put(sortedKey, 0L);
//                            map.get(pack).put("daily", 0L);
//                            map.get(pack).put("weekly", 0L);
                        }
                    }

                    Map<String, Map<String, Object>> sortedMap = sortByDaily(map, sortedKey);

//                    Long usage;
//                    if(sortedKey.equals( "daily"))
//                        usage = maxUsageDaily;
//                    else usage = maxUsageWeekly;


                    Log("SizeOfMap", sortedMap.size() + " size");

                    AppListRecycler appListRecycler = new AppListRecycler(sortedMap, AppListRecycler.Modes.APP_USAGE, maxUsage, sortedKey, totalUsage);
                    recyclerView.setAdapter(appListRecycler);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AppUsageActivity.this, RecyclerView.VERTICAL, true);
                    linearLayoutManager.setStackFromEnd(true);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    bar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                });
            });
    }

    private static Map<String, Map<String, Object>> sortByDaily(Map<String, Map<String, Object>> unsortMap, String key) {

        // 1. Convert Map to List of Map
        List<Map.Entry<String, Map<String, Object>>> list =
                new LinkedList<Map.Entry<String, Map<String, Object>>>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, new Comparator<Map.Entry<String, Map<String, Object>>>() {
            public int compare(Map.Entry<String, Map<String, Object>> o1,
                               Map.Entry<String, Map<String, Object>> o2) {
                return ((Long) o1.getValue().get(key)).compareTo((Long) o2.getValue().get(key));
            }
        });

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<String, Map<String, Object>> sortedMap = new LinkedHashMap<String, Map<String, Object>>();
        for (Map.Entry<String, Map<String, Object>> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        /*
        //classic iterator example
        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext(); ) {
            Map.Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }*/


        return sortedMap;
    }

    @Override
    public void onAppSetLimit(String app, String pack) {
        Log("ONSetLimitPress",pack);
        LimitBottomSheet bottomSheet = LimitBottomSheet.newInstance(app, pack);
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.TAG);
    }

    @Override
    public void onItemClick(String pack, String hour, String minute) {
        Long time = (Long.parseLong(hour) * 3600000) +  (Long.parseLong(minute) * 60000);

        Map<String, Object> map = new HashMap<>();
        map.put(pack.replace(".", ""), time);

        db.child(user.getUid())
                .child(preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), ""))
                .child("AppUsageLimit").updateChildren(map);
        Log("HoursAndMIns", time + " ");
    }
}