package com.example.membershipapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderlistDetailActivity extends LogActivity {

    TextView tvOrderlistDetailDate, tvOrderlistDetailTotalPrice, tvUserOrderCancel;
    Button btnUserOrderCancel;
    RecyclerView recyclerOrderlistDetail;
    OrderlistDetailAdapter orderlistDetailAdapter;

    String getOrderDateData, orderState, orderEmail;
    String dateKey, emailKey;
    int getClickPosition, getOrderTotalPriceData, currentUserCardPrice;

    ArrayList<OrderlistDTO> orderlistDTOArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //앱바 제목 변경
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("주문 상세");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderlist_detail);

        tvOrderlistDetailDate = (TextView)findViewById(R.id.tvOrderlistDetailDate);
        tvOrderlistDetailTotalPrice = (TextView)findViewById(R.id.tvOrderlistDetailTotalPrice);
        tvUserOrderCancel = (TextView)findViewById(R.id.tvUserOrderCancel);
        btnUserOrderCancel = (Button)findViewById(R.id.btnUserOrderCancel);

        //리사이클러뷰 초기화
        recyclerInit();
        //orderlist 에서 받아온 데이터
        getOrderlistData();

        //주문취소 관련 버튼&텍스트 설정
        if(orderState.matches("대기")){
            btnUserOrderCancel.setBackground(getResources().getDrawable(R.color.colorRed));
            tvUserOrderCancel.setText("※ 주문취소는 메뉴 제조 전까지 가능합니다.");
        }else {
            btnUserOrderCancel.setVisibility(View.GONE);
            tvUserOrderCancel.setText("※ 메뉴 제조중인 상태에선 주문취소를 할 수 없습니다.");
        }

        //주문취소 버튼 클릭시
        btnUserOrderCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //사용자가 주문을 취소했을때
                makeDialogOrderCancel();
            }
        });

        //주문목록 데이터 불러오기
        loadOrderlistDetailData();

        //사용자 카드잔액 데이터 불러오기
        loadUserStampCountData();

        tvOrderlistDetailDate.setText(getOrderDateData);
        tvOrderlistDetailTotalPrice.setText(getOrderTotalPriceData+"원");

    }

    public void recyclerInit(){
        recyclerOrderlistDetail = (RecyclerView)findViewById(R.id.recyclerOrderlistDetail);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerOrderlistDetail.setLayoutManager(linearLayoutManager);

        //어댑터 객체 생성
        orderlistDetailAdapter = new OrderlistDetailAdapter(this);
        //리사이클러뷰에 어댑터 설정
        recyclerOrderlistDetail.setAdapter(orderlistDetailAdapter);
    }

    public void getOrderlistData(){
        Intent intent = getIntent();

        orderEmail = intent.getStringExtra("orderEmail");
        orderState = intent.getStringExtra("orderState");
        getClickPosition = intent.getIntExtra("clickPosition", 0);
        getOrderDateData = intent.getStringExtra("orderDate");
        getOrderTotalPriceData = intent.getIntExtra("orderTotalPrice", 0);
//        orderlistDTOArrayList = (ArrayList<OrderlistDTO>) intent.getSerializableExtra("orderlistDetailArrayList");

        dateKey = getOrderDateData;
        emailKey = orderEmail;
    }

    //데이터 불러오기
    public void loadOrderlistDetailData(){

        Log.d("test01", "111=01");

        applicationClass.databaseReference.child("orderlist")
                .child(applicationClass.EncodeString(applicationClass.loginEmail))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        applicationClass.makeLog("///OrderlistActivity : onDataChange()///");
                        Log.d("test01", "111=02");
                        //초기화
//                        orderlistAdapter.orderInfoList.clear();

                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String dateKey = snapshot.getKey();
                            orderlistDetailAdapter.dateKeyList.add(dateKey);
                        }
                        Log.d("test01", "111=03");
                        String detailDatekey = orderlistDetailAdapter.dateKeyList.get(getClickPosition);
                        applicationClass.makeLog("detail 날짜 키 : "+detailDatekey);
                        applicationClass.makeLog("detail orderlist 몇개 : "+ dataSnapshot.child(detailDatekey).child("orderlist").getChildrenCount());
                        Log.d("test01", "111=04");
                        long size = dataSnapshot.child(detailDatekey).child("orderlist").getChildrenCount();
                        int orderlistSize = (int) size;
                        Log.d("test01", "111=05");
                        for(int i=0; i<orderlistSize; i++){
                            OrderlistDTO orderlistDTO = dataSnapshot.child(detailDatekey).child("orderlist").child("orderItem"+i).getValue(OrderlistDTO.class);
                            orderlistDetailAdapter.orderlistDTOArrayList.add(orderlistDTO);
                        }
                        Log.d("test01", "111=06");
                        orderlistDetailAdapter.notifyDataSetChanged();
                        Log.d("test01", "111=07");
                        applicationClass.makeLog("dateKey 확인 끝");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

    }

    //사용자가 주문취소할때
    public void makeDialogOrderCancel(){
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderlistDetailActivity.this);
        builder.setTitle("주문을 취소하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "다이얼로그 확인 선택");
                                //데이터베이스에서 주문 상태값 -> 취소 로 변경
                                changeUserCancelOrderStateData("주문취소");
                                //파이어베이스 주문한 사용자의 총결제금액 취소
                                addUserCardPrice(getOrderTotalPriceData);
                                //관리자에게 주문 푸시알림 보내기
                                SendNotification.sendToAdminFCM(applicationClass.loginName, "주문을 취소합니다.");
                                //사용자 주문목록 -> 리스트 화면으로 전환
                                Intent intent = new Intent(OrderlistDetailActivity.this, OrderlistActivity.class);
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

    //사용자가 주문을 취소했을때 -> 파이어베이스에서 데이터 처리
    public void changeUserCancelOrderStateData(String stateStr){

        Map<String, Object> postValues = new HashMap<String,Object>();
        postValues.put("orderState", stateStr);

        applicationClass.databaseReference.child("AdminOrderlist").child(dateKey)
                .child(applicationClass.EncodeString(emailKey)).updateChildren(postValues);

        applicationClass.databaseReference.child("orderlist")
                .child(applicationClass.EncodeString(emailKey))
                .child(dateKey).updateChildren(postValues);

    }

    //사용자가 주문을 취소했을때 -> 총결제금액 환불
    public void addUserCardPrice(int cancelPrice){

        int afterCardPrice = currentUserCardPrice+cancelPrice;
        applicationClass.makeLog("사용자의 주문취소 후 카드잔액 : " + afterCardPrice);

        Map<String, Object> userValues = new HashMap<String,Object>();
        userValues.put("userCardPrice", afterCardPrice);

        applicationClass.databaseReference.child("users")
                .child(applicationClass.EncodeString(emailKey)).updateChildren(userValues);
    }

    //사용자 카드잔액 데이터 불러오기
    public void loadUserStampCountData() {
        Log.d("test01", "222=01");

        applicationClass.databaseReference.child("users")
                .child(applicationClass.EncodeString(emailKey))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Log.d("test01", "222=02");
                        Log.d(TAG, "///OrderlistDetailActivity : onDataChange()///");

                        Users users = dataSnapshot.getValue(Users.class);
                        Log.d("test01", "222=03");
                        currentUserCardPrice = users.getUserCardPrice();
                        applicationClass.makeLog(users.getUserName() + " 사용자의 현재 카드 잔액 : " + currentUserCardPrice);
                        Log.d("test01", "222=04");
                     }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}

                });
    }
}
