package com.rrc.wilson.developerreference;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;

import java.util.ArrayList;

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

        populateClasses();
    }

    public void processCustomQuery(){
        // TODO custom search
    }


    private void populateClasses(){
        
    }
}
