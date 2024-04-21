package com.infernalbitsoft.guardianangel.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.chaos.view.PinView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.infernalbitsoft.guardianangel.R;

import java.util.Locale;

import static com.infernalbitsoft.guardianangel.Activities.PermissionActivity.PERMISSION_KEY;
import static com.infernalbitsoft.guardianangel.Activities.PermissionActivity.permissions;
import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Log;

public class SplashActivity extends AppCompatActivity {

    private Animation topAnim, bottomAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        checkLanguage();

//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        ImageView image = findViewById(R.id.imageView);
        TextView textView = findViewById(R.id.textView);
        TextView textView1 = findViewById(R.id.textView2);

        image.setAnimation(topAnim);
        textView.setAnimation(bottomAnim);
        textView1.setAnimation(bottomAnim);

        Handler handler = new Handler();
        handler.postDelayed(() -> {

            int activity = continueActivity();

            if (isUserSignedIn() && (activity == -2 || activity == -1))
                startActivity(new Intent(this, MainMenuActivity.class));
            else {

                Pair[] pair = new Pair[1];
                pair[0] = new Pair<View, String>(image, "logo_image");

                Intent intent;
                if (activity == -3)
                    intent = new Intent(this, ParentChildProfileActivity.class);
                else if (activity == -2)
                    intent = new Intent(this, LoginActivity.class);
                else if (activity >= 0 && activity < 10) {
                    intent = new Intent(this, PermissionActivity.class);
                    intent.putExtra(PERMISSION_KEY, permissions[activity]);
                } else if (activity == 10) {
                    intent = new Intent(this, ChildProfileActivity.class);
                } else if (activity == 11) {
                    intent = new Intent(this, SetPinActivity.class);
                } else if (activity == 100) {
                    intent = new Intent(this, AllSetActivity.class);
                } else {
                    intent = new Intent(this, LoginActivity.class);
                }


                if (activity == -2) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, pair);
                    startActivity(intent, options.toBundle());
                } else
                    startActivity(intent);
            }
            finish();
        }, 3000);
    }

    private int continueActivity() {
        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);
        return sharedpreferences.getInt(getString(R.string.sharedpreferenced_activity), -2);
    }

    private boolean isUserSignedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    private void checkLanguage(){
        Locale current = getResources().getConfiguration().getLocales().get(0);
        Log("Langugage",current.getLanguage());

        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);
        String userLanguage = sharedpreferences.getString(getString(R.string.sharedpreferenced_language), "en");

        if(!userLanguage.equals(current.getLanguage())){
            Locale locale = new Locale(userLanguage);
            Locale.setDefault(locale);
            Configuration config = getBaseContext().getResources().getConfiguration();
            config.setLocale(locale);
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
            recreate();
        }
    }

}