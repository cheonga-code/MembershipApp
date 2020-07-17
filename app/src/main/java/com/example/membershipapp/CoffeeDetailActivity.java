package com.example.membershipapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;

public class CoffeeDetailActivity extends LogActivity implements View.OnClickListener{

    FloatingActionButton btnFloatingCart;
    ImageView menuDetailImg;
    TextView menuDetailName, menuDetailPrice, tvCartCounter, menuDetailQuantity;
    Button btnDetailDecrement, btnDetailIncrement, btnCartGO;

    String menuCategoryStr;

    Users users;
    CartlistDTO loadCartlistDTO;
    String key;
    String cartSameMenuName;
    ArrayList<String> cartMenuLists = new ArrayList<>();
    ArrayList<Integer> cartMenuQuantityLists = new ArrayList<>();

    Boolean findSameMenu = false;
    int currentCartItemCount = 0;       //장바구니에 담겨져 있는 아이템 갯수
    int currentSelectedItemCount = 0;   //장바구니에 담기위해 선택한 아이템 갯수
    int currentCartItemQuantity = 0;        //내가 담을 아이템이 장바구니에 이미 담겨있을 경우 -> 현재 담겨있는 수량 갯수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_detail);

        //앱바 제목 변경
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("메뉴 상세");

        btnFloatingCart = (FloatingActionButton)findViewById(R.id.btnFloatingCart);
        menuDetailImg = (ImageView)findViewById(R.id.menuDetailImg);
        menuDetailName = (TextView)findViewById(R.id.menuDetailName);
        menuDetailPrice = (TextView)findViewById(R.id.menuDetailPrice);
        tvCartCounter = (TextView)findViewById(R.id.tvCartCounter);
        menuDetailQuantity = (TextView)findViewById(R.id.menuDetailQuantity);
        btnDetailDecrement = (Button)findViewById(R.id.btnDetailDecrement);
        btnDetailIncrement = (Button)findViewById(R.id.btnDetailIncrement);
        btnCartGO = (Button)findViewById(R.id.btnCartGO);

        Intent intent = getIntent();
        menuCategoryStr = intent.getStringExtra("menuCategory");
        String menuNameStr = intent.getStringExtra("menuName");
        int menuPriceInt = intent.getIntExtra("menuPrice", 0);

        //데이터 불러오기
//        loadCartCountData();
        checkCartlistData();

        //장바구니 수량 = cartMenuLists.size();

        //받은 데이터 setText() 하기
        menuDetailName.setText(menuNameStr);
        menuDetailPrice.setText(menuPriceInt+"");
        menuDetailQuantity.setText(currentSelectedItemCount+"");
//        menuDetailQuantity.setText(""+cartMenuLists.size());
//        applicationClass.makeLog("현재 장바구니 수량 (2) : "+currentCartItemCount);

        //클릭 이벤트
        btnDetailDecrement.setOnClickListener(this);
        btnDetailIncrement.setOnClickListener(this);
        btnCartGO.setOnClickListener(this);
        btnFloatingCart.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
                //수량 증가 버튼
            case R.id.btnDetailIncrement:
                currentSelectedItemCount = ++currentSelectedItemCount;
                menuDetailQuantity.setText(""+currentSelectedItemCount);
                break;
                //수량 감소 버튼
            case R.id.btnDetailDecrement:

                if(currentSelectedItemCount <= 0){
                    applicationClass.makeToast("수량 감소가 불가합니다.");
                }else{
                    currentSelectedItemCount = --currentSelectedItemCount;
                    menuDetailQuantity.setText(""+currentSelectedItemCount);
                }
                break;
                //장바구니 담기 버튼
            case R.id.btnCartGO:

                    //수량 0일 때 -> 아이템 수량을 0으로 선택하고 장바구니 담기를 누르면 -> 장바구니 담기 불가
                    if(menuDetailQuantity.getText().toString().matches("0")) {
                        applicationClass.makeToast("수량 선택 후 장바구니 담기가 가능합니다.");
                    }else {

                        //수량 1 이상일 때 -> 장바구니에 내가 담을 아이템 있는지없는지 체크
                        //장바구니에 담을 아이템이 이미 있을 경우
                        for(int i=0; i < cartMenuLists.size(); i++){
                            applicationClass.makeLog("cartMenu 키 리스트 size() : "+cartMenuLists.size());

                            cartSameMenuName = cartMenuLists.get(i);
                            applicationClass.makeLog("cartMenu 키 이름 : "+cartSameMenuName);

                            if(menuDetailName.getText().toString().matches(cartSameMenuName)) {
                                //현재 담겨있는 장바구니의 수량 데이터 받아오기

                                currentCartItemQuantity = cartMenuQuantityLists.get(i);
                                applicationClass.makeLog("현재 장바구니에 담겨있는 수량 : " + currentCartItemQuantity);
                                //수량만 추가할 건지 물어보는 다이얼로그 띄우기
                                sameItemDialog();
                                applicationClass.makeLog("+++장바구니에서 같은 메뉴 발견함+++");

                                findSameMenu = true;
                                break;
                            }
                        }

                        //장바구니에 담을 아이템이 없을 경우
                        if(findSameMenu == false){
                            applicationClass.makeLog("---장바구니에서 같은 메뉴 발견못함---");
                            NotSameItemDialog();
                        }
                    }

                break;
            case R.id.btnFloatingCart:
                Intent cartlistIntent = new Intent(this, CartlistActivity.class);
                startActivity(cartlistIntent);
                finish();
                break;
        }
    }

//    public void loadCartCountData(){
//        //데이터 저장소에서 카트수량 데이터 받아와서 setText() 하기
//        applicationClass.databaseReference.child("users").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
////                    currentCartCount = snapshot.getValue();
//                    users = snapshot.getValue(Users.class);
//                    String uidKey = snapshot.getKey();
//                    applicationClass.makeLog("key(확인) : "+uidKey);
//
//                    //이메일키 , -> . 로 디코딩
//                    key = applicationClass.DecodeString(uidKey);
//
//                    if(applicationClass.loginEmail.equals(key)){
//                        currentCartItemCount = users.getUserCartCount();
//                        tvCartCounter.setText(""+currentCartItemCount);
//                        applicationClass.makeLog("현재 장바구니 수량 (1) : "+currentCartItemCount);
//
//                        break;
//                    }
////                    applicationClass.makeLog("currentCartCount : " + snapshot.getValue());
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    //장바구니에 내가 담을 아이템이 이미 담겨있는지 없는지 체크하는 메소드
    public void checkCartlistData(){
        //데이터 저장소에서 카트리스트 데이터 받아오기
        applicationClass.databaseReference.child("cartlist").child(applicationClass.EncodeString(applicationClass.loginEmail))
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            //장바구니리스트 메뉴키 리스트 초기화
            cartMenuLists.clear();
            //장바구니리스트 수량 리스트 초기화
            cartMenuQuantityLists.clear();

            applicationClass.makeLog("//CoffeeDetailActivity : onDataChange()//");

             for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                 loadCartlistDTO = snapshot.getValue(CartlistDTO.class);
                 //장바구니 리스트 사용자 이메일 키값
                 String cartlistKey = snapshot.getKey();
                 applicationClass.makeLog("장바구니 리스트 key : "+ cartlistKey);
                 //장바구니에 담긴 메뉴 key 들을 리스트에 담는다
                 cartMenuLists.add(cartlistKey);

                 //장바구니에 담긴 메뉴 key -> 수량
                 int num = loadCartlistDTO.getCartMenuQuantity();
                 cartMenuQuantityLists.add(num);
                 applicationClass.makeLog("cartMenuQuantityLists : "+ cartMenuQuantityLists.toString());
             }

                applicationClass.makeLog("현재 장바구니 수량 (2) cartMenuLists.size() : "+cartMenuLists.size());
                //현재 장바구니 수량 count setText()
                tvCartCounter.setText(cartMenuLists.size()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    private void saveCartlistData() {

        String cartMenuNameStr = menuDetailName.getText().toString();
        String cartMenuPriceStr = menuDetailPrice.getText().toString();
        int cartMenuPriceInt = Integer.parseInt(cartMenuPriceStr);
        String cartMenuQuantityStr = menuDetailQuantity.getText().toString();
        int cartMenuQuantityInt = Integer.parseInt(cartMenuQuantityStr);

        CartlistDTO saveCartlistDTO = new CartlistDTO();
        saveCartlistDTO.cartMenuCategory = menuCategoryStr;
        saveCartlistDTO.cartMenuImgPath = "default";
        saveCartlistDTO.cartMenuName = cartMenuNameStr;
        saveCartlistDTO.cartMenuPrice = cartMenuPriceInt;
        saveCartlistDTO.cartMenuQuantity = cartMenuQuantityInt;

        //로그인이메일을 키값으로 저장할때는 . -> , 로 인코딩해서 저장함
        applicationClass.databaseReference.child("cartlist")
                .child(applicationClass.EncodeString(applicationClass.loginEmail))
                .child(cartMenuNameStr).setValue(saveCartlistDTO);
    }

    //장바구니에 담을 아이템이 이미 있는 경우
    public void sameItemDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CoffeeDetailActivity.this);
        builder.setTitle("메뉴가 이미 담겨져 있습니다.")
                .setMessage("수량을 추가하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "다이얼로그 수량만 추가 선택");
                                //추가할 수량 데이터
                                int addCartItemQuantity = Integer.parseInt(menuDetailQuantity.getText().toString());
                                //현재 수량 데이터 + 추가할 수량 데이터
                                currentCartItemQuantity = currentCartItemQuantity + addCartItemQuantity;
                                applicationClass.makeLog("(후) 장바구니에 담겨있는 수량 : "+currentCartItemQuantity);
                                //담으려는 메뉴 찾아서 수량만 변경
                                applicationClass.databaseReference.child("cartlist")
                                        .child(applicationClass.EncodeString(applicationClass.loginEmail))
                                        .child(cartSameMenuName).child("cartMenuQuantity").setValue(currentCartItemQuantity);

                                //장바구니 수량추가 토스트 메세지
                                applicationClass.makeToast("수량이 추가되었습니다.");
                            }
                        })
                .setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "다이얼로그 취소 선택");
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void NotSameItemDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CoffeeDetailActivity.this);
        builder.setTitle("메뉴를 장바구니에 추가하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG, "다이얼로그 장바구니에 담기 선택");
                            //장바구니에 아이템이 담겨있지 않은 경우
//                            //장바구니 수량 증가
//                            currentCartItemCount = ++currentCartItemCount;
//                            //장바구니 플로팅 버튼 옆 textview 수량 증가 setText()
//                            tvCartCounter.setText("" + currentCartItemCount);
//                            applicationClass.makeLog("현재 장바구니 수량 (3) : "+currentCartItemCount);
//                            //장바구니 수량 데이터 저장
//                            applicationClass.databaseReference.child("users")
//                                    .child(applicationClass.EncodeString(key)).child("userCartCount").setValue(currentCartItemCount);

                            //장바구니 리스트 파이어베이스 저장
                            saveCartlistData();

                            //장바구니 담김 토스트 메세지
                            applicationClass.makeToast("장바구니에 상품이 담겼습니다.");
                            }
                        })
                .setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "다이얼로그 취소 선택");
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


}
