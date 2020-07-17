package com.example.membershipapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class EventNewsActivity extends LogActivity {

    RecyclerView recyclerEventNewslist;
    EventNewsAdapter eventNewsAdapter;
    AdminNewslistDTO itemNum;   //클릭한 아이템 위치
    String TAG = "LogActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_news);

        //앱바 제목 변경
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("잼잼오더 소식");

        recyclerEventNewslist = (RecyclerView)findViewById(R.id.recyclerEventNewslist);

        //리사이클러뷰 초기화한다
        recyclerInit();
        eventNewsAdapter.newslistDTOArrayList.clear();

        //파이어베이스에 있는 데이터를 불러온다
        loadData();
    }

    //리사이클러뷰 데이터 초기화
    public void recyclerInit(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerEventNewslist.setLayoutManager(linearLayoutManager);
        //어댑터 객체 생성
        eventNewsAdapter = new EventNewsAdapter(this);
        recyclerEventNewslist.setAdapter(eventNewsAdapter);
    }

    //firebase realtime 데이터 가져와서 recyclerview에 적용
    public void loadData(){
        //firebase test중 (옵저버패턴 -> 글자 수정시 -> 보고있는 앱 화면에서 자동 새로고침 됨)
        applicationClass.firebaseDatabase.getReference().child("AdminNewslist")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //초기화
                        eventNewsAdapter.newslistDTOArrayList.clear();
                        eventNewsAdapter.uidLists.clear();

                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){    //getChildren = 한 유저에 대한 정보 (한줄) 이라고 생각하면 됨
                            AdminNewslistDTO newslistDTO = snapshot.getValue(AdminNewslistDTO.class);
                            String uidKey = snapshot.getKey();
                            Log.d(TAG, "key :"+ uidKey);    //key :-M8T777K-KXhth72lNRF
                            eventNewsAdapter.newslistDTOArrayList.add(newslistDTO);
                            eventNewsAdapter.uidLists.add(uidKey);
                        }
                        eventNewsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
