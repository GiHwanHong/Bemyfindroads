package com.example.gihwan.bemyfindroads;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
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

    private static final String TAG_JSON="gihwan";
    private static final String TAG_USERID = "userid";
    private static final String TAG_NAME = "name";
    private static final String TAG_PHONENUMBER ="phonenumber";

    private ArrayList<CustomItem> personList;
    String serverURL = "http://13.124.195.151/pselect.php";
    String myJSON;
    private ListView mListView;

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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //mTextViewResult.setText(result);
            Log.d(TAG, "response  - " + result);

            if (result == null){
                //mTextViewResult.setText(errorString);
                Toast.makeText(AdministratorActivity.this, "회원 가입을 한 사람이 없습니다 ", Toast.LENGTH_SHORT).show();
            }
            else {

                myJSON = result;
                //showList();
            }
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
    }
    public void Btn_DBSelect(View v) {
        switch (v.getId()) {
            case R.id.DB_select:
                personList.clear();
                //cAdapter.notifyDataSetChanged();
                showList();
                break;
        }

    }

    protected void showList() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            JSONArray jsonArray = jsonObj.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String id = item.getString(TAG_USERID); //userid JSON으로 가져올 것을 파악한다
                String name = item.getString(TAG_NAME); //name JSON으로 가져올 것을 파악한다
                String phonenumber = item.getString(TAG_PHONENUMBER);   //Phonenumber JSON으롤 가져올 것을 파악한다

                Log.e("id : ",id);
                Log.e("name :",name);
                Log.e("phonenumber :",phonenumber);

                c0 = new CustomItem(id, name, phonenumber);
                Log.e(id, "아이디가 서버에서 가져와진다!!!!");
                personList.add(c0);
            }

            cAdapter = new CustomAdapter(AdministratorActivity.this, R.layout.listview_item_customer, personList);

            mListView.setAdapter(cAdapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}



