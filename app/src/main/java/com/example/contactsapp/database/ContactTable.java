package com.example.contactsapp.database;

public class ContactTable {
        public static final String TABLE_NAME = "contacts";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PHONE = "phone";

        // Create table SQL query
        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + "("
                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + COLUMN_NAME + " TEXT,"
                        + COLUMN_PHONE + " TEXT"
                        + ")";
}
