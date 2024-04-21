package com.infernalbitsoft.guardianangel.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.infernalbitsoft.guardianangel.R;

import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.RealToast;

public class ForgotPassword extends AppCompatActivity {

    private EditText loginEmail;
    private Button send;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        loginEmail = findViewById(R.id.login_email);
        send = findViewById(R.id.reset_button);

        send.setOnClickListener(v -> {
            String emailText = loginEmail.getText().toString();
            if (!emailText.toUpperCase().matches("^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"))
                RealToast(this, "Enter a valid email.");
            else {
                auth.sendPasswordResetEmail(emailText)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                RealToast(ForgotPassword.this, "A password reset link has been sent to your email");
                            }
                        });
            }

        });
    }
}