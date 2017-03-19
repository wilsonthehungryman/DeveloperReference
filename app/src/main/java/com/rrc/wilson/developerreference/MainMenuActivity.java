package com.rrc.wilson.developerreference;

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
        View sView = findViewById(R.id.scrollView);
        int height = sView.getHeight();
        LinearLayout layout = (LinearLayout)findViewById(R.id.menuLayout);
        layout.getLayoutParams().height = height;
        layout.requestLayout();

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
        switch(v.getId()){
            case R.id.searchAll:
                break;
            case R.id.searchOfficial:
            case R.id.searchForum:
            case R.id.searchGuide:
            case R.id.searchCodeExamples:
            case R.id.compare:
                Toast.makeText(this, "Coming soon!", Toast.LENGTH_SHORT);
                break;
        }
    }
}
