package com.example.membershipapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class JoinActivity extends LogActivity {

    EditText joinName, joinID, joinEmail, joinPW;
    Button btnJoinOK;

    private String emailStr = "";
    private String passwordStr = "";

    // 파이어베이스 인증 객체 생성
    private FirebaseAuth firebaseAuth;

    // 비밀번호 정규식
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        joinName = (EditText)findViewById(R.id.joinName);
        joinID = (EditText)findViewById(R.id.joinID);
        joinEmail = (EditText)findViewById(R.id.joinEmail);
        joinPW = (EditText)findViewById(R.id.joinPW);
        btnJoinOK = (Button)findViewById(R.id.btnJoinOK);

        // 파이어베이스 인증 객체 선언
        firebaseAuth = FirebaseAuth.getInstance();

        btnJoinOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singUp(v);
            }
        });
    }

    public void singUp(View view) {
        emailStr = joinEmail.getText().toString();
        passwordStr = joinPW.getText().toString();

        if(isValidEmail() && isValidPasswd() && isValidAdmin()) {
            createUser(emailStr, passwordStr);
        }
    }

    // 이메일 유효성 검사
    private boolean isValidEmail() {
        if (emailStr.isEmpty()) {
            // 이메일 공백
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            // 이메일 형식 불일치
            return false;
        } else {
            return true;
        }
    }

    // 비밀번호 유효성 검사
    private boolean isValidPasswd() {
        if (passwordStr.isEmpty()) {
            // 비밀번호 공백
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passwordStr).matches()) {
            // 비밀번호 형식 불일치
            return false;
        } else {
            return true;
        }
    }

    // 관리자 이메일 검사
    private boolean isValidAdmin(){
        if(emailStr.equals("admin@naver.com")){
            Toast.makeText(JoinActivity.this, "사용할수있는 이메일이 아닙니다.", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }

    // 회원가입
    private void createUser(final String email, final String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 회원가입 성공
                            Toast.makeText(JoinActivity.this, R.string.success_signup, Toast.LENGTH_SHORT).show();
                            // 회원 정보 getText()
                            String joinNameStr = joinName.getText().toString();
                            String joinIdStr = joinID.getText().toString();
                            // 저장소에 user 객체 정보 저장
                            Users users = new Users();
//                            users.userId = joinIdStr;
                            users.userEmail = email;
                            users.userName = joinNameStr;
                            users.userStampCount = 0;
                            users.membershipCardcheck = false;
                            users.membershipUserCheck = false;
                            users.userCardPrice = 0;
                            //Firebase paths must not contain '.', '#', '$', '[', or ']' 에러
                            //이메일을 키값으로 사용하려고 했는데 . 이 들어가서 에러가 남
                            //저장할때는 . -> , 로 인코딩해서 저장함
                            //나중에 가져올때는 , -> . 으로 디코딩해서 사용하면 됨
                            applicationClass.databaseReference.child("users").child(EncodeString(email)).setValue(users);

                            // 로그인 화면전환
                            Intent joinIntent = new Intent(JoinActivity.this, LoginActivity.class);
                            startActivity(joinIntent);
                            finish();

                        } else {
                            // 회원가입 실패
                            Toast.makeText(JoinActivity.this, R.string.failed_signup, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public String EncodeString(String string) {
        return string.replace(".", ",");
    }

    public String DecodeString(String string) {
        return string.replace(",", ".");
    }

}
