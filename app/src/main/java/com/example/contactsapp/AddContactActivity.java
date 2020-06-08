package com.example.contactsapp;

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
import static com.example.contactsapp.MainActivity.db;
import static com.example.contactsapp.MainActivity.filteredContactList;

public class AddContactActivity extends AppCompatActivity {
    EditText name, phoneNumber;
    int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        position = intent.getIntExtra("position", -1);
        name = findViewById(R.id.name);
        phoneNumber = findViewById(R.id.phoneNumber);

        if (position > -1) {
            updateWithDefaultValues(position);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        getMenuInflater().inflate(R.menu.add_contact, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void updateWithDefaultValues(int position) {
        Contact n = filteredContactList.get(position);
        name.setText(n.getName());
        phoneNumber.setText(n.getNumber());
    }

    private void saveToDb() {
        if (name.getText().toString().isEmpty() || phoneNumber.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter name and phone number", Toast.LENGTH_LONG).show();
        } else if (position == -1) {
            Contact contact = new Contact(0, name.getText().toString(), phoneNumber.getText().toString());
            createContact(contact);
            Toast.makeText(this, "Successfully added contact", Toast.LENGTH_SHORT).show();
            System.out.println("Saving to Db");
            finish();
        } else if (position > -1) {
            updateContact(name.getText().toString(), phoneNumber.getText().toString(), position);
            Toast.makeText(this, "Successfully updated contact", Toast.LENGTH_SHORT).show();
            System.out.println("Saving to Db");
            finish();
        }
    }

    private void updateContact(String name, String phoneNumber, int position) {
        Contact n = filteredContactList.get(position);
        n.setName(name);
        n.setNumber(phoneNumber);
        db.updateContact(n, db.getWritableDatabase());
        adapter.notifyItemChanged(position);
    }

    private void createContact(Contact contact) {
        long id = db.insertContact(contact, db.getWritableDatabase());

        Contact n = db.getContact(id, db.getReadableDatabase());

        if (n != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
