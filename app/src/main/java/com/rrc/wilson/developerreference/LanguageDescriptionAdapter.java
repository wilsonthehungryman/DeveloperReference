package com.rrc.wilson.developerreference;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Wilson on 2017-04-06.
 */

public class LanguageDescriptionAdapter extends ArrayAdapter<LanguageDescription> implements Filterable {
    private ArrayList<LanguageDescription> languages, filteredOutLanguages;
    private Context context;
    private Filter filter;

    public LanguageDescriptionAdapter(Context context, int textViewResourceId, ArrayList<LanguageDescription> languages){
        super(context, textViewResourceId, languages);
        this.languages = languages;
        this.context = context;
        this.filter = createFilter();
        this.filteredOutLanguages = new ArrayList<>();
    }

    public View getView(int position, View v, ViewGroup parent){
        if (v == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            v = inflater.inflate(R.layout.language_list_view, null);
        }

        LanguageDescription languageDescription = languages.get(position);

        if(languageDescription != null){
            TextView lang = (TextView)v.findViewById(R.id.listViewLang);
            lang.setText(languageDescription.getName());
            
            if(languageDescription.supported())
                lang.setTextColor(Color.GREEN);
        }
        return v;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter createFilter(){
        return new Filter() {
            byte lastLength = 0;
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();

                if(constraint == null)
                    return filterResults;

                if(lastLength > constraint.length()){
                    languages.addAll(filteredOutLanguages);
                    filteredOutLanguages.clear();
                }

                lastLength = (byte)constraint.length();

                ArrayList<LanguageDescription> tempList = new ArrayList<>();
                ArrayList<LanguageDescription> topMatches = new ArrayList<>();

                String c = constraint.toString().toUpperCase();
                for(int i = 0; i < languages.size(); i++){
                    String lang = languages.get(i).getName().toUpperCase();
                    if(lang.contains(c) || c.contains(lang)) {
                        if (lang.startsWith(c))
                            topMatches.add(languages.get(i));
                        else
                            tempList.add(languages.get(i));
                    }else
                        filteredOutLanguages.add(languages.get(i));
                }
                topMatches.addAll(tempList);
                filterResults.values = topMatches;
                filterResults.count = tempList.size();

                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if(results.count > 0) {
                    languages.clear();
                    languages.addAll((ArrayList<LanguageDescription>)results.values);
                    notifyDataSetChanged();
                }
                else
                    notifyDataSetInvalidated();
            }
        };
    }
}
