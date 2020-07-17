package com.example.membershipapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CouponlistAdapter extends RecyclerView.Adapter<CouponlistAdapter.CouponlistViewHolder> {

    Context context;
    String TAG = "ScannerActivity";

    //아이템을 위한 데이터만 ArrayList 형태로 보관
    ArrayList<CouponlistDTO> couponLists = new ArrayList<CouponlistDTO>();
    ArrayList<String> uidLists = new ArrayList<>();

//    String accessPath;  //접근경로 = cartlistPath, couponlistPath
    String loginEmail, loginName;
    OnItemClickListener clickListener;

    public static interface OnItemClickListener{
        public void OnItemClick(RecyclerView.ViewHolder holder, View view, int position);
    }

    public CouponlistAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public CouponlistAdapter.CouponlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_available_coupon, parent, false);
        CouponlistViewHolder couponlistViewHolder = new CouponlistViewHolder(view);

        return couponlistViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CouponlistAdapter.CouponlistViewHolder holder, int position) {

        final CouponlistDTO item = couponLists.get(position);
//        holder.setItem(item);

//        holder.coffeeImg.setImageResource(item.coffeeImgPath);
        holder.couponName.setText(item.getCouponName());
        holder.couponDeadline.setText(item.getCouponDeadline()+" 까지");

        holder.setOnItemClickListener(clickListener);

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent barCodeIntent = new Intent(context, CouponBarcodeActivity.class);
//                context.startActivity(barCodeIntent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return couponLists.size();
    }

    public void addItem(CouponlistDTO item){
        couponLists.add(item);
    }

    public CouponlistDTO getItem(int position){
        return couponLists.get(position);
    }

    //아이템 클릭 리스너 메소드
    public void setOnItemClickListener(OnItemClickListener clickListener){
        this.clickListener = clickListener;
    }

    public class CouponlistViewHolder extends RecyclerView.ViewHolder {

        //아이템을 위한 view를 holder에 담아두기 위한 역할
        ImageView couponImg;
        TextView couponName, couponDeadline;

        CouponlistDTO couponlistDTO;

        OnItemClickListener clickListener;  //뷰홀더 안에도 설정 (어댑터와 마찬가지로)

        public CouponlistViewHolder(@NonNull View itemView) {
            super(itemView);

            couponImg = (ImageView) itemView.findViewById(R.id.couponImg);
            couponName = (TextView) itemView.findViewById(R.id.couponName);
            couponDeadline = (TextView) itemView.findViewById(R.id.couponDeadline);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();

//                    if(clickListener != null){
//                        clickListener.OnItemClick(CouponlistViewHolder.this, v, position);

                    //접근경로 accessPath
//                    if(accessPath.contentEquals("cartlistPath")){
                        //액티비티를 띄운다
                        Intent detailIntent = new Intent(v.getContext(), CouponBarcodeActivity.class);
                        detailIntent.putExtra("userEmail", loginEmail);
                        detailIntent.putExtra("userName", loginName);
                        detailIntent.putExtra("couponName", couponLists.get(position).getCouponName());
                        detailIntent.putExtra("couponDeadline",couponLists.get(position).getCouponDeadline());
                        detailIntent.putExtra("couponMakeDate", couponLists.get(position).getCouponMakeDate());

                        Log.d(TAG, "바코드 userEmail : "+loginEmail);
                        Log.d(TAG, "바코드 couponName : "+couponLists.get(position).getCouponName());
                        Log.d(TAG, "바코드 couponDeadline : "+couponLists.get(position).getCouponDeadline());
                        Log.d(TAG, "바코드 couponMakeDate : "+couponLists.get(position).getCouponMakeDate());

                        context.startActivity(detailIntent);
//                        ((Activity)context).finish();
//                    }
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
