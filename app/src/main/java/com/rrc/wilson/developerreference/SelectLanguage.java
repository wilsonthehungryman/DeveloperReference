package com.rrc.wilson.developerreference;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class SelectLanguage extends AppCompatActivity {

    LinearLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);
        mLayout = (LinearLayout)findViewById(R.id.layout);
        generateCheckboxes();
    }

    private void generateCheckboxes(){
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        ArrayList<String> languages = dbHelper.getLanguages();
        if(languages == null || languages.size() == 0)
            return;

        for(String lang : languages){
            CheckBox chkBox = new CheckBox(this);
            chkBox.setChecked(false);
            chkBox.setText(lang);
            mLayout.addView(chkBox, mLayout.getChildCount() - 1);
        }
    }
}
