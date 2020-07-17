package com.example.membershipapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminMenuEditActivity extends LogActivity {

    FirebaseFirestore firebaseFirestore;

    ImageView imgEditMenu;
    TextView etEditMenuName, etEditMenuPrice;
    Button btnEditMenuOK;
    RadioGroup radioGroupAddMenuEdit;
    RadioButton radioBtnBeverageEdit, radioBtnDessertEdit;

    String menulistUidKey, editBeforeMenuCategory, editAfterMenuCategory,
            editBeforeMenuName, editAfterMenuName;
    int editBeforeMenuPriceInt, editAfterMenuPriceInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu_edit);

        firebaseFirestore = FirebaseFirestore.getInstance();

        imgEditMenu = (ImageView)findViewById(R.id.imgEditMenu);
        etEditMenuName = (TextView)findViewById(R.id.etEditMenuName);
        etEditMenuPrice = (TextView)findViewById(R.id.etEditMenuPrice);
        btnEditMenuOK = (Button)findViewById(R.id.btnEditMenuOK);
        radioGroupAddMenuEdit = (RadioGroup)findViewById(R.id.radioGroupAddMenuEdit);
        radioBtnBeverageEdit = (RadioButton)findViewById(R.id.radioBtnBeverageEdit);
        radioBtnDessertEdit = (RadioButton)findViewById(R.id.radioBtnDessertEdit);

        Intent intent = getIntent();
        menulistUidKey = intent.getStringExtra("menulistUidKey");
        editBeforeMenuCategory = intent.getStringExtra("editMenuCategory");
        editBeforeMenuName = intent.getStringExtra("editMenuName");
        editBeforeMenuPriceInt = intent.getIntExtra("editMenuPrice", 0);

        //받은 데이터 setText() 해놓기
        if(editBeforeMenuCategory.matches(" 음료")){
            radioBtnBeverageEdit.setChecked(true);
        }else if(editBeforeMenuCategory.matches("디저트")){
            radioBtnDessertEdit.setChecked(true);
        }

        etEditMenuName.setText(editBeforeMenuName);
        etEditMenuPrice.setText(editBeforeMenuPriceInt+"");

        btnEditMenuOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editAfterMenuName = etEditMenuName.getText().toString();
                editAfterMenuPriceInt = Integer.parseInt(etEditMenuPrice.getText().toString());

                //변경된 데이터 저장
                changeData(menulistUidKey);

                finish();

            }
        });

    }

    //firestore에 변경된 데이터 저장
    public void changeData(String menulistUidKey){
        applicationClass.databaseReference.child("AdminMenulist").child(menulistUidKey).child("menuCategory").setValue(checkMenuCategory());
        applicationClass.databaseReference.child("AdminMenulist").child(menulistUidKey).child("menuName").setValue(editAfterMenuName);
        applicationClass.databaseReference.child("AdminMenulist").child(menulistUidKey).child("menuPrice").setValue(editAfterMenuPriceInt);
        Log.d(TAG, "파이어베이스 데이터 변경해서 저장 완료");
    }

    //체크된 라디오 버튼 아이디값 가져오기
    public String checkMenuCategory(){
        int radioID = radioGroupAddMenuEdit.getCheckedRadioButtonId();  //리턴값은 선택된 adioButton 의 id 값
        RadioButton checkedRadioBtn = (RadioButton)findViewById(radioID);
        String checkedMenuCategory = checkedRadioBtn.getText().toString();
        applicationClass.makeLog("체크한 라디오 버튼 이름 : "+checkedRadioBtn.getText().toString());

        return checkedMenuCategory;
    }

//    //firestore에 변경된 데이터 저장
//    public void changeData(){
//        firebaseFirestore.collection("menu").document(editBeforeMenuName)
//                .update("menuName", editAfterMenuName, "menuPrice", editAfterMenuPriceLong)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d(TAG, "firestore(field) 데이터 수정 완료");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d(TAG, "firestore(field) 데이터 수정 실패", e);
//                    }
//                });
//    }
}
