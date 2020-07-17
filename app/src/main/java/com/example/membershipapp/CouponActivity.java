package com.example.membershipapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CouponActivity extends BaseActivity {

    Button btnAvailableCoupon, btnCouponHistory;
    LinearLayout CntAvailableCoupon, CntCouponHistory;

    RecyclerView recyclerAvailableCoupon, recyclerCouponHistory;
    CouponlistAdapter couponlistAdapter;
    CouponHistoryAdapter couponHistoryAdapter;

    String TAG = "ScannerActivity";
    TextView tvAvailableCouponNull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_coupon);

        btnAvailableCoupon = (Button)findViewById(R.id.btnAvailableCoupon);
        btnCouponHistory = (Button)findViewById(R.id.btnCouponHistory);
        CntAvailableCoupon = (LinearLayout)findViewById(R.id.CntAvailableCoupon);
        CntCouponHistory = (LinearLayout)findViewById(R.id.CntCouponHistory);
        tvAvailableCouponNull = (TextView)findViewById(R.id.tvAvailableCouponNull);

        //초기화
        recyclerAvailableCouponInit();
        //파이어베이스에서 쿠폰리스트 데이터 불러오기
        loadCouponlistData();
        //어댑터로 로그인한 유저의 이름 보내기
        couponlistAdapter.loginEmail = applicationClass.loginEmail;
        couponlistAdapter.loginName = applicationClass.loginName;

//        //리스트 null값 처리
//        if(couponlistAdapter.couponLists.size() == 0){
////            applicationClass.makeToast("사용가능한 쿠폰이 없습니다.");
//            tvAvailableCouponNull.setVisibility(View.VISIBLE);
//            CntAvailableCoupon.setVisibility(View.GONE);
//            CntCouponHistory.setVisibility(View.GONE);
//        }

         btnAvailableCoupon.setOnClickListener(new View.OnClickListener() {
             @SuppressLint("ResourceAsColor")
             @Override
             public void onClick(View v) {
                 btnAvailableCoupon.setBackgroundColor(getResources().getColor(R.color.colorMainOrange));
                 btnCouponHistory.setBackgroundColor(getResources().getColor(R.color.colorbackOrange1));

                 //리스트 null값 처리
                 if(couponlistAdapter.couponLists.size() == 0){
                     tvAvailableCouponNull.setVisibility(View.VISIBLE);
                     CntAvailableCoupon.setVisibility(View.GONE);
                     CntCouponHistory.setVisibility(View.GONE);
                 }else{
                     //null값이 아닐때
                     tvAvailableCouponNull.setVisibility(View.GONE);
                     CntAvailableCoupon.setVisibility(View.VISIBLE);
                     CntCouponHistory.setVisibility(View.INVISIBLE);
                 }

                 //리사이클러뷰 객체 선언 및 초기화
                 recyclerAvailableCouponInit();
                 //파이어베이스에서 쿠폰리스트 데이터 불러오기
                 loadCouponlistData();
             }
         });

         btnCouponHistory.setOnClickListener(new View.OnClickListener() {
             @SuppressLint("ResourceAsColor")
             @Override
             public void onClick(View v) {
                 btnAvailableCoupon.setBackgroundColor(getResources().getColor(R.color.colorbackOrange1));
                 btnCouponHistory.setBackgroundColor(getResources().getColor(R.color.colorMainOrange));
                 tvAvailableCouponNull.setVisibility(View.GONE);
                 CntAvailableCoupon.setVisibility(View.INVISIBLE);
                 CntCouponHistory.setVisibility(View.VISIBLE);

                 recyclerCouponHistoryInit();
                 //파이어베이스에서 쿠폰히스토리 리스트 데이터 불러오기
                 loadCouponHistoryListData();
             }
         });
    }

    private void recyclerAvailableCouponInit() {

        recyclerAvailableCoupon = (RecyclerView)findViewById(R.id.recyclerAvailableCoupon);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerAvailableCoupon.setLayoutManager(linearLayoutManager);

        //어댑터 객체 생성
        couponlistAdapter = new CouponlistAdapter(this);

//        임시로 데이터 생성
//        couponlistAdapter.addItem(new CouponlistDTO(R.drawable.stamp_coffee,"스탬프 10개 적립 무료음료 쿠폰", "2020.05.31 까지"));
//        couponlistAdapter.addItem(new CouponlistDTO(R.drawable.stamp_coffee,"스탬프 10개 적립 무료음료 쿠폰", "2020.06.07 까지"));
//        couponlistAdapter.addItem(new CouponlistDTO(R.drawable.stamp_coffee,"스탬프 10개 적립 무료음료 쿠폰", "2020.06.13 까지"));

        //리사이클러뷰에 어댑터 설정
        recyclerAvailableCoupon.setAdapter(couponlistAdapter);
    }

    private void recyclerCouponHistoryInit() {

        recyclerCouponHistory = (RecyclerView)findViewById(R.id.recyclerCouponHistory);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerCouponHistory.setLayoutManager(linearLayoutManager);

        //어댑터 객체 생성
        couponHistoryAdapter = new CouponHistoryAdapter(this);

        //임시로 데이터 생성
//        couponHistoryAdapter.addItem(new CouponHistoryDTO(R.drawable.stamp_coffee,"스탬프 10개 적립 무료음료 쿠폰", "2020.05.31 까지", "2020.05.15 12:30:23 사용 (남성역점)"));
//        couponHistoryAdapter.addItem(new CouponHistoryDTO(R.drawable.stamp_coffee,"스탬프 10개 적립 무료음료 쿠폰", "2020.06.07 까지", "2020.05.18 18:13:01 사용 (백석역점)"));
//        couponHistoryAdapter.addItem(new CouponHistoryDTO(R.drawable.stamp_coffee,"스탬프 10개 적립 무료음료 쿠폰", "2020.06.13 까지", "2020.06.10 09:45:47 사용 (신사점)"));
//

        //리사이클러뷰에 어댑터 설정
        recyclerCouponHistory.setAdapter(couponHistoryAdapter);
    }

    //데이터 불러오기
    public void loadCouponlistData(){
        //파이어베이스 저장소 데이터 변경 자동 반영
        applicationClass.firebaseDatabase.getReference().child("couponlist")
                .child(applicationClass.EncodeString(applicationClass.loginEmail))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        applicationClass.makeLog("onDataChange()");
                        //초기화
                        couponlistAdapter.couponLists.clear();
                        couponlistAdapter.uidLists.clear();

                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){    //getChildren = 한 유저에 대한 정보 (한줄) 이라고 생각하면 됨
                            CouponlistDTO couponlistDTO = snapshot.getValue(CouponlistDTO.class);
                            String uidKey = snapshot.getKey();
                            applicationClass.makeLog("key : "+uidKey);
                            couponlistAdapter.couponLists.add(couponlistDTO);
                            couponlistAdapter.uidLists.add(uidKey);
                        }
                        couponlistAdapter.notifyDataSetChanged();

                        //리스트 null값 처리
                        if(couponlistAdapter.couponLists.size() == 0){
                            tvAvailableCouponNull.setVisibility(View.VISIBLE);
                            CntAvailableCoupon.setVisibility(View.GONE);
                            CntCouponHistory.setVisibility(View.GONE);
                        }else{
                            tvAvailableCouponNull.setVisibility(View.GONE);
                            CntAvailableCoupon.setVisibility(View.VISIBLE);
                            CntCouponHistory.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    //쿠폰 히스토리 리스트 데이터 불러오기
    public void loadCouponHistoryListData(){
        //파이어베이스 저장소 데이터 변경 자동 반영
        applicationClass.firebaseDatabase.getReference().child("couponHistoryList")
                .child(applicationClass.EncodeString(applicationClass.loginEmail))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        applicationClass.makeLog("onDataChange()");
                        //초기화
                        couponHistoryAdapter.items.clear();
//                        couponHistoryAdapter.uidLists.clear();

                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){    //getChildren = 한 유저에 대한 정보 (한줄) 이라고 생각하면 됨
                            CouponHistoryDTO couponHistoryDTO = snapshot.getValue(CouponHistoryDTO.class);
                            String uidKey = snapshot.getKey();
                            applicationClass.makeLog("key : "+uidKey);
                            couponHistoryAdapter.items.add(couponHistoryDTO);
//                            couponHistoryAdapter.uidLists.add(uidKey);
                        }
                        couponHistoryAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    int getContentViewId() {
        return R.layout.activity_coupon;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.bottomNav03;
    }
}
