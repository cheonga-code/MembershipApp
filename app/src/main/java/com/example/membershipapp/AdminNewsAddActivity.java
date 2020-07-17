package com.example.membershipapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class AdminNewsAddActivity extends LogActivity {

    EditText etAddNewsTitle, etAddNewsContent;
    Button btnAddNewsOK;

    String newsTitleStr, newsContentStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_news_add);

        etAddNewsTitle = (EditText) findViewById(R.id.etAddNewsTitle);
        etAddNewsContent = (EditText) findViewById(R.id.etAddNewsContent);
        btnAddNewsOK = (Button) findViewById(R.id.btnAddNewsOK);

        //뉴스 추가 버튼 클릭했을때 처리
        btnAddNewsOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //editText 에서 입력한 값을 받아와서 -> string 형변환을 시킨다
                newsTitleStr = etAddNewsTitle.getText().toString();
                newsContentStr = etAddNewsContent.getText().toString();

                //editText 가 공백인지 검사하는 코드
                if(newsTitleStr.length() == 0 || newsContentStr.length() == 0){
                    Toast.makeText(AdminNewsAddActivity.this, "공백을 채워주세요.", Toast.LENGTH_SHORT).show();
                }else{

                    //파이어베이스에 데이터 저장
                    saveData();

                    //이전화면으로 되돌아가기
                    Intent intent = getIntent();
//                    //firestore에 입력한 데이터가 저장됨
//                    createDate();
                    setResult(RESULT_OK, intent);
                    finish();
                }

            }
        });
    }


    //firebase realtime 저장소에 데이터를 저장하는 코드
    public void saveData(){

            //이미지가 없을때
            AdminNewslistDTO newslistDTO = new AdminNewslistDTO();
            newslistDTO.newsTitle = newsTitleStr;
            newslistDTO.newsContent = newsContentStr;
            applicationClass.databaseReference.child("AdminNewslist").push().setValue(newslistDTO);
            Log.d(TAG, "realtime database에 데이터 전달 성공");

    }
}
