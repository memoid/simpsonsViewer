package com.xfinity.simpsonsviewer.entity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xfinity.simpsonsviewer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gmoro on 5/12/2016.
 */
public class DBCharacterHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "";
    public static final String CHARACTER_TABLE = "characterTable";
    public static final String CHARACTER_ID = "id";
    public static final String CHARACTER_NAME = "name";
    public static final String CHARACTER_DESCRIPTION = "description";
    public static final String CHARACTER_IMAGE = "imageurl";


    public DBCharacterHelper(Context context) {
        super(context, context.getResources().getString(R.string.database_name), null , 2);
        DATABASE_NAME = context.getResources().getString(R.string.database_name);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlExe = "create table " + CHARACTER_TABLE + " ("
                + CHARACTER_ID + " integer primary key, "
                + CHARACTER_NAME + " text unique, "
                + CHARACTER_DESCRIPTION + " text, "
                + CHARACTER_IMAGE + " text)";
        db.execSQL(sqlExe);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + CHARACTER_TABLE);
        onCreate(db);
    }

    public boolean insert(String name, String description, String charImage) {

        SQLiteDatabase db = this.getReadableDatabase();

        /*String sqlExe = "insert into "
                + CHARACTER_TABLE + " ("
                + CHARACTER_NAME + ", "
                + CHARACTER_DESCRIPTION + ", "
                + CHARACTER_IMAGE + ") "
                + "values (" + name
                + ", " + description + ", " + charImage +")"
                + " where "
                + CHARACTER_NAME + " = " + name;*/
        Cursor res = db.rawQuery("select * from " + CHARACTER_TABLE + " where " + CHARACTER_NAME + " = \"" + name + "\"", null);
        if (res.getCount()<=0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(CHARACTER_NAME, name);
            contentValues.put(CHARACTER_DESCRIPTION, description);
            contentValues.put(CHARACTER_IMAGE, charImage);
            db.insert(CHARACTER_TABLE, null, contentValues);
        }

        return true;

    }

    public List<CharacterEntity> getAllCharacters() {
        List<CharacterEntity> allCharacters = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + CHARACTER_TABLE, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            CharacterEntity characterEntity = new CharacterEntity();
            characterEntity.setName(cursor.getString(cursor.getColumnIndex(CHARACTER_NAME)));
            characterEntity.setDescription(cursor.getString(cursor.getColumnIndex(CHARACTER_DESCRIPTION)));
            characterEntity.setUrl(cursor.getString(cursor.getColumnIndex(CHARACTER_IMAGE)));
            allCharacters.add(characterEntity);
            cursor.moveToNext();
        }

        return allCharacters;
    }

}
