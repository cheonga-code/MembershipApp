package com.example.membershipapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class AdminNewslistAdapter extends RecyclerView.Adapter<AdminNewslistAdapter.AdminNewslistViewHolder> {

    String TAG = "LogActivity";
    Context context;
    OnItemClickListener clickListener;

    ArrayList<AdminNewslistDTO> newsList = new ArrayList<AdminNewslistDTO>();
    ArrayList<String> uidLists = new ArrayList<>();

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    public AdminNewslistAdapter(Context context) {
        this.context = context;
    }

    public static interface OnItemClickListener{
      public void OnItemClick(RecyclerView.ViewHolder holder, View view, int position);
    }

    @NonNull
    @Override
    public AdminNewslistAdapter.AdminNewslistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_admin_newslist, parent, false);
        AdminNewslistViewHolder adminNewslistViewHolder = new AdminNewslistViewHolder(view);

        return adminNewslistViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdminNewslistAdapter.AdminNewslistViewHolder holder, final int position) {

        final int itemPosition = position;
        final AdminNewslistDTO item = newsList.get(position);

        holder.setOnItemClickListener(clickListener);

        //setText()
//        newsList.size();

        holder.newsNumber.setText(Integer.toString(itemPosition+1));
        holder.newsTitle.setText(item.getNewsTitle());

        //삭제버튼 클릭시
        holder.btnDeleteNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //알럿 빌더 생성
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                //빌더에 정보입력
                builder.setTitle("삭제학인")
                        .setMessage("삭제하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //firebase 삭제 진행
//                                databaseDelete(item.getMenuName(), itemPosition);
                                databaseDelete(position);
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                //알럿 생성
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public void addItem(AdminNewslistDTO item){
        newsList.add(item);
    }

    public AdminNewslistDTO getItem(int position){
        return newsList.get(position);
    }

    //아이템 클릭 리스너 메소드
    public void setOnItemClickListener(OnItemClickListener clickListener){
        this.clickListener = clickListener;
    }

    public class AdminNewslistViewHolder extends RecyclerView.ViewHolder {

        TextView newsNumber, newsTitle;
        ImageButton btnEditNews, btnDeleteNews;

        OnItemClickListener clickListener;

        public AdminNewslistViewHolder(@NonNull final View itemView) {
            super(itemView);

            newsNumber = (TextView) itemView.findViewById(R.id.newsNumber);
            newsTitle = (TextView) itemView.findViewById(R.id.newsTitle);
            btnEditNews = (ImageButton) itemView.findViewById(R.id.btnEditNews);
            btnDeleteNews = (ImageButton) itemView.findViewById(R.id.btnDeleteNews);

            //각각 아이템 뷰에 올릴 리스너 이벤트 설정 -> 각각의 아이템 뷰가 클릭됬을 때
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //위의 어댑터에 등록한 리스너로 넘겨준다.
                    int position = getAdapterPosition();    //클릭한 아이템의 위치를 알수있다

                    if(clickListener != null){
                        clickListener.OnItemClick(AdminNewslistViewHolder.this, v, position);
                    }

                }
            });

            //수정 버튼 클릭시
            btnEditNews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition(); //클릭한 아이템의 위치를 알수있다
                    Log.d("LogActivity", "수정버튼 클릭 위치 : "+ position);

                    //newslist 의 uidkey 값 intent에 넣어서 전달
                    String newslistUidKey = uidLists.get(position);
                    Log.d(TAG, "수정버튼 클릭 uidKey :"+ newslistUidKey);

                    Context context = v.getContext();

                    Intent intent = new Intent(v.getContext(), AdminNewsEditActivity.class);
                    intent.putExtra("newslistUidKey", newslistUidKey);
                    intent.putExtra("editNewsTitle", newsList.get(position).getNewsTitle());
                    intent.putExtra("editNewsContent", newsList.get(position).getNewsContent());

                    context.startActivity(intent);
                    Log.d("LogActivity", "adapter -> edit화면으로 보냄");
                }
            });


        }

        //아이템 클릭 리스너 메소드
        public void setOnItemClickListener(OnItemClickListener clickListener){
            this.clickListener = clickListener;
        }
    }

    //database realtime 에서 삭제
    public void databaseDelete(int position){

        firebaseDatabase.getReference().child("AdminNewslist").child(uidLists.get(position)).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context , "db 삭제 완료", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "db 삭제 완료");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context , "db 삭제 실패", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "db 삭제 실패");
            }
        });
    }

}
