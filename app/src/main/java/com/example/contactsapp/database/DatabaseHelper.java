package com.example.contactsapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.contactsapp.models.Contact;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "contacts_db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ContactTable.CREATE_TABLE);
        System.out.println("DB created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ContactTable.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertNote(Contact contact) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();
        long id = 0;

        ContentValues values = new ContentValues();
        values.put(ContactTable.COLUMN_NAME, contact.getName());
        values.put(ContactTable.COLUMN_PHONE, contact.getNumber());

        Cursor cursor = db.query(ContactTable.TABLE_NAME,
                new String[]{ContactTable.COLUMN_ID, ContactTable.COLUMN_NAME, ContactTable.COLUMN_PHONE},
                ContactTable.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if(cursor.getCount() == 1){
            return updateNote(contact);
        }else{
            id = db.insert(ContactTable.TABLE_NAME, null, values);
            System.out.println("Insert successful");
        }

        db.close();

        return id;
    }

    public Contact getContact(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(ContactTable.TABLE_NAME,
                new String[]{ContactTable.COLUMN_ID, ContactTable.COLUMN_NAME, ContactTable.COLUMN_PHONE},
                ContactTable.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        System.out.println("Cursor is "+cursor.getCount());

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        Contact contact = new Contact(
                cursor.getInt(cursor.getColumnIndex(ContactTable.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN_PHONE)));

        // close the db connection
        cursor.close();

        return contact;
    }

    public List<Contact> getAllNotes() {
        List<Contact> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + ContactTable.TABLE_NAME + " ORDER BY " +
                ContactTable.COLUMN_NAME + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(cursor.getInt(cursor.getColumnIndex(ContactTable.COLUMN_ID)));
                contact.setName(cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN_NAME)));
                contact.setNumber(cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN_PHONE)));

                notes.add(contact);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return notes;
    }

    public int getNotesCount() {
        String countQuery = "SELECT  * FROM " + ContactTable.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public int updateNote(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactTable.COLUMN_NAME, contact.getName());
        values.put(ContactTable.COLUMN_PHONE, contact.getNumber());

        // updating row
        return db.update(ContactTable.TABLE_NAME, values, ContactTable.COLUMN_ID + " = ?",
                new String[]{String.valueOf(contact.getId())});
    }
}
