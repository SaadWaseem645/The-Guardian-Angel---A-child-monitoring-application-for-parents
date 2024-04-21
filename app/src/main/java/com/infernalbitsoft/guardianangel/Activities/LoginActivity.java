package com.infernalbitsoft.guardianangel.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.infernalbitsoft.guardianangel.R;

import java.util.Locale;

import static com.infernalbitsoft.guardianangel.Activities.PermissionActivity.PERMISSION_KEY;
import static com.infernalbitsoft.guardianangel.Activities.PermissionActivity.permissions;
import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Log;
import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.RealLog;
import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.RealToast;
import static com.infernalbitsoft.guardianangel.Utilities.SpannableText.setTextWithSpan;

public class LoginActivity extends AppCompatActivity {

    //Ui Components
    private ImageView logo;
    private TextView headerText;
    private TextView subText;
    private EditText email;
    private EditText password;
    private Button loginButton;
    private RadioGroup modeGroup;
    private Button forgetButton;
    private Button sosButton;
    private LinearLayout languageChange;


    private String[] languages = {"English","Urdu"};
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        logo = findViewById(R.id.login_image);
        headerText = findViewById(R.id.login_header);
        subText = findViewById(R.id.login_subheader);
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        modeGroup = findViewById(R.id.login_modeRadioGroup);
        forgetButton = findViewById(R.id.forget_button);
        sosButton = findViewById(R.id.sos_mode);
        languageChange = findViewById(R.id.login_language_change);

        setLoginNoAccount();

        loginButton.setOnClickListener(v -> {
            signInUser(email.getText().toString(), password.getText().toString());
        });

        forgetButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ForgotPassword.class);
            startActivity(intent);
        });

        sosButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SosActivity.class);
            startActivity(intent);
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(languages, (dialog, which) -> {
            changeLanguage(languages[which]);
        });

        languageChange.setOnClickListener(v -> {
            builder.show();
        });
    }

    void setLoginNoAccount(){
        TextView loginNoAccount = findViewById(R.id.login_noaccount);

        StyleSpan boldStyle = new StyleSpan(Typeface.BOLD);
        setTextWithSpan(loginNoAccount,
                getString(R.string.noAccount),
                getString(R.string.noAccountBold),
                boldStyle);

        loginNoAccount.setOnClickListener(v -> {

            Pair[] pair = new Pair[7];
            pair[0] = new Pair<View,String>(logo,"logo_image");
            pair[1] = new Pair<View,String>(headerText,"logo_text");
            pair[2] = new Pair<View,String>(subText,"logo_subtext");
            pair[3] = new Pair<View, String>(email,"logo_firstopt");
            pair[4] = new Pair<View, String>(password, "logo_lastopt");
            pair[5] = new Pair<View,String>(loginButton, "logo_button");
            pair[6] = new Pair<View,String>(loginNoAccount, "logo_subbutton");

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this,pair);
            Intent intent = new Intent(this, RegistrationActivity.class);
            startActivity(intent,options.toBundle());
            finish();
        });
    }

    private void signInUser(String email, String password){

        if(email.isEmpty()) {
            RealToast(this, "Please Enter Email");
            return;
        }

        if(password.isEmpty()) {
            RealToast(this, "Please Enter Password");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        boolean parentMode = true;
                        if(modeGroup.getCheckedRadioButtonId() == R.id.login_child_mode)
                            parentMode = false;

                        if(parentMode)
                            startActivity(new Intent(this, ParentChildProfileActivity.class));
                        else {
                            Intent intent = new Intent(this, PermissionActivity.class);
                            intent.putExtra(PERMISSION_KEY,permissions[0]);
                            startActivity(intent);
                        }
                        finish();
                    } else {
                        RealToast(LoginActivity.this, "Authentication failed.");
                        RealLog("Login_Activity",task.getException().toString() + " ");
                    }
                });
    }

    private void changeLanguage(String lang){

        Locale locale;
        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);

        if(lang.equals("Urdu")){
            locale = new Locale("ur");
            Locale.setDefault(locale);
            Configuration config = getBaseContext().getResources().getConfiguration();
            config.setLocale(locale);
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());

            preferences.edit().putString(getString(R.string.sharedpreferenced_language), "ur").commit();

        }else {
            locale = new Locale("en");
            Locale.setDefault(locale);
            Configuration config = getBaseContext().getResources().getConfiguration();
            config.setLocale(locale);
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());

            preferences.edit().putString(getString(R.string.sharedpreferenced_language), "en").commit();

        }
        startActivity(new Intent(this, SplashActivity.class));
        finish();
    }
}