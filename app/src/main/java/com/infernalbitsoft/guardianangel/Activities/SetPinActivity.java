package com.infernalbitsoft.guardianangel.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.infernalbitsoft.guardianangel.Utilities.LogAndToast;

import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Toast;

public class SetPinActivity extends AppCompatActivity {

    //Views
    private PinView pinView;
    private Button setPinContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pin);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);

        pinView = findViewById(R.id.set_pin_view);
        setPinContinue = findViewById(R.id.set_pin_continue);

        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);
        sharedpreferences.edit().putInt(getString(R.string.sharedpreferenced_activity), 11).commit();
        SharedPreferences.Editor editor = sharedpreferences.edit();

        pinView.setShowSoftInputOnFocus(false);
        setPinContinue.setOnClickListener(v -> {
            if(pinView.getText().toString().length() < 4) {
                Toast(setPinContinue.getContext(), "Please enter complete pin code");
            }else{
                editor.putString(getString(R.string.sharedpreferences_set_pin_key),pinView.getText().toString());
                editor.commit();
                db.child(user.getUid())
                        .child(preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), ""))
                        .child("ChildPin").setValue(pinView.getText().toString());
                Intent intent = new Intent(setPinContinue.getContext(), AllSetActivity.class);
                startActivity(intent);
                finish();
            }
        });

//        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_preferences),MODE_PRIVATE);
//        String childProfileName = preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key),"");
//        Toast(this, childProfileName);
    }

    public void addText(View view) {
        pinView.setText(pinView.getText().toString() + ((TextView) view).getText().toString());
    }

    public void removeText(View view){
        pinView.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
    }
}