package com.example.membershipapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class CoffeeEditActivity extends LogActivity {

    String TAG = "LogActivity";

    ImageView imgAddCoffee;
    EditText etAddCoffeeName, etAddCoffeePrice;
    Button btnAddCoffeeOK;

    String coffeeNameStr, coffeePriceStr;
    String currentPhotoPath;
    private final int CAMERA_CODE = 3001;
    private final int GALLERY_CODE = 3002;
    int flag;
    String imgPath;

    Uri imgUri, photoURI, providerURI;

    //이미지 관련
    String mImageCaptureName;           //이미지 이름
    String prefSaveImgPath = "0";       //쉐어드프리퍼런스에 저장할 이미지 경로

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_edit);

        imgAddCoffee = (ImageView)findViewById(R.id.imgAddCoffee);
        etAddCoffeeName = (EditText)findViewById(R.id.etAddCoffeeName);
        etAddCoffeePrice = (EditText)findViewById(R.id.etAddCoffeePrice);
        btnAddCoffeeOK = (Button)findViewById(R.id.btnAddCoffeeOK);

        //권한요청
        checkSelfPermission();

        //커피 추가 버튼 클릭시
        btnAddCoffeeOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                coffeeNameStr = etAddCoffeeName.getText().toString();
                coffeePriceStr = etAddCoffeePrice.getText().toString();

                //editText 공백 검사
                if(coffeeNameStr.length() == 0 || coffeePriceStr.length() == 0){
                    Toast.makeText(CoffeeEditActivity.this, "공백을 채워주세요.", Toast.LENGTH_SHORT).show();
                }else{

                    //firebase storage 로컬 파일에서 이미지 업로드
                    if(imgPath != null){
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
//                            ImageDTO imageDTO = new ImageDTO();
//                            imageDTO.imageUrl = imgPath;
//                            imageDTO.name = coffeeNameStr;
//                            imageDTO.price = coffeePriceStr;
//                            imageDTO.uid = firebaseAuth.getCurrentUser().getUid();
//                            imageDTO.userId = firebaseAuth.getCurrentUser().getEmail();
//                            firebaseDatabase.getReference().child("images").push().setValue(imageDTO);
                                CoffeelistDTO coffeelistDTO = new CoffeelistDTO();
                                coffeelistDTO.coffeeImgPath = imgPath;
                                coffeelistDTO.imgName = file.getLastPathSegment();
                                coffeelistDTO.coffeeName = coffeeNameStr;
                                coffeelistDTO.uid = applicationClass.firebaseAuth.getCurrentUser().getUid();
                                coffeelistDTO.userId = applicationClass.firebaseAuth.getCurrentUser().getEmail();
                                int addCoffeePriceInt = Integer.parseInt(coffeePriceStr);
                                coffeelistDTO.coffeePrice = addCoffeePriceInt;
                                applicationClass.firebaseDatabase.getReference().child("coffee").push().setValue(coffeelistDTO);
                                Log.d(TAG, "realtime database에 데이터 전달 성공 + 이미지 포함");

                            }
                        });
                    }else{
                        CoffeelistDTO coffeelistDTO = new CoffeelistDTO();
                        coffeelistDTO.coffeeImgPath = "default";
                        coffeelistDTO.imgName = "default";
                        coffeelistDTO.coffeeName = coffeeNameStr;
                        coffeelistDTO.uid = applicationClass.firebaseAuth.getCurrentUser().getUid();
                        coffeelistDTO.userId = applicationClass.firebaseAuth.getCurrentUser().getEmail();
                        int addCoffeePriceInt = Integer.parseInt(coffeePriceStr);
                        coffeelistDTO.coffeePrice = addCoffeePriceInt;
                        applicationClass.firebaseDatabase.getReference().child("coffee").push().setValue(coffeelistDTO);
                        Log.d(TAG, "realtime database에 데이터 전달 성공 + 이미지 제외");
                    }

                    //이전화면으로 되돌아가기
                    Intent intent = getIntent();
//                    intent.putExtra("addCoffeeName", coffeeNameStr);
//                    intent.putExtra("addCoffeePrice", coffeePriceStr);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        imgAddCoffee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //다이얼로그 띄우기
                makeDialog();
            }
        });
    }

    public void makeDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CoffeeEditActivity.this);
        builder.setTitle("사진업로드")
                .setCancelable(false)
                .setPositiveButton("사진촬영",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "다이얼로그 사진촬영 선택");
//                                takePhoto();
                                selectCamera();

                            }
                        })
                .setNeutralButton("앨범선택",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "다이얼로그 앨볌 선택");
//                                takeAlbum();
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

//    public void takePhoto(){
//
//        String state = Environment.getExternalStorageState();
//        if(Environment.MEDIA_MOUNTED.equals(state)){
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            if(intent.resolveActivity(getPackageManager())!=null){
//                File photoFile = null;
//                try{
//                    photoFile = createImageFile();
//                }catch (IOException e){
//                    e.printStackTrace();
//                }
//                if(photoFile!=null){
//                    providerURI = FileProvider.getUriForFile(this,getPackageName(),photoFile);
//                    imgUri = providerURI;
//                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, providerURI);
//                    startActivityForResult(intent, CAMERA_CODE);
//                }
//            }
//        }else{
//            Log.v(TAG, "저장공간에 접근 불가능");
//            return;
//        }
//    }

    //카메라 앱으로 인텐트 전달
    public void selectCamera(){

        String state = Environment.getExternalStorageState();

        if(Environment.MEDIA_MOUNTED.equals(state)){
            //외부저장소가 현재 read와 write를 할수있는 상태인지 확인
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //이 인텐트를 수행할수 있는 액티비티를 찾는거고 그게 없으면 null값을 리턴합니다.
            //먼저 인텐트를 처리할 수 있는 앱이 존재하는지를 확인하기 위하여 Intent 오브젝트를 사용해 resolveActivity() 메서드를 호출한다.
            //결과가 null이 아니면 인텐트를 처리할 수 있는 앱이 최소 하나는 존재한다는 뜻
            if(cameraIntent.resolveActivity(getPackageManager()) != null){
                File photoFile = null;
                try{
                    //createImageFile() 메소드 호출 -> 해당 함수의 return형이 File이며 이 File은 임시파일로 사용할 것이다.
                    photoFile = createImageFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                if(photoFile != null){
                    //getUriForFile 의 두번째 인자는 Manifest provider의 authorites와 일치해야 함
                    //photoURI 란 변수가 위의 임시파일의 위치를 가지고 있다.
                    //photoURI : file:// 로 시작, FileProvider (Content Provider 하위) 는 conent:// 로 시작
                    //누가(7.0) 이상부터는 file://로 시작되는 Uri 값을 다른 앱과 주고 받기가 불가능해졌다.
                    photoURI = FileProvider.getUriForFile(this, getPackageName(), photoFile);
//                    makeLog("[3] selectCamera() photoFile : "+ photoFile.toString());
                    Log.d(TAG, "[2] selectCamera() photoUri : "+ photoURI.toString()); //보안상 안전 -> 이 photoURI 값을 쉐어드에 저장하면되는것 같은데..?

                    //putExtra 두번째 매개변수에 해당 파일의 URI 값을 전달한다.
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(cameraIntent, CAMERA_CODE);
                }
            }
        }else{
            Log.v(TAG, "저장공간에 접근 불가능");
            return;
        }


    }

//    //이미지를 만드는 createImageFile 메소드
//    private File createImageFile() throws IOException {
//
//        //pathvalue = file_path.xml의 path 값과 같은 이름이어야한다.
//        File dir = new File(Environment.getExternalStorageDirectory() + "/pathvalue/");
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//        //이미지 파일 이름 정하기 -> 오늘날짜_시간.png
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        mImageCaptureName = timeStamp + ".png";
//
//        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/pathvalue/" + mImageCaptureName);
//        //새로 생성된 파일의 해당 위치를 currentPhotoPath 란 변수에 저장하고, 저장한 파일을 리턴한다.
//        //currentPhotoPath(절대경로) 와 Uri 값 비교
//        //Uri -> file://storage/emulated..
//        //currentPhotoPath -> storage/emulated.. (앞에 file:// 제외됨)
//        currentPhotoPath = storageDir.getAbsolutePath();
//
//        //내가 찍은 사진 앨범에 저장함
////        makeLog("[1] selectCamera() storageDir : "+storageDir);
//        makeLog("[1] selectCamera() currentPhotoPath : "+currentPhotoPath);
//
//        return storageDir;
//
//    }

    public File createImageFile() throws IOException{
        String imgFileName = System.currentTimeMillis() + ".jpg";
        File imageFile= null;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/path/");
        if(!storageDir.exists()){
            //존재하지 않을때
            Log.v(TAG,"storageDir 존재하지 않음 " + storageDir.toString());
            storageDir.mkdirs();
        }
        Log.v(TAG,"storageDir 존재함 " + storageDir.toString());
        imageFile = new File(storageDir,imgFileName);
        currentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }

//    //찍은 사진을 갤러리에 저장하는 메서드
//    public void galleryAddPic(){
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        File f = new File(currentPhotoPath);
//        Uri contentUri = Uri.fromFile(f);
//        mediaScanIntent.setData(contentUri);
//        sendBroadcast(mediaScanIntent);
//        Toast.makeText(this,"사진이 저장되었습니다",Toast.LENGTH_SHORT).show();
//    }


    //갤러리 앱으로 인텐트 전달
    public void selectGallery(){

        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_CODE);
    }


//    public void takeAlbum(){
//        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
////        galleryIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        galleryIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
//        startActivityForResult(galleryIntent, ALBUM_CODE);
//    }

    public void checkSelfPermission(){
        String temp = "";

        //파일 읽기 권한 확인
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            temp += Manifest.permission.READ_EXTERNAL_STORAGE + " ";
        }
        //파일 쓰기 권한 확인
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            temp += Manifest.permission.WRITE_EXTERNAL_STORAGE + " ";
        }
        if(TextUtils.isEmpty(temp) == false){
            //권한 요청
            ActivityCompat.requestPermissions(this, temp.trim().split(" "),1);
        }else{
            //모두 허용 상태
            Toast.makeText(this, "권한을 모두 허용", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //권한을 허용했을 경우
        if(requestCode == 1){
            int length = permissions.length;
            for(int i=0 ; i<length ; i++){
                if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    //동의
                    Log.d(TAG, "권한허용 : "+permissions[i]);
                }
            }
        }
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
                        imgAddCoffee.setImageBitmap(bitmap);
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
            case CAMERA_CODE: {
                //카메라 촬영
                try {
                    Log.v(TAG, "CODE_CAMERA 처리");
//                    galleryAddPic();
                    sendPicture(data.getData());
                    imgAddCoffee.setImageURI(imgUri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
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

    //카메라로 찍은 사진 ImageView에 적용시키기
    private void getPictureForPhoto() {
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
        Log.d(TAG, "카메라 imagePath : "+currentPhotoPath); // -> 요게 절대경로 인가??
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(currentPhotoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation;
        int exifDegree;

        if (exif != null) {
            exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            exifDegree = exifOrientationToDegrees(exifOrientation);
        } else {
            exifDegree = 0;
        }
        imgAddCoffee.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
        //쉐어드에 저장할 이미지 변수에 경로 넣기
        prefSaveImgPath = currentPhotoPath;

    }

    //갤러리에서 사진 가져와서 ImageView에 적용시키기
    private void sendPicture(Uri imgUri) {

        String imagePath = getRealPathFromURI(imgUri); // path 경로
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = exifOrientationToDegrees(exifOrientation);

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);//경로를 통해 비트맵으로 전환
        Log.d(TAG, "갤러리 imagePath : "+imagePath); // -> 요게 절대경로 인가??
        imgAddCoffee.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기

        //쉐어드에 저장할 이미지 변수에 경로 넣기
        prefSaveImgPath = imagePath;

    }

    //사진의 절대경로 구하기 (uri -> 절대경로)
    private String getRealPathFromURI(Uri contentUri) {
        int column_index=0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        return cursor.getString(column_index);
    }

    //사진의 회전값 가져오기
    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    //사진을 정방향대로 회전하기 -> 회전된 섬네일 이미지를 돌려주는 함수
    private Bitmap rotate(Bitmap src, float degree) {
        // Matrix 객체 생성
        Matrix matrix = new Matrix();
        // 회전 각도 셋팅
        matrix.postRotate(degree);
        // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                src.getHeight(), matrix, true);
    }


}
