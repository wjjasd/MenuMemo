package com.wjjasd.google.menumemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import javax.xml.transform.Result;

import io.realm.Realm;

public class DetailActivity extends Activity implements View.OnClickListener {

    private String mTableNo;
    private String mMenu;
    private TextView tv_tableNo;
    private EditText et_menu;
    private Button saveBtn;
    private Button deleteBtn;
    private Button cancelBtn;
    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detail);

        tv_tableNo = findViewById(R.id.tv_tableNo_detail);
        et_menu = findViewById(R.id.et_menu_detail);
        saveBtn = findViewById(R.id.btn_save_detail);
        deleteBtn = findViewById(R.id.btn_delete_detail);
        cancelBtn = findViewById(R.id.btn_cancel_detail);

        saveBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        mRealm = Realm.getDefaultInstance();

        Intent intent = getIntent();
        MemoData memoData = (MemoData) intent.getExtras().getSerializable("memoData");

        mTableNo = memoData.getTableNo();
        mMenu = memoData.getMenu();

        if (mTableNo != null && mMenu != null && mTableNo != "" && mMenu != "" && mTableNo.getBytes().length > 0 && mMenu.getBytes().length > 0) {
            tv_tableNo.setText(mTableNo);
            et_menu.setText(mMenu);
        }

    }

    @Override
    public void onClick(View v) {
        if (v == saveBtn) {
            mTableNo = tv_tableNo.getText().toString();
            mMenu = et_menu.getText().toString();
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    MemoVo memoVo = realm.where(MemoVo.class).equalTo("tableNo", mTableNo).findFirst();
                    memoVo.setMemo(mMenu);
                    setResult(RESULT_OK);
                    finish();
                }
            });
        } else if (v == deleteBtn) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    MemoVo memoVo = realm.where(MemoVo.class).equalTo("tableNo", mTableNo).findFirst();
                    memoVo.deleteFromRealm();
                    setResult(RESULT_OK);
                    finish();
                }
            });
        } else if (v == cancelBtn) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
