package com.example.membershipapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;

public class AdminHomeActivity extends LogActivity implements View.OnClickListener {

    Button btnAdminMenuOrderlist, btnAdminMenuSetting, btnAdminLogout;
    FloatingActionButton btnAdminQRcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        //파이어베이스 사용자 확인하기
        applicationClass.makeLog("파이어베이스 현재 사용자는 누구 : "+applicationClass.firebaseAuth.getCurrentUser().getEmail());

        //FCM token 서버에서 받아오기
        passPushTokenToServer();

        btnAdminMenuOrderlist = (Button)findViewById(R.id.btnAdminMenuOrderlist);
        btnAdminMenuSetting = (Button)findViewById(R.id.btnAdminMenuSetting);
        btnAdminLogout = (Button)findViewById(R.id.btnAdminLogout);
        btnAdminQRcode = (FloatingActionButton)findViewById(R.id.btnAdminQRcode);

        btnAdminMenuOrderlist.setOnClickListener(this);
        btnAdminMenuSetting.setOnClickListener(this);
        btnAdminLogout.setOnClickListener(this);
        btnAdminQRcode.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            //주문관리
            case R.id.btnAdminMenuOrderlist:
                Intent orderListIntent = new Intent(this, AdminOrderlistWaitActivity.class);
                startActivityForResult(orderListIntent, 10);
                break;

            //메뉴관리
            case R.id.btnAdminMenuSetting:
                Intent menuSettingIntent = new Intent(this, AdminMenulistActivity.class);
                startActivityForResult(menuSettingIntent, 11);
                break;

            //QR코드
            case R.id.btnAdminQRcode:
                Intent adminQRScannerIntent = new Intent(this, AdminQrScannerActivity.class);
                startActivityForResult(adminQRScannerIntent, 12);
                break;

            //로그아웃
            case R.id.btnAdminLogout:
                //로그아웃 다이얼로그
                makeLogoutDialog();
                break;
        }
    }

    //서버에서 토큰 받아오기
    public void passPushTokenToServer(){
        final String adminEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String deviceToken = instanceIdResult.getToken();

                //토큰값 저장
                Map<String, Object> map = new HashMap<>();
                map.put("adminToken", deviceToken);

                applicationClass.databaseReference
                        .child("Admin").child(applicationClass.EncodeString(adminEmail))
                        .setValue(map);

                Log.d(TAG, "관리자 디바이스 토큰 : " + deviceToken);
            }
        });
    }

    public void makeLogoutDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminHomeActivity.this);
        builder.setTitle("로그아웃 하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //쉐어드에 있는 자동로그인 관련 값 초기화
                                applicationClass.editor.putBoolean("autoLoginCheck", false);
                                applicationClass.editor.putString("autoLoginID", "");
                                applicationClass.editor.putString("autoLoginPW", "");
                                applicationClass.editor.apply();

                                //파이어베이스에서 사용자 로그아웃
                                applicationClass.firebaseAuth.getInstance().signOut();

                                Log.d(TAG, "로그아웃 완료");
                                //로그인 페이지로 화면전환
                                Intent loginIntent = new Intent(AdminHomeActivity.this, LoginActivity.class);
                                startActivity(loginIntent);
                                finish();
                            }
                        })
                .setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "로그아웃 취소");
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}
