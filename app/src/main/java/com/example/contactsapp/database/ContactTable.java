package com.example.contactsapp.database;

class ContactTable {
    static final String TABLE_NAME = "contacts";

    static final String COLUMN_ID = "id";
    static final String COLUMN_NAME = "name";
    static final String COLUMN_PHONE = "phone";

    // Create table SQL query
    static final String CREATE_TABLE_QUERY =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_PHONE + " TEXT"
                    + ")";
    static final String SELECT_ALL_CONTACTS_QUERY = "SELECT  * FROM " + TABLE_NAME + " ORDER BY " +
            COLUMN_NAME + " ASC ";
}
