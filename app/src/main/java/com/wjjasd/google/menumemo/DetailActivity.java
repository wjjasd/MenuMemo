package com.wjjasd.google.menumemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import io.realm.Realm;

//테이블 메모 저장 후 카드 클릭시 상세 화면
public class DetailActivity extends Activity implements View.OnClickListener {

    private String mTableNo;    //매장내 테이블 번호
    private String mMenu;       //메뉴
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
        //홈 화면에서 넘어온 메모VO
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
        //저장
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

        //삭제
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
         //취소
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
