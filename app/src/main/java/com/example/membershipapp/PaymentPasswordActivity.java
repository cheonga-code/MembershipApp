package com.example.membershipapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PaymentPasswordActivity extends AppCompatActivity {

    EditText etPayPw;
    Button btnPayPwOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_password);

        etPayPw = (EditText)findViewById(R.id.etPayPw);
        btnPayPwOK = (Button)findViewById(R.id.btnPayPwOK);

        btnPayPwOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String payPwStr = etPayPw.getText().toString();

                if(payPwStr.equals("")){
                    Toast.makeText(PaymentPasswordActivity.this, "비밀번호를 적어주세요", Toast.LENGTH_SHORT).show();
                }else if(payPwStr.equals("1234")){
//                    Toast.makeText(PaymentPasswordActivity.this, "비밀번호가 맞습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = getIntent();
                    setResult(RESULT_OK, intent);
                    finish();
                }else{
                    Toast.makeText(PaymentPasswordActivity.this, "비밀번호가 틀립니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
