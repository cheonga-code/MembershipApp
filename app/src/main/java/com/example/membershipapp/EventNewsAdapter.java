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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class EventNewsAdapter extends RecyclerView.Adapter<EventNewsAdapter.EventNewsViewHolder> {

    Context context;
    OnItemClickListener clickListener;

    ArrayList<AdminNewslistDTO> newslistDTOArrayList = new ArrayList<AdminNewslistDTO>();
    ArrayList<String> uidLists = new ArrayList<>();

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    public EventNewsAdapter(Context context) {
        this.context = context;
    }

    public static interface OnItemClickListener{
        public void OnItemClick(RecyclerView.ViewHolder holder, View view, int position);
    }

    @NonNull
    @Override
    public EventNewsAdapter.EventNewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_event_newslist, parent, false);
        EventNewsAdapter.EventNewsViewHolder eventNewsViewHolder = new EventNewsAdapter.EventNewsViewHolder(view);

        return eventNewsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventNewsAdapter.EventNewsViewHolder holder, final int position) {

        final int itemPosition = position;
        final AdminNewslistDTO item = newslistDTOArrayList.get(position);

        holder.setOnItemClickListener(clickListener);

        holder.newsEventNumber.setText(Integer.toString(itemPosition+1));
        holder.newsEventTitle.setText(item.getNewsTitle());
    }

    @Override
    public int getItemCount() {
        return newslistDTOArrayList.size();
    }

    public void addItem(AdminNewslistDTO item){
        newslistDTOArrayList.add(item);
    }

    public AdminNewslistDTO getItem(int position){
        return newslistDTOArrayList.get(position);
    }

    //아이템 클릭 리스너 메소드
    public void setOnItemClickListener(EventNewsAdapter.OnItemClickListener clickListener){
        this.clickListener = clickListener;
    }

    public class EventNewsViewHolder extends RecyclerView.ViewHolder {

        TextView newsEventNumber, newsEventTitle;

        OnItemClickListener clickListener;

        public EventNewsViewHolder(@NonNull final View itemView) {
            super(itemView);

            newsEventNumber = (TextView) itemView.findViewById(R.id.newsEventNumber);
            newsEventTitle = (TextView) itemView.findViewById(R.id.newsEventTitle);

            //각각 아이템 뷰에 올릴 리스너 이벤트 설정 -> 각각의 아이템 뷰가 클릭됬을 때
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //위의 어댑터에 등록한 리스너로 넘겨준다.
                    int position = getAdapterPosition();    //클릭한 아이템의 위치를 알수있다

                    if(clickListener != null){
                        clickListener.OnItemClick(EventNewsAdapter.EventNewsViewHolder.this, v, position);
                    }

                }
            });


        }

        //아이템 클릭 리스너 메소드
        public void setOnItemClickListener(EventNewsAdapter.OnItemClickListener clickListener){
            this.clickListener = clickListener;
        }
    }



}


