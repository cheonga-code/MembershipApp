package com.example.membershipapp;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public interface OnCoffeeListClickListener {
    public void onItemClick(CoffeelistAdapter.CoffeelistViewHolder holder, View view, int position);
}
