package com.rrc.wilson.developerreference;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SearchView;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SelectLanguage extends AppCompatActivity implements android.widget.SearchView.OnQueryTextListener {

    LinearLayout mLayout;
    ArrayList<LanguageDescription> languages;
    DatabaseHelper dbHelper;
    SearchView search;
    ListView listView;
    LanguageDescriptionAdapter adapter;
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

        mLayout = (LinearLayout) findViewById(R.id.layoutItems);
        search = (SearchView)findViewById(R.id.search);
        listView = (ListView)findViewById(R.id.listViewLangs);

        source = intent.getIntExtra("source", 0);

        if(source == R.id.searchOfficial) {
            generateCheckboxes();
        }else if(source == R.id.allLanguages){
            adapter = new LanguageDescriptionAdapter(this, listView.getId(), languages);
            listView.setAdapter(adapter);
            search.setOnQueryTextListener(this);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return onQueryTextChange(s);
    }

    @Override
    public boolean onQueryTextChange(String s) {
        Log.d("wilson", "Starting onQueryTextChange s:" + s);
        adapter.getFilter().filter(s);

        return true;
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
