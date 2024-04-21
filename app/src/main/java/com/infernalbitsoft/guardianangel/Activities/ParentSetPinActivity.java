package com.infernalbitsoft.guardianangel.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chaos.view.PinView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.infernalbitsoft.guardianangel.R;

import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Toast;

public class ParentSetPinActivity extends AppCompatActivity {

    private PinView pinView;
    private Button setPinContinue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_set_pin);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);

        pinView = findViewById(R.id.set_pin_view);
        setPinContinue = findViewById(R.id.set_pin_continue);

        pinView.setShowSoftInputOnFocus(false);
        setPinContinue.setOnClickListener(v -> {
            if(pinView.getText().toString().length() < 4) {
                Toast(setPinContinue.getContext(), "Please enter complete pin code");
            }else{

                db.child(user.getUid())
                        .child(preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), ""))
                        .child("ChildPin").setValue(pinView.getText().toString());
                finish();
            }
        });
    }

    public void addText(View view) {
        pinView.setText(pinView.getText().toString() + ((TextView) view).getText().toString());
    }

    public void removeText(View view){
        pinView.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
    }
}