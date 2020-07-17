package com.example.membershipapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CouponHistoryAdapter extends RecyclerView.Adapter<CouponHistoryAdapter.CouponHistoryViewHolder> {

    Context context;

    //아이템을 위한 데이터만 ArrayList 형태로 보관
    ArrayList<CouponHistoryDTO> items = new ArrayList<CouponHistoryDTO>();

    OnItemClickListener clickListener;

    public static interface OnItemClickListener{
        public void OnItemClick(RecyclerView.ViewHolder holder, View view, int position);
    }

    public CouponHistoryAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public CouponHistoryAdapter.CouponHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_coupon_history, parent, false);
        CouponHistoryViewHolder couponHistoryViewHolder = new CouponHistoryViewHolder(view);

        return couponHistoryViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CouponHistoryAdapter.CouponHistoryViewHolder holder, int position) {

        final CouponHistoryDTO item = items.get(position);
//        holder.setItem(item);

//        holder.coffeeImg.setImageResource(item.coffeeImgPath);
        holder.couponUseName.setText(item.getCouponUseName());
        holder.couponUseDeadline.setText(item.getCouponUseDeadline());
        holder.couponUseDate.setText(item.getCouponUseDate());

        holder.setOnItemClickListener(clickListener);

//        //삭제버튼 클릭시
//        holder.btnDeleteCoffeeItem.setOnItemClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                //알럿 빌더 생성
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                //빌더에 정보입력
//                builder.setTitle("삭제학인")
//                        .setMessage("삭제하시겠습니까?")
//                        .setCancelable(false)
//                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                //삭제 진행
//                                items.remove(position);
//                                notifyItemRemoved(position);
//                                notifyItemRangeChanged(position, items.size());
//                            }
//                        })
//                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        });
//                //알럿 생성
//                AlertDialog alertDialog = builder.create();
//                alertDialog.show();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(CouponHistoryDTO item){
        items.add(item);
    }

    public CouponHistoryDTO getItem(int position){
        return items.get(position);
    }

    //아이템 클릭 리스너 메소드
    public void setOnItemClickListener(OnItemClickListener clickListener){
        this.clickListener = clickListener;
    }

    public class CouponHistoryViewHolder extends RecyclerView.ViewHolder {

        //아이템을 위한 view를 holder에 담아두기 위한 역할
        ImageView couponUseImg;
        TextView couponUseName, couponUseDeadline, couponUseDate;

        CouponHistoryDTO couponHistoryDTO;

        OnItemClickListener clickListener;  //뷰홀더 안에도 설정 (어댑터와 마찬가지로)

        public CouponHistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            couponUseImg = (ImageView) itemView.findViewById(R.id.couponUseImg);
            couponUseName = (TextView) itemView.findViewById(R.id.couponUseName);
            couponUseDeadline = (TextView) itemView.findViewById(R.id.couponUseDeadline);
            couponUseDate = (TextView) itemView.findViewById(R.id.couponUseDate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(clickListener != null){
                        clickListener.OnItemClick(CouponHistoryViewHolder.this, v, position);
                    }
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
