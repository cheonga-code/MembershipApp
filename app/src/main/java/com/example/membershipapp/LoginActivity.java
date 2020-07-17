package com.example.membershipapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class LoginActivity extends LogActivity {

    EditText loginEmail, loginPW;
    Button btnLoginOK, btnJoin;
    CheckBox checkboxAutoLogin;

    // 비밀번호 정규식
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");

    private String emailStr = "";
    private String passwordStr = "";

    //파이어베이스 인증 객체 생성
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = (EditText)findViewById(R.id.loginEmail);
        loginPW = (EditText)findViewById(R.id.loginPW);
        btnLoginOK = (Button)findViewById(R.id.btnLoginOK);
        btnJoin = (Button)findViewById(R.id.btnJoin);
        checkboxAutoLogin = (CheckBox)findViewById(R.id.checkboxAutoLogin);

        //파이어베이스 인증 객체 선언
        firebaseAuth = FirebaseAuth.getInstance();

        btnLoginOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(v);
            }
        });

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent joinIntent = new Intent(LoginActivity.this, JoinActivity.class);
                startActivity(joinIntent);
                finish();

            }
        });


    }

    public void signIn(View view) {
        emailStr = loginEmail.getText().toString();
        passwordStr = loginPW.getText().toString();

        if(isValidEmail() && isValidPasswd()) {
            loginUser(emailStr, passwordStr);
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


    //서버에서 토큰 받아오기
    public void passPushTokenToServer(final String loginEmail){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String deviceToken = instanceIdResult.getToken();

                //토큰값 저장
                Map<String, Object> map = new HashMap<>();
                map.put("userToken", deviceToken);

                applicationClass.databaseReference
                        .child("users").child(applicationClass.EncodeString(loginEmail))
                        .updateChildren(map);

                applicationClass.userToken = deviceToken;

                Log.d(TAG, "현재 디바이스 토큰 (1) : " + deviceToken);
            }
        });
    }

    // 로그인
    private void loginUser(final String email, final String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //로그인 성공
                            Toast.makeText(LoginActivity.this, R.string.success_login, Toast.LENGTH_SHORT).show();
//                            //자동로그인 체크
//                            autoLoginCheck(email, password);

                            //FCM token 서버에서 받아오기
                            passPushTokenToServer(email);

                            //관리자 로그인일때 홈 화면전환
                            if(email.equals(applicationClass.adminEmail)) {
                                Intent adminHomeIntent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                                startActivity(adminHomeIntent);
                                applicationClass.makeLog("관리자 로그인 : "+email);
                                applicationClass.loginEmail = email;
                                autoLoginCheck(email, password);
                                finish();
                            }else{
                                //사용자 로그인일때 홈 화면전환
                                Intent userHomeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(userHomeIntent);
                                applicationClass.makeLog("사용자 로그인 : "+email);
                                applicationClass.loginEmail = email;
                                autoLoginCheck(email, password);
                                finish();
                            }
                        } else {
                            // 로그인 실패
                            Toast.makeText(LoginActivity.this, R.string.failed_login, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // 자동로그인
    public void autoLoginCheck(String autoLoginID, String autoLoginPW){

        applicationClass.makeLog("자동로그인 체크유무 : "+checkboxAutoLogin.isChecked());

        if(checkboxAutoLogin.isChecked()){
            // 자동로그인 체크 되어있을때
            applicationClass.editor.putBoolean("autoLoginCheck", true);
            applicationClass.editor.putString("autoLoginID", autoLoginID);
            applicationClass.editor.putString("autoLoginPW", autoLoginPW);
            applicationClass.editor.apply();

        }else{
            // 자동로그인 체크 안 되어있을때
            applicationClass.editor.putBoolean("autoLoginCheck", false);
            applicationClass.editor.putString("autoLoginID", "");
            applicationClass.editor.putString("autoLoginPW", "");
            applicationClass.editor.apply();
        }

    }
}
