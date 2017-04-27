package com.ekdorn.silentiumproject.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ekdorn.silentiumproject.silent_core.Message;

import java.util.ArrayList;
import java.util.List;

public class NoteDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 457;
    public static final String DATABASE_NAME = "SilentiumDB";
    public static final String MY_TABLE = "NotesList";
    public static final String KEY_TITLE = "keyTitle";
    public static final String KEY_STRING = "keyString";
    public static final String KEY_DATA = "keyInteger";
    public static final String KEY_ID = "_id";

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NOTES_TABLE = "CREATE TABLE " + MY_TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_TITLE + " VALCHAR, "
                + KEY_STRING + " VALCHAR, "
                + KEY_DATA + " INTEGER" + ")";
        db.execSQL(CREATE_NOTES_TABLE);
    }

    public NoteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MY_TABLE);
        onCreate(db);
    }

    public void addRec(String title, String str, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, title);
        values.put(KEY_STRING, str);
        values.put(KEY_DATA, date);
        System.out.println(values);
        db.insert(MY_TABLE, null, values);
        db.close();
    }

    public List<Message.Note> getNoteList() {
        List<Message.Note> lst = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + MY_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Message.Note note = new Message().new Note();
                note.Title = cursor.getString(1);
                note.Text = cursor.getString(2);
                note.CreateDate = cursor.getString(3);
                lst.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lst;
    }
    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MY_TABLE, null, null);
        db.close();
    }
}