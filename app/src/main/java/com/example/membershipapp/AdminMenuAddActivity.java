package com.example.membershipapp;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class AdminMenuAddActivity extends LogActivity {

    ImageView imgAddMenu;
    EditText etAddMenuName, etAddMenuPrice;
    Button btnAddMenuOK;
    RadioGroup radioGroupAddMenu;
    RadioButton radioBtnBeverage, radioBtnDessert;

    String menuNameStr, menuPriceStr;
    int menuPriceInt;

    String currentPhotoPath;
    private final int CAMERA_CODE = 3001;
    private final int GALLERY_CODE = 3002;
    String imgPath;

    Uri imgUri, photoURI, providerURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu_add);

        imgAddMenu = (ImageView)findViewById(R.id.imgAddMenu);
        etAddMenuName = (EditText)findViewById(R.id.etAddMenuName);
        etAddMenuPrice = (EditText)findViewById(R.id.etAddMenuPrice);
        btnAddMenuOK = (Button)findViewById(R.id.btnAddMenuOK);
        radioGroupAddMenu = (RadioGroup)findViewById(R.id.radioGroupAddMenu);
        radioBtnBeverage = (RadioButton)findViewById(R.id.radioBtnBeverage);
        radioBtnDessert = (RadioButton)findViewById(R.id.radioBtnDessert);

//        radioGroupAddMenu.setOnCheckedChangeListener(radioGroupButtonChangeListener);

        //권한요청
        checkSelfPermission();

        //메뉴 추가 버튼 클릭했을 때 처리
        btnAddMenuOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //editText 입력한 값을 받아와서 -> string 형변환
                menuNameStr = etAddMenuName.getText().toString();
                menuPriceStr = etAddMenuPrice.getText().toString();
                menuPriceInt = Integer.parseInt(menuPriceStr) ;

                //editText 공백인지 검사하는 코드
                if(menuNameStr.length() == 0 || menuPriceStr.length() == 0){
                    Toast.makeText(AdminMenuAddActivity.this, "공백을 채워주세요.", Toast.LENGTH_SHORT).show();
                }else{

                    //파이어베이스에 데이터 저장
                    saveData();

                    //이전화면으로 되돌아가기
                    Intent intent = getIntent();
//                    //firestore에 입력한 데이터가 저장됨
//                    createDate();
                    setResult(RESULT_OK, intent);
                    finish();
                }

            }
        });

        //이미지 버튼 클릭시
        imgAddMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //다이얼로그 띄우기
                makeDialog();
            }
        });

    }

//    //라디오 그룹 클릭 리스너
//    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
//        @Override public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
//            if(checkedId == R.id.radioBtnBeverage){
//                Toast.makeText(AdminMenuAddActivity.this, "음료 선택", Toast.LENGTH_SHORT).show();
//            } else if(checkedId == R.id.radioBtnDessert){
//                Toast.makeText(AdminMenuAddActivity.this, "디저트 선택", Toast.LENGTH_SHORT).show();
//            }
//        }
//    };

    public void checkSelfPermission(){
        String temp = "";
        //파일 읽기 권한 확인
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            temp += Manifest.permission.READ_EXTERNAL_STORAGE + " "; }
        //파일 쓰기 권한 확인
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            temp += Manifest.permission.WRITE_EXTERNAL_STORAGE + " ";
        }
        if(TextUtils.isEmpty(temp) == false){
            //권한 요청
            ActivityCompat.requestPermissions(this, temp.trim().split(" "),1);
        }else{
            //모두 허용 상태
            Toast.makeText(this, "카메라와 사진 권한 모두 허용", Toast.LENGTH_SHORT).show();
        }
    }

    public void makeDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminMenuAddActivity.this);
        builder.setTitle("사진업로드")
                .setCancelable(false)
                .setPositiveButton("사진촬영",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "다이얼로그 사진촬영 선택");
//                                selectCamera();
                            }
                        })
                .setNeutralButton("앨범선택",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "다이얼로그 앨볌 선택");
                                selectGallery();
                            }
                        })
                .setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "다이얼로그 취소 선택");
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    //갤러리 앱으로 인텐트 전달
    public void selectGallery(){

        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            Log.d(TAG, "resultCode != RESULT_OK");
            return;
        }

        switch (requestCode) {
            case GALLERY_CODE: {
                //앨범에서 사진 데이터 가져오기
                if (data.getData() != null) {
                    try {
                        photoURI = data.getData();  //사진파일 경로 = photoURI
                        Log.d(TAG, "photoURI : "+photoURI); //content://media/external/images/media/8428
//                        ImageView에 setImage(); => 비트맵형식
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoURI);
                        imgAddMenu.setImageBitmap(bitmap);
                        //firebase에 업로드하는 이미지 경로
                        imgPath = getImgPathData(data.getData());
                        Log.d(TAG, "imgPath : "+imgPath); ///storage/emulated/0/DCIM/Camera/20200520_084245.jpg (파일업로드가능 경로)
                        //ImageView에 setImage(); => Uri형식
//                        File file = new File(imgPath);
//                        imgAddCoffee.setImageURI(Uri.fromFile(file));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

//                //카메라로 찍은 사진 ImageView에 적용시키기
//                getPictureForPhoto();

                break;
            }
//            case CAMERA_CODE: {
//                //카메라 촬영
//                try {
//                    Log.v(TAG, "CODE_CAMERA 처리");
////                    galleryAddPic();
//                    sendPicture(data.getData());
//                    imgAddCoffee.setImageURI(imgUri);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                break;
//            }
            default:
                break;
        }
    }

    //앨범에서 사진 경로 가져오는 메서드 /storage/emulated/0/DCIM/Camera/20200520_084245.jpg
    public String getImgPathData(Uri uri){
        String [] project = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(this, uri, project, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(index);
    }

    //firebase realtime 저장소에 데이터를 저장하는 코드
    public void saveData(){
        //firebase storage 로컬 파일에서 이미지 업로드
        if(imgPath != null){
            //이미지가 있을때
            final Uri file = Uri.fromFile(new File(imgPath));
            StorageReference riversRef = applicationClass.storageRef.child("images/"+file.getLastPathSegment());
            UploadTask uploadTask = riversRef.putFile(file);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d(TAG, "storage에 이미지 업로드 실패");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "storage에 이미지 업로드 성공");
                    //업로드 성공했을 때 데이터 넘겨주기 setValue()
                    AdminMenulistDTO menulistDTO = new AdminMenulistDTO();
                    menulistDTO.menuCategory = checkMenuCategory();
                    menulistDTO.menuImgPath = imgPath;
                    menulistDTO.menuImgName = file.getLastPathSegment();
                    menulistDTO.menuName = menuNameStr;
                    menulistDTO.menuPrice = menuPriceInt;
//                    menulistDTO.uid = applicationClass.firebaseAuth.getCurrentUser().getUid();
//                    menulistDTO.userId = applicationClass.firebaseAuth.getCurrentUser().getEmail();
                    applicationClass.databaseReference.child("AdminMenulist").push().setValue(menulistDTO);
                    Log.d(TAG, "realtime database에 데이터 전달 성공 + 이미지 포함");

                }
            });
        }else{
            //이미지가 없을때
            AdminMenulistDTO menulistDTO = new AdminMenulistDTO();
            menulistDTO.menuCategory = checkMenuCategory();
            menulistDTO.menuImgPath = "default";
            menulistDTO.menuImgName = "default";
            menulistDTO.menuName = menuNameStr;
            menulistDTO.menuPrice = menuPriceInt;
//            menulistDTO.uid = applicationClass.firebaseAuth.getCurrentUser().getUid();
//            menulistDTO.userId = applicationClass.firebaseAuth.getCurrentUser().getEmail();
            applicationClass.databaseReference.child("AdminMenulist").push().setValue(menulistDTO);
            Log.d(TAG, "realtime database에 데이터 전달 성공 + 이미지 제외");
        }
    }

    //체크된 라디오 버튼 아이디값 가져오기
    public String checkMenuCategory(){
        int radioID = radioGroupAddMenu.getCheckedRadioButtonId();  //리턴값은 선택된 adioButton 의 id 값
        RadioButton checkedRadioBtn = (RadioButton)findViewById(radioID);
        String checkedMenuCategory = checkedRadioBtn.getText().toString();
        applicationClass.makeLog("체크한 라디오 버튼 이름 : "+checkedRadioBtn.getText().toString());

        return checkedMenuCategory;
    }

//    //firestore 저장소에 데이터를 저장하는 코드
//    public void createDate(){
//        AdminMenulistDTO menulistDTO = new AdminMenulistDTO(R.drawable.icon_coffee02, menuNameStr, menuPriceLong);
//
//        applicationClass.firebaseFirestore.collection("menu").document(menuNameStr).set(menulistDTO)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d(TAG, "firestore 저장 완료");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d(TAG, "firestore 저장 실패");
//                    }
//                });
//    }
}
