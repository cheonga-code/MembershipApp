package com.example.membershipapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CartlistItemEditActivity extends LogActivity {

    EditText etEditCartItmeQuantity;
    Button btnEditCartItemQuantityOK;
    String menulistNameKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartlist_item_edit);

        etEditCartItmeQuantity = (EditText)findViewById(R.id.etEditCartItmeQuantity);
        btnEditCartItemQuantityOK = (Button)findViewById(R.id.btnEditCartItemQuantityOK);

        Intent intent = getIntent();
        int beforeQuantity = intent.getIntExtra("editCartItemQuantity", 0);
        menulistNameKey = intent.getStringExtra("menulistNameKey");

        etEditCartItmeQuantity.setText(beforeQuantity+"");

        btnEditCartItemQuantityOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //getText()
                String afterQuantityStr = etEditCartItmeQuantity.getText().toString();
                int afterQuantityInt = Integer.parseInt(afterQuantityStr);

                //파이어베이스에 변경된 수량 데이터 저장
                changeData(afterQuantityInt);

                finish();
            }
        });
    }

    public void changeData(int changeValue){
        applicationClass.databaseReference.child("cartlist")
                .child(applicationClass.EncodeString(applicationClass.loginEmail))
                .child(menulistNameKey)
                .child("cartMenuQuantity").setValue(changeValue);
        Log.d(TAG, "파이어베이스 데이터 변경해서 저장 완료");
    }
}
