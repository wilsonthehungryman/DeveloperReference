package com.rrc.wilson.developerreference;

import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

/**
 * Created by Wilson on 2017-04-02.
 */

public class Search implements SearchView.OnQueryTextListener {
    private ViewGroup mLayout;

    public Search(ViewGroup group){
        mLayout = group;
    }
    @Override
    public boolean onQueryTextSubmit(String s) {
        return onQueryTextChange(s);
    }

    @Override
    public boolean onQueryTextChange(String s) {
        s = s.replaceAll("\\s", "").replaceAll("\\+", "\\+").toUpperCase();
        for(int i = 0; i < mLayout.getChildCount(); i++){
            View v = mLayout.getChildAt(i);
            if(((TextView)v).getText().toString().replaceAll("\\s", "").toUpperCase().contains(s))
                v.setVisibility(View.VISIBLE);
            else
                v.setVisibility(View.GONE);
        }
        return false;
    }
}
