package com.example.membershipapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import org.w3c.dom.Text;

public class CoffeelistActivity extends LogActivity{

    RecyclerView recyclerCoffeeList;
    CoffeelistAdapter coffeeListAdapter;

    public int currentCardPriceInt; //현재카드잔액
    public int currentStampInt; //현재스탬프수량
    public int paymentPriceInt; //지불할금액

    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;

    AdminMenulistDTO itemNum;   //클릭한 아이템 위치

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffeelist);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("전체 메뉴");

        recyclerCoffeeList = (RecyclerView)findViewById(R.id.recyclerCoffeeList);

        recyclerInit();

        //아이템 클릭 이벤트 메소드
        clickItemView();

    }

    //리사이클러뷰 데이터 초기화
    public void recyclerInit(){

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerCoffeeList.setLayoutManager(linearLayoutManager);

        //어댑터 객체 생성
        coffeeListAdapter = new CoffeelistAdapter(this);
        //리사이클러뷰에 어댑터 설정
        recyclerCoffeeList.setAdapter(coffeeListAdapter);

        //+ - 버튼 클릭 이벤트

        //임시로 데이터 생성
//        coffeeListAdapter.addItem(new CoffeelistDTO(R.drawable.icon_coffee01, "커피 프라푸치노", 5500));
//        coffeeListAdapter.addItem(new CoffeelistDTO(R.drawable.icon_coffee02, "모카 프라푸치노", 5300));
//        coffeeListAdapter.addItem(new CoffeelistDTO(R.drawable.icon_coffee03, "에스프레소 프라푸치노", 5600));

        //firebase test중 (옵저버패턴 -> 글자 수정시 -> 보고있는 앱 화면에서 자동 새로고침 됨)
        applicationClass.firebaseDatabase.getReference().child("AdminMenulist").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                applicationClass.makeLog("//CoffeelistActivity : onDataChange()//");
                //초기화
                coffeeListAdapter.menuList.clear();
                coffeeListAdapter.uidLists.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){    //getChildren = 한 유저에 대한 정보 (한줄) 이라고 생각하면 됨
                    AdminMenulistDTO menulistDTO = snapshot.getValue(AdminMenulistDTO.class);
                    String uidKey = snapshot.getKey();
                    coffeeListAdapter.menuList.add(menulistDTO);
                    coffeeListAdapter.uidLists.add(uidKey);
                }
                coffeeListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2001 && resultCode == RESULT_OK){
//            String addCoffeeNameStr = data.getStringExtra("addCoffeeName");
//            String addCoffeePriceStr = data.getStringExtra("addCoffeePrice");
//            int addCoffeePriceInt = Integer.parseInt(addCoffeePriceStr);
//
//            coffeeListAdapter.addItem(new CoffeelistDTO(R.drawable.icon_coffee01, addCoffeeNameStr, addCoffeePriceInt));
//            coffeeListAdapter.notifyDataSetChanged();
        }else if(requestCode == 99 && resultCode == RESULT_OK){
            Toast.makeText(CoffeelistActivity.this, "결제가 완료되었습니다.", Toast.LENGTH_SHORT).show();
            //쉐어드에 남은 현재잔액 저장
            prefEditor.putInt(sharedCardPriceKey, currentCardPriceInt);

            ++currentStampInt;  //결제완료 -> 스탬프수량 +1
            prefEditor.putInt(sharedStampKey, currentStampInt); //스탬프수량 저장

            prefEditor.apply();
            Log.d(TAG, "쉐어드에 저장한 카드잔액 : "+currentCardPriceInt);
            Log.d(TAG, "쉐어드에 저장한 스탬프수량 : "+currentStampInt);

            Intent intent = getIntent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public void saveSharedCardPriceData(){
        editor.putInt(sharedCardPriceKey, currentCardPriceInt);
        editor.apply();
        Log.d(TAG, "쉐어드에 저장한 카드잔액 : "+currentCardPriceInt);
    }

    //adapter item 클릭 이벤트
    public void clickItemView(){
        //아이템 클릭 이벤트
        coffeeListAdapter.setOnItemClickListener(new CoffeelistAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(RecyclerView.ViewHolder holder, View view, int position) {

                //메뉴명과 메뉴가격 인텐트에 데이터 담아서 보내기
                Intent detailIntent = new Intent(CoffeelistActivity.this, CoffeeDetailActivity.class);
                detailIntent.putExtra("menuCategory", coffeeListAdapter.menuList.get(position).getMenuCategory());
                detailIntent.putExtra("menuName",coffeeListAdapter.menuList.get(position).getMenuName());
                detailIntent.putExtra("menuPrice",coffeeListAdapter.menuList.get(position).getMenuPrice());
                startActivity(detailIntent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shopping_cart_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.btnAppbarMenuCart:

                Intent intent = new Intent(CoffeelistActivity.this, CartlistActivity.class);
                startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
