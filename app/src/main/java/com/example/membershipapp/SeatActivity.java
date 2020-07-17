package com.example.membershipapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;

public class SeatActivity extends AppCompatActivity {

    LinearLayout seat01, seat02, seat03;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat);

        seat01 = (LinearLayout)findViewById(R.id.seat01);
        seat02 = (LinearLayout)findViewById(R.id.seat02);
        seat03 = (LinearLayout)findViewById(R.id.seat03);
    }
}
