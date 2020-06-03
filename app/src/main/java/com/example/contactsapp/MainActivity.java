package com.example.contactsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.contactsapp.database.DatabaseHelper;
import com.example.contactsapp.models.Contact;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

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

        contactList.addAll(db.getAllNotes());
//        // data to populate the RecyclerView with
//        ArrayList<String> contactNames = new ArrayList<>();
//        contactNames.add("Sarah");
//        contactNames.add("Barack");
//        contactNames.add("Michelle");
//        contactNames.add("Shirley");
//        contactNames.add("Richa");
//        contactNames.add("Watson");
//        contactNames.add("Wiley");
//        contactNames.add("Amrand");
//        contactNames.add("Elley");

        // set up the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, contactList);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
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
}
