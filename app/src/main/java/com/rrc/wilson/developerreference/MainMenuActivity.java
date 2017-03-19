package com.rrc.wilson.developerreference;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

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

    }
}
