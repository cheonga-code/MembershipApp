package com.example.membershipapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class AdminNewslistActivity extends LogActivity {

    RecyclerView recyclerNewslist;
    Button btnAddNews;
    AdminNewslistAdapter adminNewslistAdapter;
    AdminNewslistDTO itemNum;   //클릭한 아이템 위치
    String TAG = "LogActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_newslist);

        //앱바 제목 변경
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("관리자 뉴스 관리");

        recyclerNewslist = (RecyclerView)findViewById(R.id.recyclerNewslist);
        btnAddNews = (Button)findViewById(R.id.btnAddNews);

        //리사이클러뷰 초기화한다
        recyclerInit();
        //파이어베이스에 있는 데이터를 불러온다
        loadData();
        //아이템 클릭 이벤트 메소드
        clickItemView();

        btnAddNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newsAddIntent = new Intent(AdminNewslistActivity.this, AdminNewsAddActivity.class);
                int listIndex = adminNewslistAdapter.newsList.size();
                Log.d(TAG, "btnAddMenu() 클릭 -> menuList.size()  : "+listIndex);
                newsAddIntent.putExtra("listIndex", listIndex);
                startActivityForResult(newsAddIntent, 5001);
            }
        });
    }

    //리사이클러뷰 데이터 초기화
    public void recyclerInit(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerNewslist.setLayoutManager(linearLayoutManager);
        //어댑터 객체 생성
        adminNewslistAdapter = new AdminNewslistAdapter(this);
        recyclerNewslist.setAdapter(adminNewslistAdapter);
    }

    //adapter item 클릭 이벤트
    public void clickItemView(){
        //아이템 클릭 이벤트
        adminNewslistAdapter.setOnItemClickListener(new AdminNewslistAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                //아이템 위치
                itemNum = adminNewslistAdapter.getItem(position);
                Log.d(TAG, "클릭한 아이템 위치 : "+position);
//                String key = applicationClass.databaseReference.getKey();
//                Log.d(TAG, "클릭한 아이템 key : "+key);
            }
        });

    }

    //firebase realtime 데이터 가져와서 recyclerview에 적용
    public void loadData(){
        //firebase test중 (옵저버패턴 -> 글자 수정시 -> 보고있는 앱 화면에서 자동 새로고침 됨)
        applicationClass.firebaseDatabase.getReference().child("AdminNewslist")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //초기화
                        adminNewslistAdapter.newsList.clear();
                        adminNewslistAdapter.uidLists.clear();

                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){    //getChildren = 한 유저에 대한 정보 (한줄) 이라고 생각하면 됨
                            AdminNewslistDTO newslistDTO = snapshot.getValue(AdminNewslistDTO.class);
                            String uidKey = snapshot.getKey();
                            Log.d(TAG, "key :"+ uidKey);    //key :-M8T777K-KXhth72lNRF
                            adminNewslistAdapter.newsList.add(newslistDTO);
                            adminNewslistAdapter.uidLists.add(uidKey);
                        }
                        adminNewslistAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
