package com.rrc.wilson.developerreference;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        startClassScraper();

        Toolbar tbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(tbar);

        View sView = findViewById(R.id.scrollView);
        LinearLayout layout = (LinearLayout)findViewById(R.id.menuLayout);

        //iter
        for(int i = 1; i < layout.getChildCount(); i++){
            TextView textView = (TextView)layout.getChildAt(i);
            textView.setGravity(Gravity.CENTER);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processClick(v);
                }
            });
        }
    }

    private void processClick(View v){
        Intent intent = new Intent(this, SelectLanguage.class);
        boolean startNextActivity = false;
        switch(v.getId()){
            case R.id.searchAll:
                break;
            case R.id.searchOfficial:
                intent.putExtra("source", R.id.searchOfficial);
                startNextActivity = true;
                break;
            case R.id.allLanguages:
                intent.putExtra("source", R.id.allLanguages);
                intent.putExtra("allLanguages", true);
                startNextActivity = true;
                break;
            case R.id.searchForum:
            case R.id.searchGuide:
            case R.id.searchCodeExamples:
            case R.id.compare:
                Toast.makeText(this, "Coming soon!", Toast.LENGTH_SHORT);
                break;
        }
        if(startNextActivity)
            startActivity(intent);
    }

    private void startClassScraper(){
        Intent intent = new Intent(this, ClassScraper.class);
        intent.putExtra("language", "ALL");
        intent.putExtra("langTable", true);
        startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater m = getMenuInflater();
        m.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}
