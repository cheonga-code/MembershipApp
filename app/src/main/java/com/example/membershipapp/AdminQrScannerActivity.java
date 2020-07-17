package com.example.membershipapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AdminQrScannerActivity extends LogActivity {

    String TAG = "ScannerActivity";

    String userEmail, couponMakeDate, couponDeadline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_qrcode);

        new IntentIntegrator(this).initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                // todo
                finish();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show(); //scanned: aaa (aaa 는 result.getContents())`
                // QR코드를 읽어서 가져오는 부분
                // null이 아니면 결과값이 있다는 것!
                String resultScannerStr = result.getContents();
                Log.d(TAG, "스캐너 : "+resultScannerStr);
                int index1 = resultScannerStr.indexOf("/");
                int index2 = resultScannerStr.indexOf("*");
                Log.d(TAG, "스캐너 / index : "+index1);
                Log.d(TAG, "스캐너 * index : "+index2);

                userEmail = resultScannerStr.substring(0, index1);
                couponDeadline = resultScannerStr.substring(index1+1, index2);
                couponMakeDate = resultScannerStr.substring(index2+1);

                Log.d(TAG, "스캐너 userEmail : "+userEmail);
                Log.d(TAG, "스캐너 couponDeadline : "+couponDeadline);
                Log.d(TAG, "스캐너 couponMakeDate : "+couponMakeDate);

                //다이얼로그 띄우기
                makeDialog(userEmail, couponDeadline, couponMakeDate);

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //주문 승인시
    public void makeDialog(final String userEmail, final String couponDeadline, final String couponMakeDate){
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminQrScannerActivity.this);
        builder.setTitle("쿠폰 결제를 승인하시겠습니까?")
                .setMessage("사용자 : "+userEmail+"\n쿠폰 유효기간 : "+couponDeadline)
                .setCancelable(false)
                .setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "다이얼로그 확인 선택");
                                //이용가능한 쿠폰리스트에서 삭제 처리
                                removeUserAvailableCouponData(userEmail, couponMakeDate);
//                                //히스토리 쿠폰리스트에 데이터 추가
//                                addUserHistoryCouponData(userEmail);
                                //FCM 알림 보내기
                                SendNotification.sendToUserFCM(
                                        applicationClass.EncodeString(userEmail), "잼잼오더", "쿠폰이 사용되었습니다.");
                                //화면 전환
                                Intent intent = new Intent(AdminQrScannerActivity.this, AdminHomeActivity.class);
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
                                finish();
                            }
                        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
        couponHistoryDTO.couponUseDeadline = couponDeadline;

        applicationClass.databaseReference.child("couponHistoryList")
                .child(applicationClass.EncodeString(userEmail))
                .child(currentDate).setValue(couponHistoryDTO);
        Log.d(TAG, "///쿠폰 히스토리 완료 (3)///");
    }
}
