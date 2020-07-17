package com.example.membershipapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
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
import com.google.firestore.v1.StructuredQuery;

import java.util.ArrayList;

public class CartlistAdapter extends RecyclerView.Adapter<CartlistAdapter.CartlistViewHolder> {

    String TAG = "LogActivity";
    Context context;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    //아이템을 위한 데이터만 ArrayList 형태로 보관
    ArrayList<CartlistDTO> menuList = new ArrayList<CartlistDTO>();     //장바구니 리스트
//    ArrayList<CartlistDTO> orderList = new ArrayList<CartlistDTO>();    //주문내역 리스트
    ArrayList<String> uidLists = new ArrayList<>();

    OnItemClickListener clickListener;

    ApplicationClass applicationClass;

    public static interface OnItemClickListener{
        public void OnItemClick(RecyclerView.ViewHolder holder, View view, int position);
    }

    public CartlistAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public CartlistAdapter.CartlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        applicationClass = (ApplicationClass)context.getApplicationContext();

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_cartlist, parent, false);
        CartlistViewHolder cartlistViewHolder = new CartlistViewHolder(view);

        return cartlistViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CartlistAdapter.CartlistViewHolder holder, final int position) {

        final int itemPosition = position;
        final CartlistDTO item = menuList.get(position);
//        holder.setItem(item);

        holder.setOnItemClickListener(clickListener);

        //setImg()
        if(menuList.get(position).cartMenuImgPath.equals("default")){
            //이미지 경로가 없을때 기본 이미지 보여주기
            holder.cartMenuImg.setImageResource(R.drawable.icon_coffee01);
        }else{
            //이미지 경로가 있을때
            Glide.with(holder.itemView.getContext())
                    .load(menuList.get(position).cartMenuImgPath)
                    .into(holder.cartMenuImg);
        }
        //setText()
        holder.cartMenuName.setText(item.getCartMenuName());
        holder.cartMenuPrice.setText(item.getCartMenuPrice()+"원");
        holder.cartMenuQuantity.setText(item.getCartMenuQuantity()+"");

        //삭제버튼 클릭시
        holder.btnCartItemDelete.setOnClickListener(new View.OnClickListener() {
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
        return menuList.size();
    }

    public void addItem(CartlistDTO item){
        menuList.add(item);
    }

    public CartlistDTO getItem(int position){
        return menuList.get(position);
    }

    //아이템 클릭 리스너 메소드
    public void setOnItemClickListener(OnItemClickListener clickListener){
        this.clickListener = clickListener;
    }

    public class CartlistViewHolder extends RecyclerView.ViewHolder {

        //아이템을 위한 view를 holder에 담아두기 위한 역할
        ImageView cartMenuImg;
        TextView cartMenuName, cartMenuPrice, cartMenuQuantity;
        ImageButton btnCartItemEdit, btnCartItemDelete;

        OnItemClickListener clickListener;  //뷰홀더 안에도 설정 (어댑터와 마찬가지로)

        public CartlistViewHolder(@NonNull View itemView) {
            super(itemView);

            cartMenuImg = (ImageView) itemView.findViewById(R.id.cartMenuImg);
            cartMenuName = (TextView) itemView.findViewById(R.id.cartMenuName);
            cartMenuPrice = (TextView) itemView.findViewById(R.id.cartMenuPrice);
            cartMenuQuantity = (TextView) itemView.findViewById(R.id.cartMenuQuantity);
            btnCartItemEdit = (ImageButton) itemView.findViewById(R.id.btnCartItemEdit);
            btnCartItemDelete = (ImageButton) itemView.findViewById(R.id.btnCartItemDelete);

            //각각 아이템 뷰에 올릴 리스너 이벤트 설정 -> 각각의 아이템 뷰가 클릭됬을 때
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //위의 어댑터에 등록한 리스너로 넘겨준다.
                    int position = getAdapterPosition();    //클릭한 아이템의 위치를 알수있다

                    if(clickListener != null){
                        clickListener.OnItemClick(CartlistAdapter.CartlistViewHolder.this, v, position);
                    }

                }
            });

            //수정 버튼 클릭시
            btnCartItemEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition(); //클릭한 아이템의 위치를 알수있다
                    Log.d("LogActivity", "수정버튼 클릭 위치 : "+ position);

//                    //menulist 의 uidkey 값 intent에 넣어서 전달
                    String menulistNameKey = uidLists.get(position);
                    Log.d(TAG, "수정버튼 클릭 uidKey :"+ menulistNameKey);
//
                    Context context = v.getContext();
                    Intent intent = new Intent(v.getContext(), CartlistItemEditActivity.class);
                    intent.putExtra("menulistNameKey", menulistNameKey);
                    intent.putExtra("editCartItemQuantity", menuList.get(position).getCartMenuQuantity());
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

        firebaseDatabase.getReference().child("cartlist")
                .child(applicationClass.EncodeString(applicationClass.loginEmail))
                .child(menuList.get(position).getCartMenuName())
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context , "장바구니 삭제 완료", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "장바구니 삭제 완료");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context , "장바구니 삭제 실패", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "장바구니 삭제 실패");
            }
        });
    }

}
