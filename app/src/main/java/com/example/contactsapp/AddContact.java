package com.example.contactsapp;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.contactsapp.models.Contact;

import static com.example.contactsapp.MainActivity.adapter;
import static com.example.contactsapp.MainActivity.contactList;
import static com.example.contactsapp.MainActivity.db;

public class AddContact extends AppCompatActivity {
    EditText name, phoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        name = findViewById(R.id.name);
        phoneNumber = findViewById(R.id.phoneNumber);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_save:
                saveToDb();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_contact,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void saveToDb() {
        if(name.getText().toString().isEmpty() || phoneNumber.getText().toString().isEmpty()){
            Toast.makeText(this,"Please enter name and phone number",Toast.LENGTH_LONG).show();
        }else{
            Contact contact = new Contact(0, name.getText().toString(), phoneNumber.getText().toString());
            createNote(contact);
            Toast.makeText(this,"Successfully added contact",Toast.LENGTH_SHORT).show();
            System.out.println("Saving to Db");
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void createNote(Contact contact) {
        long id = db.insertContact(contact,db.getWritableDatabase());

        Contact n = db.getContact(id,db.getReadableDatabase());

        if (n != null) {
            contactList.add( n);
            adapter.notifyDataSetChanged();
        }
    }
}
