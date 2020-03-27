package com.wjjasd.google.menumemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TableActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton backBtn, homeBtn;
    private String newMenu = "";
    private String newTableNo;
    private String[] newMenuArr;
    private int[] newCountArr;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private boolean dataCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        backBtn = findViewById(R.id.backBtn_settings);
        homeBtn = findViewById(R.id.homeBtn_settings);
        recyclerView = findViewById(R.id.recycler_table_memo);

        backBtn.setOnClickListener(this);
        homeBtn.setOnClickListener(this);

        Intent intent = getIntent();
        dataCheck = intent.getExtras().getBoolean("dataCheck");
        if (dataCheck) { //새로운 데이터 있을때

            newTableNo = intent.getExtras().getString("tableNo");
            newMenuArr = intent.getExtras().getStringArray("menu");
            newCountArr = intent.getExtras().getIntArray("count");

            for (int i = 0; i < newMenuArr.length; i++) {
                newMenu = newMenu + newMenuArr[i] + " " + newCountArr[i] + "\n";
            }

            MemoData memoDataSet = new MemoData();
            memoDataSet.setTableNo(newTableNo);
            memoDataSet.setMenu(newMenu);
            List<MemoData> memoList = new ArrayList<>();
            memoList.add(memoDataSet);
            mAdapter = new MemoAdapter(memoList, TableActivity.this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            }, new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });
            recyclerView.setAdapter(mAdapter);


        } else { //없을때


        }


    }


    @Override
    public void onClick(View v) {

        if (v == backBtn || v == homeBtn) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
