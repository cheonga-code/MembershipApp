package com.example.membershipapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import kr.co.bootpay.Bootpay;
import kr.co.bootpay.enums.Method;
import kr.co.bootpay.enums.PG;
import kr.co.bootpay.enums.UX;
import kr.co.bootpay.listener.CancelListener;
import kr.co.bootpay.listener.CloseListener;
import kr.co.bootpay.listener.ConfirmListener;
import kr.co.bootpay.listener.DoneListener;
import kr.co.bootpay.listener.ErrorListener;
import kr.co.bootpay.listener.ReadyListener;
import kr.co.bootpay.model.BootExtra;
import kr.co.bootpay.model.BootUser;

public class MypageActivity extends BaseActivity {

    Button btnCard, btnLogout, btnUserOut;
    TextView tvCurrentCardPrice, tvLoginUserName, tvLoginUserId;
    ImageView btnLoginInfoEdit;
    int currentUserCardPrice;
    String currentUserName, currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_mypage);

        btnCard = (Button)findViewById(R.id.btnCard);
        btnLogout = (Button)findViewById(R.id.btnLogout);
        btnUserOut = (Button)findViewById(R.id.btnUserOut);
        btnLoginInfoEdit = (ImageView)findViewById(R.id.btnLoginInfoEdit);
        tvCurrentCardPrice = (TextView)findViewById(R.id.tvCurrentCardPrice);
        tvLoginUserName = (TextView)findViewById(R.id.myLoginUserName);
        tvLoginUserId = (TextView)findViewById(R.id.myLoginUserId);

        //유저 정보 파이어베이스에서 불러오기
        loadUserData();

        //유저정보 수정 아이콘 클릭시
        btnLoginInfoEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //현재 유저 이름
                Intent intent = new Intent(MypageActivity.this, MypageEditActivity.class);
                intent.putExtra("userName", currentUserName);
                startActivityForResult(intent, 1012);
            }
        });

        //충전하기 버튼 클릭시
        btnCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //현재 잔액
                Intent intent = new Intent(MypageActivity.this, CardChargeActivity.class);
                startActivityForResult(intent, 1011);
            }
        });

        //로그아웃 버튼 클릭시
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //로그아웃 다이얼로그
                makeLogoutDialog();
            }
        });

        //회원탈퇴 버튼 클릭시
        btnUserOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //다이얼로그 띄우기
                makeDialog();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1011 && resultCode == RESULT_OK){
            //충전된 유저 카드 금액 받기
            int addCardPriceInt = data.getIntExtra("chargeCardPrice", 0);
            //현재잔액 + 충전잔액
            currentUserCardPrice = currentUserCardPrice + addCardPriceInt;
            //setText() 하기
            tvCurrentCardPrice.setText(currentUserCardPrice+"원");
            //데이터베이스에 변경된 카드잔액 저장
            changeUserIntData("userCardPrice", currentUserCardPrice);

        }else if(requestCode == 1012 && resultCode == RESULT_OK){
            //변경된 유저 이름 데이터 받기
            String changeUserName = data.getStringExtra("changeUserName");
            //setText() 하기
            tvLoginUserName.setText(changeUserName);
            //데이터베이스에 변경된 유저 이름 데이터 저장하기
            changeUserStringData("userName", changeUserName);
        }

    }

    @Override
    int getContentViewId() {
        return R.layout.activity_mypage;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.bottomNav04;
    }

//    public void loadSharedCardPriceData(){
//        //쉐어드에 저장된 현재카드잔액 데이터 불러와서 setText() 하기
//        currentCardPriceInt = sharedPreferences.getInt(sharedCardPriceKey, 0);
//        Log.d(TAG, "쉐어드에 저장된 카드잔액 : "+currentCardPriceInt);
//        tvCurrentCardPrice.setText(currentCardPriceInt+"원");
//    }

    //파이어베이스에서 유저 정보 불러오기
    public void loadUserData(){
        applicationClass.databaseReference.child("users")
                .child(applicationClass.EncodeString(applicationClass.loginEmail))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d(TAG, "///MypageActivity : onDataChange()///");

                        Users users = dataSnapshot.getValue(Users.class);
                        String usersKey = dataSnapshot.getKey();
                        Log.d(TAG, "usersKey(확인) : " + usersKey);

                        currentUserName = users.getUserName();
                        currentUserId = users.getUserId();
                        currentUserCardPrice = users.getUserCardPrice();
                        Log.d(TAG, "현재 유저 닉네임 (1) : " + currentUserName);
                        Log.d(TAG, "현재 유저 멤버십 카드 잔액 (1) : " + currentUserCardPrice);

                        //setText()하기
                        tvLoginUserName.setText(currentUserName);
                        tvLoginUserId.setText(currentUserId);
                        tvCurrentCardPrice.setText(currentUserCardPrice+"원");
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
    }

    //파이어베이스에 있는 유저 정보 업데이트하기
    public void changeUserIntData(String changeKey , int changeValue){

        Map<String, Object> userValues = new HashMap<String,Object>();
        userValues.put(changeKey, changeValue);

        applicationClass.databaseReference.child("users")
                .child(applicationClass.EncodeString(applicationClass.loginEmail))
                .updateChildren(userValues);
    }

    public void changeUserStringData(String changeKey , String changeValue){

        Map<String, Object> userValues = new HashMap<String,Object>();
        userValues.put(changeKey, changeValue);

        applicationClass.databaseReference.child("users")
                .child(applicationClass.EncodeString(applicationClass.loginEmail))
                .updateChildren(userValues);
    }

//    public void saveSharedCardPriceData(){
//        editor.putInt(sharedCardPriceKey, currentCardPriceInt);
//        editor.apply();
//        Log.d(TAG, "쉐어드에 저장한 카드잔액 : "+currentCardPriceInt);
//    }

    //로그아웃 다이얼로그
    public void makeLogoutDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MypageActivity.this);
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
                                Intent loginIntent = new Intent(MypageActivity.this, LoginActivity.class);
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

    //회원탈퇴 다이얼로그
    public void makeDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MypageActivity.this);
        builder.setTitle("정말 회원을 탈퇴하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("LogActivity", "다이얼로그 확인 선택");

                                applicationClass.firebaseUser.delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                applicationClass.makeToast("계정이 삭제되었습니다.");
                                                finish();
                                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                                Log.d(TAG, "회원탈퇴 완료");
                                            }
                                        });
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
