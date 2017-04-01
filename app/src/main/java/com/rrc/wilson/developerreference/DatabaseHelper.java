package com.rrc.wilson.developerreference;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

/**
 * Created by Wilson on 2017-03-25.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 3;

    private static final String DB_NAME = "LanguageInfo";
    private static final String CLASS_TABLE = "Classes";

    private static final String LANG_TABLE = "Languages";
    private static final String COL_LANG = "language";
    private static final String COL_UPDATED = "updated";
    private static final String COL_SUPPORTED = "supported";

    private static final String COL_DOMAINS = "domains";
    private static final String COL_LANG_FK = "language_id";
    private static final String COL_NAME = "class";
    private static final String COL_PACKAGE = "package";

    private static final String COL_URL = "url";

    private static final String LANG_TABLE_CREATE = String.format(
            "CREATE TABLE IF NOT EXISTS %1$s (" +
                "_id INTEGER PRIMARY KEY, " +
                "%2$s TEXT NOT NULL, " +
                "%3$s INTEGER NOT NULL, " +
                "%4$s TEXT, " +
                "%5$s INTEGER)",
            LANG_TABLE, COL_LANG, COL_SUPPORTED, COL_DOMAINS, COL_UPDATED);

    private static final String CLASS_TABLE_CREATE = String.format(
            "CREATE TABLE IF NOT EXISTS %1$s (" +
                "_id INTEGER PRIMARY KEY, " +
                "%2$s INTEGER NOT NULL, " +
                "%3$s TEXT NOT NULL, " +
                "%4$s TEXT, " +
                "%5$s TEXT, " +
                "UNIQUE (%2$s, %3$s) ON CONFLICT REPLACE," +
                "FOREIGN KEY(%2$s) REFERENCES %6$s(_id))",
                CLASS_TABLE, COL_LANG_FK, COL_NAME, COL_PACKAGE, COL_URL, LANG_TABLE);

    private static final String DROP_TABLE = String.format(
            "DROP TABLE IF EXISTS %1$s;" +
            "DROP TABLE IF EXISTS %2$s;",
            LANG_TABLE, CLASS_TABLE);

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CLASS_TABLE_CREATE);
        db.execSQL(LANG_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    private void performClassInsert(SQLiteDatabase db, String language, String className, String packageName, String url){
        ContentValues content = new ContentValues();

        content.put(COL_LANG_FK, getLangId(language));
        content.put(COL_NAME, className);
        content.put(COL_PACKAGE, packageName);
        content.put(COL_URL, url);

        db.insert(CLASS_TABLE, null, content);
    }

    public void insertClass(String language, String className){
        insertClass(language, className, null, null);
    }

    public void insertClass(String language, String className, String packageName, String url){
        SQLiteDatabase db = this.getWritableDatabase();

        performClassInsert(db, language, className, packageName, url);

        db.close();
    }

    public void insertClasses(AbstractList<ClassDescription> values){
        Iterator<ClassDescription> iter = values.iterator();
        boolean java = false;
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            while(iter.hasNext()){
                ClassDescription classDescription = iter.next();
                String lang = classDescription.getLanguage();
                ContentValues content = new ContentValues();

                content.put(COL_LANG_FK, getLangId(lang));
                content.put(COL_NAME, classDescription.getClassName());
                content.put(COL_URL, formatUrls(classDescription.getUrls()));
                switch (lang){
                    case "JAVA":
                        content.put(COL_PACKAGE, ((JavaClassDescription)classDescription).getPackageName());
                        java = true;
                        break;
                }
                db.insert(CLASS_TABLE, null, content);
            }
            if(java){
                ContentValues vals = new ContentValues();
                vals.put(COL_UPDATED, System.currentTimeMillis());
                vals.put(COL_SUPPORTED, 1);
                vals.put(COL_DOMAINS, "docs.oracle.com:");
                db.update(LANG_TABLE, vals, COL_LANG + " = ?", new String[]{"JAVA"});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public Stack<ClassDescription> get(String lang){
        Stack<ClassDescription> classes = new Stack<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String[] cols;
        if(lang.equals("JAVA")){
            cols = new String[]{ COL_LANG_FK, COL_NAME, COL_URL, COL_PACKAGE };
        }else{
            cols = new String[]{ COL_LANG_FK, COL_NAME, COL_URL };
        }
        String[] selectionParams = { COL_LANG_FK, String.valueOf(getLangId(lang)) };
        Cursor c = db.query(false, CLASS_TABLE, cols, "WHERE ? = ?", selectionParams, null, null, COL_NAME, null);

        if(!c.moveToFirst())
            return null;

        int col_name = c.getColumnIndex(COL_NAME);
        int col_urls = c.getColumnIndex(COL_URL);
        if(lang.equals("JAVA")){
            int col_package = c.getColumnIndex(COL_PACKAGE);
            do{
                classes.push(new JavaClassDescription(c.getString(col_name), c.getString(col_package), c.getString(col_urls).split(":")));
            }while(c.moveToNext());
        }else{
            return null;
        }

        return classes;
    }

    public boolean needsUpdate(String lang){
        SQLiteDatabase db = getReadableDatabase();
        boolean flag = true;
        Cursor c = db.rawQuery("SELECT " + COL_UPDATED + " FROM " + LANG_TABLE + " WHERE " + COL_LANG + " = '" + lang.toUpperCase() + "'", null);
        if(c.moveToFirst()){
            Long updated = c.getLong(0);
            if(updated == null){
                flag = true;

            }
            Long today = System.currentTimeMillis();
            Long difference = today - updated;
            if(!(difference / 1000 > 60 * 24 * 14))
                flag = false;
        }else
            flag = true;

        db.close();

        db = getWritableDatabase();
        ContentValues val = new ContentValues();
        val.put(COL_LANG, lang.toUpperCase());
        db.insert(LANG_TABLE, null, val);
        db.close();

        return flag;
    }

    public int getLangId(String lang){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT _id FROM " + LANG_TABLE + " WHERE " + COL_LANG + " = " + lang, null);
        if(!c.moveToFirst())
            return -1;
        return c.getInt(c.getInt(0));
    }

    public HashMap<String, Integer> getLangIds(String[] langs){
        SQLiteDatabase db = getReadableDatabase();
        HashMap<String, Integer> results = new HashMap<>();
        String query = String.format(
                "SELECT %1$s, %2$s " +
                "FROM %3$s " +
                "%4$s",
                COL_LANG, "_id", LANG_TABLE,
                (langs == null) ? "" : generateInClause(langs, COL_LANG));
        Cursor c = db.rawQuery(query, null);

        if(!c.moveToFirst())
            return null;

        int id = c.getColumnIndex("_id");
        int lang = c.getColumnIndex(COL_LANG);
        do{
            results.put(c.getString(lang), c.getInt(id));
        }while(c.moveToNext());
        c.close();

        return results;
    }

    public ArrayList<String> getLanguages(){
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> languages = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT " + COL_LANG + " FROM " + LANG_TABLE, null);
        if(!c.moveToFirst()){
            c.close();
            return null;
        }
        do{
            languages.add(c.getString(0));
        }while(c.moveToNext());
        c.close();
        return languages;
    }

    public HashMap<String, Integer> getLangIds(){
        return getLangIds(null);
    }

    public void insertLanguages(AbstractList<LanguageDescription> langs){
        ArrayList<String> tableLangs = getLanguages();

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        try {
            for(LanguageDescription lang : langs){
                if(!tableLangs.contains(lang.getName())){
                    ContentValues vals = new ContentValues();
                    vals.put(COL_LANG, lang.getName());
                    db.insert(LANG_TABLE, null, vals);
                }
            }
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    private String generateInClause(String[] items, String col){
        StringBuilder builder = new StringBuilder("WHERE " + col + " IN (");
        for (int i = 0; i < items.length; i++) {
            builder.append("'" + items[i] + "'");
            if(i < items.length - 1)
                builder.append(", ");
        }
        builder.append(")");

        return builder.toString();
    }

    private String formatUrls(String[] urls){
        StringBuilder s = new StringBuilder();
        for(String url : urls) {
            s.append(url);
            s.append(":");
        }
        return s.toString();
    }
}
