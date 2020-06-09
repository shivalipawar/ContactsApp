package com.example.contactsapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.contactsapp.models.Contact;

import java.util.ArrayList;
import java.util.List;

import static com.example.contactsapp.database.ContactTable.*;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "contacts_db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_QUERY);
        System.out.println("DB created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public int updateContact(Contact contact, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, contact.getName());
        values.put(COLUMN_PHONE, contact.getNumber());
        values.put(COLUMN_EMAIL, contact.getEmail());

        return db.update(TABLE_NAME, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(contact.getId())});
    }

    public void deleteContact(Contact contact, SQLiteDatabase db) {
        db.delete(TABLE_NAME, COLUMN_ID + " = ?",
                new String[]{String.valueOf(contact.getId())});
        db.close();
    }

    public long insertContact(Contact contact, SQLiteDatabase db) {
        long id = 0;

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, contact.getName());
        values.put(COLUMN_PHONE, contact.getNumber());
        values.put(COLUMN_EMAIL, contact.getEmail());

        if (selectQueryForId(db, id).getCount() == 1) {
            return updateContact(contact, db);
        } else {
            id = db.insert(TABLE_NAME, null, values);
        }
        db.close();
        return id;
    }

    public List<Contact> getAllContacts(SQLiteDatabase db) {
        List<Contact> contactList = new ArrayList<>();
        Cursor cursor = db.rawQuery(SELECT_ALL_CONTACTS_QUERY, null);
        if (cursor.moveToFirst()) {
            do {
                Contact contact = getContact(cursor);
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        db.close();
        return contactList;
    }

    public Contact getContact(long id, SQLiteDatabase db) {
        Cursor cursor = selectQueryForId(db, id);
        if (cursor != null)
            cursor.moveToFirst();
        Contact contact = getContact(cursor);
        cursor.close();
        return contact;
    }

    private Cursor selectQueryForId(SQLiteDatabase db, long id) {
        return db.query(TABLE_NAME,
                new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_PHONE,COLUMN_EMAIL},
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
    }

    private Contact getContact(Cursor cursor) {
        return new Contact(
                cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)),
                cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL))
                );
    }
}
