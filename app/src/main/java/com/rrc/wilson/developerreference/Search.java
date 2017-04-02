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
        int lastMove = 0;
        boolean exactMatch = false;
        for(int i = 0; i < mLayout.getChildCount(); i++){
            View v = mLayout.getChildAt(i);
            String text = ((TextView)v).getText().toString().replaceAll("\\s|^\\(.*\\)", "").toUpperCase();
            if(text.contains(s)) {
                v.setVisibility(View.VISIBLE);
                if(text.equals(s)){
                    mLayout.removeView(v);
                    mLayout.addView(v, 0);
                    exactMatch = true;
                }else if (text.substring(0, s.length()).equals(s)) {
                    mLayout.removeView(v);
                    mLayout.addView(v, (exactMatch) ? 1 : 0);
                    lastMove++;
                }else if(text.length() == s.length() -1){
                    mLayout.removeView(v);
                    mLayout.addView(v, lastMove);
                }

            }else
                v.setVisibility(View.GONE);
        }
        return false;
    }
}
