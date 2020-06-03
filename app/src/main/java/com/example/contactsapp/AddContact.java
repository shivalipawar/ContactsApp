package com.example.contactsapp;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.contactsapp.database.ContactTable;
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
            case R.id.action_save: //TODO Add toast and move to home page
                saveToDb();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_contact,menu);
        return super.onCreateOptionsMenu(menu);
//        return true;
    }

    private void saveToDb() {
        if(name.getText().toString().isEmpty() || phoneNumber.getText().toString().isEmpty()){
            Toast.makeText(this,"Please enter name and phone number",Toast.LENGTH_SHORT).show();
        }else{
            Contact contact = new Contact(0, name.getText().toString(), phoneNumber.getText().toString());
            createNote(contact);
            System.out.println("Saving to Db");
            finish();
        }
    }

    private void createNote(Contact contact) {
        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertNote(contact);

        // get the newly inserted note from db
        Contact n = db.getContact(id);

        if (n != null) {
            // adding new note to array list at 0 position
            contactList.add(0, n);

            // refreshing the list
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Updating note in db and updating
     * item in the list by its position
     */
    private void updateNote(Contact contact, int position) {
        Contact n  = contactList.get(position);
        // updating note text
        n.setName(contact.getName());
        n.setNumber(contact.getNumber());

        // updating note in db
        db.updateNote(n);

        // refreshing the list
        contactList.set(position, n);
        adapter.notifyItemChanged(position);

    }
}
