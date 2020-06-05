package com.example.contactsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.provider.ContactsContract;

import com.example.contactsapp.database.DatabaseHelper;
import com.example.contactsapp.models.Contact;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener, View.OnClickListener {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private static final int SELECT_CONTACT = 2;
    public static MyRecyclerViewAdapter adapter;
    public static DatabaseHelper db;
    public static List<Contact> contactList = new ArrayList<>();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rvContacts);
        db = new DatabaseHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        contactList = new ArrayList<>();
        contactList.addAll(db.getAllContacts(db.getWritableDatabase()));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, contactList);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        showActionsDialog(position);
    }


    @Override
    public void onClick(View v) {
        if (v instanceof FloatingActionButton) {
            FloatingActionButton fab = (FloatingActionButton) v;
            if (fab.getId() == R.id.addContactButton) {
                goToAddContactActivity();
            } else if (fab.getId() == R.id.importContactButton) {
                System.out.println("Getting list of contacts");
                getPermissionAndImportContact();
            }
        }

    }

    private void getPermissionAndImportContact() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
            startActivityForResult(intent, SELECT_CONTACT);
        }
    }

    private void goToAddContactActivity() {
        Intent intent = new Intent(this, AddContactActivity.class);
        startActivity(intent);
    }

    private void showActionsDialog(final int position) {
        CharSequence items[] = new CharSequence[]{"Edit", "Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    gotToViewContactActivity(position);
                } else {
                    deleteContact(position);
                }
            }
        });
        builder.show();
    }

    private void gotToViewContactActivity(int position) {
        Intent intent = new Intent(this, AddContactActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    private void deleteContact(int position) {
        db.deleteContact(contactList.get(position), db.getWritableDatabase());
        contactList.remove(position);
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull
            String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                    startActivityForResult(intent, SELECT_CONTACT);
                }
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (reqCode) {
                case SELECT_CONTACT:
                    Uri contactData = data.getData();
                    Cursor c = getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                        String hasNumber = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        String num = "";
                        String name = "";
                        if (Integer.valueOf(hasNumber) == 1) {
                            Cursor numbers = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                            while (numbers.moveToNext()) {
                                num = numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                name = numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                                Log.i(">>number", "onActivityResult: " + num + "of person named : "+name);
                                Contact contact = new Contact(0,name,num);
                                createContact(contact);
                            }
                        }
                    }
                    c.close();
                    break;
            }
        }

    }

    private void createContact(Contact contact) {
        long id = db.insertContact(contact, db.getWritableDatabase());

        Contact n = db.getContact(id, db.getReadableDatabase());

        if (n != null) {
            adapter.notifyDataSetChanged();
        }
    }

}
