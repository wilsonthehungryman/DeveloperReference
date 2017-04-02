package com.rrc.wilson.developerreference;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Stack;

public class SearchWebActivity extends AppCompatActivity {

    ArrayList<String> languages;
    ArrayList<ClassDescription> classes;
    LinearLayout mItems;
    SearchView search;
    int source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_web);

        Intent intent = getIntent();
        languages = intent.getStringArrayListExtra("languages");
        source = intent.getIntExtra("source", -1);

        mItems = (LinearLayout)findViewById(R.id.items);
        search = (SearchView)findViewById(R.id.search);

        Search searcher = new Search(mItems);
        search.setOnQueryTextListener(searcher);

        findViewById(R.id.customGo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processCustomQuery();
            }
        });

        if(populateClasses())
            generateTextViews();
    }

    public void processCustomQuery(){
        // TODO custom search
    }


    private boolean populateClasses(){
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        classes = new ArrayList<>();
        try {
            for(String lang : languages){
                classes.addAll(dbHelper.get(lang));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void generateTextViews(){
        for(ClassDescription c : classes){
            TextView v = new TextView(this);
            v.setText(String.format("(%1$s) %2$s", c.getLanguage(), c.getClassName()));
            v.setGravity(Gravity.CENTER);
            mItems.addView(v);
        }
    }
}
