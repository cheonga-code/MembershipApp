package com.example.membershipapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class EventNewsDetailActivity extends AppCompatActivity {

    TextView tvNewsDetailTitle, tvNewsDetailContent;
    String newsTitleStr, newsContentStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_news_detail);

        tvNewsDetailTitle = (TextView)findViewById(R.id.tvNewsDetailTitle);
        tvNewsDetailContent = (TextView)findViewById(R.id.tvNewsDetailContent);

        Intent intent = getIntent();
        newsTitleStr = intent.getStringExtra("newsTitle");
        newsContentStr = intent.getStringExtra("newsContent");

        tvNewsDetailTitle.setText(newsTitleStr);
        tvNewsDetailContent.setText(newsContentStr);

    }
}
