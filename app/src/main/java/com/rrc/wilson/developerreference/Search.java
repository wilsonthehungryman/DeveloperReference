package com.rrc.wilson.developerreference;

import android.util.Log;
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
        Log.d("wilson", "Starting onQueryTextChange s:" + s);
        s = s.replaceAll("\\s", "").replaceAll("\\+", "\\+").toUpperCase();
        for(int i = 0; i < mLayout.getChildCount() && s.length() > 2; i++){
            View v = mLayout.getChildAt(i);
            if(!(s.length() == 0)) {
                //.replaceAll("\\s|^\\(.*\\)", "")
                String text = ((TextView) v).getText().toString().toUpperCase();
                if (text.contains(s)) {
                    v.setVisibility(View.VISIBLE);
                    if (text.equals(s)) {
                        mLayout.removeView(v);
                        mLayout.addView(v, 0);
                    }

                } else
                    v.setVisibility(View.GONE);
            }else
                v.setVisibility(View.VISIBLE);
        }
        Log.d("wilson", "Leaving onQueryTextChange s:" + s);

        return false;
    }
}
