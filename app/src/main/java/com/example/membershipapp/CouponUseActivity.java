package com.example.membershipapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class CouponUseActivity extends LogActivity {

    RecyclerView recyclerUseCoupon;
    CouponUseAdapter couponUseAdapter;
    CouponlistDTO itemNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_use);

        //리사이클러뷰 객체 선언 및 초기화
        recyclerAvailableCouponInit();
        //파이어베이스에서 쿠폰리스트 데이터 불러오기
        loadCouponlistData();
        //클릭 이벤트
        clickItemView();

//        //리스트 null값 처리
//        if(couponUseAdapter.couponLists.size() == 0){
//            applicationClass.makeToast("사용가능한 쿠폰이 없습니다.");
//            finish();
//        }
    }


    //adapter item 클릭 이벤트
    public void clickItemView(){
        //아이템 클릭 이벤트
        couponUseAdapter.setOnItemClickListener(new CouponUseAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                //아이템 위치
                itemNum = couponUseAdapter.getItem(position);
                Log.d(TAG, "클릭한 아이템 위치 : "+position);
                String key = applicationClass.databaseReference.getKey();
                Log.d(TAG, "클릭한 아이템 key : "+key);

                String useCouponDeadline = couponUseAdapter.couponLists.get(position).getCouponDeadline();
                String useCouponMakeDate = couponUseAdapter.couponLists.get(position).getCouponMakeDate();

                Intent intent = getIntent();
                intent.putExtra("useCouponState", "true");
                intent.putExtra("useCouponDeadline", useCouponDeadline);
                intent.putExtra("useCouponMakeDate", useCouponMakeDate);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    private void recyclerAvailableCouponInit() {
        recyclerUseCoupon = (RecyclerView)findViewById(R.id.recyclerUseCoupon);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerUseCoupon.setLayoutManager(linearLayoutManager);
        //어댑터 객체 생성
        couponUseAdapter = new CouponUseAdapter(this);
        //리사이클러뷰에 어댑터 설정
        recyclerUseCoupon.setAdapter(couponUseAdapter);
    }

    //데이터 불러오기
    public void loadCouponlistData(){
        //파이어베이스 저장소 데이터 변경 자동 반영
        applicationClass.firebaseDatabase.getReference().child("couponlist")
                .child(applicationClass.EncodeString(applicationClass.loginEmail))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        applicationClass.makeLog("onDataChange()");
                        //초기화
                        couponUseAdapter.couponLists.clear();
                        couponUseAdapter.uidLists.clear();

                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){    //getChildren = 한 유저에 대한 정보 (한줄) 이라고 생각하면 됨
                            CouponlistDTO couponlistDTO = snapshot.getValue(CouponlistDTO.class);
                            String uidKey = snapshot.getKey();
                            applicationClass.makeLog("key : "+uidKey);
                            couponUseAdapter.couponLists.add(couponlistDTO);
                            couponUseAdapter.uidLists.add(uidKey);
                        }
                        couponUseAdapter.notifyDataSetChanged();

                        //리스트 null값 처리
                        if(couponUseAdapter.couponLists.size() == 0){
                            applicationClass.makeToast("사용가능한 쿠폰이 없습니다.");
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
