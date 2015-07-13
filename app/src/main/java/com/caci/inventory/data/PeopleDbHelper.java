package com.caci.inventory.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Mike Coddington on 6/30/2015.
 */
public class PeopleDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "people.db";

    public static final String TABLE_NAME = "people";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_ROOM = "room";

    private static final String[] COLUMNS = { COLUMN_ID, COLUMN_NAME, COLUMN_LOCATION, COLUMN_ROOM};

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    COLUMN_LOCATION + TEXT_TYPE + COMMA_SEP +
                    COLUMN_ROOM + TEXT_TYPE +
            " )";


    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public PeopleDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    public Person createPerson(Person person) {
        SQLiteDatabase db = this.getWritableDatabase();

        // make values to be inserted
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, person.getName());
        values.put(COLUMN_LOCATION, person.getLocation());
        values.put(COLUMN_ROOM, person.getRoom());

        // insert book
        Long id = db.insert(TABLE_NAME, null, values);
        // close database transaction

        db.close();
        return getPerson(id.intValue());
    }

    public Person getPerson(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, // a. table
                COLUMNS, " "+COLUMN_ID+" = ?", new String[]{String.valueOf(id)}, null, null, null, null);
        // if results !=null, parse the first one
        if (cursor != null)
            cursor.moveToFirst();
        Person person = new Person();
        person.setId(Integer.parseInt(cursor.getString(0)));
        person.setName(cursor.getString(1));
        person.setLocation(cursor.getString(2));
        person.setRoom(cursor.getString(3));
        return person;
    }

    public List getAllPeople() {
        List people = new LinkedList();
        Cursor cursor = getAllPeopleCursor();
        // parse all results
        Person person = null;
        if (cursor.moveToFirst()) {
            do {
                person = new Person();
                person.setId(Integer.parseInt(cursor.getString(0)));
                person.setName(cursor.getString(1));
                person.setLocation(cursor.getString(2));
                person.setRoom(cursor.getString(3));
                people.add(person);
            } while (cursor.moveToNext());
        }
        return people;
    }

    public Cursor getAllPeopleCursor() {
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public int updatePerson(Person person) {
        // get reference of the database
        SQLiteDatabase db = this.getWritableDatabase();
        // make values to be inserted
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, person.getName());
        values.put(COLUMN_LOCATION, person.getLocation());
        values.put(COLUMN_ROOM, person.getRoom());
        // update
        int i = db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[] { String.valueOf(person.getId()) });
        db.close();
        return i;
    }

    public void deletePerson(Person person) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[] { String.valueOf(person.getId()) });
        db.close();
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}