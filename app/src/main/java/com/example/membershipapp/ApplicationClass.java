package com.example.membershipapp;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ApplicationClass extends Application {
    //컴포넌트들 사이에 공통으로 사용할 수 있게 해주는 클래스를 제공함

    String TAG = "LogActivity";
    String classname = getClass().getSimpleName().trim();

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseStorage firebaseStorage;
    StorageReference storageRef;
    FirebaseDatabase firebaseDatabase;
    FirebaseFirestore firebaseFirestore;
    DatabaseReference databaseReference;

    String loginID, loginUID, loginEmail, loginName, userToken;
    String adminEmail = "admin@naver.com";

    //쉐어드관련
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String sharedFileNameStr = "sharedFile";
    String sharedCardPriceKey = "chargeCardPrice";
    String sharedStampKey = "stampQuantity";

    @Override
    public void onCreate() {
        super.onCreate();

        //firestore 인스턴스 초기화
        firebaseFirestore  = FirebaseFirestore.getInstance();
        //파이어베이스 인증 객체 선언
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //파이어베이스 저장소 객체 선언
        firebaseStorage = FirebaseStorage.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        if(firebaseUser != null){
            makeLog("파이어베이스 유저 객체가 null 값이 아님");
            loginUID = firebaseUser.getUid();
            loginEmail = firebaseUser.getEmail();
            Log.d(TAG, "로그인한 UID : " + loginUID);
            Log.d(TAG, "로그인한 loginEmail : " + loginEmail);
        }else {
            makeLog("파이어베이스 유저 객체가 null 값임");
        }

//        loginID = databaseReference.child("users").getKey();

        //쉐어드 관련 객체 선언
        sharedPreferences = getSharedPreferences(sharedFileNameStr, MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    //컴포넌트가 실행되는 동안 단말의 화면이 바뀌면 시스템이 실행한다
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    //로그
    public void makeLog(String strData){
        Log.d(TAG, classname+"-"+strData);
    }

    //토스트메세지
    public void makeToast(String str){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    //파이어베이스 users 데이터 key 값 저장할때 . 안되서 , 로 바꿔서 저장함
    public String EncodeString(String string) {
        return string.replace(".", ",");
    }

    //key값 불러올때는 , -> . 로 변환해야함
    public String DecodeString(String string) {
        return string.replace(",", ".");
    }

}
