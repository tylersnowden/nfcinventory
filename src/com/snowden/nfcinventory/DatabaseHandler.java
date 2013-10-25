package com.snowden.nfcinventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
 

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
 
public class DatabaseHandler extends SQLiteOpenHelper {
 
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "itemsManager";
 
    // ITEMS table name
    private static final String TABLE_ITEMS = "items";
 
    // ITEMS Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TAG = "tag";
    private static final String KEY_NAME = "name";
    private static final String KEY_STATUS = "status";
 
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," 
        		+ KEY_TAG + " TEXT,"
                + KEY_NAME + " TEXT,"
                + KEY_STATUS + " INTEGER" + ")";
        db.execSQL(CREATE_ITEMS_TABLE);
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
 
        // Create tables again
        onCreate(db);
    }
 
    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
 
    // Adding new item
    void addItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, item.getName()); // Item Name
        values.put(KEY_TAG, item.getTag()); // Item Tag
        values.put(KEY_STATUS, item.getStatus()); // Item Status
 
        // Inserting Row
        db.insert(TABLE_ITEMS, null, values);
        db.close(); // Closing database connection
    }
 
    // Getting single item
    Item getItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_ITEMS, new String[] { KEY_ID,
        		KEY_TAG, KEY_NAME, KEY_STATUS }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
 
        Item item = new Item(Integer.parseInt(cursor.getString(0)),cursor.getString(1), cursor.getString(2), Integer.parseInt(cursor.getString(3)));
        // return item
        return item;
    }
     
    // Getting All ITEMS
    public List<Item> getAllItems() {
        List<Item> itemList = new ArrayList<Item>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ITEMS;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Item item = new Item();
                item.setID(Integer.parseInt(cursor.getString(0)));
                item.setTag(cursor.getString(1));
                item.setName(cursor.getString(2));
                item.setStatus(Integer.parseInt(cursor.getString(3)));
                // Adding item to list
                itemList.add(item);
            } while (cursor.moveToNext());
        }
 
        // return item list
        return itemList;
    }
    
 // Getting All ITEMS as List<String>
    public ArrayList<String> getAllItemsAsString() {
        ArrayList<String> itemList = new ArrayList<String>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ITEMS;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int status;
        String status_str;
        
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {   
            	// Format: < Name: Status >
            	status = Integer.parseInt(cursor.getString(3));
            	if (status == 1) status_str = " (checked out)";
            	else status_str = " (in stock)";
                itemList.add(cursor.getString(2)+status_str);
            } while (cursor.moveToNext());
        }
 
        // return item list
        return itemList;
    }
 
    // Updating single item
    public int updateItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_TAG, item.getTag());
        values.put(KEY_NAME, item.getName());
        values.put(KEY_STATUS, item.getStatus());
 
        // updating row
        return db.update(TABLE_ITEMS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(item.getID()) });
    }
 
    // Deleting single item
    public void deleteItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, KEY_ID + " = ?",
                new String[] { String.valueOf(item.getID()) });
        db.close();
    }
    
    // Drops Table
    public void deleteAllItems() {
    	List<Item> tmp = this.getAllItems();
	        
        Iterator<Item> tmpIt = tmp.iterator();
        while (tmpIt.hasNext()) {
        	this.deleteItem(tmpIt.next());
        }
    }
 
 
    // Getting ITEMS Count
    public int getItemsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_ITEMS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        // return count
        return cursor.getCount();
    }
 
}