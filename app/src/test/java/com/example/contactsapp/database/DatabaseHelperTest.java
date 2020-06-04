package com.example.contactsapp.database;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.contactsapp.models.Contact;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DatabaseHelperTest {
    DatabaseHelper dbHelper;
    SQLiteDatabase db;

    @Before
    public void init() {
        dbHelper = new DatabaseHelper(null);
        db = mock(SQLiteDatabase.class);
    }

    @Test
    public void onCreate() {
        dbHelper.onCreate(db);

        Mockito.verify(db, atLeastOnce()).execSQL(ContactTable.CREATE_TABLE);
    }

    @Test
    public void onUpgrade() {
        dbHelper.onUpgrade(db, 1, 2);

        Mockito.verify(db, atLeastOnce()).execSQL("DROP TABLE IF EXISTS " + ContactTable.TABLE_NAME);
    }

    @Test
    public void insertContactShouldAddNewContactInDB() {
        long id = 0;
        Cursor cursorMocked = mock(Cursor.class);
        when(cursorMocked.getCount()).thenReturn(0);
        when(db.query(ContactTable.TABLE_NAME,
                new String[]{ContactTable.COLUMN_ID, ContactTable.COLUMN_NAME, ContactTable.COLUMN_PHONE},
                ContactTable.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null)).thenReturn(cursorMocked);
        when(db.insert(Mockito.anyString(), Mockito.nullable(String.class), Mockito.any(ContentValues.class))).thenReturn(Long.valueOf(1));
        Contact mockedContact = new Contact(0, "ABC", "12345");

        long resId = dbHelper.insertContact(mockedContact, db);

        Assert.assertEquals(1, resId);
    }

    @Test
    public void insertContactShouldUpdateContactAlreadyPresentInDBAndReturnRowsAffectedByUpdation() {
        long id = 0;
        Cursor cursorMocked = mock(Cursor.class);
        Contact mockedContact = new Contact(0, "ABC", "12345");
        when(cursorMocked.getCount()).thenReturn(1);
        when(db.query(ContactTable.TABLE_NAME,
                new String[]{ContactTable.COLUMN_ID, ContactTable.COLUMN_NAME, ContactTable.COLUMN_PHONE},
                ContactTable.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null)).thenReturn(cursorMocked);
        when(db.update(Mockito.anyString(), Mockito.any(ContentValues.class), Mockito.anyString() + " = ?",
                Mockito.any(String[].class))).thenReturn(1);

        long resId = dbHelper.insertContact(mockedContact, db);

        Assert.assertEquals(1, resId);
    }

    @Test
    public void getContactShouldReturnContactForGivenId() {
        Cursor cursorMocked = mock(Cursor.class);
        Contact mockedContact = new Contact(2, "PQR", "5678");

        when(db.query(ContactTable.TABLE_NAME,
                new String[]{ContactTable.COLUMN_ID, ContactTable.COLUMN_NAME, ContactTable.COLUMN_PHONE},
                ContactTable.COLUMN_ID + "=?",
                new String[]{String.valueOf(mockedContact.getId())}, null, null, null, null)).thenReturn(cursorMocked);
        when(cursorMocked.getCount()).thenReturn(1);
        when(cursorMocked.moveToFirst()).thenReturn(true);
        when(cursorMocked.getInt(0)).thenReturn(2);
        when(cursorMocked.getString(1)).thenReturn("PQR");
        when(cursorMocked.getString(2)).thenReturn("5678");

        Contact contact = dbHelper.getContact(mockedContact.getId(), db);

        Assert.assertEquals(mockedContact.getId(),contact.getId());
        Assert.assertEquals(mockedContact.getName(),contact.getName());
        Assert.assertEquals(mockedContact.getNumber(),contact.getNumber());
    }

    @Test
    public void getAllContacts() {
        Cursor cursorMocked = mock(Cursor.class);
        when(db.rawQuery("SELECT  * FROM " + ContactTable.TABLE_NAME + " ORDER BY " +
                ContactTable.COLUMN_NAME + " ASC ",null)).thenReturn(cursorMocked);
        when(cursorMocked.getCount()).thenReturn(2);
        when(cursorMocked.moveToFirst()).thenReturn(true);
        when(cursorMocked.getInt(0)).thenReturn(2);
        when(cursorMocked.getString(1)).thenReturn("PQR");
        when(cursorMocked.getString(2)).thenReturn("5678");

        List<Contact> allContacts = dbHelper.getAllContacts(db);

        Assert.assertEquals(1,allContacts.size());
    }

    @Test
    public void updateContactShouldReturnNumberOfRowUpdated() {
        Contact mockedContact = new Contact(2, "PQR", "5678");
        when(db.update(Mockito.anyString(), Mockito.any(ContentValues.class), Mockito.anyString() + " = ?",
                Mockito.any(String[].class))).thenReturn(1);

        int updatedContactId = dbHelper.updateContact(mockedContact, db);

        Assert.assertEquals(1,updatedContactId);
    }

    @Test
    public void deleteContactShouldDeleteContactPassedToIt() {
        Contact mockedContact = new Contact(2, "PQR", "5678");
        when(db.delete(ContactTable.TABLE_NAME, ContactTable.COLUMN_ID + " = ?",
                new String[]{String.valueOf(mockedContact.getId())})).thenReturn(1);

        dbHelper.deleteContact(mockedContact, db);

        Mockito.verify(db,atLeastOnce()).delete(ContactTable.TABLE_NAME, ContactTable.COLUMN_ID + " = ?",
                new String[]{String.valueOf(mockedContact.getId())});
    }
}