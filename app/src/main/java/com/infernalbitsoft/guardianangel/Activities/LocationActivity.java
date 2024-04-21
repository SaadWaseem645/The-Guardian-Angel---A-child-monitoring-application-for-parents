package com.infernalbitsoft.guardianangel.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.slider.Slider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.infernalbitsoft.guardianangel.Model.GeofenceClass;
import com.infernalbitsoft.guardianangel.R;
import com.infernalbitsoft.guardianangel.Services.LocationService;
import com.infernalbitsoft.guardianangel.Utilities.GeofenceHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.RealToast;
import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Toast;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private SharedPreferences preferences;

    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;

    private float radius = 200;

    private LatLng editLatlng = null;
    private Marker currentLocationMarker = null;
    private Marker editMarker = null;
    private Circle editCircle = null;
    private Map<String, Circle> mapCircles = new HashMap<>();
    private ArrayList<String> fenceKeys = new ArrayList<>();

    private LinearLayout geofenceLayout;
    private LinearLayout geofenceDeleteLayout;
    private Button geofenceDeleteClose;
    private Button geofenceDelete;
    private EditText geofenceName;
    private Slider radiusSlider;
    private Button geofenceClose;
    private Button geofenceAdd;

    private Bundle bundle = new Bundle();
    private String childProfileName = "";

    private Boolean moveCamera = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        TextView headerTitle = findViewById(R.id.header_title);
        headerTitle.setText(getString(R.string.option3));
        ImageView back = findViewById(R.id.header_back);

        TextView headerUtility = findViewById(R.id.header_utility);
        headerUtility.setText(getString(R.string.fa_history));
        headerUtility.setVisibility(View.VISIBLE);
        headerUtility.setOnClickListener(v -> {
           Intent intent = new Intent(this, GeofenceHistoryActivity.class);
           startActivity(intent);
        });

        preferences = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);

        geofenceLayout = findViewById(R.id.geofence_layout);
        geofenceDeleteLayout = findViewById(R.id.geofence_delete_layout);
        geofenceDeleteClose = findViewById(R.id.geofence_delete_close);
        geofenceDelete = findViewById(R.id.geofence_delete);
        geofenceName = findViewById(R.id.geofence_name);
        geofenceClose = findViewById(R.id.geofence_close);
        geofenceAdd = findViewById(R.id.geofence_add);
        radiusSlider = findViewById(R.id.radiusSlider);

        fetchGeofences();

        geofenceClose.setOnClickListener(v -> {
            geofenceName.setText("");
            radiusSlider.setValue(200f);
            radius = 200f;
            editMarker.remove();
            editCircle.remove();
            geofenceLayout.setVisibility(View.GONE);
        });

        geofenceAdd.setOnClickListener(v -> {
            geofenceDeleteLayout.setVisibility(View.GONE);
            if (!addGeofence())
                return;
            geofenceName.setText("");
            radiusSlider.setValue(200f);
            radius = 200f;
            geofenceLayout.setVisibility(View.GONE);
        });

        back.setOnClickListener(v -> finish());
        radiusSlider.addOnChangeListener((slider, value, fromUser) -> {
            radius = value;
            if(fromUser)
                addCircle(editLatlng, radius);
        });

        geofenceDeleteClose.setOnClickListener(v -> geofenceDeleteLayout.setVisibility(View.GONE));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);
        childProfileName = preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), "");

        if (user != null) {
            reference.child(user.getUid()).child(childProfileName).child("Location").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Map<String, Object> coor = (Map<String, Object>) snapshot.getValue();

                    if(coor == null)
                        return;

                    Double lat = (Double) coor.get("lat");
                    Double lon = (Double) coor.get("lon");
                    Long timestamp = (Long) coor.get("timestamp");

                    bundle.putDouble("lat", lat);
                    bundle.putDouble("lon", lon);
                    bundle.putLong("date", timestamp);

                    Date date = new Date(timestamp);
                    DateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
                    format.setTimeZone(TimeZone.getDefault());
                    String formatted = format.format(date);

                    // Add a marker to location
                    LatLng location = new LatLng(lat, lon);
                    showCurrentLocation(location, formatted);

//                    if (currentLocationMarker != null)
//                        currentLocationMarker.remove();
//
//                    currentLocationMarker = mMap.addMarker(new MarkerOptions()
//                            .position(location)
//                            .title(childProfileName.concat("'s Last Position: ").concat(formatted)));
//
//                    currentLocationMarker.showInfoWindow();

                    if (moveCamera) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                        moveCamera = false;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }

    private void showCurrentLocation(LatLng location, String formatted){
        if (currentLocationMarker != null)
            currentLocationMarker.remove();

        currentLocationMarker = mMap.addMarker(new MarkerOptions()
                .position(location)
                .title(childProfileName.concat("'s Last Position: ").concat(formatted)));

        currentLocationMarker.showInfoWindow();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
    }


    @Override
    public void onInfoWindowClick(Marker marker) {

        if (fenceKeys.contains(marker.getTitle())) {

            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Delete geofence?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();

        } else return;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        geofenceLayout.setVisibility(View.GONE);
        if (!marker.equals(currentLocationMarker)) {
            Toast(this, (String) marker.getTag());
            geofenceDeleteLayout.setVisibility(View.VISIBLE);

            geofenceDelete.setOnClickListener(v -> {
                reference.child(user.getUid())
                        .child(preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), ""))
                        .child("Geofence").child((String)marker.getTag()).removeValue();

                mapCircles.get((String) marker.getTag()).remove();
                marker.remove();
                geofenceDeleteLayout.setVisibility(View.GONE);
            });
        }
        return true;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        geofenceDeleteLayout.setVisibility(View.GONE);
        editLatlng = latLng;
        addMarker(latLng);
        addCircle(latLng, radius);
    }

    private void addMarker(LatLng latLng) {
        if (editMarker != null)
            editMarker.remove();
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        editMarker = mMap.addMarker(markerOptions);
    }

    private void addCircle(LatLng latLng, float radius) {
        if (editCircle != null)
            editCircle.remove();

        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
        circleOptions.fillColor(Color.argb(64, 255, 0, 0));
        circleOptions.strokeWidth(4);
        editCircle = mMap.addCircle(circleOptions);

        geofenceLayout.setVisibility(View.VISIBLE);
    }

    private boolean addGeofence() {

        Map<String, Object> map = new HashMap<>();

        String name = geofenceName.getText().toString();
        if (name.trim().isEmpty()) {
            RealToast(this, "Please add a geofence name");
            return false;
        }

        map.put("lat", editLatlng.latitude);
        map.put("lon", editLatlng.longitude);
        map.put("name", name);
        map.put("radius", radius);

        editMarker.setTitle(name);
        editMarker.showInfoWindow();

        String key = reference.child(user.getUid())
                .child(preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), "")).child("Geofence")
                .push().getKey();

        editMarker.setTag(key);
        mapCircles.put(key, editCircle);

        editMarker = null;
        editCircle = null;

        reference.child(user.getUid())
                .child(preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), "")).child("Geofence")
                .child(key).setValue(map);

        return true;
    }

    private void fetchGeofences() {
        reference.child(user.getUid())
                .child(preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), "")).child("Geofence")
                .get().addOnSuccessListener(dataSnapshot -> {

            Map<String, Map<String, Object>> map = (HashMap<String, Map<String, Object>>) dataSnapshot.getValue();
//            ArrayList<Map<String,Object>> fences = new ArrayList<>();

            if (map == null)
                return;

            for (String key : map.keySet()) {
                Map<String, Object> fence = map.get(key);
                LatLng latLng = new LatLng((Double) fence.get("lat"), (Double) fence.get("lon"));
                addMarker(latLng);
                addCircle(latLng, (long) fence.get("radius"));

                editMarker.setTag(key);
                mapCircles.put(key, editCircle);

                editMarker.setTitle((String) fence.get("name"));
                editMarker.showInfoWindow();

                editMarker = null;
                editCircle = null;
                geofenceLayout.setVisibility(View.GONE);
            }

//            for (String key : map.keySet()) {
//                fences.add(map.get(key));
//            }
//
//            int i = 1;
//            for(Map<String,Object> fence: fences){
//                LatLng latLng = new LatLng((Double) fence.get("lat"),(Double) fence.get("lon"));
//                addMarker(latLng);
//                addCircle(latLng, (long) fence.get("radius"));
//                editMarker.setTitle(String.valueOf(i++)+"."+fence.get("name"));
//                fenceKeys.add(editMarker.getTitle());
//                editMarker.showInfoWindow();
//                editMarker = null;
//                editCircle = null;
//                geofenceLayout.setVisibility(View.GONE);
//            }

            if (currentLocationMarker != null)
                currentLocationMarker.showInfoWindow();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMap != null) {
            mMap.clear();
            fetchGeofences();

            if(bundle != null)
                if(bundle.containsKey("lat") && bundle.containsKey("lon") && bundle.containsKey("date")){
                    Date date = new Date(bundle.getLong("date"));
                    DateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
                    format.setTimeZone(TimeZone.getDefault());
                    String formatted = format.format(date);

                    // Add a marker to location
                    LatLng location = new LatLng(bundle.getDouble("lat"), bundle.getDouble("lon"));
                    showCurrentLocation(location, formatted);
                }
        }
    }
}