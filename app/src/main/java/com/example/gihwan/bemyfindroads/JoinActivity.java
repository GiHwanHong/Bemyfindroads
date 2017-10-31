package com.example.gihwan.bemyfindroads;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import static com.gun0912.tedpermission.TedPermission.TAG;

/**
 * Created by user on 2017-05-03.
 */
public class JoinActivity extends Activity {


    EditText idedit, etpassword, etname;

    EditText etphonenumber;
    EditText ettype, etdegree;
    TextView mTextViewResult, etaddress ;
    SpeechRecognizer sr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        idedit = (EditText) findViewById(R.id.idEdit);      // 아이디 입력 EditText
        etpassword = (EditText) findViewById(R.id.etPassword);  // 비밀번호 입력
        etname = (EditText) findViewById(R.id.joinName); // 가입자 이름
        ettype = (EditText) findViewById(R.id.Type);      // 가입자 장애 유형
        etdegree = (EditText) findViewById(R.id.Grade);    // 등급

        etaddress = (TextView) findViewById(R.id.Address);   // 가입자 주소
        etphonenumber = (EditText) findViewById(R.id.Phonenumber);   // 가입자 핸드폰 번호
        mTextViewResult = (TextView) findViewById(R.id.textView_db_result); // DB 확인 메시지

        sr = SpeechRecognizer.createSpeechRecognizer(this); // SpeechRecognizer 초기화
        sr.setRecognitionListener(listener);

        //핸드폰 번호 가져오기
        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        String phoneNum = telephonyManager.getLine1Number();
        if (phoneNum.startsWith("+82")) {
            phoneNum = phoneNum.replace("+82", "0");
        }
        etphonenumber.setText(phoneNum);


    }

    public void Btn_Join_info(View v) {
        switch (v.getId()) {
            case R.id.btnCancel: //취소
                Toast.makeText(getApplicationContext(), "취소되었습니다.", Toast.LENGTH_LONG).show();
                finish();
                break;
            case R.id.btnDone:  // 회원가입 가입 버튼
                //각 Edittext에 있는 값을 가져 온다
                String id = idedit.getText().toString();
                String passwd = etpassword.getText().toString();
                String name = etname.getText().toString();
                String types = ettype.getText().toString();
                String degree = etdegree.getText().toString();
                String address = etaddress.getText().toString();
                String phonenumber = etphonenumber.getText().toString();

                InsertData task = new InsertData();
                task.execute(id, passwd, name, types, degree, address, phonenumber);


                Toast.makeText(getApplicationContext(), etname.getText().toString() + "님 환영합니다.", Toast.LENGTH_LONG).show();
                idedit.setText(null);
                etpassword.setText(null);
                etname.setText(null);
                ettype.setText(null);
                etdegree.setText(null);
                etaddress.setText(null);
                break;


            case R.id.idButton:
                promptSpeechInput() ;
                break;
            case R.id.nameButton: // 스피치(Speech To Text)
                promptSpeechInput() ;
                break;
            case R.id.inAddress:  // Daum 주소를 얻기위해 이동하기
                Intent in_getData = new Intent(JoinActivity.this, AdselectActivity.class);
                startActivityForResult(in_getData, 0);
                break;

        }

    }

    /// Speech To Text 부분 ///
    private void promptSpeechInput() {
        Intent I_rec = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);            //intent 생성
        I_rec.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        I_rec.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());    //호출한 패키지
        I_rec.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");                            //음성인식 언어 설정
        I_rec.putExtra(RecognizerIntent.EXTRA_PROMPT, "말을 하세요.");                     //사용자에게 보여 줄 글자

        try {
            startActivityForResult(I_rec, 1);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "SPEECH를 지원하지 않습니다 ", Toast.LENGTH_LONG).show();
            Log.e("STT Error: ",e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        switch (requestCode) {       // startActivityForResult에서 넘긴 값을 처리하기 위함
            case 0:                 // Daum에서 받은 주소 값을 받기 위함.
                if (resultCode == RESULT_OK) {
                    String str;
                    str = intent.getStringExtra("address");
                    Log.e("잘 받았어 고마워 : ", str);
                    etaddress.setText(str);
                }
                break;
            case 1:
                String key = RecognizerIntent.EXTRA_RESULTS;
                ArrayList<String> mResult = intent.getStringArrayListExtra(key);        //인식된 데이터 list 받아옴.
                String[] result = new String[mResult.size()];            //배열생성. 다이얼로그에서 출력하기 위해
                mResult.toArray(result);                                    //    list 배열로 변환

                for (int i = 0; i < result.length; i++)
                    etname.setText(result[i]);
        }
    }

    RecognitionListener listener = new RecognitionListener() {

        @Override
        public void onReadyForSpeech(Bundle params) { }

        @Override
        public void onBeginningOfSpeech() { }

        @Override
        public void onRmsChanged(float rmsdB) { }

        @Override
        public void onBufferReceived(byte[] buffer) { }

        @Override
        public void onEndOfSpeech() { }

        @Override
        public void onError(int error) { }

        @Override
        public void onResults(Bundle results) {
            String key = "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = results.getStringArrayList(key);
            String[] result = new String[mResult.size()];
            mResult.toArray(result);

            for (int i = 0; i < result.length; i++)
                idedit.append(result[i] + "\n");

        }

        @Override
        public void onPartialResults(Bundle partialResults) { }

        @Override
        public void onEvent(int eventType, Bundle params) { }
    };


    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(JoinActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {


            String userid = (String) params[0];
            String passwd = (String) params[1];
            String name = (String) params[2];
            String types = (String) params[3];
            String degree = (String) params[4];
            String address = (String) params[5];
            String phonenumber = (String) params[6];

            Log.e("iD : ", (String) params[0]);
            Log.e("passwd : ", (String) params[1]);
            Log.e("name : ", (String) params[2]);
            Log.e("types : ", (String) params[3]);
            Log.e("degree : ", (String) params[4]);
            Log.e("address : ", (String) params[5]);
            Log.e("phonenumber : ", (String) params[6]);

            String serverURL = "http://13.124.195.151/pinsert.php";
            String postParameters = "userid=" + userid + "&passwd=" + passwd + "&name=" + name + "&types=" + types + "&degree=" + degree + "&address=" + address + "&phonenumber=" + phonenumber;
            Log.e("postParameters : ", postParameters);
            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }


    }
}