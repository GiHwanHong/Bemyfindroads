package com.example.gihwan.bemyfindroads;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by GiHwan on 2017. 10. 1..
 */

public class AdministratorActivity extends Activity {
    // 내장 DB를 관리자만 확인 할 수 있는 곳
    // select

    private ArrayList<CustomItem> items;
    private CustomAdapter cAdapter;
    private CustomItem c0;
    private ListView mListView;
    private Cursor mCursor = null;
    private int itemposition;

    DBManager mHelper = null;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator);

        db = openOrCreateDatabase("PERS_LIST.db", MODE_PRIVATE, null);   // 있으면 열고 없으면 DB를 생성
        mListView = (ListView) findViewById(R.id.customer_info); // 리스트뷰를 가지고 온다
        items = new ArrayList<CustomItem>();
        mHelper = new DBManager(getApplicationContext());
        cAdapter = new CustomAdapter(AdministratorActivity.this, R.layout.listview_item_customer,items);
        mListView.setAdapter(cAdapter);
    }

    public void Btn_DBSelect(View v) {
        switch (v.getId()) {
            case R.id.DB_select:
                Cursor cursor = db.rawQuery("select * from PERS_LIST", null);
                while (cursor.moveToNext()) {
                    String id = cursor.getString(0);            //ID
                    String name = cursor.getString(2);          // NAME
                    String phone = cursor.getString(6);         // Phone
                    c0 = new CustomItem(id,name,phone);
                    Log.e(id,"아이디가 가져와진다!!!!");
                    items.add(c0);
                }
                cAdapter = new CustomAdapter(AdministratorActivity.this, R.layout.listview_item_customer, items);
                mListView.setAdapter(cAdapter);
                break;

        }

    }


}



