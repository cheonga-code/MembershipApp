package com.example.membershipapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class AdminOrderlistStatisticActivity extends AdminOrderNavActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_admin_orderlist_statistic);

        //앱바 제목 변경
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("관리자 주문 관리");
        //네비 색상 변경
        bottomNavigationView.setItemBackground(getResources().getDrawable(R.color.colorLightBrown));
    }

    @Override
    int getContentViewId() {
        return R.layout.activity_admin_orderlist_statistic;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.bottomNavAdminOrder05;
    }
}
