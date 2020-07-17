package com.example.membershipapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class AdminMenulistActivity extends LogActivity {

    RecyclerView recyclerMenulist;
    Button btnAddMenu;
    AdminMenulistAdapter adminMenulistAdapter;
    AdminMenulistDTO itemNum;   //클릭한 아이템 위치

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_memulist);

        //앱바 제목 변경
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("관리자 메뉴 관리");

        recyclerMenulist = (RecyclerView)findViewById(R.id.recyclerMenulist);
        btnAddMenu = (Button)findViewById(R.id.btnAddMenu);

        //리사이클러뷰 초기화한다
        recyclerInit();
        //파이어베이스에 있는 데이터를 불러온다
        loadData();
        //아이템 클릭 이벤트 메소드
        clickItemView();

        btnAddMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuAddIntent = new Intent(AdminMenulistActivity.this, AdminMenuAddActivity.class);
                int listIndex = adminMenulistAdapter.menuList.size();
                Log.d(TAG, "btnAddMenu() 클릭 -> menuList.size()  : "+listIndex);
                menuAddIntent.putExtra("listIndex", listIndex);
                startActivityForResult(menuAddIntent, 2001);
            }
        });
    }

    //리사이클러뷰 데이터 초기화
    public void recyclerInit(){

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerMenulist.setLayoutManager(linearLayoutManager);

        //어댑터 객체 생성
        adminMenulistAdapter = new AdminMenulistAdapter(this);
        recyclerMenulist.setAdapter(adminMenulistAdapter);

    }

    //adapter item 클릭 이벤트
    public void clickItemView(){
        //아이템 클릭 이벤트
        adminMenulistAdapter.setOnItemClickListener(new AdminMenulistAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                //아이템 위치
                itemNum = adminMenulistAdapter.getItem(position);
                Log.d(TAG, "클릭한 아이템 위치 : "+position);

                String key = applicationClass.databaseReference.getKey();
                Log.d(TAG, "클릭한 아이템 key : "+key);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2001 && requestCode == RESULT_OK){

        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        //firestore "menu" 데이터 가져와서 로드 시킴
//        Log.d(TAG, "onResume() -> menuList.size() : "+adminMenulistAdapter.menuList.size());
//        loadData();
//    }

    //firebase realtime 데이터 가져와서 recyclerview에 적용
    public void loadData(){
        //firebase test중 (옵저버패턴 -> 글자 수정시 -> 보고있는 앱 화면에서 자동 새로고침 됨)
        applicationClass.firebaseDatabase.getReference().child("AdminMenulist")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //초기화
                adminMenulistAdapter.menuList.clear();
                adminMenulistAdapter.uidLists.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){    //getChildren = 한 유저에 대한 정보 (한줄) 이라고 생각하면 됨
                    AdminMenulistDTO menulistDTO = snapshot.getValue(AdminMenulistDTO.class);
                    String uidKey = snapshot.getKey();
                    Log.d(TAG, "key :"+ uidKey);    //key :-M8T777K-KXhth72lNRF
                    adminMenulistAdapter.menuList.add(menulistDTO);
                    adminMenulistAdapter.uidLists.add(uidKey);
                }
                adminMenulistAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//    //firestore "menu" 데이터 가져와서 recyclerview에 적용
//    public void loadData(){
//
//        firebaseFirestore.collection("menu")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()){
//                            for(QueryDocumentSnapshot document : task.getResult()){
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                                //53kLTFtaoRUHP2y00OYx => {menuName=보쌈, menuPrice=21000, menuImg=2131165347}
//
//                                String menuNameStr = (String) document.get("menuName");
//                                Long menuPriceInt = (Long) document.get("menuPrice");
//
//                                Log.d(TAG, menuNameStr+" : " + menuPriceInt);   // 보쌈 : 21000
//                                adminMenulistAdapter.addItem(new AdminMenulistDTO(R.drawable.icon_coffee02, menuNameStr, menuPriceInt));
//                            }
//                            adminMenulistAdapter.notifyDataSetChanged();
//                        }else{
//                            Log.d(TAG, "firestore에서 document를 가져오지 못함", task.getException());
//                        }
//                    }
//                });
//    }

}
