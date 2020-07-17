package com.example.membershipapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public abstract class BaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    BottomNavigationView bottomNavigationView;

    private FirebaseAuth firebaseAuth;

    //쉐어드관련
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String sharedFileNameStr = "sharedFile";
    String sharedCardPriceKey = "chargeCardPrice";
    String sharedStampKey = "stampQuantity";

    //로그관련
    String classname = getClass().getSimpleName().trim();
    String TAG = "LogActivity";

    ApplicationClass applicationClass;

    //로그
    public void makeLog(String strData){
        Log.d(TAG, classname+"-"+strData);
    }

    //토스트메세지
    public void makeToast(String str){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());

        Log.d(TAG, classname+"-onCreate");

        applicationClass = (ApplicationClass)getApplicationContext();

        sharedPreferences = getSharedPreferences(sharedFileNameStr, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        //파이어베이스 인증 객체 선언
        firebaseAuth = FirebaseAuth.getInstance();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bottomNav01:
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return true;
            case R.id.bottomNav02:
                startActivity(new Intent(this, OrderlistActivity.class));
//                bottomNavigationView.removeBadge(R.id.bottomNav02);
//                bottomNavigationView.getOrCreateBadge(R.id.bottomNav02).setNumber(1);
                finish();
                return true;
            case R.id.bottomNav03:
                startActivity(new Intent(this, CouponActivity.class));
                finish();
                return true;
            case R.id.bottomNav04:
                //TODO: badge 제거 테스트중
                hideBottomNavigationViewBadge(bottomNavigationView, 2);
                startActivity(new Intent(this, MypageActivity.class));
                finish();
                return true;
        }
        return false;
    }

    private void updateNavigationBarState() {
        int actionId = getNavigationMenuItemId();
        selectBottomNavigationBarItem(actionId);

    }

    void selectBottomNavigationBarItem(int itemId) {
        MenuItem item = bottomNavigationView.getMenu().findItem(itemId);
        item.setChecked(true);
//        updateFirstBadge(1);
        //TODO: badge 생성 테스트중
        showBottomNavigationViewBadge(getApplicationContext(), bottomNavigationView, 2);
    }

    //상속받은 클래스에서 해당 액티비티 layout을 리턴함
    //return R.layout.activity_parking;
    abstract int getContentViewId();

    //상속받은 클래스에서 해당 BottomNavigationView id를 리턴함
    //return R.id.bottomNav04; -> bottom_navigation_menu.xml 에 적은 아이디
    abstract int getNavigationMenuItemId();

    //bottom navigation badge 생성
//    public void updateFirstBadge(int count) {
////        if (count <= 0) {
////            bottomNavigationView.removeBadge(R.id.bottomNav02);
////        } else {
//            BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.bottomNav02);
//            badgeDrawable.setBackgroundColor(getResources().getColor(R.color.colorRed));
//            badgeDrawable.setNumber(count);
//            badgeDrawable.isVisible();
////        }
//    }

    public void showBottomNavigationViewBadge(Context context, BottomNavigationView navigationView, int index){
        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) navigationView.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(index);
        BottomNavigationItemView itemView = (BottomNavigationItemView) v;
        View badge = LayoutInflater.from(context).inflate(R.layout.bottom_navigation_badge, bottomNavigationMenuView, false);
        itemView.addView(badge);
    }

    public static void hideBottomNavigationViewBadge(BottomNavigationView navigationView, int index) {
        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) navigationView.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(index);
        BottomNavigationItemView itemView = (BottomNavigationItemView) v;
        itemView.removeViewAt(itemView.getChildCount() - 1);
    }


    //생명주기
    @Override
    protected void onStart() {
        super.onStart();
        updateNavigationBarState();

        Log.d(TAG, classname+"-onStart");
    }

    @Override
    protected void onResume() {
        Log.d(TAG, classname+"-onResume");
        super.onResume();
    }

    // Remove inter-activity transition to avoid screen tossing on tapping bottom navigation items
    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);   //0,0 이면 전환효과 해제

        Log.d(TAG, classname+"-onPause");
    }

    @Override
    protected void onStop() {
        Log.d(TAG, classname+"-onStop");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, classname+"+onRestart");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, classname+"=onDestroy");
        super.onDestroy();
    }
}
