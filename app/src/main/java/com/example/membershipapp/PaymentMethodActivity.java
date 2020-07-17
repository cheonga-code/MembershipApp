package com.example.membershipapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import kr.co.bootpay.Bootpay;
import kr.co.bootpay.BootpayAnalytics;
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
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PaymentMethodActivity extends LogActivity implements View.OnClickListener{

    Button btnPaymentMethodMembershipCard, btnPaymentMethodBootpayPhone, btnPaymentMethodBootpayCard, btnPaymentMethodCoupon;

    String orderDate, orderTime;                   //주문한 날짜 & 시간
    String firstOrderMenuName;

    String orderMenuName;
    int totalOrderPrice;                //결제할 총 금액
    int totalOrderQuantity;             //결제할 메뉴 수량
    int totalOrderBeverageQuantity;     //결제할 음료만 수량
    int currentUserCardPrice;           //현재 멤버십 카드 잔액
    int currentUserStampCount;          //현재 유저 스탬프 갯수
    Boolean couponUseState;             //쿠폰 사용 상태값
    String useCouponMakeDate, useCouponDeadline;           //사용할 쿠폰 생성날짜&유효기간

    HomeActivity homeActivity;

    ArrayList<OrderlistDTO> orderlistArrayList;
    private int stuck = 10; //부트페이 관련 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        orderlistArrayList = new ArrayList<OrderlistDTO>();

        Intent intent = getIntent();
        orderMenuName = intent.getStringExtra("orderMenuName");
        totalOrderPrice = intent.getIntExtra("totalOrderPrice", 0);
        totalOrderQuantity = intent.getIntExtra("totalOrderQuantity", 0);
        totalOrderBeverageQuantity = intent.getIntExtra("totalOrderBeverageQuantity", 0);
        couponUseState = intent.getBooleanExtra("couponUseState", false);
        useCouponMakeDate = intent.getStringExtra("useCouponMakeDate");
        useCouponDeadline = intent.getStringExtra("useCouponDeadline");
        //TODO: CartlistActivity 에서 받은 orderlistArrayList 데이터 다시 OrderlistActivity로 보내야함
        orderlistArrayList = (ArrayList<OrderlistDTO>) intent.getSerializableExtra("orderlistArrayList");

        applicationClass.makeLog("내가 결제해야할 금액 (1) : "+totalOrderPrice);
        applicationClass.makeLog("내가 결제해야할 메뉴 수량 (1) : "+totalOrderQuantity);
        applicationClass.makeLog("내가 결제해야할 음료만 수량 (1) : "+totalOrderBeverageQuantity);
        //파이어베이스에 있는 유저 데이터 불러오기 -> 카드 충전 금액/스탬프 수량
        loadUserData();

        //부트페이 : 초기설정 - 해당 프로젝트(안드로이드)의 application id 값을 설정합니다. 결제와 통계를 위해 꼭 필요합니다.
        BootpayAnalytics.init(this, "5b3d9d66396fa605ccad552a");
        //결제 방법 버튼 클릭시
        btnPaymentMethodMembershipCard = (Button)findViewById(R.id.btnPaymentMethodMembershipCard);
        btnPaymentMethodBootpayPhone = (Button)findViewById(R.id.btnPaymentMethodBootpayPhone);
        btnPaymentMethodBootpayCard = (Button)findViewById(R.id.btnPaymentMethodBootpayCard);
//        btnPaymentMethodCoupon = (Button)findViewById(R.id.btnPaymentMethodCoupon);
        btnPaymentMethodMembershipCard.setOnClickListener(this);
        btnPaymentMethodBootpayPhone.setOnClickListener(this);
        btnPaymentMethodBootpayCard.setOnClickListener(this);
//        btnPaymentMethodCoupon.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //멤버십 카드 결제
            case R.id.btnPaymentMethodMembershipCard:
                //멤버십카드 결제 다이얼로그
                makePaymentMembershipDialog();
                break;
            case R.id.btnPaymentMethodBootpayPhone:
                //휴대폰 결제 다이얼로그
                makePaymentBootpayDialog("휴대폰으로", "PHONE");
                break;
            case R.id.btnPaymentMethodBootpayCard:
                //신용카드 결제 다이얼로그
                makePaymentBootpayDialog("카드로", "CARD");
                break;
//            case R.id.btnPaymentMethodCoupon:
//                //쿠폰 결제 다이얼로그
//                makePaymentCouponDialog();
//                break;

        }
    }

    public void makePaymentMembershipDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentMethodActivity.this);
        builder.setTitle("멤버십카드로 결제하시겠습니까?")
                .setMessage("멤버십 카드 잔액 : "+currentUserCardPrice+"\n"+
                                "결제할 금액 : "+totalOrderPrice)
                .setCancelable(false)
                .setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //비밀번호 화면으로 전환
                                Intent payPwIntent = new Intent(PaymentMethodActivity.this, PaymentPasswordActivity.class);
                                startActivityForResult(payPwIntent, 999);
                            }
                        })
                .setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                applicationClass.makeLog(" 결제 취소");
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void makePaymentBootpayDialog(String paymentTitle, final String paymentBootpayMethod){
        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentMethodActivity.this);
        builder.setTitle(paymentTitle+" 결제하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //부트페이로 결제요청
                                requestBootpay(paymentBootpayMethod,orderMenuName+".. "+totalOrderQuantity+"건", totalOrderPrice);
                            }
                        })
                .setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                applicationClass.makeLog(" 결제 취소");
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

//    public void makePaymentCouponDialog(){
//        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentMethodActivity.this);
//        builder.setTitle("쿠폰으로 결제하시겠습니까?")
//                .setCancelable(false)
//                .setPositiveButton("예",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                               Intent intent = new Intent(PaymentMethodActivity.this, CouponActivity.class);
//                               intent.putExtra("accessPath", "cartlistPath");
//                               startActivityForResult(intent, 998);
//                            }
//                        })
//                .setNegativeButton("아니오",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                applicationClass.makeLog(" 결제 취소");
//                                dialog.cancel();
//                            }
//                        });
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 999 && resultCode == RESULT_OK){

            //카드 잔액이 결제할 금액보다 작은 경우
            if(currentUserCardPrice < totalOrderPrice){
                //결제 불가
                applicationClass.makeToast("멤버십 카드잔액이 부족하여 결제가 불가합니다.");
                //마이페이지 화면으로 전환 (멤버십 카드 충전할 수 있도록)
                makeMypageDialog();
            }else{
                //카드 잔액이 결제할 금액보다 큰 경우
                //결제 가능
                currentUserCardPrice = currentUserCardPrice - totalOrderPrice;
                //남은 카드 잔액 파이어베이스에 저장
                saveUserData("userCardPrice", currentUserCardPrice);

//                //주문한 음료 수량만큼 스탬프 적립 수량 더해서 파이어베이스에 데이터 반영하기
//                currentUserStampCount = currentUserStampCount + totalOrderBeverageQuantity;
//                saveUserData("userStampCount", currentUserStampCount);

                //쿠폰 사용 상태값 받아서 true 이면 쿠폰리스트 데이터에서 삭제하기
                if(couponUseState==true){
                    //이용가능한 쿠폰리스트에서 삭제처리 + 히스토리 쿠폰리스트에 데이터 추가
                    removeUserAvailableCouponData(applicationClass.loginEmail, useCouponMakeDate);
                }

                //결제가 완료됬을 때 그 이후의 프로세스
                //주문내역 파이어베이스에 저장 & 장바구니 초기화 & 홈화면전환
                payCompleteAfterOrderlistDataSave();
            }

        }
    }

    //파이어베이스에 있는 사용가능한 쿠폰 리스트 제거하기
    public void removeUserAvailableCouponData(String userEmail , String couponMakeDate){

//        Map<String, Object> userValues = new HashMap<String,Object>();
//        userValues.put(changeKey, changeValue);

        applicationClass.databaseReference.child("couponlist")
                .child(applicationClass.EncodeString(userEmail))
                .child(couponMakeDate).removeValue();

        //히스토리 쿠폰리스트에 데이터 추가
        addUserHistoryCouponData(userEmail);
    }

    public void addUserHistoryCouponData(String userEmail) {

        Log.d(TAG, "///addCouponHistoryData() 진입///");

        //쿠폰을 사용한 오늘 날짜
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = dateFormat.format(calendar.getTime());
        Log.d(TAG, "쿠폰 사용 오늘 날짜 (3) : "+currentDate);

        //쿠폰리스트 DTO 객체
        CouponHistoryDTO couponHistoryDTO = new CouponHistoryDTO();
        couponHistoryDTO.couponUseName = "스탬프 10개 적립 무료음료 쿠폰";
        couponHistoryDTO.couponUseDate = currentDate+" 사용";
        couponHistoryDTO.couponUseDeadline = useCouponDeadline;

        applicationClass.databaseReference.child("couponHistoryList")
                .child(applicationClass.EncodeString(userEmail))
                .child(currentDate).setValue(couponHistoryDTO);
        Log.d(TAG, "///쿠폰 히스토리 완료 (3)///");
    }

    //결제가 완료됬을 때 주문목록을 저장하는 프로세스
    public void payCompleteAfterOrderlistDataSave(){
        //파이어베이스에 주문내역 데이터 저장하기
        //현재 시간 데이터 구하기
        currentTimeData();
        //사용자 주문관리 orderlist -> UsersOrderlist 로 네이밍 바꾸기
        saveOrderlistData("orderlist", applicationClass.EncodeString(applicationClass.loginEmail), orderDate, "orderlist", "대기");
        //관리자 주문관리
        saveOrderlistData("AdminOrderlist", orderDate, applicationClass.EncodeString(applicationClass.loginEmail),"orderlist", "대기");
    }

    //주문목록 저장을 완료했을 때 장바구니초기화&화면전환 프로세스
    public void orderlistDataSaveCompleteAfterProcess(){
        //파이어베이스에서 장바구니 데이터 초기화 시키기
        resetCartlistData();

        //관리자에게 주문 푸시알림 보내기
        if(totalOrderQuantity == 1){
            SendNotification.sendToAdminFCM(applicationClass.loginName, firstOrderMenuName+" 1개 주문");
        }else{
            SendNotification.sendToAdminFCM(applicationClass.loginName, firstOrderMenuName+" 외 "+totalOrderQuantity+"개 주문");
        }

        //주문목록 페이지로 화면전환
        Intent homeIntent = new Intent(PaymentMethodActivity.this, HomeActivity.class);
//                //TODO: orderlistDTOArraylist 데이터 intent로 전달
//                orderlistIntent.putExtra("orderlistArrayList", orderlistArrayList);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
        finish();

        applicationClass.makeToast(" 결제가 완료되었습니다.");
    }


    public void loadUserData(){
        //데이터 저장소에서 충전된 카드금액 데이터 불러오기
        applicationClass.databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                applicationClass.makeLog("///PaymentMethodActivity : onDataChange()///");
                //초기화
                currentUserCardPrice = 0;
                currentUserStampCount = 0;

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    currentCartCount = snapshot.getValue();
                    Users users = snapshot.getValue(Users.class);
                    String usersKey = snapshot.getKey();
                    applicationClass.makeLog("usersKey(확인) : "+usersKey);

                    //이메일키 , -> . 로 디코딩
                    String key = applicationClass.DecodeString(usersKey);

                    if(applicationClass.loginEmail.equals(key)){
                        currentUserCardPrice = users.getUserCardPrice();
                        currentUserStampCount = users.getUserStampCount();
                        applicationClass.makeLog("현재 멤버십 카드 잔액 (2) : "+currentUserCardPrice);
                        applicationClass.makeLog("현재 유저 스탬프 갯수 (2) : "+currentUserStampCount);

                        break;
                    }
//                    applicationClass.makeLog("currentCartCount : " + snapshot.getValue());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //파이어베이스 유저 정보 데이터 저장 (값 변경)
    public void saveUserData(String changeKey , int changeValue){
        applicationClass.databaseReference.child("users")
                .child(applicationClass.EncodeString(applicationClass.loginEmail))
                .child(changeKey).setValue(changeValue);
    }

    //현재 날짜와 시간 데이터 받아오기
    public void currentTimeData(){
        //TODO: 주문시간도 이때 저장 !!
        //주문한 날짜와 시간 데이터
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String getDate = simpleDateFormat.format(mDate);
        String getTime = timeFormat.format(mDate);
        orderDate = getDate; //orderDate = 결제한 주문날짜+시간
        orderTime = getTime; //orderTime = 주문한 현재시간
    }

//    //사용자가 주문리스트 저장하기
//    // TODO: runTransaction test중
//    public void saveOrderInfoStringData(String topKey, String secondKeyStr, String thirdKeyStr, String key, String value){
//
////        applicationClass.databaseReference.child(topKey)
////                .child(secondKeyStr)
////                .child(thirdKeyStr).child(key).setValue(value);
//
//        applicationClass.databaseReference.child(topKey)
//                .runTransaction(new Transaction.Handler() {
//                    @NonNull
//                    @Override
//                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
//                        applicationClass.makeLog("///PaymentMethodActivity : postTransaction:doTransaction:///");
//
//                        return Transaction.success(mutableData);
//                    }
//
//                    @Override
//                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
//                        applicationClass.makeLog("///PaymentMethodActivity : postTransaction:onComplete:///");
//                        // Transaction completed
//                        if(b==true){
//                            Log.d(TAG, "///aaa");
//
//                        }else {
//                            Log.d(TAG, "///bbb");
//                            Log.d(TAG, "///postTransaction:onComplete:" + databaseError);
//                        }
//                    }
//                });
//    }
//    //TODO: runTransaction test중 여기까지..

    //파이어베이스 주문 정보 데이터 저장
    public void saveOrderlistData(final String topKeyStr, String secondKeyStr, String thirdKeyStr, String subKeyStr, String orderStateStr){

        //주문정보 저장 (주문정보 : 주문한유저아이디, 주문한날짜, 주문한시간, 주문한총결제금액, 주문한메뉴수량, 주문한음료만수량)
        saveOrderInfoStringData(topKeyStr, secondKeyStr, thirdKeyStr, "orderState", orderStateStr);
        saveOrderInfoStringData(topKeyStr, secondKeyStr, thirdKeyStr,  "orderName", applicationClass.loginName);
        saveOrderInfoStringData(topKeyStr, secondKeyStr, thirdKeyStr,  "orderEmail", applicationClass.loginEmail);
        saveOrderInfoStringData(topKeyStr, secondKeyStr, thirdKeyStr,  "orderDate", orderDate);
        saveOrderInfoStringData(topKeyStr, secondKeyStr, thirdKeyStr,  "orderTime", orderTime);
        saveOrderInfoIntData(topKeyStr, secondKeyStr, thirdKeyStr,  "totalOrderPrice", totalOrderPrice);
        saveOrderInfoIntData(topKeyStr, secondKeyStr, thirdKeyStr,  "totalOrderQuantity", totalOrderQuantity);
        saveOrderInfoIntData(topKeyStr, secondKeyStr, thirdKeyStr, "totalOrderBeverageQuantity", totalOrderBeverageQuantity);

        //주문목록 리스트 저장
        applicationClass.makeLog("orderlistArrayList.size() : "+orderlistArrayList.size());

        for(int i=0 ; i<orderlistArrayList.size() ; i++){

            OrderlistDTO orderlistDTO = new OrderlistDTO();

            applicationClass.makeLog("주문list : "+orderlistArrayList.get(i).getOrderMenuItemName());
            String orderMenuCategory = orderlistArrayList.get(i).getOrderMenuItemCategory();
            String orderMenuName = orderlistArrayList.get(i).getOrderMenuItemName();
            int orderMenuPrice = orderlistArrayList.get(i).getOrderMenuItemPrice();
            int orderMenuQuantity = orderlistArrayList.get(i).getOrderMenuItemQuantity();

            //첫번째 메뉴의 이름
            firstOrderMenuName = orderlistArrayList.get(0).getOrderMenuItemName();

            orderlistDTO.setOrderMenuItemCategory(orderMenuCategory);
            orderlistDTO.setOrderMenuItemName(orderMenuName);
            orderlistDTO.setOrderMenuItemPrice(orderMenuPrice);
            orderlistDTO.setOrderMenuItemQuantity(orderMenuQuantity);

            //로그인이메일을 키값으로 저장할때는 . -> , 로 인코딩해서 저장함
            applicationClass.databaseReference.child(topKeyStr)
                    .child(secondKeyStr).child(thirdKeyStr).child(subKeyStr).child("orderItem"+i).setValue(orderlistDTO)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if(topKeyStr.contentEquals("AdminOrderlist")){
//                                //파이어베이스에서 장바구니 데이터 초기화 시키기
//                                resetCartlistData();
//
//                                //관리자에게 주문 푸시알림 보내기
//                                if(totalOrderQuantity == 1){
//                                    SendNotification.sendToAdminFCM(applicationClass.loginName, firstOrderMenuName+" 1개 주문");
//                                }else{
//                                    SendNotification.sendToAdminFCM(applicationClass.loginName, firstOrderMenuName+" 외 "+totalOrderQuantity+"개 주문");
//                                }
//
//                                //주문목록 페이지로 화면전환
//                                Intent homeIntent = new Intent(PaymentMethodActivity.this, HomeActivity.class);
////                               //TODO: orderlistDTOArraylist 데이터 intent로 전달
////                orderlistIntent.putExtra("orderlistArrayList", orderlistArrayList);
//                                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(homeIntent);
//                                finish();

                                //주문목록 데이터 저장 후 장바구니 초기화 & 관리자에게 푸시알람 & 홈화면으로 전환 하는 프로세스
                                orderlistDataSaveCompleteAfterProcess();
                            }
                        }
                    });

        }
    }


    //파이어베이스 주문리스트 데이터 저장
    //string형 데이터 저장
    public void saveOrderInfoStringData(String topKey, String secondKeyStr, String thirdKeyStr, String key, String value){
        applicationClass.databaseReference.child(topKey)
                .child(secondKeyStr)
                .child(thirdKeyStr).child(key).setValue(value);
    }
    //int형 데이터 저장
    public void saveOrderInfoIntData(String topKey, String secondKeyStr, String thirdKeyStr, String key, int value){
        applicationClass.databaseReference.child(topKey)
                .child(secondKeyStr)
                .child(thirdKeyStr).child(key).setValue(value);
    }

    //파이어베이스 장바구니 데이터 리셋 (삭제)
    public void resetCartlistData(){
        applicationClass.databaseReference.child("cartlist")
                .child(applicationClass.EncodeString(applicationClass.loginEmail))
                .removeValue();

        applicationClass.makeLog("///장바구니 데이터 삭제 완료///");

    }

    public void makeMypageDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentMethodActivity.this);
        builder.setTitle("충전하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //마이페이지로 화면전환
                                Intent mypageIntent = new Intent(PaymentMethodActivity.this, MypageActivity.class);
                                startActivity(mypageIntent);
                                finish();
                            }
                        })
                .setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                applicationClass.makeLog(" 다이얼로그 취소");
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void requestBootpay(String payMethodStr, String itemNameStr, int itemPriceInt){
        // 결제호출
        BootUser bootUser = new BootUser().setPhone("010-7476-7126");
        BootExtra bootExtra = new BootExtra().setQuotas(new int[] {0,2,3});

        kr.co.bootpay.Bootpay.init(getFragmentManager())
                .setApplicationId("5b3d9d66396fa605ccad552a") // 해당 프로젝트(안드로이드)의 application id 값
                .setPG(PG.INICIS) // 결제할 PG 사
                .setMethod(Method.valueOf(payMethodStr)) // 결제수단
                .setContext(this)
                .setBootUser(bootUser)
                .setBootExtra(bootExtra)
                .setUX(UX.PG_DIALOG)
//                .setUserPhone("010-1234-5678") // 구매자 전화번호
                .setName(itemNameStr) // 결제할 상품명
                .setOrderId("1234") // 결제 고유번호expire_month
                .setPrice(itemPriceInt) // 결제할 금액
                .addItem("마우's 스", 1, "ITEM_CODE_MOUSE", 100) // 주문정보에 담길 상품정보, 통계를 위해 사용
                .addItem("키보드", 1, "ITEM_CODE_KEYBOARD", 200, "패션", "여성상의", "블라우스") // 주문정보에 담길 상품정보, 통계를 위해 사용
                .onConfirm(new ConfirmListener() { // 결제가 진행되기 바로 직전 호출되는 함수로, 주로 재고처리 등의 로직이 수행
                    @Override
                    public void onConfirm(@Nullable String message) {
                        if (0 < stuck) kr.co.bootpay.Bootpay.confirm(message); // 재고가 있을 경우.
                        else Bootpay.removePaymentWindow(); // 재고가 없어 중간에 결제창을 닫고 싶을 경우
                        Log.d("confirm", message);
                    }
                })
                .onDone(new DoneListener() { // 결제완료시 호출, 아이템 지급 등 데이터 동기화 로직을 수행합니다
                    @Override
                    public void onDone(@Nullable String message) {
                        Log.d("done", message);
                        //결제가 완료됬을 때 그 이후의 프로세스
                        //주문내역 파이어베이스에 저장 & 장바구니 초기화 & 홈화면전환
                        payCompleteAfterOrderlistDataSave();
                    }
                })
                .onReady(new ReadyListener() { // 가상계좌 입금 계좌번호가 발급되면 호출되는 함수입니다.
                    @Override
                    public void onReady(@Nullable String message) {
                        Log.d("ready", message);
                    }
                })
                .onCancel(new CancelListener() { // 결제 취소시 호출
                    @Override
                    public void onCancel(@Nullable String message) {

                        Log.d("cancel", message);
                    }
                })
                .onError(new ErrorListener() { // 에러가 났을때 호출되는 부분
                    @Override
                    public void onError(@Nullable String message) {
                        Log.d("error", message);
                    }
                })
                .onClose(
                        new CloseListener() { //결제창이 닫힐때 실행되는 부분
                            @Override
                            public void onClose(String message) {
                                Log.d("close", "close");
                            }
                        })
                .request();
    }

}
