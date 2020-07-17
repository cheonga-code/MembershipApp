package com.example.membershipapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import static java.lang.Thread.sleep;

public class SplashActivity extends LogActivity {

    String TAG = "splash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //자동로그인 체크
        autoLoginCheck();

    }


    //자동로그인
    public void autoLoginCheck(){
        Boolean autoLoginCheck = applicationClass.sharedPreferences.getBoolean("autoLoginCheck", false);
        final String autoLoginID = applicationClass.sharedPreferences.getString("autoLoginID", "");
        final String autoLoginPW = applicationClass.sharedPreferences.getString("autoLoginPW", "");
        applicationClass.makeLog("자동로그인 autoLoginCheck("+autoLoginCheck+")");
        applicationClass.makeLog("자동로그인 ID("+autoLoginID+"), PW("+autoLoginPW+")");

        //관리자 로그인일때 홈 화면전환
        if(autoLoginCheck == true && autoLoginID.matches(applicationClass.adminEmail)) {
            //firebase auth login
            applicationClass.firebaseAuth.signInWithEmailAndPassword(autoLoginID, autoLoginPW);
            //관리자 홈화면으로 전환
            try {
                sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent adminHomeIntent = new Intent(SplashActivity.this, AdminHomeActivity.class);
            startActivity(adminHomeIntent);
            Toast.makeText(SplashActivity.this, "관리자 자동로그인", Toast.LENGTH_SHORT).show();
            applicationClass.makeLog("관리자 자동로그인 : "+autoLoginID);
            finish();
        }else if(autoLoginCheck == true && !autoLoginID.matches(applicationClass.adminEmail)){
        //사용자 로그인일때 홈 화면전환
            //firebase auth login
            applicationClass.firebaseAuth.signInWithEmailAndPassword(autoLoginID, autoLoginPW);
            //사용자 홈화면으로 전환
            try {
                sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent userHomeIntent = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(userHomeIntent);
            Toast.makeText(SplashActivity.this, "사용자 자동로그인", Toast.LENGTH_SHORT).show();
            applicationClass.makeLog("사용자 자동로그인 : "+autoLoginID);
            finish();
        }else if(autoLoginCheck == false && autoLoginID.matches("")){
            // 자동 로그인 실패
            Toast.makeText(SplashActivity.this, "자동로그인 해제 상태", Toast.LENGTH_SHORT).show();
            // 로그인 화면으로 화면 전환
            Intent splashIntent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(splashIntent);
            finish();
        }
    }

}
