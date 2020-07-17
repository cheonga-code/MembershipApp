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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.EventListener;

public class AdminMenulistAdapter extends RecyclerView.Adapter<AdminMenulistAdapter.AdminMenulistViewHolder> {

    String TAG = "LogActivity";
    Context context;
    OnItemClickListener clickListener;

    ArrayList<AdminMenulistDTO> menuList = new ArrayList<AdminMenulistDTO>();
    ArrayList<String> uidLists = new ArrayList<>();

//    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    public  AdminMenulistAdapter(Context context) {
        this.context = context;
    }

    public static interface OnItemClickListener{
      public void OnItemClick(RecyclerView.ViewHolder holder, View view, int position);
    }

    @NonNull
    @Override
    public AdminMenulistAdapter.AdminMenulistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_admin_menulist, parent, false);
        AdminMenulistViewHolder adminMenulistViewHolder = new AdminMenulistViewHolder(view);

        return adminMenulistViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdminMenulistAdapter.AdminMenulistViewHolder holder, final int position) {

        final int itemPosition = position;
        final AdminMenulistDTO item = menuList.get(position);

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

        //삭제버튼 클릭시
        holder.btnAddMenuDelete.setOnClickListener(new View.OnClickListener() {
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
                                deleteStorage(position);
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

    public class AdminMenulistViewHolder extends RecyclerView.ViewHolder {

        ImageView menuImg;
        TextView menuName, menuPrice;
        ImageButton btnAddMenuDelete, btnAddMenuEdit;

        OnItemClickListener clickListener;

        public AdminMenulistViewHolder(@NonNull final View itemView) {
            super(itemView);

            menuImg = (ImageView) itemView.findViewById(R.id.menuImg);
            menuName = (TextView) itemView.findViewById(R.id.menuName);
            menuPrice = (TextView) itemView.findViewById(R.id.menuPrice);
            btnAddMenuEdit = (ImageButton) itemView.findViewById(R.id.btnAddMenuEdit);
            btnAddMenuDelete = (ImageButton) itemView.findViewById(R.id.btnAddMenuDelete);

            //각각 아이템 뷰에 올릴 리스너 이벤트 설정 -> 각각의 아이템 뷰가 클릭됬을 때
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //위의 어댑터에 등록한 리스너로 넘겨준다.
                    int position = getAdapterPosition();    //클릭한 아이템의 위치를 알수있다

                    if(clickListener != null){
                        clickListener.OnItemClick(AdminMenulistViewHolder.this, v, position);
                    }

                }
            });

            //수정 버튼 클릭시
            btnAddMenuEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition(); //클릭한 아이템의 위치를 알수있다
                    Log.d("LogActivity", "수정버튼 클릭 위치 : "+ position);

                    //menulist 의 uidkey 값 intent에 넣어서 전달
                    String menulistUidKey = uidLists.get(position);
                    Log.d(TAG, "수정버튼 클릭 uidKey :"+ menulistUidKey);

                    Context context = v.getContext();

                    Intent intent = new Intent(v.getContext(), AdminMenuEditActivity.class);
                    intent.putExtra("menulistUidKey", menulistUidKey);
                    intent.putExtra("editMenuCategory" , menuList.get(position).getMenuCategory());
                    intent.putExtra("editMenuName", menuList.get(position).getMenuName());
                    intent.putExtra("editMenuPrice", menuList.get(position).getMenuPrice());
                    Log.d("LogActivity", "adapter -> edit 보내는 uidKey 데이터 : " + menulistUidKey);
                    Log.d("LogActivity", "adapter -> edit 보내는 category 데이터 : " + menuList.get(position).getMenuCategory());
                    Log.d("LogActivity", "adapter -> edit 보내는 name 데이터 : " + menuList.get(position).getMenuName());
                    Log.d("LogActivity", "adapter -> edit 보내는 price 데이터: " + menuList.get(position).getMenuPrice());

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

    //firebase storage 에서 삭제
    private void deleteStorage(final int position){

        //기본 이미지일 때
        if(menuList.get(position).menuImgName.equals("default")){
            Log.d(TAG, "++ 삭제할 이미지가 없습니다 ++");
            //db 삭제
            databaseDelete(position);

        }else{

            //경로 이미지일 때
            Log.d(TAG, "++ 삭제할 이미지가 있습니다 ++");
            //storage 이미지 삭제
            firebaseStorage.getReference().child("images").child(menuList.get(position).menuImgName).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(context , "img 삭제 완료", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "img 삭제 완료 : "+menuList.get(position).menuImgName);
                    //db 삭제
                    databaseDelete(position);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "img 삭제 실패 : "+menuList.get(position).menuImgName, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "img 삭제 실패");
                }
            });
        }

    }

    //database realtime 에서 삭제
    public void databaseDelete(int position){

        firebaseDatabase.getReference().child("menulist").child(uidLists.get(position)).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
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

//    //database 삭제 메서드 (firestore 에서)
//    public void databaseDelete(String menuName, final int position){
//
//        Log.d("LogActivity", "삭제하려는 메뉴이름" + menuName);
//        Log.d("LogActivity", "삭제하려는 메뉴포지션" + position);
//        firebaseFirestore.collection("menu")
//                .document(menuName)
//                .delete()
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        //삭제 진행
//                        menuList.remove(position);
//                        notifyItemRemoved(position);
//                        notifyItemRangeChanged(position, menuList.size());
//                        Log.d("LogActivity", "firestore 데이터 삭제 완료");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d("LogActivity", "firestore 데이터 삭제 실패", e);
//                    }
//                });
//
//    }

}
