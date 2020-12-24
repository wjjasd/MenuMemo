package com.wjjasd.google.menumemo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.HashMap;

//메뉴편집 화면
public class EditMenuActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE = 1000;
    private ImageButton backBtn, homeBtn, refreshBtn;
    private Button saveBtn, deleteBtn, clearBtn;
    private EditText menuEdt, categoryEdt;
    private Spinner spinner;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_menu);
        setContent();

        //구글광고 초기화, 요청, 로드
        MobileAds.initialize(this, getString(R.string.admob_app_id));
        AdView adView = findViewById(R.id.adView_editMenu);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

    }

    private void setContent() {
        backBtn = findViewById(R.id.backBtn_editMenu);
        homeBtn = findViewById(R.id.homeBtn_editMenu);
        saveBtn = findViewById(R.id.saveBtn_editMenu);
        deleteBtn = findViewById(R.id.deleteBtn_editMenu);
        clearBtn = findViewById(R.id.clearBtn_editMenu);
        menuEdt = findViewById(R.id.edtMenu_editMenu);
        categoryEdt = findViewById(R.id.edtCategory_editMenu);
        spinner = findViewById(R.id.spinner_editMenu);
        listView = findViewById(R.id.list_editMenu);
        refreshBtn = findViewById(R.id.refresh_editMenu);

        backBtn.setOnClickListener(this);
        homeBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
        clearBtn.setOnClickListener(this);
        refreshBtn.setOnClickListener(this);

        setSpinner(); //카테고리
        setList();  //카테고리에 따른 메뉴리스트

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditMenuActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    private void setSpinner() {

        SQLiteDatabase db = MemoDBHelper.getInstance(this).getReadableDatabase();
        Cursor cursor = db.rawQuery("select distinct category from menu order by category", null);

        if (cursor != null && cursor.getCount() != 0) {

            ArrayList<String> list = new ArrayList<>();
            list.add(getString(R.string.spinner_default));
            while (cursor.moveToNext()) {
                String category = cursor.getString(0);
                list.add(category);
            }
            Adapter adapter_spinner = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, list);
            spinner.setAdapter((SpinnerAdapter) adapter_spinner);
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                setList();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

    }

    // 스피너에서 선택한 카테고리의 메뉴를 리스트뷰에 뿌려주는 메서드
    private void setList() {
        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        SQLiteDatabase db = MemoDBHelper.getInstance(this).getReadableDatabase();
        String selectedCategory;
        if (spinner.getItemAtPosition(0) != null) {
            selectedCategory = spinner.getSelectedItem().toString();
        } else {
            selectedCategory = getResources().getString(R.string.spinner_default);
        }
        //전체검색
        if (selectedCategory == getResources().getString(R.string.spinner_default)) {
            Cursor cursor = db.rawQuery("select * from menu order by category ", null);
            while (cursor.moveToNext()) {
                HashMap<String, String> map = new HashMap<>();
                map.put("name", cursor.getString(0));
                map.put("category", cursor.getString(1));
                data.add(map);
            }
            //특정 카테고리만 검색해서 arraylist에 add
        } else {
            Cursor cursor = db.rawQuery("select * from menu where category = ? order by category ", new String[]{selectedCategory});
            while (cursor.moveToNext()) {
                HashMap<String, String> map = new HashMap<>();
                map.put("name", cursor.getString(0));
                map.put("category", cursor.getString(1));
                data.add(map);
            }
        }

        //검색한 데이터 메뉴리스트에 뿌리기
        SimpleAdapter adapter = new SimpleAdapter(this, data, android.R.layout.simple_list_item_2,
                new String[]{"name", "category"}, new int[]{android.R.id.text1, android.R.id.text2});
        listView.setAdapter(adapter);
        //클릭하면 해당 메뉴와 카테고리 editText로 뿌려줌
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Object clickItemObj = listView.getAdapter().getItem(position);
                HashMap clickItemMap = (HashMap) clickItemObj;
                String menu = (String) clickItemMap.get("name");
                String category = (String) clickItemMap.get("category");

                menuEdt.setText(menu);
                categoryEdt.setText(category);

            }
        });
        //롱클릭시 메뉴 수정하는 팝업 띄어줌
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Object clickItemObj = listView.getAdapter().getItem(position);
                HashMap clickItemMap = (HashMap) clickItemObj;
                String category = (String) clickItemMap.get("category");

                Intent intent = new Intent(EditMenuActivity.this, EditCategoryActivity.class);
                intent.putExtra("category", category);
                startActivityForResult(intent, REQUEST_CODE);

                return true;
            }
        });
    }

    @Override
    //메뉴 수정후 돌아왔을때 리스트 업데이트
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            setList();
        }
    }

    @Override
    public void onClick(View v) {

        String menu_userInput;
        String category_userInput;

        //뒤로가기 버튼
        if (v == backBtn) {
            Intent intent = new Intent(EditMenuActivity.this, SettingsActivity.class);
            startActivity(intent);
            finish();
            //홈
        } else if (v == homeBtn) {
            Intent intent = new Intent(EditMenuActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            //저장
        } else if (v == saveBtn) {
            menu_userInput = menuEdt.getText().toString();
            category_userInput = categoryEdt.getText().toString();
            //널체크
            if (menu_userInput.getBytes().length <= 0 || category_userInput.getBytes().length <= 0) {
                Toast.makeText(this, R.string.emptyMsg, Toast.LENGTH_SHORT).show();
            } else {
                ContentValues values = new ContentValues();
                values.put("name", menu_userInput);
                values.put("category", category_userInput);

                // DB 쓰기
                SQLiteDatabase db = MemoDBHelper.getInstance(this).getWritableDatabase();
                long rowId = db.insert("menu", null, values);
                if (rowId == -1) {  //insert 실행결과 : 실패
                    Toast.makeText(this, R.string.failed_toast, Toast.LENGTH_SHORT).show();
                } else {            //insert 실행결과 : 성공
                    Toast.makeText(this, R.string.saved_toast, Toast.LENGTH_SHORT).show();
                    setList();
                }
            }
            //삭제
        } else if (v == deleteBtn) {
            menu_userInput = menuEdt.getText().toString();
            category_userInput = categoryEdt.getText().toString();
            if (menu_userInput.getBytes().length <= 0 || category_userInput.getBytes().length <= 0) {
                Toast.makeText(this, R.string.emptyMsg, Toast.LENGTH_SHORT).show();
            } else {
                SQLiteDatabase db = MemoDBHelper.getInstance(this).getWritableDatabase();
                try {
                    db.execSQL("delete from menu where name = ? and category = ?", new String[]{menu_userInput, category_userInput});
                    setList();
                    Toast.makeText(this, R.string.deleted_toast, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
            //editText 값 초기화
        } else if (v == clearBtn) {
            menuEdt.setText("");
            categoryEdt.setText("");
            //스피너 새로고침
        } else if (v == refreshBtn) {
            setSpinner();
        }

    }


}
