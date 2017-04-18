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
 * Created by Wilson on 2017-03-20.
 */

public class ClassScraper extends IntentService {
    public ClassScraper(){ super("ClassScraper"); }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null)
                return;

        DatabaseHelper dbHelper = null;
        Stack<ClassDescription> classes = null;
        try {
            dbHelper = new DatabaseHelper(this);

            if(intent.getBooleanExtra("cleanHouse", false))
                dbHelper.cleanHouse();

            String language = intent.getStringExtra("language");
            boolean updateLangTable = intent.getBooleanExtra("langTable", false);// || language.equals("ALL");

            if(updateLangTable){
                SharedPreferences prefs = getSharedPreferences("DeveloperReference", MODE_PRIVATE);
                Long lastUpdate = prefs.getLong("languageLastUpdate", 0);
                if(intent.getBooleanExtra("langTableForce", false) || TimeManager.updateLanguage(lastUpdate, System.currentTimeMillis())) {
                    Stack<LanguageDescription> languages = languageScraper();
                    if(dbHelper.insertLanguages(languages)){
                        SharedPreferences.Editor e = prefs.edit();
                        e.putLong("languageLastUpdate", System.currentTimeMillis());
                        e.apply();
                    }

                }

            }

            classes = new Stack<>();
            boolean force = intent.getBooleanExtra("classTableForce", false);
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

            //rubyScraper();

            dbHelper.insertClasses(classes);

        } catch (IllegalStateException e) {
            intent.putExtra("langTableForce", true);
            intent.putExtra("classTableForce", true);
            intent.putExtra("cleanHouse", true);
            startService(intent);
        }

    }

    Stack<LanguageDescription> languageScraper(){
        Log.d("wilson", "In languageScraper");
        Stack<LanguageDescription> languages = new Stack<>();

        try {
            Document doc = Jsoup.connect("https://en.wikipedia.org/wiki/List_of_programming_languages").get();
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

    Stack<JavaClassDescription> javaScraper(){
        Log.d("wilson", "In java scraper");
        Stack<JavaClassDescription> classes = new Stack<>();

        try {
            Document doc = Jsoup.connect("http://docs.oracle.com/javase/7/docs/api/allclasses-noframe.html").get();
            Elements links = doc.getElementsByTag("a");

            for(Element link : links){
                String partialPath = link.attr("href");
                String[] packageNames = partialPath.split("/|\\.");

                StringBuilder packageName = new StringBuilder();
                for(int i = 0; i < packageNames.length - 2; i++){
                    packageName.append(packageNames[i]);
                    packageName.append('.');
                }

                classes.push(new JavaClassDescription(packageNames[packageNames.length - 2], packageName.deleteCharAt(packageName.length() - 1).toString(), generateJavaUrls(partialPath)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }

        return classes;
    }

    private String[] generateJavaUrls(String partialPath){
        return new String[]{ "http://docs.oracle.com/javase/7/docs/api/" + partialPath };
    }

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
