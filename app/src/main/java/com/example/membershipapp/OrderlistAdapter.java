package com.example.membershipapp;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class OrderlistAdapter extends RecyclerView.Adapter<OrderlistAdapter.OrderlistViewHolder> {

    Context context;

    //아이템을 위한 데이터만 ArrayList 형태로 보관
    ArrayList<OrderlistDTO> orderlistDTOArrayList = new ArrayList<OrderlistDTO>();
    ArrayList<OrderInfoDTO> orderInfoList = new ArrayList<OrderInfoDTO>();
    ArrayList<String> dateKeyList = new ArrayList<>();

    OnItemClickListener clickListener;

    public static interface OnItemClickListener{
        public void OnItemClick(RecyclerView.ViewHolder holder, View view, int position);
    }

    public OrderlistAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public OrderlistAdapter.OrderlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_orderlist, parent, false);
        OrderlistViewHolder orderlistViewHolder = new OrderlistViewHolder(view);

        return orderlistViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderlistAdapter.OrderlistViewHolder holder, int position) {

        final OrderInfoDTO OrderInfoItem = orderInfoList.get(position);
        final OrderlistDTO OrderlistItem = orderlistDTOArrayList.get(position);
//        holder.setItem(item);

//        holder.tvOrderDate.setText(item.getOrderDate());
//        holder.tvOrderContent.setText(item.getOrderMenuItemName()+"/"+item.getOrderMenuItemPrice()+"원/"+item.getOrderMenuItemQuantity()+"개");
//        holder.tvOrderContent.setText(item.getOrderMenuItemName() +"등...");

//        for(int i=0; i<orderInfoList.size(); i++){
//            addPrice = orderInfoList.get(i).getOrderMenuItemPrice();
//            orderTotalPrice = orderTotalPrice + addPrice;
//        }

        holder.tvOrderDate.setText(OrderInfoItem.getOrderDate());
        holder.tvOrderTotalPrice.setText(OrderInfoItem.getTotalOrderPrice()+"원");

//        String firstOrderMenuName = item.getOrderlistDTO().getOrderMenuItemName();
        String firstOrderMenuName = OrderlistItem.getOrderMenuItemName();
        Log.d("LogActivity", "이름 확인(adapter) : "+firstOrderMenuName);

        int orderNum = OrderInfoItem.getTotalOrderQuantity();
        if(orderNum == 1){
            holder.tvOrderContent.setText(firstOrderMenuName+" "+orderNum+"개 주문");
        }else{
            holder.tvOrderContent.setText(firstOrderMenuName+" 외 "+(orderNum-1)+"개 주문");
        }

        //주문 상태값에 따른 프로그레스바 수치 처리
        if(OrderInfoItem.getOrderState().matches("대기")){
            holder.progressBarOrderState.setProgress(5);
            int pgbColor = context.getResources().getColor(R.color.colorSub);
            holder.progressBarOrderState.getProgressDrawable().setColorFilter(pgbColor, PorterDuff.Mode.MULTIPLY);
            holder.tvOrderState.setText("메뉴를 확인하고 있습니다.");
            holder.tvOrderState.setTextColor(context.getResources().getColor(R.color.colorSub));
        }else if(OrderInfoItem.getOrderState().matches("제조")){
            //프로그래스바 애니메이션
            ObjectAnimator progressAnimator = ObjectAnimator.ofInt(holder.progressBarOrderState, "progress", 5, 50);
            progressAnimator.setDuration(1000);
            progressAnimator.start();
            int pgbColor = context.getResources().getColor(R.color.colorSub);
            holder.progressBarOrderState.getProgressDrawable().setColorFilter(pgbColor, PorterDuff.Mode.MULTIPLY);
            holder.tvOrderState.setText("메뉴를 준비중입니다.");
            holder.tvOrderState.setTextColor(context.getResources().getColor(R.color.colorSub));
        }else if(OrderInfoItem.getOrderState().matches("완료")){
//            holder.progressBarOrderState.setIndeterminate(false);
            //프로그래스바 애니메이션
            ObjectAnimator progressAnimator = ObjectAnimator.ofInt(holder.progressBarOrderState, "progress", 50, 100);
            progressAnimator.setDuration(1000);
            progressAnimator.start();
//            holder.progressBarOrderState.setProgress(100);
            //프로그래스바 색상 변경
            int pgbColor = context.getResources().getColor(R.color.colorSub);
            holder.progressBarOrderState.getProgressDrawable().setColorFilter(pgbColor, PorterDuff.Mode.MULTIPLY);
            holder.tvOrderState.setText("픽업대에서 메뉴를 픽업해주세요.");
            holder.tvOrderState.setTextColor(context.getResources().getColor(R.color.colorSub));
        }else if(OrderInfoItem.getOrderState().matches("주문거부")){  //주문거부 = 관리자가 재고소진시 메뉴 거부
            holder.progressBarOrderState.setProgress(0);
            holder.tvOrderState.setText("가게 사정으로 인해 주문이 취소되었습니다.");
            holder.tvOrderState.setTextColor(context.getResources().getColor(R.color.colorRed));
        }else if(OrderInfoItem.getOrderState().matches("주문취소")){  //주문취소 = 사용자가 제조전 메뉴 취소
            holder.progressBarOrderState.setProgress(0);
            holder.tvOrderState.setText("주문을 취소하였습니다.");
            holder.tvOrderState.setTextColor(context.getResources().getColor(R.color.colorRed));
        }else if(OrderInfoItem.getOrderState().matches("결제취소")){  //결제취소 = 사용자가 주문완료 후 결제 취소
//            holder.progressBarOrderState.setIndeterminate(false);
            //프로그래스바 애니메이션
            ObjectAnimator progressAnimator = ObjectAnimator.ofInt(holder.progressBarOrderState, "progress", 0, 100);
            progressAnimator.setDuration(1000);
            progressAnimator.start();
//            holder.progressBarOrderState.setProgress(100);
            holder.tvOrderState.setText("결제가 취소되었습니다.");
            int pgbColor = context.getResources().getColor(R.color.colorRed);
            holder.progressBarOrderState.getProgressDrawable().setColorFilter(pgbColor, PorterDuff.Mode.MULTIPLY);
            holder.tvOrderState.setTextColor(context.getResources().getColor(R.color.colorRed));
        }

        holder.setOnItemClickListener(clickListener);

    }

    @Override
    public int getItemCount() {
        return orderInfoList.size();
    }

    public void addItem(OrderInfoDTO item){
        orderInfoList.add(item);
    }

    public OrderInfoDTO getItem(int position){
        return orderInfoList.get(position);
    }

    //아이템 클릭 리스너 메소드
    public void setOnItemClickListener(OnItemClickListener clickListener){
        this.clickListener = clickListener;
    }

    public class OrderlistViewHolder extends RecyclerView.ViewHolder {

        //아이템을 위한 view를 holder에 담아두기 위한 역할
        TextView tvOrderDate, tvOrderContent, tvOrderTotalPrice, tvOrderState;
        Button btnOrderReviewGo, btnOrderDetailGo;
        ProgressBar progressBarOrderState;

//        OrderlistDTO orderlistDTO;
        OrderInfoDTO orderInfoDTO;

        OnItemClickListener clickListener;  //뷰홀더 안에도 설정 (어댑터와 마찬가지로)

        public OrderlistViewHolder(@NonNull View itemView) {
            super(itemView);

            tvOrderDate = (TextView) itemView.findViewById(R.id.tvOrderDate);
            tvOrderContent = (TextView) itemView.findViewById(R.id.tvOrderContent);
            tvOrderTotalPrice = (TextView) itemView.findViewById(R.id.tvOrderTotalPrice);
            tvOrderState = (TextView) itemView.findViewById(R.id.tvOrderState);
//            btnOrderReviewGo = (Button) itemView.findViewById(R.id.btnOrderReviewGo);
            btnOrderDetailGo = (Button) itemView.findViewById(R.id.btnOrderDetailGo);
            progressBarOrderState = (ProgressBar) itemView.findViewById(R.id.progressBarOrderState);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(clickListener != null){
                        clickListener.OnItemClick(OrderlistViewHolder.this, v, position);
                    }
                }
            });

            btnOrderDetailGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition(); //클릭한 아이템의 위치를 알수있다

                    //orderlist 의 dateKey 값 intent에 넣어서 전달
//                    String datelistKey = dateKeyList.get(position);
//                    Log.d(TAG, "수정버튼 클릭 dateKey :"+ datelistKey);

                    Context context = v.getContext();

                    Intent intent = new Intent(v.getContext(), OrderlistDetailActivity.class);
                    intent.putExtra("clickPosition", position);
                    Log.d("LogActivity", "adapter -> 클릭 위치 : : " + position);
                    intent.putExtra("orderDate" , orderInfoList.get(position).getOrderDate());
                    intent.putExtra("orderEmail" , orderInfoList.get(position).getOrderEmail());
                    intent.putExtra("orderTotalPrice", orderInfoList.get(position).getTotalOrderPrice());
                    intent.putExtra("orderState", orderInfoList.get(position).getOrderState());
                    Log.d("LogActivity", "adapter -> detail 보내는 state 데이터 : " + orderInfoList.get(position).getOrderState());
                    Log.d("LogActivity", "adapter -> detail 보내는 date 데이터 : " + orderInfoList.get(position).getOrderDate());
                    Log.d("LogActivity", "adapter -> detail 보내는 price 데이터 : " + orderInfoList.get(position).getTotalOrderPrice());

                    context.startActivity(intent);
                    Log.d("LogActivity", "adapter -> detail화면으로 보냄");

                }
            });

        }

//        //데이터 설정을 위한 메서드
//        public void setItem(CoffeelistDTO coffeelistDTO){
////            coffeeImg
//            coffeeName.setText(coffeelistDTO.getCoffeeName());
//            coffeePrice.setText(coffeelistDTO.getCoffeePrice()+"원");
//        }

        public void setOnItemClickListener(OnItemClickListener clickListener){
            this.clickListener = clickListener;
        }

    }
}
