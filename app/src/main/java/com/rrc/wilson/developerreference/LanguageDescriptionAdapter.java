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
 * LanguageDescriptionAdapter is a custom ArrayAdapter for ListViews that use the ClassDescription custom ListView.
 *
 * Inherits from ArrayAdapter<ClassDescription>
 * Implements the Filterable interface
 *
 * <pre>
 * Created by Wilson on 2017-04-06.
 *
 * Revisions
 * Wilson       2017-04-06      Created
 * Wilson       2017-04-20      Finalized and commented
 * </pre>
 */
public class LanguageDescriptionAdapter extends ArrayAdapter<LanguageDescription> implements Filterable {
    private ArrayList<LanguageDescription> languages, filteredOutLanguages;
    private Context context;
    private Filter filter;

    /**
     * The constructor
     * @param context The context for the creation of the adapter
     * @param textViewResourceId The resource id (required by super constructor)
     * @param languages The arraylist of languages
     */
    public LanguageDescriptionAdapter(Context context, int textViewResourceId, ArrayList<LanguageDescription> languages){
        super(context, textViewResourceId, languages);
        this.languages = languages;
        this.context = context;
        this.filter = createFilter();
        this.filteredOutLanguages = new ArrayList<>();
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
            v = inflater.inflate(R.layout.language_list_view, null);
        }

        // Get the specific LanguageDescription
        LanguageDescription languageDescription = languages.get(position);

        // If the language description is not null, display the data in it
        if(languageDescription != null){
            TextView lang = (TextView)v.findViewById(R.id.listViewLang);
            lang.setText(languageDescription.getName());

            if(languageDescription.supported())
                lang.setTextColor(Color.GREEN);
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

                ArrayList<LanguageDescription> tempList = new ArrayList<>();
                ArrayList<LanguageDescription> topMatches = new ArrayList<>();

                // If the new constraint is smaller than the last constraint,
                // readd all the classes (brings back previously filtered out results)
                if(lastLength > constraint.length()){
                    languages.addAll(filteredOutLanguages);
                    filteredOutLanguages.clear();
                }

                lastLength = (byte)constraint.length();

                // prep the constraint
                String c = constraint.toString().toUpperCase();

                // loop through the list of languages
                for(int i = 0; i < languages.size(); i++){
                    // prep the class name
                    String lang = languages.get(i).getName().toUpperCase();

                    // perform the comparison
                    if(lang.contains(c) || c.contains(lang)) {
                        // if this language name starts with the query,
                        // put it into the top results (so they appear closer to the top)
                        if (lang.startsWith(c))
                            topMatches.add(languages.get(i));
                        else
                            // Otherwise just add it normally
                            tempList.add(languages.get(i));
                     // if it failed the comparison, add it to the filtered out list
                    }else
                        filteredOutLanguages.add(languages.get(i));
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
                    languages.clear();
                    // add all the results
                    languages.addAll((ArrayList<LanguageDescription>)results.values);
                    // and refresh the list
                    notifyDataSetChanged();
                }
                else
                    notifyDataSetInvalidated();
            }
        };
    }
}
