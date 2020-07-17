package com.example.membershipapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
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

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CoffeelistAdapter extends RecyclerView.Adapter<CoffeelistAdapter.CoffeelistViewHolder> implements Filterable {

    String TAG = "LogActivity";
    Context context;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    //아이템을 위한 데이터만 ArrayList 형태로 보관
    ArrayList<AdminMenulistDTO> menuList = new ArrayList<AdminMenulistDTO>();
    ArrayList<String> uidLists = new ArrayList<>();
    //검색된 리스트
    ArrayList<AdminMenulistDTO> filteredList = new ArrayList<>();

    OnItemClickListener clickListener;

    public long coffeeTotalPriceLong = 0;
    public long finalPriceLong = 0;
    long coffeePriceLong;
    int coffeeQuantityInt;
    public String finalPriceStr = "";

    public static interface OnItemClickListener{
        public void OnItemClick(RecyclerView.ViewHolder holder, View view, int position);
    }

    public  CoffeelistAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public CoffeelistAdapter.CoffeelistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_coffeelist, parent, false);
        CoffeelistViewHolder coffeelistViewHolder = new CoffeelistViewHolder(view);

        return coffeelistViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CoffeelistAdapter.CoffeelistViewHolder holder, final int position) {

        final int itemPosition = position;
        final AdminMenulistDTO item = menuList.get(position);
//        holder.setItem(item);

        holder.setOnItemClickListener(clickListener);

        //setImg()
        if(menuList.get(position).menuImgPath.equals("default")){
            //이미지 경로가 없을때 기본 이미지 보여주기
            holder.menuImg.setImageResource(R.drawable.icon_coffee01);
        }else{
            //이미지 경로가 있을때
            Glide.with(holder.itemView.getContext())
                    .load(menuList.get(position).menuImgPath)
                    .into(holder.menuImg);
        }
        //setText()
        holder.menuName.setText(item.getMenuName());
        holder.menuPrice.setText(item.getMenuPrice()+"원");

    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    public void addItem(AdminMenulistDTO item){
        menuList.add(item);
    }

    public AdminMenulistDTO getItem(int position){
        return menuList.get(position);
    }

    //아이템 클릭 리스너 메소드
    public void setOnItemClickListener(OnItemClickListener clickListener){
        this.clickListener = clickListener;
    }


    public class CoffeelistViewHolder extends RecyclerView.ViewHolder {

        //아이템을 위한 view를 holder에 담아두기 위한 역할
        ImageView menuImg;
        TextView menuName, menuPrice, menuQuantity;

        OnItemClickListener clickListener;  //뷰홀더 안에도 설정 (어댑터와 마찬가지로)

        public CoffeelistViewHolder(@NonNull View itemView) {
            super(itemView);

            menuImg = (ImageView) itemView.findViewById(R.id.coffeeImg);
            menuName = (TextView) itemView.findViewById(R.id.coffeeName);
            menuPrice = (TextView) itemView.findViewById(R.id.coffeePrice);

            //각각 아이템 뷰에 올릴 리스너 이벤트 설정 -> 각각의 아이템 뷰가 클릭됬을 때
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //위의 어댑터에 등록한 리스너로 넘겨준다.
                    int position = getAdapterPosition();    //클릭한 아이템의 위치를 알수있다

                    if(clickListener != null){
                        clickListener.OnItemClick(CoffeelistAdapter.CoffeelistViewHolder.this, v, position);
                    }

                }
            });
        }

        //아이템 클릭 리스너 메소드
        public void setOnItemClickListener(OnItemClickListener clickListener){
            this.clickListener = clickListener;
        }
    }

    @Override
    public Filter getFilter() {
        return null;
//        return new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                String charString = constraint.toString();
//                if(charString.isEmpty()) {
//                    filteredList = unFilteredlist;
//                } else {
//                    ArrayList<String> filteringList = new ArrayList<>();
//                    for(String name : unFilteredlist) {
//                        if(name.toLowerCase().contains(charString.toLowerCase())) {
//                            filteringList.add(name);
//                        }
//                    }
//                    filteredList = filteringList;
//                }
//                FilterResults filterResults = new FilterResults();
//                filterResults.values = filteredList;
//                return filterResults;
//            }
//
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//                filteredList = (ArrayList<String>)results.values;
//                notifyDataSetChanged();
//            }
//        };
    }

}
