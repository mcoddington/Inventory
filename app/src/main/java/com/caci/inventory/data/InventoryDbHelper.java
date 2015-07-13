package com.caci.inventory.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Mike Coddington on 7/6/2015.
 */
public class InventoryDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "inventory.db";

    public static final String TABLE_NAME = "inventory";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PERSONID = "people_id";
    public static final String COLUMN_DESCRIPTION = "name";
    public static final String COLUMN_BARCODE = "location";
    public static final String COLUMN_ATTACH = "attach_fl";

    private static final String[] COLUMNS = { COLUMN_ID, COLUMN_PERSONID, COLUMN_DESCRIPTION, COLUMN_BARCODE, COLUMN_ATTACH};

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_PERSONID + " INTEGER," +
                    COLUMN_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    COLUMN_BARCODE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_ATTACH + TEXT_TYPE +
                    " )";


    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    public InventoryItem createInventoryItem(InventoryItem inventoryItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        // make values to be inserted
        ContentValues values = new ContentValues();
        values.put(COLUMN_PERSONID, inventoryItem.getPersonId());
        values.put(COLUMN_DESCRIPTION, inventoryItem.getDescription());
        values.put(COLUMN_BARCODE, inventoryItem.getBarcode());
        values.put(COLUMN_ATTACH, inventoryItem.isAttach());

        // insert book
        Long id = db.insert(TABLE_NAME, null, values);
        // close database transaction

        db.close();
        return getInventoryItem(id.intValue());
    }

    public InventoryItem getInventoryItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, // a. table
                COLUMNS, " " + COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null, null);
        // if results !=null, parse the first one
        if (cursor != null)
            cursor.moveToFirst();
        InventoryItem inventoryItem = populateFromCursor(cursor);
        return inventoryItem;
    }

    public List getAllInventory() {
        List inventory = new LinkedList();
        Cursor cursor = getAllInventoryCursor();
        // parse all results
        InventoryItem inventoryItem = null;
        if (cursor.moveToFirst()) {
            do {
                inventoryItem = populateFromCursor(cursor);
                inventory.add(inventoryItem);
            } while (cursor.moveToNext());
        }
        return inventory;
    }

    public Cursor getAllInventoryCursor() {
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public List getAllInventory(int personId) {
        List inventory = new LinkedList();
        Cursor cursor = getAllInventoryCursor(personId);
        // parse all results
        InventoryItem inventoryItem = null;
        if (cursor.moveToFirst()) {
            do {
                inventoryItem = populateFromCursor(cursor);
                inventory.add(inventoryItem);
            } while (cursor.moveToNext());
        }
        return inventory;
    }

    public Cursor getAllInventoryCursor(int personId) {
        String query = "SELECT  * FROM " + TABLE_NAME +" WHERE "+COLUMN_PERSONID+" = "+personId;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public int updateInventoryItem(InventoryItem inventoryItem) {
        // get reference of the database
        SQLiteDatabase db = this.getWritableDatabase();
        // make values to be inserted
        ContentValues values = new ContentValues();
        values.put(COLUMN_DESCRIPTION, inventoryItem.getDescription());
        values.put(COLUMN_BARCODE, inventoryItem.getBarcode());
        values.put(COLUMN_ATTACH, inventoryItem.isAttach());
        // update
        int i = db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(inventoryItem.getId())});
        db.close();
        return i;
    }

    public void deleteInventoryItem(InventoryItem inventoryItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[] { String.valueOf(inventoryItem.getId()) });
        db.close();
    }

    public void deleteInventoryItemsByPerson(int personId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_PERSONID + " = ?", new String[] { String.valueOf(personId) });
        db.close();
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    private InventoryItem populateFromCursor(Cursor cursor) {
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setId(Integer.parseInt(cursor.getString(0)));
        inventoryItem.setPersonId(Integer.parseInt(cursor.getString(1)));
        inventoryItem.setDescription(cursor.getString(2));
        inventoryItem.setBarcode(cursor.getString(3));
        inventoryItem.setAttach(cursor.getString(4)!=null && cursor.getString(4).equals("1"));
        return inventoryItem;
    }
}