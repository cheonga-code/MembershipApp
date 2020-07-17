package com.example.membershipapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdminOrderlistAdapter extends RecyclerView.Adapter<AdminOrderlistAdapter.OrderlistViewHolder> {

    Context context;
    String activityName;

    //아이템을 위한 데이터만 ArrayList 형태로 보관
    ArrayList<OrderlistDTO> orderlistDTOArrayList = new ArrayList<OrderlistDTO>();
    ArrayList<OrderInfoDTO> orderInfoDTOArrayList = new ArrayList<OrderInfoDTO>();
    ArrayList<String> timeKeyList = new ArrayList<>();      //주문한 날짜 키 리스트
    ArrayList<String> emailKeyList = new ArrayList<>();     //주문한 유저 이메일 키 리스트

    OnItemClickListener clickListener;

    public static interface OnItemClickListener{
        public void OnItemClick(RecyclerView.ViewHolder holder, View view, int position);
    }

    public AdminOrderlistAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public AdminOrderlistAdapter.OrderlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_admin_orderlist_wait, parent, false);
        OrderlistViewHolder orderlistViewHolder = new OrderlistViewHolder(view);

        return orderlistViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdminOrderlistAdapter.OrderlistViewHolder holder, int position) {

        final OrderInfoDTO orderInfoItem = orderInfoDTOArrayList.get(position);

        holder.tvAdminOrderId.setText(orderInfoItem.getOrderName());
        holder.tvAdminOrderTime.setText(orderInfoItem.getOrderTime());
        holder.tvAdminOrderContent.setText("주문수량 "+orderInfoItem.getTotalOrderQuantity()+"개");
        holder.tvAdminOrderTotalPrice.setText("결제완료 "+orderInfoItem.getTotalOrderPrice()+"원");
        holder.tvAdminOrderState.setText(orderInfoItem.getOrderState());

        holder.setTextStateColor();

        holder.setOnItemClickListener(clickListener);

    }

    @Override
    public int getItemCount() {
        return orderInfoDTOArrayList.size();
    }

    public void addItem(OrderInfoDTO item){
        orderInfoDTOArrayList.add(item);
    }

    public OrderInfoDTO getItem(int position){
        return orderInfoDTOArrayList.get(position);
    }

    //아이템 클릭 리스너 메소드
    public void setOnItemClickListener(OnItemClickListener clickListener){
        this.clickListener = clickListener;
    }

    public class OrderlistViewHolder extends RecyclerView.ViewHolder {

        //아이템을 위한 view를 holder에 담아두기 위한 역할
        TextView tvAdminOrderId, tvAdminOrderTime, tvAdminOrderContent, tvAdminOrderTotalPrice, tvAdminOrderState;

        OnItemClickListener clickListener;  //뷰홀더 안에도 설정 (어댑터와 마찬가지로)

        public OrderlistViewHolder(@NonNull View itemView) {
            super(itemView);

            Log.d("LogActivity", "//////viewholder에 도착했음//////");

            tvAdminOrderId = (TextView) itemView.findViewById(R.id.tvAdminOrderId);
            tvAdminOrderTime = (TextView) itemView.findViewById(R.id.tvAdminOrderTime);
            tvAdminOrderContent = (TextView) itemView.findViewById(R.id.tvAdminOrderContent);
            tvAdminOrderTotalPrice = (TextView) itemView.findViewById(R.id.tvAdminOrderTotalPrice);
            tvAdminOrderState = (TextView) itemView.findViewById(R.id.tvAdminOrderState);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

//                    if(clickListener != null){
//                        clickListener.OnItemClick(OrderlistViewHolder.this, v, position);
//                        Toast.makeText(context, "null 아님", Toast.LENGTH_SHORT).show();
//                    }else{
//                        Toast.makeText(context, "null ", Toast.LENGTH_SHORT).show();
//                    }

                    Intent intent = new Intent(v.getContext(), AdminOrderlistDetailActivity.class);
                    intent.putExtra("clickPosition", position);
                    Log.d("LogActivity", "adapter -> 클릭 위치 : : " + position);

                    intent.putExtra("activityName", activityName);
                    intent.putExtra("DateKey" , orderInfoDTOArrayList.get(position).getOrderDate());
                    intent.putExtra("emailKey", orderInfoDTOArrayList.get(position).getOrderEmail());
                    intent.putExtra("orderId" , orderInfoDTOArrayList.get(position).getOrderName());
                    intent.putExtra("orderDate" , orderInfoDTOArrayList.get(position).getOrderDate());
                    intent.putExtra("orderTotalPrice", orderInfoDTOArrayList.get(position).getTotalOrderPrice());
                    intent.putExtra("orderBeverageQuantity", orderInfoDTOArrayList.get(position).getTotalOrderBeverageQuantity());

                    Log.d("LogActivity", "adapter -> detail 보내는 activityName 데이터 : " + activityName);
                    Log.d("LogActivity", "adapter -> detail 보내는 DateKey 데이터 : " + orderInfoDTOArrayList.get(position).getOrderDate());
                    Log.d("LogActivity", "adapter -> detail 보내는 emailKey 데이터 : " + orderInfoDTOArrayList.get(position).getOrderEmail());

                    context.startActivity(intent);
                    ((Activity)context).finish();

                    Log.d("LogActivity", "adapter -> detail화면으로 보냄");
                }
            });

        }

        public void setOnItemClickListener(OnItemClickListener clickListener){
            this.clickListener = clickListener;
        }

        //상태 textView 컬러 변경하기
        public void setTextStateColor(){
            if(activityName.matches("orderWait")){
                tvAdminOrderState.setTextColor(context.getResources().getColor(R.color.colorMain3));
                tvAdminOrderState.setBackground(context.getDrawable(R.drawable.round_border_wait));
            }else if(activityName.matches("orderProcess")){
                tvAdminOrderState.setTextColor(context.getResources().getColor(R.color.colorYellow));
                tvAdminOrderState.setBackground(context.getDrawable(R.drawable.round_border_process));
            }else if(activityName.matches("orderComplete")){
                tvAdminOrderState.setTextColor(context.getResources().getColor(R.color.colorSub));
                tvAdminOrderState.setBackground(context.getDrawable(R.drawable.round_border_complete));
            }else if(activityName.matches("orderCancel")){
                tvAdminOrderState.setTextColor(context.getResources().getColor(R.color.colorSubOrange));
                tvAdminOrderState.setBackground(context.getDrawable(R.drawable.round_border_cancel));
            }

        }
    }

}
