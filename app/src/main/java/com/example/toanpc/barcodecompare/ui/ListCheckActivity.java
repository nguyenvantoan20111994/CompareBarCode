package com.example.toanpc.barcodecompare.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.toanpc.barcodecompare.R;
import com.example.toanpc.barcodecompare.adapter.RecycleViewAdapter;
import com.example.toanpc.barcodecompare.controller.RealmCotroller;
import com.example.toanpc.barcodecompare.model.ItemCheck;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by toannguyen201194 on 04/10/2016.
 */
public class ListCheckActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    List<ItemCheck> itemChecks=new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_time_check);
        mRecyclerView=(RecyclerView) findViewById(R.id.recycler_itemcheck);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemChecks=RealmCotroller.getAll();
        RecycleViewAdapter recycleViewAdapter=new RecycleViewAdapter(itemChecks);
        mRecyclerView.setAdapter(recycleViewAdapter);
    }
}
