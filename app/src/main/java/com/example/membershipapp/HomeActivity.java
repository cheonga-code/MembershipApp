package com.example.membershipapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends BaseActivity implements View.OnClickListener{

//    String TAG = "stampLog";

    Button btnOrder, btnReview, btnSeat, btnNews;
    ImageView stamp01, stamp02, stamp03, stamp04, stamp05, stamp06, stamp07, stamp08, stamp09, stamp10;
    TextView tvHomeCardPrice, tvHomeName;
    int currentUserCardPrice;       //현재유저카드잔액
    public int currentUserStampCount;      //현재유저스탬프갯수
    String currentUserName, currentUserId;         //현재유저이름

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home);

        //파이어베이스 사용자 확인하기
        applicationClass.makeLog("파이어베이스 현재 사용자는 누구 : "+applicationClass.firebaseAuth.getCurrentUser().getEmail());

        btnOrder = (Button)findViewById(R.id.btnOrder);
//        btnReview = (Button)findViewById(R.id.btnReview);
//        btnSeat = (Button)findViewById(R.id.btnSeat);
        btnNews = (Button)findViewById(R.id.btnNews);
        tvHomeCardPrice = (TextView)findViewById(R.id.tvHomeCardPrice);
        tvHomeName = (TextView)findViewById(R.id.tvHomeName);
        //스탬프
        stamp01 = (ImageView)findViewById(R.id.stamp01);
        stamp02 = (ImageView)findViewById(R.id.stamp02);
        stamp03 = (ImageView)findViewById(R.id.stamp03);
        stamp04 = (ImageView)findViewById(R.id.stamp04);
        stamp05 = (ImageView)findViewById(R.id.stamp05);
        stamp06 = (ImageView)findViewById(R.id.stamp06);
        stamp07 = (ImageView)findViewById(R.id.stamp07);
        stamp08 = (ImageView)findViewById(R.id.stamp08);
        stamp09 = (ImageView)findViewById(R.id.stamp09);
        stamp10 = (ImageView)findViewById(R.id.stamp10);

        btnOrder.setOnClickListener(this);
//        btnReview.setOnClickListener(this);
//        btnSeat.setOnClickListener(this);
        btnNews.setOnClickListener(this);

        //쉐어드에서 현재잔액&스탬프수량 데이터 불러오기
        loadUserData();

        //FCM token 서버에서 받아오기
        passPushTokenToServer();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnOrder:
                Intent coffeeOrderIntent = new Intent(this, CoffeelistActivity.class);
                startActivityForResult(coffeeOrderIntent, 1001);
                break;
//            case R.id.btnReview:
//                Intent inicisItent = new Intent(this, BootpayActivity.class);
//                startActivity(inicisItent);
//                break;
//            case R.id.btnSeat:
//                Intent seatIntent = new Intent(this, SeatActivity.class);
//                startActivity(seatIntent);
//                break;
            case R.id.btnNews:
                Intent newsIntent = new Intent(this, EventNewsActivity.class);
                startActivityForResult(newsIntent, 1004);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    int getContentViewId() {
        return  R.layout.activity_home;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.bottomNav01;
    }

//    public void loadSharedData(){
//        //쉐어드에 저장된 현재카드잔액 데이터 불러와서 setText() 하기
//        currentCardPriceInt = sharedPreferences.getInt(sharedCardPriceKey, 0);
//        currentStampInt = sharedPreferences.getInt(sharedStampKey,0);
//        Log.d(TAG, "쉐어드에 저장된 카드잔액 : "+currentCardPriceInt);
//        Log.d(TAG, "쉐어드에 저장된 스탬프수량 : "+currentStampInt);
//    }

    //서버에서 토큰 받아오기
    public void passPushTokenToServer(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String deviceToken = instanceIdResult.getToken();

                //토큰값 저장
                Map<String, Object> map = new HashMap<>();
                map.put("userToken", deviceToken);

                applicationClass.databaseReference
                        .child("users").child(applicationClass.EncodeString(applicationClass.loginEmail))
                        .updateChildren(map);

                applicationClass.userToken = deviceToken;

                Log.d(TAG, "현재 디바이스 토큰 (1) : " + deviceToken);
            }
        });
    }

    //파이어베이스에 저장된 유저 정보 가져오기
    public void loadUserData(){

        applicationClass.databaseReference.child("users")
            .child(applicationClass.EncodeString(applicationClass.loginEmail))
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "///HomeActivity : onDataChange()///");

//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //초기화
//                    currentCartCount = snapshot.getValue();
                    Users users = dataSnapshot.getValue(Users.class);
                    String usersKey = dataSnapshot.getKey();
                    Log.d(TAG, "usersKey(확인) : " + usersKey);

                    //이메일키 , -> . 로 디코딩
//                    String key = applicationClass.DecodeString(usersKey);

//                    if (applicationClass.loginEmail.matches(key)) {

//                        currentUserStampCount = 0;

                        currentUserCardPrice = users.getUserCardPrice();
                        currentUserStampCount = users.getUserStampCount();
                        currentUserName = users.getUserName();
                        currentUserId = users.getUserId();
                        Log.d(TAG, "현재 유저 이름 (1) : " + currentUserName);
                        Log.d(TAG, "현재 유저 아이디 (1) : " + currentUserId);
                        Log.d(TAG, "현재 유저 멤버십 카드 잔액 (1) : " + currentUserCardPrice);
                        Log.d(TAG, "현재 유저 스탬프 갯수 (1) : " + currentUserStampCount);

                        applicationClass.loginID = currentUserId;
                        applicationClass.loginName = currentUserName;

//                        break;

//                    }else{
//                        applicationClass.makeLog("applicationClass.loginEmail.matches(key) not!!!");
//                    }
//                }

//                setText()하기
                tvHomeName.setText(currentUserName);
                tvHomeCardPrice.setText(currentUserCardPrice + "원");

                //TODO: 이미지 셋팅을 이때 하는게 맞나?? 데이터 불러오고 나서.. 흠..
                //스탬프 갯수가 10개 이상일때
                if(currentUserStampCount >= 10){
                    Log.d(TAG, "///스탬프 코드 진입///");
                    //현재 스탬프 갯수 = 기존 스탬프갯수 - 10
                    currentUserStampCount = currentUserStampCount - 10;
                    Log.d(TAG, "10을 뺀 현재 스탬프 갯수 (3) : "+currentUserStampCount);

                    //파이어베이스 유저 정보의 쿠폰카운트에 변경된 쿠폰카운트 데이터 업데이트
                    changeUserData("userStampCount", currentUserStampCount);

                    //쿠폰리스트 데이터 저장
                    savaCouponlistData();
                }

                //setImg();
                stampImgSetColor();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }

        });

        applicationClass.databaseReference.child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "///HomeActivity : onChildAdded()///");

//                //스탬프 갯수가 10개 이상일때
//                if(currentUserStampCount >= 10){
//                    Log.d(TAG, "///스탬프 코드 진입///");
//                    //현재 스탬프 갯수 = 기존 스탬프갯수 - 10
//                    currentUserStampCount = currentUserStampCount - 10;
//                    Log.d(TAG, "10을 뺀 현재 스탬프 갯수 (3) : "+currentUserStampCount);
//
//                    //파이어베이스 유저 정보의 쿠폰카운트에 변경된 쿠폰카운트 데이터 업데이트
//                    changeUserData("userStampCount", currentUserStampCount);
//
//                    //쿠폰리스트 데이터 저장
//                    savaCouponlistData();
//                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "///HomeActivity : onChildChanged()///");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "///HomeActivity : onChildRemoved()///");

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "///HomeActivity : onChildMoved()///");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "///HomeActivity : onCancelled()///");
            }
        });
    }

    //파이어베이스 유저 정보 데이터 저장 (값 변경)
    public void changeUserData(String changeKey , int changeValue){

        Map<String, Object> userValues = new HashMap<String,Object>();
        userValues.put(changeKey, changeValue);

        applicationClass.databaseReference.child("users")
                .child(applicationClass.EncodeString(applicationClass.loginEmail))
                .updateChildren(userValues);
    }

    private void savaCouponlistData() {

        Log.d(TAG, "///savaCouponlistData() 진입///");

        //쿠폰이 생성된 오늘 날짜
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = dateFormat.format(calendar.getTime());
        Log.d(TAG, "쿠폰 생성 오늘 날짜 (3) : "+currentDate);

        //오늘 날짜에 월, 일을 추가하는 코드
        calendar.add(Calendar.MONTH, 1);    //한달 뒤 날짜
//        calendar.add(Calendar.DATE, +3);    //3일 후 날짜
        String afterOneMonthDate = dateFormat.format(calendar.getTime());
        Log.d(TAG, "쿠폰 생성 한달뒤 날짜 (3) : "+afterOneMonthDate);

        //쿠폰리스트 DTO 객체
        CouponlistDTO couponlistDTO = new CouponlistDTO();
        couponlistDTO.couponCategory = "스탬프쿠폰";
        couponlistDTO.couponName = "스탬프 10개 적립 무료음료 쿠폰";
        couponlistDTO.couponMakeDate = currentDate;
        couponlistDTO.couponDeadline = afterOneMonthDate; //유효기간 : 한달뒤
        couponlistDTO.couponUseState = false;

        applicationClass.databaseReference.child("couponlist")
                .child(applicationClass.EncodeString(applicationClass.loginEmail))
                .child(currentDate).setValue(couponlistDTO);
        Log.d(TAG, "///쿠폰 적립 완료 (3)///");
    }

    public void stampImgSetColor(){
        if(currentUserStampCount == 0){
        }else if(currentUserStampCount == 1){
            stamp01.setImageResource(R.drawable.stamp_coffee_on);
        }else if(currentUserStampCount == 2){
            stamp01.setImageResource(R.drawable.stamp_coffee_on);
            stamp02.setImageResource(R.drawable.stamp_coffee_on);
        }else if(currentUserStampCount == 3){
            stamp01.setImageResource(R.drawable.stamp_coffee_on);
            stamp02.setImageResource(R.drawable.stamp_coffee_on);
            stamp03.setImageResource(R.drawable.stamp_coffee_on);
        }else if(currentUserStampCount == 4){
            stamp01.setImageResource(R.drawable.stamp_coffee_on);
            stamp02.setImageResource(R.drawable.stamp_coffee_on);
            stamp03.setImageResource(R.drawable.stamp_coffee_on);
            stamp04.setImageResource(R.drawable.stamp_coffee_on);
        }else if(currentUserStampCount == 5){
            stamp01.setImageResource(R.drawable.stamp_coffee_on);
            stamp02.setImageResource(R.drawable.stamp_coffee_on);
            stamp03.setImageResource(R.drawable.stamp_coffee_on);
            stamp04.setImageResource(R.drawable.stamp_coffee_on);
            stamp05.setImageResource(R.drawable.stamp_coffee_on);
        }else if(currentUserStampCount == 6){
            stamp01.setImageResource(R.drawable.stamp_coffee_on);
            stamp02.setImageResource(R.drawable.stamp_coffee_on);
            stamp03.setImageResource(R.drawable.stamp_coffee_on);
            stamp04.setImageResource(R.drawable.stamp_coffee_on);
            stamp05.setImageResource(R.drawable.stamp_coffee_on);
            stamp06.setImageResource(R.drawable.stamp_coffee_on);
        }else if(currentUserStampCount == 7){
            stamp01.setImageResource(R.drawable.stamp_coffee_on);
            stamp02.setImageResource(R.drawable.stamp_coffee_on);
            stamp03.setImageResource(R.drawable.stamp_coffee_on);
            stamp04.setImageResource(R.drawable.stamp_coffee_on);
            stamp05.setImageResource(R.drawable.stamp_coffee_on);
            stamp06.setImageResource(R.drawable.stamp_coffee_on);
            stamp07.setImageResource(R.drawable.stamp_coffee_on);
        }else if(currentUserStampCount == 8){
            stamp01.setImageResource(R.drawable.stamp_coffee_on);
            stamp02.setImageResource(R.drawable.stamp_coffee_on);
            stamp03.setImageResource(R.drawable.stamp_coffee_on);
            stamp04.setImageResource(R.drawable.stamp_coffee_on);
            stamp05.setImageResource(R.drawable.stamp_coffee_on);
            stamp06.setImageResource(R.drawable.stamp_coffee_on);
            stamp07.setImageResource(R.drawable.stamp_coffee_on);
            stamp08.setImageResource(R.drawable.stamp_coffee_on);
        }else if(currentUserStampCount == 9){
            stamp01.setImageResource(R.drawable.stamp_coffee_on);
            stamp02.setImageResource(R.drawable.stamp_coffee_on);
            stamp03.setImageResource(R.drawable.stamp_coffee_on);
            stamp04.setImageResource(R.drawable.stamp_coffee_on);
            stamp05.setImageResource(R.drawable.stamp_coffee_on);
            stamp06.setImageResource(R.drawable.stamp_coffee_on);
            stamp07.setImageResource(R.drawable.stamp_coffee_on);
            stamp08.setImageResource(R.drawable.stamp_coffee_on);
            stamp09.setImageResource(R.drawable.stamp_coffee_on);
        }else if(currentUserStampCount == 10){
            stamp01.setImageResource(R.drawable.stamp_coffee_on);
            stamp02.setImageResource(R.drawable.stamp_coffee_on);
            stamp03.setImageResource(R.drawable.stamp_coffee_on);
            stamp04.setImageResource(R.drawable.stamp_coffee_on);
            stamp05.setImageResource(R.drawable.stamp_coffee_on);
            stamp06.setImageResource(R.drawable.stamp_coffee_on);
            stamp07.setImageResource(R.drawable.stamp_coffee_on);
            stamp08.setImageResource(R.drawable.stamp_coffee_on);
            stamp09.setImageResource(R.drawable.stamp_coffee_on);
            stamp10.setImageResource(R.drawable.stamp_coffee_on);
        }
    }

}
