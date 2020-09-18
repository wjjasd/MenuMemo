package com.wjjasd.google.menumemo;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//메뉴 편집 화면에서 메뉴 롱클릭시 팝업
public class EditCategoryActivity extends Activity implements View.OnClickListener {

    private Button saveBtn, cancelBtn, deleteBtn;
    private String mCategory;
    private EditText categoryEdt;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_category);

        intent = getIntent();
        setContent();
    }

    private void setContent() {

        saveBtn = findViewById(R.id.saveBtn_editCategory);
        deleteBtn = findViewById(R.id.deleteBtn_editCategory);
        cancelBtn = findViewById(R.id.cancelBtn_editCategory);
        categoryEdt = findViewById(R.id.categoryEdt);

        saveBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        //롱클릭시 넣은 카테고리 받기
        mCategory = intent.getExtras().getString("category");
        categoryEdt.setText(mCategory);

    }

    @Override
    public void onClick(View v) {
        if (v == cancelBtn) {
            finish();
        } else if (v == saveBtn) {
            SQLiteDatabase db = MemoDBHelper.getInstance(this).getWritableDatabase();
            String temp = categoryEdt.getText().toString();
            try {
                db.execSQL("update menu set category = ? where category = ?", new String[]{temp, mCategory});
                setResult(RESULT_OK);
                finish();
            } catch (Exception e) {
                Toast.makeText(EditCategoryActivity.this, e.getMessage(), Toast.LENGTH_LONG);
            }
        } else if (v == deleteBtn) {
            String temp = categoryEdt.getText().toString();
            SQLiteDatabase db = MemoDBHelper.getInstance(this).getWritableDatabase();
            db.execSQL("delete from menu where category = ?", new String[]{temp});
            setResult(RESULT_OK);
            finish();
        }
    }
}
