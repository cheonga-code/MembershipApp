package com.example.membershipapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class AdminNewslistActivity extends AppCompatActivity {

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
//        //파이어베이스에 있는 데이터를 불러온다
//        loadData();
        //아이템 클릭 이벤트 메소드
        clickItemView();
//
//        btnAddNews.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent menuAddIntent = new Intent(AdminNewslistActivity.this, AdminMenuAddActivity.class);
//                int listIndex = adminMenulistAdapter.menuList.size();
//                Log.d(TAG, "btnAddMenu() 클릭 -> menuList.size()  : "+listIndex);
//                menuAddIntent.putExtra("listIndex", listIndex);
//                startActivityForResult(menuAddIntent, 2001);
//            }
//        });
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
}
