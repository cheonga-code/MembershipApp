package com.example.membershipapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MypageEditActivity extends AppCompatActivity {

    TextView etEditUserName;
    Button btnEditUserNameOK;
    String changeUserName;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_edit);

        etEditUserName = (TextView)findViewById(R.id.etEditUserName);
        btnEditUserNameOK = (Button)findViewById(R.id.btnEditUserNameOK);

        intent = getIntent();
        String currentUserName = intent.getStringExtra("userName");
        etEditUserName.setText(currentUserName);

        btnEditUserNameOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //editText에서 변경할 이름 데이터 가져오기
                changeUserName = etEditUserName.getText().toString();
                //공백체크
                if(changeUserName.equals("")){
                    Toast.makeText(MypageEditActivity.this, "변경할 이름을 적어주세요", Toast.LENGTH_SHORT).show();
                }else{
                    //다이얼로그 띄우기
                    makeDialog();
                }
            }
        });
    }

    //이름 변경 다이얼로그
    public void makeDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MypageEditActivity.this);
        builder.setTitle("이름을 변경하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("LogActivity", "다이얼로그 확인 선택");
                                intent.putExtra("changeUserName", changeUserName);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        })
                .setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("LogActivity", "다이얼로그 취소 선택");
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
