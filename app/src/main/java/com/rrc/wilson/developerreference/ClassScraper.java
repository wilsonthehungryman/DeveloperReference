package com.rrc.wilson.developerreference;

import android.app.IntentService;
import android.content.Intent;
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

        DatabaseHelper dbHelper = new DatabaseHelper(this);

        boolean updateLangTable = intent.getBooleanExtra("langTable", false);
        String language = intent.getStringExtra("language");

        if(updateLangTable){
            Stack<LanguageDescription> languages = languageScraper();
            dbHelper.insertLanguages(languages);
        }

        Stack<ClassDescription> classes = new Stack<>();
        switch(language){
            case "JAVA":
                if(dbHelper.needsUpdate(language))
                    classes.addAll(javaScraper());
                break;
            case "ALL":
                //if(dbHelper.needsUpdate("JAVA"))
                    classes.addAll(javaScraper());
                break;
            default:
                return;
        }

        dbHelper.insertClasses(classes);
    }

    Stack<LanguageDescription> languageScraper(){
        Log.d("wilson", "In languageScraper");
        Stack<LanguageDescription> languages = new Stack<>();

        try {
            Document doc = Jsoup.connect("https://en.wikipedia.org/wiki/List_of_programming_languages").get();
            Log.d("wilson", "got doc");
            Elements divs = doc.select("div.div-col.columns.column-count");
            for(Element div : divs){
                Elements links = div.getElementsByTag("a");
                for(Element link : links){
                    languages.push(new LanguageDescription(link.data()));
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
            Log.d("wilson", "got doc");
            Elements links = doc.getElementsByTag("a");
            Log.d("wilson", "got links: " + links.size());

            for(Element link : links){
                String[] packageNames = link.attr("href").split("/|\\.");

                StringBuilder packageName = new StringBuilder();
                for(int i = 0; i < packageNames.length - 2; i++){
                    packageName.append(packageNames[i]);
                    packageName.append('.');
                }

                JavaClassDescription javaClassDescription = new JavaClassDescription(packageNames[packageNames.length - 2], packageName.deleteCharAt(packageName.length() - 1).toString());
//                Log.d("wilson", packageNames[packageNames.length - 2] + ", " + packageName.deleteCharAt(packageName.length() - 1).toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }

        return classes;
    }
}
