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

    private static final String SELECT_LANG = String.format("SELECT %1$s, %2$s, %3$s, %4$s FROM %5$s ",
            "_id", COL_LANG, COL_SUPPORTED, COL_DOMAINS, LANG_TABLE);

    private static final String SELECT_CLASS = String.format("SELECT %1$s, %2$s, %3$s, %4$s, %5$s FROM %6$s ",
            "_id", COL_NAME, COL_URL, COL_PACKAGE, COL_LANG_FK, CLASS_TABLE);

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

    private void performClassInsert(SQLiteDatabase db, String language, String className, String packageName, String urls){
        ContentValues content = new ContentValues();

        content.put(COL_LANG_FK, getLangId(language));
        content.put(COL_NAME, className);
        content.put(COL_PACKAGE, packageName);
        content.put(COL_URL, urls);

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
                String packageName = null;
                switch (lang.toUpperCase()){
                    case "JAVA":
                        packageName = ((JavaClassDescription)classDescription).getPackageName();
                        java = true;
                        break;
                }
                performClassInsert(db, lang, classDescription.getClassName(), packageName, formatUrls(classDescription.getUrls()));
            }
            if(java){
                ContentValues vals = new ContentValues();
                vals.put(COL_UPDATED, System.currentTimeMillis());
                vals.put(COL_SUPPORTED, 1);
                vals.put(COL_DOMAINS, "docs.oracle.com:");
                db.update(LANG_TABLE, vals, COL_LANG + " = ? COLLATE NOCASE", new String[]{"Java"});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public Stack<ClassDescription> get(String lang){
        Stack<ClassDescription> classes = new Stack<>();
        lang = lang.toUpperCase();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(SELECT_CLASS + "WHERE " + COL_LANG_FK + " = ?", new String[]{String.valueOf(getLangId(lang))});

        if(!c.moveToFirst())
            classes = null;
        else {
            int col_name = c.getColumnIndex(COL_NAME);
            int col_urls = c.getColumnIndex(COL_URL);
            if (lang.equals("JAVA")) {
                int col_package = c.getColumnIndex(COL_PACKAGE);
                do {
                    classes.push(new JavaClassDescription(c.getString(col_name), c.getString(col_package), splitUrls(c.getString(col_urls))));
                } while (c.moveToNext());
            } else
                classes = null;
        }

        c.close();
        return classes;
    }

    public boolean needsUpdate(String lang){
        SQLiteDatabase db = getReadableDatabase();
        boolean flag = true;
        Cursor c = db.rawQuery("SELECT " + COL_UPDATED + " FROM " + LANG_TABLE + " WHERE " + COL_LANG + " = ? COLLATE NOCASE", new String[]{lang});
        if(c.moveToFirst()){
            Long updated = c.getLong(0);
            if(updated == 0){
                flag = true;

            }
            Long today = System.currentTimeMillis();
            Long difference = today - updated;
            if(!TimeManager.updateClass(updated, today))
                flag = false;
        }else
            flag = true;

        c.close();
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
        Cursor c = db.rawQuery("SELECT _id FROM " + LANG_TABLE + " WHERE " + COL_LANG + " = ? COLLATE NOCASE", new String[]{lang});
        if(!c.moveToFirst())
            return -1;
        int id = c.getInt(c.getColumnIndex("_id"));
        c.close();
        return id;
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

    public ArrayList<LanguageDescription> getLanguages(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(SELECT_LANG, null);

        return generateLanguageList(c);
    }

    public ArrayList<LanguageDescription> getSupportedLanguages(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(SELECT_LANG + " WHERE " + COL_SUPPORTED + " != 0", null);

        return generateLanguageList(c);
    }

    private ArrayList<LanguageDescription> generateLanguageList(Cursor c){
        ArrayList<LanguageDescription> languages = new ArrayList<>();
        if(!c.moveToFirst()){
            c.close();
            return null;
        }

        int name = c.getColumnIndex(COL_LANG);
        int id = c.getColumnIndex("_id");
        int urls = c.getColumnIndex(COL_DOMAINS);
        int supported = c.getColumnIndex(COL_SUPPORTED);

        do{
            languages.add(new LanguageDescription(c.getString(name),
                                                    c.getInt(id),
                                                    splitUrls(c.getString(urls)),
                                                    c.getInt(supported)));
        }while(c.moveToNext());
        c.close();

        return languages;
    }

    public HashMap<String, Integer> getLangIds(){
        return getLangIds(null);
    }

    public boolean insertLanguages(AbstractList<LanguageDescription> langs) {
        SQLiteDatabase db = getWritableDatabase();
        boolean flag = false;
        db.beginTransaction();

        try {
            for (LanguageDescription lang : langs) {
                if (!languageExists(lang.getName(), db)) {
                    ContentValues vals = new ContentValues();
                    vals.put(COL_LANG, lang.getName());
                    vals.put(COL_SUPPORTED, 0);
                    db.insert(LANG_TABLE, null, vals);
                }
            }
            db.setTransactionSuccessful();
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
            flag = false;
        }finally {
            db.endTransaction();
            db.close();
        }

        return flag;
    }

    public boolean languageExists(String lang){
        SQLiteDatabase db = getReadableDatabase();
        boolean result = languageExists(lang, db);
        db.close();
        return result;
    }

    private boolean languageExists(String lang, SQLiteDatabase db){
        Cursor c = db.rawQuery(SELECT_LANG + " WHERE " + COL_LANG + " = ?", new String[]{lang});
        boolean result = false;
        if(c.getCount() > 0)
            result = true;

        c.close();
        return result;
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
        if(urls == null)
            return null;

        StringBuilder s = new StringBuilder();
        for(String url : urls) {
            s.append(url);
            s.append(":");
        }
        return s.toString();
    }

    private String[] splitUrls(String urls){
        if(urls == null)
            return null;
        else
            return urls.split(":");
    }
}
