package com.example.membershipapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class ChatActivity extends AppCompatActivity {

    RecyclerView recyclerChat;
    EditText etChat;
    Button btnChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerChat = (RecyclerView)findViewById(R.id.recyclerChat);
        etChat = (EditText)findViewById(R.id.etChat);
        btnChat = (Button)findViewById(R.id.btnChat);

    }
}
