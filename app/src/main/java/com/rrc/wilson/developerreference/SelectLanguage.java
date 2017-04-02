package com.rrc.wilson.developerreference;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class SelectLanguage extends AppCompatActivity {

    LinearLayout mLayout;
    ArrayList<LanguageDescription> languages;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);

        Intent intent = getIntent();
        dbHelper = new DatabaseHelper(this);
        if(intent.getBooleanExtra("allLanguages", false))
            languages = dbHelper.getLanguages();
        else
            languages = dbHelper.getSupportedLanguages();
        dbHelper = null;
        mLayout = (LinearLayout) findViewById(R.id.layout);

        if(intent.getIntExtra("source", 0) == R.id.searchOfficial) {
            generateCheckboxes();
        }else if(intent.getIntExtra("source", 0) == R.id.allLanguages){
            generateLabels();
        }
    }

    private void generateCheckboxes(){
        if(languages == null || languages.size() == 0)
            return;

        for(LanguageDescription lang : languages){
            CheckBox chkBox = new CheckBox(this);
            chkBox.setChecked(false);
            chkBox.setText(lang.getName());
            mLayout.addView(chkBox, mLayout.getChildCount() - 1);
        }
    }

    private void generateLabels(){
        if(languages == null || languages.size() == 0)
            return;

        for(LanguageDescription lang : languages){
            TextView textView = new TextView(this);
            textView.setText(lang.getName());
            textView.setGravity(Gravity.CENTER);
            mLayout.addView(textView, mLayout.getChildCount() - 1);
        }
    }
}
