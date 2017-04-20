package com.rrc.wilson.developerreference;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.HashSet;

public class SettingsActivity extends AppCompatActivity {

    final int REQUEST_CODE = 2;
    private Switch useBrowserSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        findViewById(R.id.forceRefresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScraperService();
            }
        });

        findViewById(R.id.selectDefaultLang).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDefaultLang();
            }
        });

        useBrowserSwitch = (Switch)findViewById(R.id.useBrowser);
        useBrowserSwitch.setChecked(getSharedPreferences("DeveloperReference", MODE_PRIVATE).getBoolean("useBrowser", false));
        useBrowserSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                useBrowserToggled(isChecked);
            }
        });
    }

    /**
     * Starts the class scraper service and forces all tables to refresh
     */
    private void startScraperService(){
        Intent intent = new Intent(this, ClassScraper.class);
        intent.putExtra("language", "ALL");
        intent.putExtra("langTable", true);
        intent.putExtra("langTableForce", true);
        intent.putExtra("classTableForce", true);
        intent.putExtra("cleanhouse", true);
        startService(intent);
    }

    void useBrowserToggled(boolean useBrowser){
        SharedPreferences.Editor prefs = getSharedPreferences("DeveloperReference", MODE_PRIVATE).edit();
        prefs.putBoolean("useBrowser", useBrowser);
        prefs.apply();
    }

    private void selectDefaultLang(){
        Intent intent = new Intent(this, SelectLanguage.class);
        intent.putExtra("source", R.id.selectDefaultLang);
        startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * Occurs when other activity finishes
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){
            ArrayList<String> languages = data.getStringArrayListExtra("languages");
            SharedPreferences.Editor prefs = getSharedPreferences("DeveloperReference", MODE_PRIVATE).edit();
            prefs.putStringSet("defaultLangs", new HashSet<String>(languages));
            if(languages.size() == 1) {
                prefs.putString("defaultLang", languages.get(0));
                prefs.apply();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("You have selected a single language,\ndo you want to skip the language selection screen from now on?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener)
                        .show();
            }else {
                prefs.putBoolean("skipLangSelect", false);
                prefs.apply();
            }
        }
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            SharedPreferences.Editor prefs = getSharedPreferences("DeveloperReference", MODE_PRIVATE).edit();
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    prefs.putBoolean("skipLangSelect", true);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    prefs.putBoolean("skipLangSelect", false);
                    break;
            }
            prefs.apply();
        }
    };

}
