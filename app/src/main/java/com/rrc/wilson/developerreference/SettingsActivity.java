package com.rrc.wilson.developerreference;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {

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
    }

    private void startScraperService(){
        Intent intent = new Intent(this, ClassScraper.class);
        intent.putExtra("language", "ALL");
        intent.putExtra("langTable", true);
        intent.putExtra("langTableForce", true);
        intent.putExtra("classTableForce", true);
        startService(intent);
    }
}
