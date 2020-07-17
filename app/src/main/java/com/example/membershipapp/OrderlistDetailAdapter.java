package com.example.membershipapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderlistDetailAdapter extends RecyclerView.Adapter<OrderlistDetailAdapter.OrderlistViewHolder> {

    Context context;

    //아이템을 위한 데이터만 ArrayList 형태로 보관
    ArrayList<OrderlistDTO> orderlistDTOArrayList = new ArrayList<OrderlistDTO>();
    ArrayList<String> dateKeyList = new ArrayList<>();

    OnItemClickListener clickListener;

    public static interface OnItemClickListener{
        public void OnItemClick(RecyclerView.ViewHolder holder, View view, int position);
    }

    public OrderlistDetailAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public OrderlistDetailAdapter.OrderlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_orderlist_detail, parent, false);
        OrderlistViewHolder orderlistViewHolder = new OrderlistViewHolder(view);

        return orderlistViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderlistDetailAdapter.OrderlistViewHolder holder, int position) {

        final OrderlistDTO item = orderlistDTOArrayList.get(position);
//        holder.setItem(item);

        holder.tvOrderDetailCategory.setText(item.getOrderMenuItemCategory());
        holder.tvOrderDetaiName.setText(item.getOrderMenuItemName());
        holder.tvOrderDetailPrice.setText(item.getOrderMenuItemPrice()+"원");
        holder.tvOrderDetailQuantity.setText(item.getOrderMenuItemQuantity()+"개");

        holder.setOnItemClickListener(clickListener);

    }

    @Override
    public int getItemCount() {
        return orderlistDTOArrayList.size();
    }

    public void addItem(OrderlistDTO item){
        orderlistDTOArrayList.add(item);
    }

    public OrderlistDTO getItem(int position){
        return orderlistDTOArrayList.get(position);
    }

    //아이템 클릭 리스너 메소드
    public void setOnItemClickListener(OnItemClickListener clickListener){
        this.clickListener = clickListener;
    }

    public class OrderlistViewHolder extends RecyclerView.ViewHolder {

        //아이템을 위한 view를 holder에 담아두기 위한 역할
        TextView tvOrderDetailCategory, tvOrderDetaiName, tvOrderDetailPrice, tvOrderDetailQuantity;

        OnItemClickListener clickListener;  //뷰홀더 안에도 설정 (어댑터와 마찬가지로)

        public OrderlistViewHolder(@NonNull View itemView) {
            super(itemView);

            tvOrderDetailCategory = (TextView) itemView.findViewById(R.id.tvOrderDetailCategory);
            tvOrderDetaiName = (TextView) itemView.findViewById(R.id.tvOrderDetaiName);
            tvOrderDetailPrice = (TextView) itemView.findViewById(R.id.tvOrderDetailPrice);
            tvOrderDetailQuantity = (TextView) itemView.findViewById(R.id.tvOrderDetailQuantity);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(clickListener != null){
                        clickListener.OnItemClick(OrderlistViewHolder.this, v, position);
                    }
                }
            });
        }

        public void setOnItemClickListener(OnItemClickListener clickListener){
            this.clickListener = clickListener;
        }
    }
}
