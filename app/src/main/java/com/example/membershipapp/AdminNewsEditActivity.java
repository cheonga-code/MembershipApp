package com.example.membershipapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AdminNewsEditActivity extends LogActivity {

    EditText etEditNewsTitle, etEditNewsContent;
    Button btnEditNewsOK;

    String newslistUidKey, editBeforeNewsTitleStr, editBeforeNewsContentStr,
            editAfterNewsTitleStr, editAfterNewsContentStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_news_edit);

        etEditNewsTitle = (EditText) findViewById(R.id.etEditNewsTitle);
        etEditNewsContent = (EditText) findViewById(R.id.etEditNewsContent);
        btnEditNewsOK = (Button) findViewById(R.id.btnEditNewsOK);

        Intent intent = getIntent();
        newslistUidKey = intent.getStringExtra("newslistUidKey");
        applicationClass.makeLog("uid키가 뭔데 : "+newslistUidKey);
        editBeforeNewsTitleStr = intent.getStringExtra("editNewsTitle");
        applicationClass.makeLog("타이틀 내용이 뭔데 : "+editBeforeNewsTitleStr);
        editBeforeNewsContentStr = intent.getStringExtra("editNewsContent");
        applicationClass.makeLog("타이틀 내용이 뭔데 : "+editBeforeNewsContentStr);

        etEditNewsTitle.setText(editBeforeNewsTitleStr);
        etEditNewsContent.setText(editBeforeNewsContentStr);

        btnEditNewsOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editAfterNewsTitleStr = etEditNewsTitle.getText().toString();
                editAfterNewsContentStr = etEditNewsContent.getText().toString();

                //변경된 데이터 저장
                changeData(newslistUidKey);
                finish();
            }
        });
    }

    //firestore에 변경된 데이터 저장
    public void changeData(String menulistUidKey){
        applicationClass.databaseReference.child("AdminNewslist").child(menulistUidKey).child("newsTitle").setValue(editAfterNewsTitleStr);
        applicationClass.databaseReference.child("AdminNewslist").child(menulistUidKey).child("newsContent").setValue(editAfterNewsContentStr);
        Log.d(TAG, "파이어베이스 데이터 변경해서 저장 완료");
    }
}
