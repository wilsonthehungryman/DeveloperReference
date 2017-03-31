package com.rrc.wilson.developerreference;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class SelectLanguage extends AppCompatActivity {

    LinearLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);
        mLayout = (LinearLayout)findViewById(R.id.layout);

    }

    private void generateCheckboxes(){
    }
}
