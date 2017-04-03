package com.rrc.wilson.developerreference;

import android.content.CursorLoader;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Stack;

public class SearchWebActivity extends AppCompatActivity implements android.widget.SearchView.OnQueryTextListener{

    ArrayList<String> languages;
    ArrayList<ClassDescription> classes;
    LinearLayout mItems;
    ListView listView;
    SearchView search;
    int source;
    SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_web);
        Toolbar tbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(tbar);

        Intent intent = getIntent();
        languages = intent.getStringArrayListExtra("languages");
        source = intent.getIntExtra("source", -1);

        mItems = (LinearLayout)findViewById(R.id.items);
//        listView = (ListView)findViewById(R.id.items);
        search = (SearchView)findViewById(R.id.search);

//        Search searcher = new Search(mItems);
//        search.setOnQueryTextListener(searcher);

        search.setOnQueryTextListener(this);

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


    @Override
    public boolean onQueryTextSubmit(String s) {
        return onQueryTextChange(s);
    }

    @Override
    public boolean onQueryTextChange(String s) {
        Log.d("wilson", "Starting onQueryTextChange s:" + s);
        s = s.replaceAll("\\s", "").replaceAll("\\+", "\\+").toUpperCase();
        for(int i = 0; i < mItems.getChildCount() && s.length() > 2; i++){
            View v = mItems.getChildAt(i);
            if(!(s.length() == 0)) {
                //.replaceAll("\\s|^\\(.*\\)", "")
                String text = ((TextView) v).getText().toString().toUpperCase();
                if (text.contains(s)) {
                    v.setVisibility(View.VISIBLE);
                    if (text.equals(s)) {
                        mItems.removeView(v);
                        mItems.addView(v, 0);
                    }

                } else
                    v.setVisibility(View.GONE);
            }else
                v.setVisibility(View.VISIBLE);
        }
        Log.d("wilson", "Leaving onQueryTextChange s:" + s);

        return false;
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
            v.setText(String.format("%2$s (%1$s)", c.getLanguage(), c.getClassName()));
            v.setGravity(Gravity.CENTER);
            mItems.addView(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    specificClassSelected((TextView)v);
                }
            });
        }
    }

    private void specificClassSelected(TextView v){
        String name = v.getText().toString().replaceAll("\\s|\\(.*\\)$", "");
        String[] urls = null;
        for(ClassDescription c : classes){
            if(c.getClassName().equals(name)) {
                urls = c.getUrls();
                break;
            }
        }

        if(!(urls == null)){
            Intent intent = new Intent(this, StandardWebViewActivity.class);
            intent.putExtra("url", urls[0]);
            startActivity(intent);
        } // TODO no urls
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater m = getMenuInflater();
        m.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}
