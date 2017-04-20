package com.rrc.wilson.developerreference;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Stack;

/**
 * Class scraper is an intent service.
 * It is responsible for populating the database from the web.
 *
 * <pre>
 * Created by Wilson on 2017-03-20.
 *
 * Revisions
 * Wilson       2017-03-20      Created
 * Wilson       2017-04-20      Finalized language and java class scrapers
 * </pre>
 */
public class ClassScraper extends IntentService {
    public ClassScraper(){ super("ClassScraper"); }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Responsible for updating all the tables
     * Pulls data from the web
     * @param intent
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // if the intent is null just exit
        if (intent == null)
                return;

        DatabaseHelper dbHelper = null;
        Stack<ClassDescription> classes = null;
        String language;
        boolean updateLangTable, force;

        try {
            dbHelper = new DatabaseHelper(this);

            // if the intent indicates to wipe the tables,
            if(intent.getBooleanExtra("cleanHouse", false))
                // clean house
                dbHelper.cleanHouse();

            // get the extras from the intent
            language = intent.getStringExtra("language");
            updateLangTable = intent.getBooleanExtra("langTable", false);// || language.equals("ALL");

            // if the lang table should be updated,
            if(updateLangTable){
                // grab the last date updated from sharedprefrences
                SharedPreferences prefs = getSharedPreferences("DeveloperReference", MODE_PRIVATE);
                Long lastUpdate = prefs.getLong("languageLastUpdate", 0);

                // If enough time has passed, or it should be forced
                if(intent.getBooleanExtra("langTableForce", false) || TimeManager.updateLanguage(lastUpdate, System.currentTimeMillis())) {
                    // scrape the list of languages from the web
                    Stack<LanguageDescription> languages = languageScraper();
                    // then insert the languaes, if succesfull,
                    if(dbHelper.insertLanguages(languages)){
                        // update the last update date
                        SharedPreferences.Editor e = prefs.edit();
                        e.putLong("languageLastUpdate", System.currentTimeMillis());
                        e.apply();
                    }

                }

            }

            // setup for pulling classes
            classes = new Stack<>();
            force = intent.getBooleanExtra("classTableForce", false);

            // This switch will be further used later when there are more languages
            // execute the specific scraper based on the language(s)
            switch(language){
                case "JAVA":
                    if(dbHelper.needsUpdate(language))
                        classes.addAll(javaScraper());
                    break;
                case "ALL":
                    if(force || dbHelper.needsUpdate("JAVA"))
                        classes.addAll(javaScraper());
                    break;
                default:
                    return;
            }

            // try and insert the classes,
            // if the language table is empty this will throw an illegal state exception
            dbHelper.insertClasses(classes);

        } catch (IllegalStateException e) {
            // If the exception is thrown,
            // try again but be sure to clean house and update the lang table
            intent.putExtra("langTableForce", true);
            intent.putExtra("classTableForce", true);
            intent.putExtra("cleanHouse", true);
            startService(intent);
        }

    }

    /**
     * language scraper will pull a wikipedia page and parse the list of languages from it
     * @return A stack of LanguageDescriptions
     */
    Stack<LanguageDescription> languageScraper(){
        // setup up the stack
        Stack<LanguageDescription> languages = new Stack<>();

        try {
            // get the page
            Document doc = Jsoup.connect("https://en.wikipedia.org/wiki/List_of_programming_languages").get();

            // parse the data from the document
            // the target are all the a tags inside of the divs matching the below css classes
            Elements divs = doc.select("div.div-col.columns.column-count");
            for(Element div : divs){
                Elements links = div.getElementsByTag("a");
                for(Element link : links){
                    if(link.text() != null)
                        languages.push(new LanguageDescription(link.text()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }

        return languages;
    }

    /**
     * java scraper pulls a list of all java classes from oracle's official site
     * @return A stack of JavaClassDescriptions
     */
    Stack<JavaClassDescription> javaScraper(){
        // setup stack
        Stack<JavaClassDescription> classes = new Stack<>();

        try {
            // get the page
            Document doc = Jsoup.connect("http://docs.oracle.com/javase/7/docs/api/allclasses-noframe.html").get();

            // target is simply all the a tags
            Elements links = doc.getElementsByTag("a");

            for(Element link : links){
                // Pull the required data from the link
                String partialPath = link.attr("href");
                String[] packageNames = partialPath.split("/|\\.");

                StringBuilder packageName = new StringBuilder();
                for(int i = 0; i < packageNames.length - 2; i++){
                    packageName.append(packageNames[i]);
                    packageName.append('.');
                }

                // Then add a description to the stack from the pulled data
                classes.push(new JavaClassDescription(packageNames[packageNames.length - 2], packageName.deleteCharAt(packageName.length() - 1).toString(), generateJavaUrls(partialPath)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }

        return classes;
    }

    /**
     * utility function for formatting the java url to the corresponding link
     * @param partialPath The package/class name(s)
     * @return A String array of urls (currently just one)
     */
    private String[] generateJavaUrls(String partialPath){
        // return the array, may have additional urls added later
        return new String[]{ "http://docs.oracle.com/javase/7/docs/api/" + partialPath };
    }

    // Not fully implemented,
    // needs a headless browser
    Stack<RubyClassDescription> rubyScraper(){
        Log.d("wilson", "In ruby scraper");
        Stack<RubyClassDescription> classes = new Stack<>();

        try {
            Document doc = Jsoup.connect("http://docs.rubydocs.org/ruby-2-3-4/panel/index.html").followRedirects(true).get();
            Elements tree = doc.getElementsByClass("tree");
            Elements listItems = doc.getElementsByTag("li");

            for(Element listItem : listItems){
                Element div = listItem.getElementsByClass("content").first();
                Element a = div.getElementsByTag("a").first();
                Element i = a.getElementsByTag("i").first();
                String className = a.data();
                String url = a.attr("href");
                String inheritance = i.data();
//                RubyClassDescription rb = new RubyClassDescription()
            }
        } catch (IOException e) {
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
//        return classes;
    }
}
