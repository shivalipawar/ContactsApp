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
        onCreate(db);
    }

    public long insertContact(Contact contact, SQLiteDatabase db) {
        long id = 0;

        ContentValues values = new ContentValues();
        values.put(ContactTable.COLUMN_NAME, contact.getName());
        values.put(ContactTable.COLUMN_PHONE, contact.getNumber());

        if(selectQueryForId(db, id).getCount() == 1){
            return updateContact(contact, db);
        }else{
            id = db.insert(ContactTable.TABLE_NAME, null, values);
            System.out.println("Insert successful");
        }

        db.close();

        return id;
    }

    private Cursor selectQueryForId(SQLiteDatabase db, long id) {
        return db.query(ContactTable.TABLE_NAME,
                new String[]{ContactTable.COLUMN_ID, ContactTable.COLUMN_NAME, ContactTable.COLUMN_PHONE},
                ContactTable.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
    }

    public Contact getContact(long id, SQLiteDatabase db) {

        Cursor cursor = selectQueryForId(db, id);

        System.out.println("Cursor is "+cursor.getCount());

        if (cursor != null)
            cursor.moveToFirst();

        Contact contact = new Contact(
                cursor.getInt(cursor.getColumnIndex(ContactTable.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN_PHONE)));

        cursor.close();

        return contact;
    }

    public List<Contact> getAllContacts(SQLiteDatabase db) {
        List<Contact> contactList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + ContactTable.TABLE_NAME + " ORDER BY " +
                ContactTable.COLUMN_NAME + " ASC ";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(cursor.getInt(cursor.getColumnIndex(ContactTable.COLUMN_ID)));
                contact.setName(cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN_NAME)));
                contact.setNumber(cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN_PHONE)));

                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        db.close();

        return contactList;
    }

    public int updateContact(Contact contact, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(ContactTable.COLUMN_NAME, contact.getName());
        values.put(ContactTable.COLUMN_PHONE, contact.getNumber());

        return db.update(ContactTable.TABLE_NAME, values, ContactTable.COLUMN_ID + " = ?",
                new String[]{String.valueOf(contact.getId())});
    }

    public void deleteContact(Contact contact, SQLiteDatabase db) {
        db.delete(ContactTable.TABLE_NAME, ContactTable.COLUMN_ID + " = ?",
                new String[]{String.valueOf(contact.getId())});
        db.close();
    }
}
