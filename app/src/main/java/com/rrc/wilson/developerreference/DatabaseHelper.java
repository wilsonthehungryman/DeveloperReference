package com.rrc.wilson.developerreference;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.AbstractList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

/**
 * Created by Wilson on 2017-03-25.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "LanguageInfo";

    private static final String CLASS_TABLE = "Classes";
    private static final String LANG_TABLE = "Languages";

    private static final String COL_LANG = "language";
    private static final String COL_UPDATED = "updated";
    private static final String COL_LANG_FK = "language_id";
    private static final String COL_NAME = "class";
    private static final String COL_PACKAGE = "package";

    private static final int DB_VERSION = 2;

    private static final String LANG_TABLE_CREATE = String.format(
            "CREATE TABLE %1$s (\n" +
                "_id INTEGER PRIMARY KEY,\n" +
                "%2$s TEXT NOT NULL,\n" +
                "%3$s INTEGER)",
            LANG_TABLE, COL_LANG, COL_UPDATED);

    private static final String CLASS_TABLE_CREATE = String.format(
            "CREATE TABLE %1$s (" +
                "_id INTEGER PRIMARY KEY,\n" +
                "%2$s INTEGER NOT NULL,\n" +
                "%3$s TEXT NOT NULL,\n" +
                "%4$s TEXT, " +
                "UNIQUE (%2$s, %3$s) ON CONFLICT REPLACE)," +
                "FOREIGN KEY(%2$s) REFERENCES %5$s(_id)",
                CLASS_TABLE, COL_LANG_FK, COL_NAME, COL_PACKAGE, LANG_TABLE);

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public void insertValue(String language, String className){
        insertValue(language, className, null);
    }

    public void insertValue(String language, String className, String packageName){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues content = new ContentValues();

        content.put(COL_LANG_FK, language);
        content.put(COL_NAME, className);
        content.put(COL_PACKAGE, packageName);

        db.insert(CLASS_TABLE, null, content);

        db.close();
    }

    public void insertValues(AbstractList<ClassDescription> values){
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
        String[] selection;
        if(lang.equals("JAVA")){
            selection = new String[]{ COL_LANG_FK, COL_NAME, COL_PACKAGE };
        }else{
            selection = new String[]{ COL_LANG_FK, COL_NAME };
        }
        String[] selectionParams = { COL_LANG_FK, String.valueOf(getLangId(lang)) };
        Cursor c = db.query(false, CLASS_TABLE, selection, "WHERE ? = ?", selectionParams, null, null, COL_NAME, null);

        if(!c.moveToFirst())
            return null;

        int col_name = c.getColumnIndex(COL_NAME);
        if(lang.equals("JAVA")){
            int col_package = c.getColumnIndex(COL_PACKAGE);
            do{
                classes.push(new JavaClassDescription(c.getString(col_name), c.getString(col_package)));
            }while(c.moveToNext());
        }else{
            return null;
        }

        return classes;
    }

    public boolean needsUpdate(String lang){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + COL_UPDATED + " FROM " + LANG_TABLE + " WHERE " + COL_LANG + " = " + lang.toUpperCase(), null);
        if(c.moveToFirst()){
            Long updated = c.getLong(0);
            Long today = System.currentTimeMillis();
            Long difference = today - updated;
            if(difference / 1000 > 60 * 24 * 14)
                return true;
            else
                return false;
        }

        db.close();

        db = getWritableDatabase();
        ContentValues val = new ContentValues();
        val.put(COL_LANG, lang.toUpperCase());
        db.insert(LANG_TABLE, null, val);
        db.close();

        return true;
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

    public HashMap<String, Integer> getLangIds(){
        return getLangIds(null);
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
}
