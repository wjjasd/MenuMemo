package com.wjjasd.google.menumemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import io.realm.Realm;

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
    private TableRow[] newTr = new TableRow[200];
    private int trIndex = -1;
    private int mtrCount;
    private int tableRowPosition;
    private int highlightCountId = 0;
    private TextView highlightTv = null;
    private int highlightTrId = 0;
    private String highlightMenuBuffer = null;
    private AdView mAdView;
    private String[] intentMenu;
    private int[] intentCount;
    private boolean dataCheck;
    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setContent();

        MobileAds.initialize(this, getString(R.string.admob_app_id));
        mAdView = findViewById(R.id.adView_main);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mRealm = Realm.getDefaultInstance();

    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.message_quit);
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

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
                for (int i = 0; i < cursorLength; i++) {
                    menuArray[i] = cursorMenu.getString(0);
                    if (memoTb.getChildCount() == 0) {
                        firstCheckerMap.put(menuArray[i], true);
                        counterMap.put(menuArray[i], 0);
                    }
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
            final Button[] menuBtn = new Button[arrayLength];
            for (int i = 0; i < arrayLength; i++) {
                menuBtn[i] = new Button(this);
                menuBtn[i].setId(i);
                menuBtn[i].setText(menuArray[i]);
                menuBtn[i].setAllCaps(false);

                buttonsTb.addView(menuBtn[i]);
                menuBtn[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        TextView tv = (TextView) v;
                        mMenu = tv.getText().toString();
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
                menuTv.setTextColor(Color.parseColor("#802D2D"));
                menuTv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

                mCount = counterMap.get(mMenu) + 1;

                counterMap.replace(mMenu, mCount);

                String st = String.valueOf(mCount);
                countTv.setText(st);
                int id = getCountId(mMenu);
                countTv.setId(id);
                countTv.setTextSize(25);
                countTv.setTextColor(Color.parseColor("#802D2D"));
                countTv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

                newTr[trIndex].setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                newTr[trIndex].addView(menuTv);
                newTr[trIndex].addView(countTv);
                memoTb.addView(newTr[trIndex]);
                scrollDown();
                highlightCountId = 0;
                highlightMenuBuffer = null;
                highlightTrId = 0;
                highlightTv = null;
                for (int i = 0; i < memoTb.getChildCount(); i++) {
                    memoTb.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }


                newTr[trIndex].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mtrCount = memoTb.getChildCount();
                        tableRowPosition = v.getId();
                        tableRowPosition = tableRowPosition - 10000;

                        ColorDrawable rowColor = (ColorDrawable) v.getBackground();
                        int colorId = rowColor.getColor();
                        if (colorId == getResources().getColor(R.color.colorPrimary)) {
                            v.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                            highlightCountId = newTr[tableRowPosition].getChildAt(1).getId();
                            highlightTrId = v.getId();
                            TextView highlightMenu = (TextView) newTr[tableRowPosition].getChildAt(0);
                            highlightMenuBuffer = highlightMenu.getText().toString();

                            if (mtrCount > 1) {
                                for (int i = 0; i < tableRowPosition; i++) {
                                    TableRow tr = findViewById(i + 10000);
                                    if (tr != null) {
                                        tr.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                    }
                                }
                                for (int i = tableRowPosition + 1; i < mtrCount; i++) {
                                    TableRow tr = findViewById(i + 10000);
                                    if (tr != null) {
                                        tr.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                    }
                                }
                            }

                        } else {
                            v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            highlightCountId = 0;
                        }
                    }
                });

            } else {
                int id = getCountId(mMenu);
                TextView tv = findViewById(id);

                mCount = counterMap.get(mMenu) + 1;
                counterMap.replace(mMenu, mCount);

                //mCount = Integer.parseInt(tv.getText().toString());
                //mCount += 1;
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
            finish();
        } else if (v == plusBtn) {
            if (highlightCountId != 0) {
                highlightTv = findViewById(highlightCountId);
                int count = Integer.valueOf(highlightTv.getText().toString());
                count += 1;
                counterMap.replace(highlightMenuBuffer, count);
                highlightTv.setText(String.valueOf(count));
            }
        } else if (v == subtractionBtn) {
            if (highlightCountId != 0) {
                highlightTv = findViewById(highlightCountId);
                int count = Integer.valueOf(highlightTv.getText().toString());
                count -= 1;
                if (count < 1) {
                    TableRow tr = findViewById(highlightTrId);
                    mtrCount = memoTb.getChildCount();
                    for (int i = tableRowPosition + 1; i < mtrCount; i++) {
                        int newId = newTr[i].getId();
                        newTr[i].setId(newId - 1);
                    }
                    for (int j = tableRowPosition; j < mtrCount; j++) {
                        newTr[j] = newTr[j + 1];
                    }
                    memoTb.removeView(tr);
                    trIndex -= 1;
                    highlightCountId = 0;
                    firstCheckerMap.replace(highlightMenuBuffer, true);
                    counterMap.replace(highlightMenuBuffer, 0);

                } else {
                    highlightTv.setText(String.valueOf(count));
                    counterMap.replace(highlightMenuBuffer, count);
                }
            }


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
                highlightCountId = 0;
            }

        } else if (v == tableBtn) {

            if (memoTb.getChildCount() <= 0) {

                Intent intent = new Intent(MainActivity.this, TableActivity.class);
                dataCheck = false;
                intent.putExtra("dataCheck", dataCheck);
                startActivity(intent);
                finish();

            } else {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(getResources().getString(R.string.tableDialogTitle));
                final EditText et = new EditText(this);
                et.setInputType(InputType.TYPE_CLASS_TEXT);
                et.setMaxLines(1);

                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(20);
                et.setFilters(FilterArray);

                alertDialogBuilder.setView(et);
                alertDialogBuilder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String st = et.getText().toString();
                        if (st == null || st.equals(null) || st == "" || st.getBytes().length <= 0) {
                            Toast.makeText(MainActivity.this,
                                    getResources().getString(R.string.tableDialogTitle), Toast.LENGTH_SHORT).show();
                        } else if (mRealm.where(MemoVo.class).equalTo("tableNo", st).count() > 0) {
                            Toast.makeText(MainActivity.this,
                                    getResources().getString(R.string.tableNo_duplicate), Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(MainActivity.this, TableActivity.class);
                            intent.putExtra("tableNo", st);
                            setTableData();
                            intent.putExtra("menu", intentMenu);
                            intent.putExtra("count", intentCount);
                            dataCheck = true;
                            intent.putExtra("dataCheck", dataCheck);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
                alertDialogBuilder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.show();
            }
        }
    }

    private void setTableData() {
        SQLiteDatabase db = MemoDBHelper.getInstance(this).getReadableDatabase();
        cursorMenu = db.rawQuery("select name from menu order by category", null);
        cursorLength = cursorMenu.getCount();
        menuArray = new String[cursorLength];
        if (cursorMenu != null && cursorMenu.getCount() != 0) {
            cursorMenu.moveToFirst();
            for (int i = 0; i < cursorLength; i++) {
                menuArray[i] = cursorMenu.getString(0);
                cursorMenu.moveToNext();
            }
        }

        int intentMenuLength = 0;
        for (int i = 0; i < menuArray.length; i++) {
            if (firstCheckerMap.get(menuArray[i]) == false) {
                intentMenuLength += 1;
            }
        }
        intentMenu = new String[intentMenuLength];
        intentCount = new int[intentMenuLength];

        int j = 0;
        for (int i = 0; i < menuArray.length; i++) {
            if (firstCheckerMap.get(menuArray[i]) == false) {
                intentMenu[j] = menuArray[i];
                intentCount[j] = counterMap.get(menuArray[i]);
                j += 1;
            }
        }
    }


    private int getCountId(String st) {
        /** code 중복발생함!
         int code = 0;
         char[] chars = st.toCharArray();
         int length = st.length();
         for (int i = 0; i < length; i++) {
         char c = chars[i];
         code += (int) c + 1000;
         }
         **/
        int multi = 1;
        int code = 1000;
        for (int i = 0; i < st.length(); i++) {
            int buffer = (int) st.charAt(i) * multi;
            code = code + buffer;
            multi *= 2;
        }

        return code;
    }


}