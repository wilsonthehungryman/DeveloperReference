package com.rrc.wilson.developerreference;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
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
 * Created by Wilson on 2017-04-05.
 */

public class ClassDescriptionAdapter extends ArrayAdapter<ClassDescription> implements Filterable {
    private ArrayList<ClassDescription> classes, filteredOutClasses;
    private Context context;
    private Filter filter;

    public ClassDescriptionAdapter(Context context, int textViewResourceId, ArrayList<ClassDescription> classes){
        super(context, textViewResourceId, classes);
        this.classes = classes;
        this.context = context;
        this.filter = createFilter();
        this.filteredOutClasses = new ArrayList<>();
    }

    public View getView(int position, View v, ViewGroup parent){
        if (v == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            v = inflater.inflate(R.layout.class_list_view, null);
        }

        ClassDescription classDescription = classes.get(position);

        if(classDescription != null){
            ((TextView)v.findViewById(R.id.listViewName)).setText(classDescription.className);
            ((TextView)v.findViewById(R.id.listViewPackage)).setText(classDescription.nameSpace);
            TextView lang = (TextView)v.findViewById(R.id.listViewLang);
            lang.setText(classDescription.language);
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
                    classes.addAll(filteredOutClasses);
                    filteredOutClasses.clear();
                }

                lastLength = (byte)constraint.length();

                ArrayList<ClassDescription> tempList = new ArrayList<>();
                ArrayList<ClassDescription> topMatches = new ArrayList<>();

                String c = constraint.toString().toUpperCase();
                for(int i = 0; i < classes.size(); i++){
                    String className = classes.get(i).className.toUpperCase();
                    if(className.contains(c)) {
                        if (className.startsWith(c))
                            topMatches.add(classes.get(i));
                        else
                            tempList.add(classes.get(i));
                    }else
                        filteredOutClasses.add(classes.get(i));
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
                    classes.clear();
                    classes.addAll((ArrayList<ClassDescription>)results.values);
                    notifyDataSetChanged();
                }
                else
                    notifyDataSetInvalidated();
            }
        };


    }
}
