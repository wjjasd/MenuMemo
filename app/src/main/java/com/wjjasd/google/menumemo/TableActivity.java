package com.wjjasd.google.menumemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class TableActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_CODE_DETAIL = 1000;
    private ImageButton backBtn, homeBtn;
    private String mMenu = "";
    private String mTableNo;
    private String[] mMenuArr;
    private int[] mCountArr;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private boolean dataCheck;
    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        mRealm = Realm.getDefaultInstance();

        backBtn = findViewById(R.id.backBtn_settings);
        homeBtn = findViewById(R.id.homeBtn_settings);
        recyclerView = findViewById(R.id.recycler_table_memo);
        backBtn.setOnClickListener(this);
        homeBtn.setOnClickListener(this);

        Intent intent = getIntent();
        dataCheck = intent.getExtras().getBoolean("dataCheck");
        if (dataCheck) { //새로운 데이터 있을때

            mTableNo = intent.getExtras().getString("tableNo");
            mMenuArr = intent.getExtras().getStringArray("menu");
            mCountArr = intent.getExtras().getIntArray("count");
            for (int i = 0; i < mMenuArr.length; i++) {
                mMenu = mMenu + mMenuArr[i] + " " + mCountArr[i] + "\n";
            }
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    MemoVo memoVo = mRealm.createObject(MemoVo.class);
                    memoVo.setTableNo(mTableNo);
                    memoVo.setMemo(mMenu);
                }
            });

            makeCard();

        } else { //없을때
            makeCard();
        }

    }

    private void makeCard() {
        RealmResults<MemoVo> result = mRealm.where(MemoVo.class).findAll();
        int resultSize = result.size();
        final List<MemoData> memoList = new ArrayList<>();
        for (int i = 0; i < resultSize; i++) {
            if (result.get(i) != null) {
                mMenu = result.get(i).getMemo();
                mTableNo = result.get(i).getTableNo();
                MemoData memoDataSet = new MemoData();
                memoDataSet.setTableNo(mTableNo);
                memoDataSet.setMenu(mMenu);
                memoList.add(memoDataSet);
            }
        }
        mAdapter = new MemoAdapter(memoList, TableActivity.this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object obj = v.getTag();
                if(obj != null){
                    int position = (int)obj;
                    MemoData memoData = ((MemoAdapter)mAdapter).getMemo(position);
                    Intent intent = new Intent(TableActivity.this,DetailActivity.class);
                    intent.putExtra("memoData", (Serializable) memoData);
                    startActivityForResult(intent, REQUEST_CODE_DETAIL);
                }

            }
        }, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(TableActivity.this, "LongClick", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_DETAIL&&resultCode==RESULT_OK){
            makeCard();
        }
    }
}
