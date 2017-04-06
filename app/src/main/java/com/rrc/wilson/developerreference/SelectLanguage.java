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

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SelectLanguage extends AppCompatActivity {

    LinearLayout mLayout;
    ArrayList<LanguageDescription> languages;
    DatabaseHelper dbHelper;
    SearchView search;
    int source;

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

        source = intent.getIntExtra("source", 0);

        if(source == R.id.searchOfficial) {
            generateCheckboxes();
        }else if(source == R.id.allLanguages){
            generateLabels();
        }
    }

    private void generateCheckboxes(){
        if(languages == null || languages.size() == 0)
            // TODO start service, with prompt
            return;

        findViewById(R.id.go).setVisibility(View.VISIBLE);

        findViewById(R.id.tvSelectLanguage).setVisibility(View.VISIBLE);

        findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processGo();
            }
        });

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

    private void processGo(){
        ArrayList<String> languages = getSelectedLanguages();
        if(languages.size() == 0)
            // TODO no language selected logic
            return;

        Intent intent = null;
        switch(source){
            case R.id.searchOfficial:
                intent = new Intent(this, SearchWebActivity.class);
                break;
        }

        if(intent == null)
            return;

        intent.putExtra("source", source);
        intent.putExtra("languages", languages);
        startActivity(intent);
    }

    private ArrayList<String> getSelectedLanguages(){
        ArrayList<String> languages = new ArrayList<>();
        for(int i = 0; i < mLayout.getChildCount(); i++){
            CheckBox c = (CheckBox)mLayout.getChildAt(i);
            if(c.isChecked())
                languages.add(c.getText().toString());
        }
        return languages;
    }
}
