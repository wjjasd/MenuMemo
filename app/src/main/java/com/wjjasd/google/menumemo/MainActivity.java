package com.wjjasd.google.menumemo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton settingsBtn;
    private Button plusBtn;
    private Button subtractionBtn;
    private Button clearBtn;
    private Button tableBtn;
    private String selectedCategory;
    private String[] menuArray;
    private LinearLayout buttonsTb;
    private Cursor cursorMenu = null;
    public static AdapterView spinner;
    public static Adapter adapter_spinner;
    private String mMenu = null;
    private TableLayout memoTb;
    private ScrollView scrollView;
    private HashMap<String, Boolean> firstCheckerMap = new HashMap<String, Boolean>();
    private HashMap<String, Integer> counterMap = new HashMap<String, Integer>();
    private int cursorLength;
    private int mCount;
    private TableRow newTr[] = new TableRow[200];
    private int trIndex = -1;
    private int mtrCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setContent();

    }

    private void setContent() {

        settingsBtn = findViewById(R.id.settingBtn_main);
        plusBtn = findViewById(R.id.plusBtn_main);
        subtractionBtn = findViewById(R.id.subtractionBtn_main);
        clearBtn = findViewById(R.id.clearBtn_main);
        tableBtn = findViewById(R.id.tableBtn_main);
        spinner = findViewById(R.id.spinner_main);
        buttonsTb = findViewById(R.id.buttonsTb_main);
        memoTb = findViewById(R.id.memoTb);
        scrollView = findViewById(R.id.scrollView_main);

        settingsBtn.setOnClickListener(this);
        plusBtn.setOnClickListener(this);
        subtractionBtn.setOnClickListener(this);
        clearBtn.setOnClickListener(this);
        tableBtn.setOnClickListener(this);

        setSpinner();
        setButtonsTb();
    }


    private void setButtonsTb() {

        getCategory();
        getButtonData();
        setButtons();

    }

    private void getButtonData() {
        SQLiteDatabase db = MemoDBHelper.getInstance(this).getReadableDatabase();
        if (selectedCategory == getResources().getString(R.string.spinner_default)) {
            cursorMenu = db.rawQuery("select name from menu order by category", null);
            cursorLength = cursorMenu.getCount();
            menuArray = new String[cursorLength];
            if (cursorMenu != null && cursorMenu.getCount() != 0) {
                cursorMenu.moveToFirst();
                for (int i = 0; i < cursorMenu.getCount(); i++) {
                    menuArray[i] = cursorMenu.getString(0);
                    firstCheckerMap.put(menuArray[i], true);
                    counterMap.put(menuArray[i], 0);
                    cursorMenu.moveToNext();

                }
            }

        } else {
            cursorMenu = db.rawQuery("select * from menu where category = ? order by name", new String[]{selectedCategory});
            int cursorLength = cursorMenu.getCount();
            menuArray = new String[cursorLength];
            if (cursorMenu != null && cursorMenu.getCount() != 0) {
                cursorMenu.moveToFirst();
                for (int i = 0; i < cursorMenu.getCount(); i++) {
                    menuArray[i] = cursorMenu.getString(0);
                    cursorMenu.moveToNext();
                }
            }
        }
    }

    private void setButtons() {

        if (menuArray != null && menuArray.length != 0) {
            buttonsTb.removeAllViews();
            int arrayLength = menuArray.length;
            final Button menuBtn[] = new Button[arrayLength];
            for (int i = 0; i < arrayLength; i++) {
                menuBtn[i] = new Button(this);
                menuBtn[i].setId(i);
                menuBtn[i].setText(menuArray[i]);
                menuBtn[i].setAllCaps(false);
                buttonsTb.addView(menuBtn[i]);
                menuBtn[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMenu = menuArray[v.getId()];
                        setMemoTb();
                        firstCheckerMap.replace(mMenu, false);
                        mMenu = null;
                    }
                });
            }
        }
    }

    private void scrollDown() {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private void setMemoTb() {
        if (mMenu != null) {

            final TextView menuTv = new TextView(this);
            final TextView countTv = new TextView(this);

            if (firstCheckerMap.get(mMenu) == true) {

                trIndex += 1;
                newTr[trIndex] = new TableRow(MainActivity.this);
                newTr[trIndex].setId(trIndex + 10000);

                menuTv.setText(mMenu);
                menuTv.setTextSize(25);
                menuTv.setTextColor(Color.parseColor("#000000"));
                menuTv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

                mCount = counterMap.get(mMenu) + 1;
                String st = String.valueOf(mCount);
                countTv.setText(st);
                countTv.setId(convAscii(mMenu));
                countTv.setTextSize(25);
                countTv.setTextColor(Color.parseColor("#000000"));
                countTv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

                newTr[trIndex].setBackgroundColor(getResources().getColor(android.R.color.background_light));
                newTr[trIndex].addView(menuTv);
                newTr[trIndex].addView(countTv);
                memoTb.addView(newTr[trIndex]);
                scrollDown();


                newTr[trIndex].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mtrCount = memoTb.getChildCount();
                        int position = v.getId();
                        position = position - 10000;

                        ColorDrawable rowColor = (ColorDrawable) v.getBackground();
                        int colorId = rowColor.getColor();
                        if (colorId == 0xffffffff) {
                            v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                            if (mtrCount > 1) {
                                for (int i = 0; i < position; i++) {
                                    TableRow tr = findViewById(i + 10000);
                                    tr.setBackgroundColor(getResources().getColor(android.R.color.background_light));
                                }
                                for (int i = position + 1; i < mtrCount; i++) {
                                    TableRow tr = findViewById(i + 10000);
                                    tr.setBackgroundColor(getResources().getColor(android.R.color.background_light));
                                }
                            }

                        } else {
                            v.setBackgroundColor(getResources().getColor(android.R.color.background_light));
                        }
                    }
                });

            } else {
                int id = convAscii(mMenu);
                TextView tv = findViewById(id);
                mCount = Integer.parseInt(tv.getText().toString());
                mCount += 1;
                tv.setText(Integer.toString(mCount));
            }


        }
    }

    private void getCategory() {
        if (spinner.getSelectedItem() != null) {
            selectedCategory = spinner.getSelectedItem().toString();
        } else {
            selectedCategory = getResources().getString(R.string.spinner_default);
        }

    }


    public void setSpinner() {
        SQLiteDatabase db = MemoDBHelper.getInstance(MainActivity.this).getReadableDatabase();
        Cursor cursor = db.rawQuery("select distinct category from menu order by category", null);
        if (cursor != null && cursor.getCount() != 0) {

            ArrayList<String> list = new ArrayList<>();
            list.add(getString(R.string.spinner_default));
            while (cursor.moveToNext()) {
                String category = cursor.getString(0);
                list.add(category);
            }
            adapter_spinner = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, list);
            spinner.setAdapter(adapter_spinner);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setButtonsTb();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    @Override
    public void onClick(View v) {

        if (v == settingsBtn) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (v == plusBtn) {

        } else if (v == subtractionBtn) {

        } else if (v == clearBtn) {

            memoTb.removeAllViews();

            SQLiteDatabase db = MemoDBHelper.getInstance(this).getReadableDatabase();
            cursorMenu = db.rawQuery("select name from menu order by category", null);
            cursorLength = cursorMenu.getCount();
            menuArray = new String[cursorLength];
            if (cursorMenu != null && cursorMenu.getCount() != 0) {
                cursorMenu.moveToFirst();
                for (int i = 0; i < cursorMenu.getCount(); i++) {
                    menuArray[i] = cursorMenu.getString(0);
                    firstCheckerMap.replace(menuArray[i], true);
                    counterMap.replace(menuArray[i], 0);
                    cursorMenu.moveToNext();
                }
                trIndex = -1;


            }


        } else if (v == tableBtn) {

        }
    }

    private int convAscii(String st) {
        int code = 0;
        char[] chars = st.toCharArray();
        for (int i = 0; i < st.length(); i++) {
            char c = chars[i];
            code += (int) c + 1000;
        }
        return code;
    }

}


