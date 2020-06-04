package com.example.contactsapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.contactsapp.models.Contact;

import static com.example.contactsapp.MainActivity.adapter;
import static com.example.contactsapp.MainActivity.contactList;
import static com.example.contactsapp.MainActivity.db;

public class ViewContact extends AppCompatActivity {

    EditText name, phoneNumber;
    int position = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contact);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        position = intent.getIntExtra("position",0);
        name = findViewById(R.id.name);
        phoneNumber = findViewById(R.id.phoneNumber);

        updateWithDefaultValues(position);
    }

    private void updateWithDefaultValues(int position) {
        Contact n  = contactList.get(position);
        name.setText(n.getName());
        phoneNumber.setText(n.getNumber());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_save:
                saveToDb(position);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_contact,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void saveToDb(int position) {
        if(name.getText().toString().isEmpty() || phoneNumber.getText().toString().isEmpty()){
            Toast.makeText(this,"Please enter name and phone number",Toast.LENGTH_LONG).show();
        }else{
            Contact contact = new Contact(0, name.getText().toString(), phoneNumber.getText().toString());
            updateContact(contact,position);
            Toast.makeText(this,"Successfully updated contact",Toast.LENGTH_SHORT).show();
            System.out.println("Saving to Db");
            finish();
        }
    }

    /**
     * Updating note in db and updating
     * item in the list by its position
     */
    private void updateContact(Contact contact, int position) {
        Contact n  = contactList.get(position);
        n.setName(contact.getName());
        n.setNumber(contact.getNumber());

        db.updateContact(n,db.getWritableDatabase());

        adapter.notifyItemChanged(position);
    }
}
