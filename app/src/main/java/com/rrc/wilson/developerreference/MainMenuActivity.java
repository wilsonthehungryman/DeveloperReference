package com.rrc.wilson.developerreference;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
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

        View sView = findViewById(R.id.scrollView);
        LinearLayout layout = (LinearLayout)findViewById(R.id.menuLayout);

        //iter
        for(int i = 0; i < layout.getChildCount(); i++){
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
        switch(v.getId()){
            case R.id.searchAll:
                break;
            case R.id.searchOfficial:
                intent.putExtra("source", R.id.searchOfficial);
                break;
            case R.id.searchForum:
            case R.id.searchGuide:
            case R.id.searchCodeExamples:
            case R.id.compare:
                Toast.makeText(this, "Coming soon!", Toast.LENGTH_SHORT);
                break;
        }
        startActivity(intent);
    }

    private void startClassScraper(){
        Intent intent = new Intent(this, ClassScraper.class);
        intent.putExtra("language", "ALL");
        intent.putExtra("langTable", true);
        startService(intent);
    }
}
