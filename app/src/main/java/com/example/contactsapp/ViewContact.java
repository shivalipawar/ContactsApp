package com.example.contactsapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.contactsapp.models.Contact;

import static com.example.contactsapp.MainActivity.adapter;
import static com.example.contactsapp.MainActivity.contactList;
import static com.example.contactsapp.MainActivity.db;
import static com.example.contactsapp.MainActivity.filteredContactList;

public class ViewContact extends AppCompatActivity implements View.OnClickListener {

    TextView name, phoneNumber;
    int position = -1;
    ImageButton btnCall, btnText, btnEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contact);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        position = intent.getIntExtra("position", -1);
        name = findViewById(R.id.name);
        phoneNumber = findViewById(R.id.phoneNumber);
        btnCall = findViewById(R.id.btnCall);
        btnText = findViewById(R.id.btnText);
        btnEmail = findViewById(R.id.btnEmail);

        if (position > -1) {
            updateWithDefaultValues(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_contact, menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_more :
                showActionsDialog(position);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateWithDefaultValues(int position) {
        Contact n = filteredContactList.get(position);
        name.setText(n.getName());
        phoneNumber.setText(n.getNumber());
    }

    @Override
    public void onClick(View v) {
        if (v instanceof ImageButton) {
            ImageButton fab = (ImageButton) v;
            switch (fab.getId()) {
                case R.id.btnCall:
                    makeCall();
                    break;
                case R.id.btnText:
                    sendSMS();
                    break;
                case R.id.btnEmail:
                    sendEmail();
                    break;
            }

        }
    }

    @SuppressLint("LongLogTag")
    private void sendEmail() {
        Log.i("Send email", "");
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Finished sending email...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendSMS() {
        if (isTelephonyEnabled()) {
            Intent intentText = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:"+phoneNumber.getText().toString()));
            intentText.putExtra("sms_body","Text SMS to "+name.getText());
            startActivity(intentText);
        } else {
            Toast.makeText(this, "Cannot send sms please check your SIM", Toast.LENGTH_LONG).show();
        }
    }

    private void makeCall() {
        if (isTelephonyEnabled()) {
            Intent intentCall = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber.getText().toString()));
            startActivity(intentCall);
        } else {
            Toast.makeText(this, "Cannot place call please check your SIM", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isTelephonyEnabled() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        return telephonyManager != null && telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY;
    }

    private void showActionsDialog(final int position) {
        CharSequence items[] = new CharSequence[]{"Edit", "Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    gotToEditContactActivity(position);
                } else {
                    deleteContact(position);
                }
            }
        });
        builder.show();
    }

    private void gotToEditContactActivity(int position) {
        Intent intent = new Intent(this, AddContactActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    private void deleteContact(int position) {
        Contact contactToBeDeleted = filteredContactList.get(position);
        db.deleteContact(contactToBeDeleted, db.getWritableDatabase());
        filteredContactList.remove(contactToBeDeleted);
        contactList.remove(contactToBeDeleted);
        adapter.notifyItemRemoved(position);
        finish();
    }
}
