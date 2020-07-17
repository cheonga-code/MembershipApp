package com.example.membershipapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.IOException;

import static java.lang.Thread.sleep;

public class CouponBarcodeActivity extends LogActivity {

    String TAG = "ScannerActivity";

    ImageView imgBarcode;
    TextView countDownBarcode, barcodeName, barcodeMakeDate, barcodeDeadLine;
    Button btnBarcodeOK;
    String conversionTime = "000010";
    String userEmailStr, userNameStr, couponNameStr, couponDeadlineStr, couponMakeDateStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coupon_barcode);

        imgBarcode = (ImageView)findViewById(R.id.imgBarcode);
        countDownBarcode = (TextView)findViewById(R.id.countDownBarcode);
        barcodeName = (TextView) findViewById(R.id.barcodeName);
        barcodeMakeDate = (TextView)findViewById(R.id.barcodeMakeDate);
        barcodeDeadLine = (TextView)findViewById(R.id.barcodeDeadLine);
        btnBarcodeOK = (Button)findViewById(R.id.btnBarcodeOK);

        //intent 데이터
        Intent intent = getIntent();
        userEmailStr = intent.getStringExtra("userEmail");
        userNameStr = intent.getStringExtra("userName");
        couponNameStr = intent.getStringExtra("couponName");
        couponDeadlineStr = intent.getStringExtra("couponDeadline");
        couponMakeDateStr = intent.getStringExtra("couponMakeDate");

        Log.d(TAG, "바코드 유저 이메일 : "+userEmailStr);
        Log.d(TAG, "바코드 이름 : "+couponNameStr);
        Log.d(TAG, "바코드 유효기간 : "+couponDeadlineStr);
        Log.d(TAG, "바코드 생성날짜 : "+couponMakeDateStr);

        //setText()
        barcodeName.setText("쿠폰이름 : "+couponNameStr);
        barcodeMakeDate.setText("생성날짜 : "+couponMakeDateStr);
        barcodeDeadLine.setText("유효기간 : "+couponDeadlineStr);

        //qr코드 생성
        createQRcode(imgBarcode, userEmailStr+"/"+couponDeadlineStr+"*"+couponMakeDateStr);

        //카운트다운 시작
        countDownStart(conversionTime);

        btnBarcodeOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    public void createQRcode(ImageView img, String text) {

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 500, 500);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            //bitmap 형식의 이미지 파일로 만들어낸다
            img.setImageBitmap(bitmap);
        } catch (Exception e) {
        }
    }

    public void countDownStart(String time){

        long conversionTime = 0;

        // 1000 단위가 1초
        // 60000 단위가 1분
        // 60000 * 3600 = 1시간

        String getHour = time.substring(0, 2);
        String getMin = time.substring(2, 4);
        String getSecond = time.substring(4, 6);

        // "00"이 아니고, 첫번째 자리가 0 이면 제거
        if (getHour.substring(0, 1) == "0") {
            getHour = getHour.substring(1, 2);
        }
        if (getMin.substring(0, 1) == "0") {
            getMin = getMin.substring(1, 2);
        }
        if (getSecond.substring(0, 1) == "0") {
            getSecond = getSecond.substring(1, 2);
        }

        // 변환시간
        conversionTime = Long.valueOf(getHour) * 1000 * 3600 + Long.valueOf(getMin) * 60 * 1000 + Long.valueOf(getSecond) * 1000;

        // 첫번쨰 인자 : 원하는 시간 (예를들어 30초면 30 x 1000(주기))
        // 두번쨰 인자 : 주기( 1000 = 1초)
        new CountDownTimer(conversionTime, 1000) {

            // 특정 시간마다 뷰 변경
            public void onTick(long millisUntilFinished) {

                // 시간단위
                String hour = String.valueOf(millisUntilFinished / (60 * 60 * 1000));
                // 분단위
                long getMin = millisUntilFinished - (millisUntilFinished / (60 * 60 * 1000)) ;
                String min = String.valueOf(getMin / (60 * 1000)); // 몫
                // 초단위
                String second = String.valueOf((getMin % (60 * 1000)) / 1000); // 나머지
                // 밀리세컨드 단위
                String millis = String.valueOf((getMin % (60 * 1000)) % 1000); // 몫
                // 시간이 한자리면 0을 붙인다
                if (hour.length() == 1) {
                    hour = "0" + hour;
                }
                // 분이 한자리면 0을 붙인다
                if (min.length() == 1) {
                    min = "0" + min;
                }
                // 초가 한자리면 0을 붙인다
                if (second.length() == 1) {
                    second = "0" + second;
                }
                countDownBarcode.setText(hour + ":" + min + ":" + second);
            }

            // 제한시간 종료시
            @SuppressLint("ResourceAsColor")
            public void onFinish() {
                countDownBarcode.setText("바코드 유효시간 종료");
                countDownBarcode.setTextColor(getResources().getColor(R.color.colorRed));
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finish();
            }
        }.start();

    }
}
