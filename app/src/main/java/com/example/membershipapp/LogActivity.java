package com.example.membershipapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LogActivity extends AppCompatActivity {

    //쉐어드관련
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String sharedFileNameStr = "sharedFile";
    String sharedCardPriceKey = "chargeCardPrice";
    String sharedStampKey = "stampQuantity";

    //로그관련
    String classname = getClass().getSimpleName().trim();
    String TAG = "LogActivity";

    ApplicationClass applicationClass;

    //생명주기
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, classname+"-onCreate");

        sharedPreferences = getSharedPreferences(sharedFileNameStr, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        applicationClass = (ApplicationClass)getApplicationContext();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, classname+"-onStart");
    }

    @Override
    protected void onResume() {
        Log.d(TAG, classname+"-onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, classname+"-onPause");
    }

    @Override
    protected void onStop() {
        Log.d(TAG, classname+"-onStop");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, classname+"+onRestart");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, classname+"=onDestroy");
        super.onDestroy();
    }
}
