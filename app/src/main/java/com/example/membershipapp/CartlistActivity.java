package com.example.membershipapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CartlistActivity extends LogActivity {

    CartlistAdapter cartlistAdapter;
    RecyclerView recyclerCartList;
    TextView tvMenuTotalPrice, tvCouponUse, tvCartlistNull;
    Button btnOrderOK, btnCouponUse;

    int totalOrderQuantity = 0;
    int totalOrderBeverageQuantity = 0;
    int totalOrderPrice = 0;
    int addPrice;
    String orderMenuName;
    Boolean couponUseState = false;
    String useCouponMakeDate, useCouponDeadline;

    String orderTime;

    ArrayList<OrderlistDTO> orderlistDTOArrayList = new ArrayList<OrderlistDTO>();

//    CartlistDTO itemNum;   //클릭한 아이템 위치

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartlist);

        //앱바 제목 변경
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("장바구니");

        recyclerCartList = (RecyclerView)findViewById(R.id.recyclerCartList);
        tvMenuTotalPrice = (TextView)findViewById(R.id.tvMenuTotalPrice);
        tvCouponUse = (TextView)findViewById(R.id.tvCouponUse);
        tvCartlistNull = (TextView)findViewById(R.id.tvCartlistNull);
        btnOrderOK = (Button)findViewById(R.id.btnOrderOK);
        btnCouponUse = (Button)findViewById(R.id.btnCouponUse);

        recyclerInit();

        //주문하기 버튼 클릭시
        btnOrderOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //주문할때 주문수량 10개 초과면 토스트메세지 띄우기
                if(totalOrderQuantity > 10){
                    //결제방법 Activity 다이얼로그식으로 화면 띄우기
                    applicationClass.makeToast("주문은 최대 10개까지 가능합니다. 가게에 문의해주세요.");
                }else{
                    applicationClass.makeLog("///주문수량 : "+totalOrderQuantity+"개///");
                    Intent paymentMethodIntent = new Intent(CartlistActivity.this, PaymentMethodActivity.class);
                    paymentMethodIntent.putExtra("totalOrderPrice", totalOrderPrice);                       //총결제금액
                    paymentMethodIntent.putExtra("totalOrderQuantity", totalOrderQuantity);                 //총주문메뉴수량
                    paymentMethodIntent.putExtra("totalOrderBeverageQuantity", totalOrderBeverageQuantity); //총주문음료수량
                    paymentMethodIntent.putExtra("orderMenuName", orderMenuName);           //주문메뉴이름
                    paymentMethodIntent.putExtra("couponUseState", couponUseState);         //쿠폰 사용 상태값
                    paymentMethodIntent.putExtra("useCouponMakeDate", useCouponMakeDate);   //사용할 쿠폰 생성날짜
                    paymentMethodIntent.putExtra("useCouponDeadline", useCouponDeadline);   //사용할 쿠폰 유효기간

                    //TODO: arraylist 값 복사하기 = 주문관리 리스트 데이터 넘겨주기
//                orderList.add(new OrderlistDTO())
                    paymentMethodIntent.putExtra("orderlistArrayList", orderlistDTOArrayList);

                    startActivity(paymentMethodIntent);
                }
            }
        });

        //쿠폰사용 버튼 클릭시
        btnCouponUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //쿠폰 결제 다이얼로그
                makePaymentCouponDialog();
            }
        });

    }

    //리사이클러뷰 데이터 초기화
    public void recyclerInit(){

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerCartList.setLayoutManager(linearLayoutManager);

        //어댑터 객체 생성
        cartlistAdapter = new CartlistAdapter(this);
        //리사이클러뷰에 어댑터 설정
        recyclerCartList.setAdapter(cartlistAdapter);

        //파이어베이스 저장소 데이터 변경 자동 반영
        loadData();

//        //null값 처리
//        if(cartlistAdapter.menuList.size() == 0){
//            tvCartlistNull.setVisibility(View.VISIBLE);
//            recyclerCartList.setVisibility(View.GONE);
//        }else{
//            tvCartlistNull.setVisibility(View.GONE);
//            recyclerCartList.setVisibility(View.VISIBLE);
//        }
    }

    //데이터 불러오기
    public void loadData(){
        //파이어베이스 저장소 데이터 변경 자동 반영
        applicationClass.firebaseDatabase.getReference().child("cartlist")
                .child(applicationClass.EncodeString(applicationClass.loginEmail))
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                applicationClass.makeLog("//CartlistActivity : onDataChange()//");
                //초기화
                cartlistAdapter.menuList.clear();
                orderlistDTOArrayList.clear();

                totalOrderPrice = 0;
                totalOrderQuantity = 0;
                totalOrderBeverageQuantity = 0;

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){    //getChildren = 한 유저에 대한 정보 (한줄) 이라고 생각하면 됨
                    CartlistDTO cartlistDTO = snapshot.getValue(CartlistDTO.class);
                    String uidKey = snapshot.getKey();
                    applicationClass.makeLog("key : "+uidKey);
                    cartlistAdapter.menuList.add(cartlistDTO);
                    cartlistAdapter.uidLists.add(uidKey);

//                    String menuPriceValue = snapshot.child(uidKey).child("cartMenuPrice").getValue();
                    String menuCategory = cartlistDTO.getCartMenuCategory();
                    int menuPrice = cartlistDTO.getCartMenuPrice();
                    int menuQuantity = cartlistDTO.getCartMenuQuantity();
                    applicationClass.makeLog("key 종류 : "+menuCategory);
                    applicationClass.makeLog("key 가격 : "+menuPrice);
                    applicationClass.makeLog("key 수량 : "+menuQuantity);

                    //가격 계산하기
                    addPrice = menuPrice*menuQuantity;
                    applicationClass.makeLog("key addPrice 더할 금액 : "+addPrice);
                    totalOrderPrice = totalOrderPrice+addPrice;
                    applicationClass.makeLog("key totalOrderPrice 총 금액 : "+totalOrderPrice);
                    //수량 계산하기
                    totalOrderQuantity = totalOrderQuantity+menuQuantity;
                    applicationClass.makeLog("key totalOrderQuantity 총 메뉴 수량 : "+totalOrderQuantity);

                    //음료만 수량 계산하기
                    if(menuCategory.matches("음료")){
                        totalOrderBeverageQuantity = totalOrderBeverageQuantity+menuQuantity;
                        applicationClass.makeLog("key totalOrderBeverageQuantity 총 음료만 수량 : "+totalOrderBeverageQuantity);
                    }

                    //OrderlistDTO 객체에 데이터 저장
                    //TODO: 주문리스트 arraylist 에 데이터 복사
                    OrderlistDTO orderlist = new OrderlistDTO();
                    orderlist.setOrderMenuItemCategory(cartlistDTO.getCartMenuCategory());
                    orderlist.setOrderMenuItemName(cartlistDTO.getCartMenuName());
                    orderlist.setOrderMenuItemPrice(cartlistDTO.getCartMenuPrice());
                    orderlist.setOrderMenuItemQuantity(cartlistDTO.getCartMenuQuantity());
                    orderlistDTOArrayList.add(orderlist);

                    //메뉴 이름 넣기
                    orderMenuName = cartlistDTO.getCartMenuName();

                }
                cartlistAdapter.notifyDataSetChanged();
                //총 결제금액 setText()
                tvMenuTotalPrice.setText(totalOrderPrice+"");

                //null값 처리
                if(cartlistAdapter.menuList.size() == 0){
                    tvCartlistNull.setVisibility(View.VISIBLE);
                    recyclerCartList.setVisibility(View.GONE);
                }else{
                    tvCartlistNull.setVisibility(View.GONE);
                    recyclerCartList.setVisibility(View.VISIBLE);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void makePaymentCouponDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CartlistActivity.this);
        builder.setTitle("쿠폰을 사용하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(CartlistActivity.this, CouponUseActivity.class);
                                startActivityForResult(intent, 99);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 99 && resultCode == RESULT_OK){

            String useCouponState = data.getStringExtra("useCouponState");
            useCouponDeadline = data.getStringExtra("useCouponDeadline");
            useCouponMakeDate = data.getStringExtra("useCouponMakeDate");

            if(useCouponState.matches("true")){
                tvCouponUse.setText("-4100");
                int minusCouponTotalPrice = totalOrderPrice - 4100;
                tvMenuTotalPrice.setText(totalOrderPrice+" - 4100 = "+minusCouponTotalPrice);
                //총가격 변경
                totalOrderPrice = minusCouponTotalPrice;
                //쿠폰사용상태값 변경
                couponUseState = true;
            }else{
                applicationClass.makeLog("쿠폰 사용 안함");
                couponUseState = false;
            }
        }
    }
}
