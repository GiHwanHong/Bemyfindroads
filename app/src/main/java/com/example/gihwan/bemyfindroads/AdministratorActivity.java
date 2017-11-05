package com.example.gihwan.bemyfindroads;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by GiHwan on 2017. 10. 1..
 */

public class AdministratorActivity extends Activity {
    // 내장 DB를 관리자만 확인 할 수 있는 곳
    // select
    private static String TAG = "phptest_MainActivity";

    private static final String TAG_JSON = "gihwan";
    private static final String TAG_USERID = "userid";
    private static final String TAG_NAME = "name";
    private static final String TAG_PHONENUMBER = "phonenumber";
    private static final String TAG_PARNUMBER = "parnumber";

    String serverURL = "http://13.124.195.151/pselect.php";
    String myJSON;
    private ListView mListView;
    private ArrayList<HashMap<String, String>> store_pList = new ArrayList<HashMap<String, String>>();
    String pernum=null;

    private ArrayList<CustomItem> personList;
    private CustomAdapter cAdapter;
    private CustomItem c0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator);

        mListView = (ListView) findViewById(R.id.customer_info); // 리스트뷰를 가지고 온다
        personList = new ArrayList<CustomItem>();

        GetData task = new GetData();
        task.execute(serverURL);


        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final String url=store_pList.get(position).get(TAG_PARNUMBER);
                Log.e("Store_pList : ", store_pList.toString());
                System.out.println("position : " + position);
                // 마시멜로우 버전이상인지 판별한다 이유는 버전마다 처리하는 방식이 다르기 때문이다
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int permissionResult = checkSelfPermission(Manifest.permission.CALL_PHONE);

                    if (permissionResult == PackageManager.PERMISSION_DENIED) {
                        //권한이 없는 곳이라면 처리한다!
                        AlertDialog.Builder dial = new AlertDialog.Builder(AdministratorActivity.this);
                        dial    .setTitle("권한 필요")
                                .setMessage("기능을 사용하기 위해 단말기의 '전화걸기' 권한이 필요합니다 ")
                                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(AdministratorActivity.this, new String[]{Manifest.permission.CALL_PHONE},1);
                                    }
                                })
                                .setNegativeButton("아니요",null).create().show();
                    }
                    else{
                        //권한이 있는 곳이라면 처리한다!
                        new AlertDialog.Builder(AdministratorActivity.this)
                                .setTitle("보호자에게 전화를 걸까요?")
                                .setMessage(store_pList.get(position).get(TAG_PARNUMBER))
                                .setPositiveButton("확인 ", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent_call = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel",url,null));
                                        try{
                                            startActivity(intent_call);
                                        }catch (Exception e){
                                            Log.e("107 Error : ",e.getMessage());
                                        }
                                    }
                                })
                                .setNegativeButton("취소",null)
                                .show();
                    }
                }
                else{
                    // 처리하는 방식 ACTION_CALL
                    new AlertDialog.Builder(AdministratorActivity.this)
                            .setTitle("보호자에게 전화를 걸까요?")
                            .setMessage(store_pList.get(position).get(TAG_PARNUMBER))
                            .setPositiveButton("확인 ", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent_call = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel",url,null));
                                    try{
                                        startActivity(intent_call);
                                    }catch (Exception e){
                                        Log.e("107 Error : ",e.getMessage());
                                    }
                                }
                            })
                            .setNegativeButton("취소",null)
                            .show();
                }
                return true;
            }
        });


    }


    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(AdministratorActivity.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response  - " + result);

            if (result == null){
                Toast.makeText(AdministratorActivity.this, "회원 가입을 한 사람이 없습니다 ", Toast.LENGTH_SHORT).show();
            }
            else {
                myJSON = result;
            }
        }
    }
    public void Btn_DBSelect(View v) {
        switch (v.getId()) {
            case R.id.DB_select:
                personList.clear();
                store_pList.clear();
                //cAdapter.notifyDataSetChanged();
                showList();
                break;
        }

    }

    protected void showList() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            JSONArray jsonArray = jsonObj.getJSONArray(TAG_JSON);
            HashMap<String,String> map ;
            for(int i=0;i<jsonArray.length();i++){
                map = new HashMap<String,String>();
                JSONObject per_json = jsonArray.getJSONObject(i);

                String id = per_json.getString(TAG_USERID); //userid에 서버의 값을 JSON으로 가져와서 파악한다.
                String name = per_json.getString(TAG_NAME); //name에 서버의 값을 JSON으로 가져와서 파악한다.
                String phonenumber = per_json.getString(TAG_PHONENUMBER);   //Phonenumber에 서버의 값을 JSON으로 가져와서 파악한다.
                String parnumber = per_json.getString(TAG_PARNUMBER);       //parnumber에 서버의 값을 JSON으로 가져와서 파악한다.
                System.out.println("Admin userid:" + id + "/name:" + name + "/phonenumber:" +
                                   phonenumber + "/parnumber:" + parnumber);

                map.put(TAG_USERID,id);
                map.put(TAG_NAME,name);
                map.put(TAG_PHONENUMBER,phonenumber);
                map.put(TAG_PARNUMBER,parnumber);

                c0 = new CustomItem(id, name, phonenumber);
                Log.e(id, "아이디가 서버에서 가져와진다!!!!");

                personList.add(c0);
                store_pList.add(map);
            }

            cAdapter = new CustomAdapter(AdministratorActivity.this, R.layout.listview_item_customer, personList);

            mListView.setAdapter(cAdapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}



