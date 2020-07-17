package com.example.membershipapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
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

public class CardChargeActivity extends LogActivity {

    EditText etChargeCardPrice;
    Button btnChargeCardPrice;
    String regExpPrice = "^[0-9]+$";

    String chargeCardPriceStr;
    int chargeCardPriceInt;
    private int stuck = 10; //부트페이 관련 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_charge);

        etChargeCardPrice = (EditText)findViewById(R.id.etChargeCardPrice);
        btnChargeCardPrice = (Button)findViewById(R.id.btnChargeCardPrice);

        //부트페이 : 초기설정 - 해당 프로젝트(안드로이드)의 application id 값을 설정합니다. 결제와 통계를 위해 꼭 필요합니다.
        BootpayAnalytics.init(this, "5b3d9d66396fa605ccad552a");

        btnChargeCardPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chargeCardPriceStr = etChargeCardPrice.getText().toString();

                //공백 처리
                if(chargeCardPriceStr.equals("")){
                    Toast.makeText(CardChargeActivity.this, "충전할 요금을 적어주세요", Toast.LENGTH_SHORT).show();
                }else{
                    //정규식 확인 = 숫자만 허용
                    if(chargeCardPriceStr.matches(regExpPrice)){
                        Log.d(TAG, "숫자만 입력됨 -> 허용");
                        chargeCardPriceInt = Integer.parseInt(chargeCardPriceStr);

//                        Intent payPwIntent = new Intent(CardChargeActivity.this, PaymentPasswordActivity.class);
//                        startActivityForResult(payPwIntent, 999);
                        //부트페이 결제창 띄어줌
                        makeBootpayDialog(chargeCardPriceInt);

                    }else {
                        Toast.makeText(CardChargeActivity.this, "숫자만 입력해주세요.", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode == 999 && resultCode == RESULT_OK){
//
//            Toast.makeText(CardChargeActivity.this, chargeCardPriceStr+"원이 충전되었습니다.", Toast.LENGTH_SHORT).show();
//
//            Intent intent = getIntent();
//            intent.putExtra("chargeCardPrice", chargeCardPriceInt);
//            setResult(RESULT_OK, intent);
//            finish();
//
//        }
//    }

    //카드충전 다이얼로그
    public void makeBootpayDialog(final int chargePrice){
        AlertDialog.Builder builder = new AlertDialog.Builder(CardChargeActivity.this);
        builder.setTitle("카드 요금을 충전하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("휴대폰결제",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("LogActivity", "다이얼로그 휴대폰결제 선택");
                                //부트페이로 결제요청
                                requestBootpay("PHONE","멤버십 카드 용금 충전", chargePrice);

                            }
                        })
                .setNegativeButton("카드결제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("LogActivity", "다이얼로그 카드결제 선택");
                        //부트페이로 결제요청
                        requestBootpay("CARD","멤버십 카드 용금 충전", chargePrice);
                    }
                })
                .setNeutralButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("LogActivity", "다이얼로그 취소 선택");
                                dialog.cancel();
                                finish();
                            }
                        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void requestBootpay(String payMethodStr, String itemNameStr, final int itemPriceInt){
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
                        //사용자의 카드금액 데이터베이스에서 변경
//                        changeUserIntData("userCardPrice", itemPriceInt);

                        Toast.makeText(CardChargeActivity.this, chargeCardPriceStr+"원이 충전되었습니다.", Toast.LENGTH_SHORT).show();

                        Intent intent = getIntent();
                        intent.putExtra("chargeCardPrice", chargeCardPriceInt);
                        setResult(RESULT_OK, intent);
                        finish();
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

    //파이어베이스에 있는 유저 정보 업데이트하기
    public void changeUserIntData(String changeKey , int changeValue){

        Map<String, Object> userValues = new HashMap<String,Object>();
        userValues.put(changeKey, changeValue);

        applicationClass.databaseReference.child("users")
                .child(applicationClass.EncodeString(applicationClass.loginEmail))
                .updateChildren(userValues);
    }

}
