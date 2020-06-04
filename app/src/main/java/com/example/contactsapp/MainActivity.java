package com.example.contactsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.contactsapp.database.DatabaseHelper;
import com.example.contactsapp.models.Contact;
import com.example.contactsapp.utils.MyDividerItemDecoration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener, View.OnClickListener {

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

        contactList = new ArrayList<>();
        contactList.addAll(db.getAllContacts());

        // set up the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, contactList);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onItemClick(View view, int position) {
        showActionsDialog(position);
    }

    @Override
    public void onClick(View v) {
        if(v instanceof FloatingActionButton)
        {
            FloatingActionButton fab = (FloatingActionButton) v;
            if(fab.getId() == R.id.addContactButton){
                goToAddContactActivity();
            }
        }
    }

    private void goToAddContactActivity() {
        Intent intent = new Intent(this, AddContact.class);
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
                    deleteNote(position);
                }
            }
        });
        builder.show();
    }

    private void gotToViewContactActivity(int position) {
        Intent intent = new Intent(this, ViewContact.class);
        intent.putExtra("position",position);
        startActivity(intent);
    }

    private void deleteNote(int position) {
        // deleting the note from db
        db.deleteContact(contactList.get(position));

        // removing the note from the list
        contactList.remove(position);
        adapter.notifyItemRemoved(position);

    }

}
