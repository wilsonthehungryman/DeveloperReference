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
 * ClassDescriptionAdapter is a custom ArrayAdapter for ListViews that use the ClassDescription custom ListView.
 *
 * Inherits from ArrayAdapter<ClassDescription>
 * Implements the Filterable interface
 *
 * <pre>
 * Created by Wilson on 2017-04-05.
 *
 * Revisions
 * Wilson       2017-04-05      Created
 * Wilson       2017-04-20      Finalized and commented
 * </pre>
 */
public class ClassDescriptionAdapter extends ArrayAdapter<ClassDescription> implements Filterable {
    private ArrayList<ClassDescription> classes, filteredOutClasses;
    private Context context;
    private Filter filter;

    /**
     * The constructor
     * @param context The context for the creation of the adapter
     * @param textViewResourceId The resource id (required by super constructor)
     * @param classes The arraylist of classes
     */
    public ClassDescriptionAdapter(Context context, int textViewResourceId, ArrayList<ClassDescription> classes){
        super(context, textViewResourceId, classes);
        this.classes = classes;
        this.context = context;
        this.filter = createFilter();
        this.filteredOutClasses = new ArrayList<>();
    }

    /**
     * GetView is responsible for inflating individual items
     * @param position Position of this item
     * @param v The view (existing or null)
     * @param parent The parent view
     * @return The inflated view
     */
    public View getView(int position, View v, ViewGroup parent){
        // If the view is null, inflate it
        if (v == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            v = inflater.inflate(R.layout.class_list_view, null);
        }

        // Get the specific class description
        ClassDescription classDescription = classes.get(position);

        // If the class description is not null, display the data in it
        if(classDescription != null){
            ((TextView)v.findViewById(R.id.listViewName)).setText(classDescription.getClassName());
            ((TextView)v.findViewById(R.id.listViewPackage)).setText(classDescription.getNameSpace());
            TextView lang = (TextView)v.findViewById(R.id.listViewLang);
            lang.setText(classDescription.getLanguage());
        }
        return v;
    }

    /**
     * GetFilter returns a filter, that can be used to filter this list
     * @return A filter object
     */
    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }

    /**
     * CreateFilter is what actually creates the filter
     * @return A new filter object
     */
    private Filter createFilter(){
        // return a new filter
        return new Filter() {
            byte lastLength = 0;

            /**
             * performFiltering is what is called to filter the list
             * This is done asynchronously
             * @param constraint The query to use to filter
             * @return A FilterResults object
             */
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();

                // If null, just return the empty FilterResults
                if(constraint == null)
                    return filterResults;

                ArrayList<ClassDescription> tempList = new ArrayList<>();
                ArrayList<ClassDescription> topMatches = new ArrayList<>();

                // If the new constraint is smaller than the last constraint,
                // readd all the classes (brings back previously filtered out results)
                if(lastLength > constraint.length()){
                    classes.addAll(filteredOutClasses);
                    filteredOutClasses.clear();
                }

                lastLength = (byte)constraint.length();

                // prep the constraint
                String c = constraint.toString().toUpperCase();

                // loop through the list of classes
                for(int i = 0; i < classes.size(); i++){
                    // prep the class name
                    String className = classes.get(i).getClassName().toUpperCase();

                    // perform the comparison
                    if(className.contains(c) || c.contains(className)) {
                        // if this class name starts with the query,
                        // put it into the top results (so they appear closer to the top)
                        if (className.startsWith(c))
                            topMatches.add(classes.get(i));
                        // Otherwise just add it normally
                        else
                            tempList.add(classes.get(i));
                     // if it failed the comparison, add it to the filtered out list
                    }else
                        filteredOutClasses.add(classes.get(i));
                }
                // prep the filter results
                topMatches.addAll(tempList);
                filterResults.values = topMatches;
                filterResults.count = tempList.size();

                return filterResults;
            }

            /**
             * publishResults is what will refresh the displayed list to only show the results
             * @param constraint The constraint used
             * @param results The FilterResults containing the results
             */
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                // if there are results,
                if(results.count > 0) {
                    // clear the member variable
                    classes.clear();
                    // add all the results
                    classes.addAll((ArrayList<ClassDescription>)results.values);
                    // and refresh the list
                    notifyDataSetChanged();
                }
                else
                    notifyDataSetInvalidated();
            }
        };
    }
}
