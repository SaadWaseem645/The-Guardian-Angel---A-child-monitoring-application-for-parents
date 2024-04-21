package com.infernalbitsoft.guardianangel.Activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.style.StyleSpan;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.infernalbitsoft.guardianangel.R;

import java.util.HashMap;
import java.util.Map;

import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.RealToast;
import static com.infernalbitsoft.guardianangel.Utilities.SpannableText.setTextWithSpan;

public class RegistrationActivity extends AppCompatActivity {

    //Ui Components
    private TextView loginNoAccount;
    private Button registerButton;
    private EditText name;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;

    //Firebase
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        loginNoAccount = findViewById(R.id.register_yesaccount);
        registerButton = findViewById(R.id.register_button);
        name = findViewById(R.id.registration_name);
        email = findViewById(R.id.registration_email);
        password = findViewById(R.id.registration_password);
        confirmPassword = findViewById(R.id.registration_confirm_password);

        setRegistrationYesAccount();
        setRegisterButton();
    }

    void setRegistrationYesAccount(){
        StyleSpan boldStyle = new StyleSpan(Typeface.BOLD);
        setTextWithSpan(loginNoAccount,
                getString(R.string.yesAccount),
                getString(R.string.yesAccountBold),
                boldStyle);

        loginNoAccount.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            Pair[] pair = new Pair[7];
            pair[0] = new Pair<View,String>(findViewById(R.id.login_image),"logo_image");
            pair[1] = new Pair<View,String>(findViewById(R.id.register_header),"logo_text");
            pair[2] = new Pair<View,String>(findViewById(R.id.register_subtext),"logo_subtext");
            pair[3] = new Pair<View, String>(email,"logo_firstopt");
            pair[4] = new Pair<View, String>(confirmPassword, "logo_lastopt");
            pair[5] = new Pair<View,String>(registerButton, "logo_button");
            pair[6] = new Pair<View,String>(loginNoAccount, "logo_subbutton");

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegistrationActivity.this,pair);

            startActivity(intent,options.toBundle());
            finish();
        });
    }

    void setRegisterButton(){
        registerButton.setOnClickListener(v -> {
                String nameText = name.getText().toString();
                String emailText = email.getText().toString();
                String passwordText = password.getText().toString();
                String confirmPasswordText = confirmPassword.getText().toString();

                if(nameText.isEmpty())
                    name.setError("Please enter name.");
                else if(nameText.length() < 4)
                    name.setError("Name should be at least 4 words.");
                else if(!emailText.toUpperCase().matches("^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"))
                    email.setError("Enter a valid email.");
                else if(passwordText.length() < 6)
                    password.setError("Password length should be 6 or greater");
                else if(!confirmPasswordText.equals(passwordText))
                    password.setError("Passwords should match");
                else{
                    auth.createUserWithEmailAndPassword(emailText, passwordText)
                            .addOnCompleteListener(this, task -> {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = auth.getCurrentUser();

                                    Map<String, Object> userMap = new HashMap<>();
                                    userMap.put("name", nameText);
                                    userMap.put("uid", user.getUid());
                                    userMap.put("created", FieldValue.serverTimestamp());

                                    db.collection("users").document(user.getUid()).set(userMap).addOnSuccessListener(aVoid -> {
                                        RealToast(this, "Account Created");
                                        Intent intent = new Intent(this, LoginActivity.class);

                                        startActivity(intent);
                                        finish();
                                    });
                                }else{
                                    RealToast(this, "Account Creation Failed - " + task.getException());
                                }
                            });
                }
        });
    }
}