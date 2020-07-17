package com.example.membershipapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        holder.newsNumber.setText(item.getNewsNumber());
        holder.newsTitle.setText(item.getNewsTitle());
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

        OnItemClickListener clickListener;

        public AdminNewslistViewHolder(@NonNull final View itemView) {
            super(itemView);

            newsNumber = (TextView) itemView.findViewById(R.id.newsNumber);
            newsTitle = (TextView) itemView.findViewById(R.id.newsTitle);

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


        }

        //아이템 클릭 리스너 메소드
        public void setOnItemClickListener(OnItemClickListener clickListener){
            this.clickListener = clickListener;
        }
    }

}
