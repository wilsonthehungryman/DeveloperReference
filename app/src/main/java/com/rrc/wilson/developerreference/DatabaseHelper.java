package com.rrc.wilson.developerreference;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.Stack;

/**
 * Created by Wilson on 2017-03-25.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Languages";

    private static final String TABLE_NAME = "Classes";

    private static final String COL_LANG = "Language";
    private static final String COL_NAME = "Class";
    private static final String COL_PACKAGE = "Package";

    private static final int DB_VERSION = 1;

    private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
            COL_LANG + " TEXT NOT NULL, " +
            COL_NAME + " TEXT NOT NULL, " +
            COL_PACKAGE + " TEXT, UNIQUE (" + COL_LANG + ", " + COL_NAME +
            ") ON CONFLICT REPLACE)";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
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

        content.put(COL_LANG, language);
        content.put(COL_NAME, className);
        content.put(COL_PACKAGE, packageName);

        db.insert(TABLE_NAME, null, content);

        db.close();
    }

    public void insertValues(AbstractList<ClassDescription> values){
        Iterator<ClassDescription> iter = values.iterator();
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            while(iter.hasNext()){
                ClassDescription classDescription = iter.next();
                String language = classDescription.getLanguage();
                ContentValues content = new ContentValues();

                content.put(COL_LANG, language);
                content.put(COL_NAME, classDescription.getClassName());
                switch (language){
                    case "JAVA":
                        content.put(COL_PACKAGE, ((JavaClassDescription)classDescription).getPackageName());
                        break;
                }
                db.insert(TABLE_NAME, null, content);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public Stack<ClassDescription> get(String language){
        Stack<ClassDescription> classes = new Stack<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String[] selection;
        if(language.equals("JAVA")){
            selection = new String[]{COL_LANG, COL_NAME, COL_PACKAGE};
        }else{
            selection = new String[]{COL_LANG, COL_NAME};
        }
        String[] selectionParams = {COL_LANG, language};
        Cursor c = db.query(false, TABLE_NAME, selection, "WHERE ? = ?", selectionParams, null, null, COL_NAME, null);

        c.moveToFirst();
        int col_name = c.getColumnIndex(COL_NAME);
        if(language.equals("JAVA")){
            int col_package = c.getColumnIndex(COL_PACKAGE);
            for(int i = 0; i < c.getCount(); i++){
                JavaClassDescription j = new JavaClassDescription(c.getString(col_name), c.getString(col_package));
            }
        }else{

        }

        return classes;
    }
}
