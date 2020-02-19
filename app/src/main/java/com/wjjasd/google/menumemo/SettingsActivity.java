package com.wjjasd.google.menumemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton backBtn, homeBtn;
    Button editMenuBtn, exportMenuBtn, importMenuBtn, resetMenuBtn;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setContent();

        MobileAds.initialize(this, getString(R.string.admob_app_id));
        mAdView = findViewById(R.id.adView_settings);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    @Override
    public void onBackPressed() {
        SQLiteDatabase db = MemoDBHelper.getInstance(SettingsActivity.this).getReadableDatabase();
        Cursor cursor = db.rawQuery("select distinct category from menu order by category", null);
        if (cursor != null && cursor.getCount() != 0) {

            ArrayList<String> list = new ArrayList<>();
            list.add(getString(R.string.spinner_default));
            while (cursor.moveToNext()) {
                String category = cursor.getString(0);
                list.add(category);
            }
            MainActivity.adapter_spinner = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, list);
            MainActivity.spinner.setAdapter(MainActivity.adapter_spinner);
        }
        super.onBackPressed();
        finish();

    }

    private void setContent() {
        backBtn = findViewById(R.id.backBtn_settings);
        homeBtn = findViewById(R.id.homeBtn_settings);
        editMenuBtn = findViewById(R.id.editMenuBtn_settings);
        resetMenuBtn = findViewById(R.id.resetMenu_settings);

        backBtn.setOnClickListener(this);
        homeBtn.setOnClickListener(this);
        editMenuBtn.setOnClickListener(this);
        resetMenuBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        if (v == backBtn || v == homeBtn) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (v == editMenuBtn) {
            Intent intent = new Intent(this, EditMenuActivity.class);
            startActivity(intent);
        } else if (v == resetMenuBtn) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getResources().getString(R.string.initDialogTitle));
            alertDialogBuilder.setMessage(getResources().getString(R.string.initDialogMsg));
            alertDialogBuilder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SQLiteDatabase db = MemoDBHelper.getInstance(SettingsActivity.this).getWritableDatabase();
                    try {
                        db.execSQL("delete from menu");
                    } catch (Exception e) {
                        Log.e("sqlException_settings", e.getMessage());
                    }
                    Toast.makeText(SettingsActivity.this, getResources().getString(R.string.initMes), Toast.LENGTH_SHORT).show();
                }
            });
            alertDialogBuilder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialogBuilder.show();
        }

    }


}
