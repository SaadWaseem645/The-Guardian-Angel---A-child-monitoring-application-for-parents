package com.infernalbitsoft.guardianangel.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.infernalbitsoft.guardianangel.R;
import com.infernalbitsoft.guardianangel.Services.LocationService;
import com.infernalbitsoft.guardianangel.Services.SOSLocationService;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Log;
import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.RealToast;

public class SosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        TextView headerTitle = findViewById(R.id.header_title);
        headerTitle.setText(getString(R.string.use_sos));
        ImageView back = findViewById(R.id.header_back);
        back.setOnClickListener(v -> finish());

        if (    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        }

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    2);
        }

        startService(new Intent(this, SOSLocationService.class));


        String[] isoCountries = Locale.getISOCountries();
//        String[] displayCountries = new String[isoCountries.length];

        SortedMap<String, String> displayCountries = new TreeMap<>();

        for (int i = 0; i < isoCountries.length; i++)
            Log("SOSCountry", isoCountries[i]);

        for (int i = 0; i < isoCountries.length; i++)
            displayCountries.put((new Locale("", isoCountries[i])).getDisplayCountry(), isoCountries[i]);

        Spinner countrySos = findViewById(R.id.sos_country);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, displayCountries.keySet().toArray(new String[isoCountries.length]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySos.setAdapter(adapter);

        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);

        countrySos.setSelection(sharedpreferences.getInt(getString(R.string.sos_country), 0));

        EditText sos1 = findViewById(R.id.sos1);
        EditText sos2 = findViewById(R.id.sos2);
        EditText sos3 = findViewById(R.id.sos3);
        EditText sos4 = findViewById(R.id.sos4);
        EditText sos5 = findViewById(R.id.sos5);
        Button save = findViewById(R.id.save_button);

        sos1.setText(sharedpreferences.getString(getString(R.string.sharedpreferences_contact_1), ""));
        sos2.setText(sharedpreferences.getString(getString(R.string.sharedpreferences_contact_2), ""));
        sos3.setText(sharedpreferences.getString(getString(R.string.sharedpreferences_contact_3), ""));
        sos4.setText(sharedpreferences.getString(getString(R.string.sharedpreferences_contact_4), ""));
        sos5.setText(sharedpreferences.getString(getString(R.string.sharedpreferences_contact_5), ""));

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        save.setOnClickListener(v -> {

            boolean errorFlag = false;
            Log("SOSCountry", displayCountries.get(countrySos.getSelectedItem().toString()));
            countrySos.getSelectedItemPosition();

            String isoCountry = displayCountries.get(countrySos.getSelectedItem().toString());

            String[] sos = {sos1.getText().toString(),
                    sos2.getText().toString(),
                    sos3.getText().toString(),
                    sos4.getText().toString(),
                    sos5.getText().toString()};

            PhoneNumber[] phoneNumbers = new PhoneNumber[5];

            try {
                phoneNumbers[0] = phoneUtil.parse(sos[0], isoCountry);
            } catch (NumberParseException e) {
                e.printStackTrace();
                phoneNumbers[0] = null;
            }

            try {
                phoneNumbers[1] = phoneUtil.parse(sos[1], isoCountry);
            } catch (NumberParseException e) {
                e.printStackTrace();
                phoneNumbers[1] = null;
            }

            try {
                phoneNumbers[2] = phoneUtil.parse(sos[2], isoCountry);
            } catch (NumberParseException e) {
                e.printStackTrace();
                phoneNumbers[2] = null;
            }

            try {
                phoneNumbers[3] = phoneUtil.parse(sos[3], isoCountry);
            } catch (NumberParseException e) {
                e.printStackTrace();
                phoneNumbers[3] = null;
            }

            try {
                phoneNumbers[4] = phoneUtil.parse(sos[4], isoCountry);
            } catch (NumberParseException e) {
                e.printStackTrace();
                phoneNumbers[4] = null;
            }

            if ((phoneNumbers[0] != null && phoneUtil.isValidNumber(phoneNumbers[0])) || sos[0].isEmpty()) {

                if(phoneNumbers[0] != null) {
                    String formattedNumber = "+" + phoneNumbers[0].getCountryCode() + phoneNumbers[0].getNationalNumber();
                    sos1.setText(formattedNumber);
                }
                sharedpreferences.edit().putString(getString(R.string.sharedpreferences_contact_1), sos1.getText().toString()).commit();
            }
            else {
                sos1.setError("Please enter correct number");
                errorFlag = true;
            }

            if ((phoneNumbers[1] != null && phoneUtil.isValidNumber(phoneNumbers[1])) || sos[1].isEmpty()) {

                if(phoneNumbers[1] != null) {
                    String formattedNumber = "+" + phoneNumbers[1].getCountryCode() + phoneNumbers[1].getNationalNumber();
                    sos2.setText(formattedNumber);
                }
                sharedpreferences.edit().putString(getString(R.string.sharedpreferences_contact_2), sos2.getText().toString()).commit();
            }
            else {
                sos2.setError("Please enter correct number");
                errorFlag = true;
            }
            if ((phoneNumbers[2] != null && phoneUtil.isValidNumber(phoneNumbers[2])) || sos[2].isEmpty()) {

                if(phoneNumbers[2] != null) {
                    String formattedNumber = "+" + phoneNumbers[2].getCountryCode() + phoneNumbers[2].getNationalNumber();
                    sos3.setText(formattedNumber);
                }
                sharedpreferences.edit().putString(getString(R.string.sharedpreferences_contact_3), sos3.getText().toString()).commit();
            }
            else {
                sos3.setError("Please enter correct number");
                errorFlag = true;
            }
            if ((phoneNumbers[3] != null && phoneUtil.isValidNumber(phoneNumbers[3])) || sos[3].isEmpty()) {

                if(phoneNumbers[3] != null) {
                    String formattedNumber = "+" + phoneNumbers[3].getCountryCode() + phoneNumbers[3].getNationalNumber();
                    sos4.setText(formattedNumber);
                }
                sharedpreferences.edit().putString(getString(R.string.sharedpreferences_contact_4), sos4.getText().toString()).commit();
            }
            else {
                sos4.setError("Please enter correct number");
                errorFlag = true;
            }
            if ((phoneNumbers[4] != null && phoneUtil.isValidNumber(phoneNumbers[4])) || sos[4].isEmpty()) {

                if(phoneNumbers[4] != null) {
                    String formattedNumber = "+" + phoneNumbers[4].getCountryCode() + phoneNumbers[4].getNationalNumber();
                    sos5.setText(formattedNumber);
                }
                sharedpreferences.edit().putString(getString(R.string.sharedpreferences_contact_5), sos5.getText().toString()).commit();
            }
            else {
                sos5.setError("Please enter correct number");
                errorFlag = true;
            }

            if (!errorFlag) {
                sharedpreferences.edit().putInt(getString(R.string.sos_country), countrySos.getSelectedItemPosition()).commit();
                RealToast(this, "Contacts Saved");
            }
        });


        startService(new Intent(this, SOSLocationService.class));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1){
            boolean locationEnabled = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            if (!locationEnabled){
                RealToast(this, "This feature requires Location Permissions to work.");
                finish();
            }
        }else if(requestCode == 2){
            boolean smsEnabled = ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
            if (!smsEnabled){
                RealToast(this, "This feature requires SMS Permissions to work.");
                finish();
            }
        }
    }
}