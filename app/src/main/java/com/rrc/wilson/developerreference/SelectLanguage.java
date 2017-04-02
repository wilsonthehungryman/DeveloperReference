package com.rrc.wilson.developerreference;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SearchView;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class SelectLanguage extends AppCompatActivity implements SearchView.OnQueryTextListener {

    LinearLayout mLayout;
    ArrayList<LanguageDescription> languages;
    DatabaseHelper dbHelper;
    SearchView search;

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

        mLayout = (LinearLayout) findViewById(R.id.items);
        search = (SearchView)findViewById(R.id.search);

        try {
            search.setOnQueryTextListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
            mLayout.addView(chkBox);
        }
    }

    private void generateLabels(){
        if(languages == null || languages.size() == 0)
            return;

        for(LanguageDescription lang : languages){
            TextView textView = new TextView(this);
            textView.setText(lang.getName());
            textView.setGravity(Gravity.CENTER);
            mLayout.addView(textView);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return onQueryTextChange(s);
    }

    @Override
    public boolean onQueryTextChange(String s) {
        s = s.replaceAll("\\s", "").replaceAll("\\+", "\\+").toUpperCase();
        for(int i = 0; i < mLayout.getChildCount(); i++){
            View v = mLayout.getChildAt(i);
            if(((TextView)v).getText().toString().replaceAll("\\s", "").toUpperCase().contains(s))
                v.setVisibility(View.VISIBLE);
            else
                v.setVisibility(View.GONE);
        }
        return false;
    }
}
