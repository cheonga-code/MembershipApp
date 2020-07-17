package com.example.membershipapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firestore.v1.StructuredQuery;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class OrderlistActivity extends BaseActivity {

    RecyclerView recyclerOrderlist;
    OrderlistAdapter orderlistAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_orderlist);

        //앱바 제목 변경
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("주문 목록");

        recyclerInit();

        orderlistAdapter.orderlistDTOArrayList.clear();

        //주문목록 데이터 불러오기
        loadOrderlistData();
    }

    public void recyclerInit(){
        recyclerOrderlist = (RecyclerView)findViewById(R.id.recyclerOrderlist);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerOrderlist.setLayoutManager(linearLayoutManager);

        //어댑터 객체 생성
        orderlistAdapter = new OrderlistAdapter(this);

        //리사이클러뷰에 어댑터 설정
        recyclerOrderlist.setAdapter(orderlistAdapter);
    }

    //데이터 불러오기
    public void loadOrderlistData(){

        applicationClass.databaseReference.child("orderlist")
                .child(applicationClass.EncodeString(applicationClass.loginEmail))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        applicationClass.makeLog("///OrderlistActivity : onDataChange()///");

                        //초기화
                        orderlistAdapter.orderInfoList.clear();

                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            applicationClass.makeLog("orderInfoList 사이즈 (시작) : " + orderlistAdapter.orderInfoList.size());
                            OrderInfoDTO orderInfoDTO = snapshot.getValue(OrderInfoDTO.class);

                            String dateKey = snapshot.getKey();
                            String orderlistKey = snapshot.child("orderlist").getKey();
                            Long orderlistKeySize = snapshot.child("orderlist").getChildrenCount();
                            applicationClass.makeLog("dateKey(확인) : " + dateKey);
                            applicationClass.makeLog("orderlistKeySize(확인) : " + orderlistKeySize);

                            int orderlistKeySizeInt = orderlistKeySize.intValue();

                            //TODO: orderlistDTOArrayList = 11개 //녹차, 아메리카노, 초코케잌, 녹차, 아메리카노, 카페라뗴
//                            for(int i=0; i<orderlistKeySizeInt; i++){
//                                OrderlistDTO orderlistDTO = snapshot.child("orderlist").child("orderItem"+(i)).getValue(OrderlistDTO.class);
//                                applicationClass.makeLog("orderlistDTO 아이템 이름 (확인) : " + orderlistDTO.getOrderMenuItemName());
//                                orderlistAdapter.orderlistDTOArrayList.add(orderlistDTO);
//                            }

                            OrderlistDTO orderlistDTO = snapshot.child("orderlist").child("orderItem"+0).getValue(OrderlistDTO.class);
//                            applicationClass.makeLog("orderlistDTO 아이템 0번째 이름 (확인) : " + orderlistDTO.getOrderMenuItemName());
                            orderlistAdapter.orderlistDTOArrayList.add(orderlistDTO);

                            orderlistAdapter.orderInfoList.add(0, orderInfoDTO);
                            orderlistAdapter.dateKeyList.add(dateKey);
//                            orderlistAdapter.orderlistKeyList.add(orderlistKey);

                            applicationClass.makeLog("orderInfoList 사이즈 : " + orderlistAdapter.orderInfoList.size());
                            applicationClass.makeLog("orderlistDTOArrayList 사이즈 : " + orderlistAdapter.orderlistDTOArrayList.size());
                        }

                        orderlistAdapter.notifyDataSetChanged();
                        applicationClass.makeLog("dateKey 확인 끝");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

    }

    @Override
    int getContentViewId() {
        return R.layout.activity_orderlist;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.bottomNav02;
    }
}
