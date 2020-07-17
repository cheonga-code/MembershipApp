package com.example.membershipapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import kr.co.bootpay.enums.Method;

public class AdminOrderlistDetailActivity extends LogActivity {

    TextView tvAdminOrderDetailDate, tvAdminOrderDetailId, tvAdminOrderDetailTotalPrice;
    Button btnAdminOrderYes, btnAdminOrderNo, btnAdminOrderComplete, btnAdminOrderCancel;
    LinearLayout btnBoxAdminOrderWait, btnBoxAdminOrderProcess, btnBoxAdminOrderCancel;
    RecyclerView recyclerAdminOrderlistDetail;
    OrderlistDetailAdapter orderlistDetailAdapter;

    String couponlistDataKey;
    String DateKey, emailKey, getOrderIdData, getOrderDateData, activityName;
    String orderDate, orderTime;
    int getClickPosition, getOrderTotalPriceData, getOrderBeverageQuantity;
    int currentUserCardPrice, currentUserStampCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_orderlist_detail);

        tvAdminOrderDetailDate = (TextView)findViewById(R.id.tvAdminOrderDetailDate);
        tvAdminOrderDetailId = (TextView)findViewById(R.id.tvAdminOrderDetailId);
        tvAdminOrderDetailTotalPrice = (TextView)findViewById(R.id.tvAdminOrderDetailTotalPrice);
        btnAdminOrderYes = (Button) findViewById(R.id.btnAdminOrderYes);
        btnAdminOrderNo = (Button)findViewById(R.id.btnAdminOrderNo);
        btnAdminOrderComplete = (Button)findViewById(R.id.btnAdminOrderComplete);
        btnAdminOrderCancel = (Button)findViewById(R.id.btnAdminOrderCancel);
        btnBoxAdminOrderWait = (LinearLayout)findViewById(R.id.btnBoxAdminOrderWait);
        btnBoxAdminOrderProcess = (LinearLayout)findViewById(R.id.btnBoxAdminOrderProcess);
        btnBoxAdminOrderCancel = (LinearLayout)findViewById(R.id.btnBoxAdminOrderCancel);

        //AdminOrderlist 에서 받아온 데이터
        getAdminOrderlistData();

        //주문승인&거부&제조완료 버튼 보이게&안보이게 처리
        orderStateButtonSetVisibility();

        //리사이클러뷰 초기화
        recyclerInit();

        //사용자의 스탬프 수량 데이터 불러오기
        loadUserStampCountData();

        //관리자의 주문목록 데이터 불러오기
        loadAdminOrderlistDetailData();

        //사용자의 쿠폰목록 데이터 불러오기
        loadUserCouponlistData();

        //setText()
        tvAdminOrderDetailDate.setText(getOrderDateData);
        tvAdminOrderDetailId.setText(getOrderIdData);
        tvAdminOrderDetailTotalPrice.setText(getOrderTotalPriceData+"원");

        //주문 상태 처리  (대기 -> 제조 -> 완료&거부)
        //주문 승인 버튼 클릭했을때
        btnAdminOrderYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //주문리스트 상태 데이터 변경하기 (대기 -> 제조)
                makeDialogOrderYes();
            }
        });

        //주문 거부 버튼 클릭했을때
        btnAdminOrderNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //주문리스트 상태 데이터 변경하기 (대기 -> 거부)
                makeDialogOrderNo();
            }
        });

        //제조 완료 버튼 클릭했을때
        btnAdminOrderComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //주문리스트 상태 데이터 변경하기 (제조 -> 완료)
                makeDialogOrderComplete();
            }
        });

        //결제 취소 버튼 클릭했을때
        btnAdminOrderCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //주문리스트 상태 데이터 변경하기 (완료 -> 결제취소)
                makeDialogOrderCancel();
            }
        });

    }

    public void recyclerInit(){
        recyclerAdminOrderlistDetail = (RecyclerView)findViewById(R.id.recyclerAdminOrderlistDetail);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerAdminOrderlistDetail.setLayoutManager(linearLayoutManager);
        //어댑터 객체 생성
        orderlistDetailAdapter = new OrderlistDetailAdapter(this);
        //리사이클러뷰에 어댑터 설정
        recyclerAdminOrderlistDetail.setAdapter(orderlistDetailAdapter);
    }

    public void getAdminOrderlistData(){
        Intent intent = getIntent();
        getClickPosition = intent.getIntExtra("clickPosition", 0);
        applicationClass.makeLog("받은 position 값 : "+getClickPosition);

        activityName = intent.getStringExtra("activityName");
        DateKey = intent.getStringExtra("DateKey");
        emailKey = intent.getStringExtra("emailKey");
        getOrderIdData = intent.getStringExtra("orderId");
        getOrderDateData = intent.getStringExtra("orderDate");
        getOrderTotalPriceData = intent.getIntExtra("orderTotalPrice", 0);
        getOrderBeverageQuantity = intent.getIntExtra("orderBeverageQuantity", 0);
    }

    //데이터 불러오기
    public void loadAdminOrderlistDetailData(){

        applicationClass.databaseReference.child("AdminOrderlist")
                .child(DateKey).child(applicationClass.EncodeString(emailKey)).child("orderlist")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        applicationClass.makeLog("///OrderlistActivity : onDataChange()///");

                        //초기화
                        orderlistDetailAdapter.orderlistDTOArrayList.clear();
                        applicationClass.makeLog("Key 확인 시작");

                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String key = snapshot.getKey();
                            orderlistDetailAdapter.dateKeyList.add(key);
                            applicationClass.makeLog("key (확인) : "+key);

                            OrderlistDTO orderlistDTO = snapshot.getValue(OrderlistDTO.class);
                            orderlistDetailAdapter.orderlistDTOArrayList.add(orderlistDTO);
                        }
                        orderlistDetailAdapter.notifyDataSetChanged();

                        applicationClass.makeLog("Key 확인 끝");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

    }

    //주문 상태 변경 버튼 linearLayout 보이게&안보이게 처리
    public void orderStateButtonSetVisibility(){
        if(activityName.matches("orderWait")){
            applicationClass.makeLog("대기 리스트에서 -> 상세로");
            btnBoxAdminOrderWait.setVisibility(View.VISIBLE);
            btnBoxAdminOrderProcess.setVisibility(View.GONE);
            btnBoxAdminOrderCancel.setVisibility(View.GONE);
        }else if(activityName.matches("orderProcess")){
            applicationClass.makeLog("제조 리스트에서 -> 상세로");
            btnBoxAdminOrderWait.setVisibility(View.GONE);
            btnBoxAdminOrderProcess.setVisibility(View.VISIBLE);
            btnBoxAdminOrderCancel.setVisibility(View.GONE);
        }else if(activityName.matches("orderComplete")){
            applicationClass.makeLog("완료 리스트에서 -> 상세로");
            btnBoxAdminOrderWait.setVisibility(View.GONE);
            btnBoxAdminOrderProcess.setVisibility(View.GONE);
            btnBoxAdminOrderCancel.setVisibility(View.VISIBLE);
        }else if(activityName.matches("orderCancel")){
            applicationClass.makeLog("취소 리스트에서 -> 상세로");
            btnBoxAdminOrderWait.setVisibility(View.GONE);
            btnBoxAdminOrderProcess.setVisibility(View.GONE);
            btnBoxAdminOrderCancel.setVisibility(View.GONE);
        }
    }

    //관리자 주문 상태 변경 메소드
    public void changeAdminOrderStateData(String stateStr){

        Map<String, Object> postValues = new HashMap<String,Object>();
        postValues.put("orderState", stateStr);

        applicationClass.databaseReference.child("AdminOrderlist").child(DateKey)
                .child(applicationClass.EncodeString(emailKey)).updateChildren(postValues);

    }

    //사용자 주문 상태 변경 메소드
    public void changeUserOrderStateData(String stateStr){

        Map<String, Object> postValues = new HashMap<String,Object>();
        postValues.put("orderState", stateStr);

        applicationClass.databaseReference.child("orderlist")
                .child(applicationClass.EncodeString(emailKey))
                .child(DateKey).updateChildren(postValues);

    }

    //사용자 카드잔액&스탬프카운트 데이터 불러오기
    public void loadUserStampCountData() {

        applicationClass.databaseReference.child("users")
                .child(applicationClass.EncodeString(emailKey))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d(TAG, "///AdminOrderlistDetailActivity : onDataChange()///");

                        Users users = dataSnapshot.getValue(Users.class);

                        currentUserCardPrice = users.getUserCardPrice();
                        currentUserStampCount = users.getUserStampCount();
                        applicationClass.makeLog(users.getUserName() + " 사용자의 현재 스탬프 갯수 : " + currentUserStampCount);
                        applicationClass.makeLog(users.getUserName() + " 사용자의 현재 카드 잔액 : " + currentUserCardPrice);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}

                });
    }

    //사용자 데이터 변경 메소드 (카드잔액&스탬프갯수)
    public void addUserStampCountData(int addStamp){

        int afterStampCount = currentUserStampCount+addStamp;
        applicationClass.makeLog("사용자의 추가 후 스탬프 갯수 : " + afterStampCount);

        Map<String, Object> userValues = new HashMap<String,Object>();
        userValues.put("userStampCount", afterStampCount);

        applicationClass.databaseReference.child("users")
                .child(applicationClass.EncodeString(emailKey)).updateChildren(userValues);
    }

    public void addUserCardPrice(int cancelPrice){

        int afterCardPrice = currentUserCardPrice+cancelPrice;
        applicationClass.makeLog("사용자의 주문취소 후 카드잔액 : " + afterCardPrice);

        Map<String, Object> userValues = new HashMap<String,Object>();
        userValues.put("userCardPrice", afterCardPrice);

        applicationClass.databaseReference.child("users")
                .child(applicationClass.EncodeString(emailKey)).updateChildren(userValues);
    }

    //주문 승인시
    public void makeDialogOrderYes(){
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminOrderlistDetailActivity.this);
        builder.setTitle("주문을 승인하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "다이얼로그 확인 선택");
                                //확인 처리
                                changeAdminOrderStateData("제조");
                                changeUserOrderStateData("제조");
                                //FCM 알림 보내기
                                SendNotification.sendToUserFCM(
                                        applicationClass.EncodeString(emailKey), "잼잼오더", "메뉴를 제조중이에요. 잠시만 기다려주세요.");
                                //화면 전환
                                Intent intent = new Intent(AdminOrderlistDetailActivity.this, AdminOrderlistProcessActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                .setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "다이얼로그 취소 선택");
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //주문 거부시
    public void makeDialogOrderNo(){
        final EditText dialogEditText = new EditText(getApplicationContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminOrderlistDetailActivity.this);
        builder.setTitle("주문을 거부하시겠습니까?")
                .setView(dialogEditText)
                .setCancelable(false)
                .setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "다이얼로그 확인 선택");
                                //확인 처리
                                changeAdminOrderStateData("주문거부");
                                changeUserOrderStateData("주문거부");
                                //파이어베이스 주문한 사용자의 총결제금액 취소
                                addUserCardPrice(getOrderTotalPriceData);
                                //editText에서 보낸 메세지 알림띄우기
                                String edittextMessage = dialogEditText.getText().toString();
                                //FCM 알림 보내기    //"재고소진으로 인해 주문이 취소되었습니다."
                                SendNotification.sendToUserFCM(
                                        applicationClass.EncodeString(emailKey), "잼잼오더", edittextMessage);
                                //관리자페이지 주문관리 -> 취소화면으로 전환
                                Intent intent = new Intent(AdminOrderlistDetailActivity.this, AdminOrderlistCancelActivity.class);
                                //TODO:주문 취소한 시간 데이터 보내기
                                startActivity(intent);
                                finish();
                            }
                        })
                .setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "다이얼로그 취소 선택");
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //제조 완료시
    public void makeDialogOrderComplete(){
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminOrderlistDetailActivity.this);
        builder.setTitle("제조 완료 알람을 보내시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "다이얼로그 확인 선택");
                                //파이어베이스 주문상태 데이터 '완료' 처리
                                changeAdminOrderStateData("완료");
                                changeUserOrderStateData("완료");
                                //파이어베이스 주문한 사용자의 스탬프 데이터 '음료수량만큼' 증가
                                addUserStampCountData(getOrderBeverageQuantity);
                                //FCM 알림 보내기
                                SendNotification.sendToUserFCM(
                                        applicationClass.EncodeString(emailKey), "잼잼오더", "픽업대에서 메뉴를 픽업해주세요!");
                                //관리자페이지 주문관리 -> 완료화면으로 전환
                                Intent intent = new Intent(AdminOrderlistDetailActivity.this, AdminOrderlistCompleteActivity.class);
                                //TODO:주문 완료한 시간 데이터 보내기
//                                currentTimeData();
                                startActivity(intent);
                                finish();
                            }
                        })
                .setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "다이얼로그 취소 선택");
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //결제 취소시
    public void makeDialogOrderCancel(){
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminOrderlistDetailActivity.this);
        builder.setTitle("결제를 취소하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "다이얼로그 확인 선택");
                                //파이어베이스 주문상태 데이터 '결제취소' 처리
                                changeAdminOrderStateData("결제취소");
                                changeUserOrderStateData("결제취소");
                                //결제취소 -> 총결제금액 환불&적립된 스탬프 수량 차감
                                //파이어베이스 주문한 사용자의 총결제금액 취소
                                addUserCardPrice(getOrderTotalPriceData);
                                //파이어베이스 주문한 사용자의 적립된 스탬프 데이터 차감
                                cancelUserStampCountData(getOrderBeverageQuantity);
                                //FCM 알림 보내기
                                SendNotification.sendToUserFCM(
                                        applicationClass.EncodeString(emailKey), "잼잼오더", "결제가 취소되었습니다.");
                                //관리자페이지 주문관리 -> 취소화면으로 전환
                                Intent intent = new Intent(AdminOrderlistDetailActivity.this, AdminOrderlistCancelActivity.class);
                                //TODO:주문 완료한 시간 데이터 보내기
                                startActivity(intent);
                                finish();
                            }
                        })
                .setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "다이얼로그 취소 선택");
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //결제취소했을 경우 적립한 스탬프 갯수 차감
    public void cancelUserStampCountData(int addStamp){

        //현재 사용자가 가지고있는 스탬프 갯수 - 적립한 스탬프 갯수
        //0이거나 양수일 때
        if(currentUserStampCount - addStamp >= 0){
            applicationClass.makeLog("///양수다!!///");
            int afterStampCount = currentUserStampCount-addStamp;
            //현재 스탬프 갯수 데이터 업데이트 메소드
            changeUserStampData(afterStampCount);
        //음수일때
        }else{
            applicationClass.makeLog("///음수다!!///");
            //최근 추가된 쿠폰리스트 1개 삭제
            databaseUserCouponlistDataDelete();
            //현재 스탬프 갯수+10-addStamp;
            int afterStampCount = currentUserStampCount+10-addStamp;
            //현재 스탬프 갯수 데이터 업데이트 메소드
            changeUserStampData(afterStampCount);
        }

        int afterStampCount = currentUserStampCount+addStamp;
        applicationClass.makeLog("사용자의 추가 후 스탬프 갯수 : " + afterStampCount);

    }

    //유저의 스탬프 갯수
    public void changeUserStampData(int afterStampCount){
        Map<String, Object> userValues = new HashMap<String,Object>();
        userValues.put("userStampCount", afterStampCount);

        applicationClass.databaseReference.child("users")
                .child(applicationClass.EncodeString(emailKey)).updateChildren(userValues);
    }

    //유저의 쿠폰리스트 데이터 불러오기 (쿠폰 중에 가장 마지막 index 에 접근할거임)
    public void loadUserCouponlistData(){
        applicationClass.databaseReference.child("couponlist")
                .child(applicationClass.EncodeString(emailKey))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d(TAG, "///AdminOrderlistDetailActivity : onDataChange()///");

                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            couponlistDataKey = snapshot.getKey();
                            applicationClass.makeLog("///쿠폰리스트 key (확인) :"+couponlistDataKey);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}

                });
    }

    //database realtime 에서 유저의 쿠폰리스트 데이터 가장 마지막에 추가된거 삭제
    public void databaseUserCouponlistDataDelete(){

        applicationClass.databaseReference.child("couponlist")
                .child(applicationClass.EncodeString(emailKey))
                .child(couponlistDataKey).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                applicationClass.makeLog("쿠폰리스트 삭제 성공 : "+couponlistDataKey);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                applicationClass.makeLog("쿠폰리스트 삭제 실패");
            }
        });
    }

    //현재 날짜와 시간 데이터 받아오기
    public void currentTimeData(){
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String getDate = simpleDateFormat.format(mDate);
        String getTime = timeFormat.format(mDate);
        orderDate = getDate; //orderDate = 결제한 주문날짜+시간
        orderTime = getTime; //orderTime = 주문한 현재시간
    }
}
