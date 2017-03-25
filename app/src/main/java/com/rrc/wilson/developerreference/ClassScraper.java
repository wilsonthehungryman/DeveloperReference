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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

/**
 * Created by Wilson on 2017-03-20.
 */

public class ClassScraper extends IntentService {
    public ClassScraper(){ super("ClassScraper"); }
    //public ClassScraper(String name) {
//        super(name);
//    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // TODO handle intent and ?start service
        if (intent == null)
                return;

        String language = intent.getStringExtra("language");
        Stack<ClassDescription> classes = new Stack();
        switch(language){
            case "JAVA":
                classes.addAll(javaScraper());
            default:
                break;
        }
        int count = classes.size();
    }

    Stack<JavaClassDescription> javaScraper(){
        Log.d("wilson", "In java scraper");
        Stack<JavaClassDescription> classes;
        classes = new Stack<>();

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
                Log.d("wilson", packageNames[packageNames.length - 2] + ", " + packageName.deleteCharAt(packageName.length() - 1).toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }

        return classes;
    }
}
