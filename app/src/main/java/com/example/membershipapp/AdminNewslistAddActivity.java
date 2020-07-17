package com.example.membershipapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AdminNewslistAddActivity extends AppCompatActivity {

    EditText etAddNewsTitle, etAddNewsContent;
    Button btnNewsSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_newslist_add);

        etAddNewsTitle = (EditText)findViewById(R.id.etAddNewsTitle);
        etAddNewsContent = (EditText)findViewById(R.id.etAddNewsContent);
        btnNewsSave = (Button)findViewById(R.id.btnNewsSave);

        btnNewsSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
