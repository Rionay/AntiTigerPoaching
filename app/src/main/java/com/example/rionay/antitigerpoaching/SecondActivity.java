package com.example.rionay.antitigerpoaching;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Da Huo on 2017/4/20.
 * 791094
 */

public class SecondActivity extends Activity {
    private Button sendNone;
    private Button sendMedium;
    private Button sendHigh;
    private DatabaseReference mDatabase;
    private EditText Intruder;
    private EditText Gender;
    private EditText GroupSize;
    private EditText Notified;
    private EditText Notes;
    private Button Update;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        sendNone = (Button)findViewById(R.id.ButtonR3);
        sendMedium = (Button)findViewById(R.id.ButtonR1);
        sendHigh = (Button)findViewById(R.id.ButtonR2);
        Update = (Button)findViewById(R.id.Update);
        Intruder = (EditText)findViewById(R.id.editText);
        Gender = (EditText) findViewById(R.id.editText2);
        GroupSize = (EditText) findViewById(R.id.GSize);
        Notified = (EditText)findViewById(R.id.Notified);
        Notes = (EditText)findViewById(R.id.Notes);
        Update = (Button)findViewById(R.id.Update);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        sendNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("RISK").setValue("None Danger");
            }
        });                                                          //set the value of RISK into None Danger

        sendMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("RISK").setValue("Medium Danger");
            }
        });

        sendHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("RISK").setValue("High Danger");
            }
        });

        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String IndruderCategary = Intruder.getText().toString();
                String GenderStr = Gender.getText().toString();
                String GroupSizeStr = GroupSize.getText().toString();
                String NotifiedStr = Notified.getText().toString();
                String NotesStr = Notes.getText().toString();
                mDatabase.child("IntruderCategary").setValue(IndruderCategary);
                mDatabase.child("Gender").setValue(GenderStr);
                mDatabase.child("GroupSize").setValue(GroupSizeStr);
                mDatabase.child("Notified").setValue(NotifiedStr);
                mDatabase.child("Notes").setValue(NotesStr);
            }
        });                                                          //set the value of different blanks into different values





    }
}