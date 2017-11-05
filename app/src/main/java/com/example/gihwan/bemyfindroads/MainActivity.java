package com.example.gihwan.bemyfindroads;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

    private ArrayList<HashMap<String, String>> personList = new ArrayList<HashMap<String, String>>();

    EditText ID_user, PW_user;
    String user_id, user_pw;

    int flag = 0, flag2 = 0;
    LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivity(new Intent(this, SplashActivity.class));


        ID_user = (EditText) findViewById(R.id.IdInput);     // 로그인 아이디 입력 EditText
        PW_user = (EditText) findViewById(R.id.pInput);      //      패스워드 입력

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            alertCheckGPS();
        }

    }

    public void Btn_main(View v) {
        switch (v.getId()) {
            case R.id.signupButton:
                Intent SignupIntent = new Intent(getApplicationContext(), JoinActivity.class);        // 다음 창으로 이동
                startActivity(SignupIntent);
                break;

            case R.id.loginButton:
                user_id = ID_user.getText().toString();
                user_pw = PW_user.getText().toString();
                if (user_id.length() == 0 || user_pw.length() == 0) {
                    Toast.makeText(getApplicationContext(), "아이디와 패스워드를 입력해주세요 ", Toast.LENGTH_SHORT).show();
                    ID_user.setText(null);
                    PW_user.setText(null);
                }
                new JsonLoadingTask().execute(); //Async스레드를 시작

                // ArrayList형태로 저장된 값이 존재하는 동안 만큼 동작한다
                for (int i = 0; i < personList.size(); i++) {
                    Intent LoginIntent = new Intent(MainActivity.this, ChooseActivity.class);
                    if (user_id.equals(personList.get(i).get("userid")) && user_pw.equals(personList.get(i).get("passwd"))) {        //사용자가 입력한 값과 서버 DB에 있는 값을 비교한다
                        // 관리자 user인 경우 회원가입한 사람들의 정보들을 보기 위하여 값을 따로 넘겨줘야함
                        Toast.makeText(getApplicationContext(), personList.get(i).get("name") + " 님 안녕하세요~", Toast.LENGTH_SHORT).show();
                        startActivity(LoginIntent);
                        finish();
                        break;
                    } else if (!user_id.equals(personList.get(i).get("userid")) && !user_pw.equals(personList.get(i).get("passwd"))) {
                        flag2 = 1;
                    }
                }
                if (flag2 == 1) {
                    Toast.makeText(getApplicationContext(), "아이디와 패스워드를 다시 입력해주세요 ", Toast.LENGTH_SHORT).show();
                    ID_user.setText(null);
                    PW_user.setText(null);
                    break;
                }
                break;
        }
    }

    // GPS 켜기 - 앱에서 자동으로 설정해주면 더 좋겠지만 개인정보 때문에 구글에서 막아뒀다 그래서 설정창으로 가서 다시 해야한다. //
    private void alertCheckGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setMessage("위치 서비스 설정")
                .setCancelable(false)  // 뒤로 버튼 클릭시 취소 가능 설정
                .setView(inflater.inflate(R.layout.activity_gpsdialog_layout, null))
                .setPositiveButton("GPS 키러 가기 ",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveConfigGPS();
                            }
                        })
                .setNegativeButton("GPS 안킬래요",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void moveConfigGPS() {
        Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(gpsOptionsIntent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= 23) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                //resume tasks needing this permission
            }
        }
    }

    private class JsonLoadingTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strs) {
            return getJsonText();
        } // doInBackground : 백그라운드 작업을 진행한다.

        @Override
        protected void onPostExecute(String result) {

        } // onPostExecute : 백그라운드 작업이 끝난 후 UI 작업을 진행한다.
    }

    public String getJsonText() {
        StringBuffer sb = new StringBuffer();
        try {

            //주어진 URL 문서의 내용을 문자열로 얻는다.
            String jsonPage = getStringFromUrl("http://13.124.195.151/pselect.php");

            //읽어들인 JSON포맷의 데이터를 JSON객체로 변환
            JSONObject json = new JSONObject(jsonPage);

            //ksk_list의 값은 배열로 구성 되어있으므로 JSON 배열생성
            JSONArray jArr = json.getJSONArray("gihwan");

            //배열의 크기만큼 반복하면서, ksNo과 korName의 값을 추출함
            for (int i = 0; i < jArr.length(); i++) {
                HashMap<String, String> map = new HashMap<String, String>();
                //i번째 배열 할당
                json = jArr.getJSONObject(i);

                //ksNo,korName의 값을 추출함
                String userid = json.getString("userid");
                String passwd = json.getString("passwd");
                String name = json.getString("name");
                System.out.println("Main userid:" + userid + "/passwd:" + passwd + "/name:" + name);

                //json으로 받은 string 값을 hashmap을 이용하여 로그인에 필요한 사용자의 정보를 얻어 온다
                map.put("userid", userid);
                map.put("passwd", passwd);
                map.put("name", name);

                personList.add(map);            // json으로 받아온 것을 리스트 형식으로 저장한다

                sb.append("[ " + userid + " ]\n");
                sb.append("[" + passwd + "]\n");
                sb.append("[" + name + "]\n");
                sb.append("\n");

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    // getStringFromUrl : 주어진 URL의 문서의 내용을 문자열로 반환
    public String getStringFromUrl(String pUrl) {

        BufferedReader bufreader = null;
        HttpURLConnection urlConnection = null;

        StringBuffer page = new StringBuffer(); //읽어온 데이터를 저장할 StringBuffer객체 생성

        try {
            URL url = new URL(pUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream contentStream = urlConnection.getInputStream();

            bufreader = new BufferedReader(new InputStreamReader(contentStream, "UTF-8"));
            String line = null;

            //버퍼의 웹문서 소스를 줄단위로 읽어(line), Page에 저장함
            while ((line = bufreader.readLine()) != null) {
                Log.d("my_data:", line);
                page.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //자원해제
            try {
                bufreader.close();
                urlConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return page.toString();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case android.view.KeyEvent.KEYCODE_BACK:
                new AlertDialog.Builder(this)
                        .setTitle("종료")
                        .setMessage("종료하시겠습니까?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton("NO", null)
                        .show();
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}