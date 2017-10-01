package com.example.gihwan.bemyfindroads;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

/**
 * Created by user on 2017-05-03.
 */
public class JoinActivity extends Activity {


    EditText idedit, etpassword, etname;

    EditText etaddress, etphonenumber, etresult;
    EditText ettype, etgrade;
    SpeechRecognizer sr;
    DBManager dbManger;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        dbManger = new DBManager(getApplicationContext(), "PERS_LIST.db", null, 1);

        idedit = (EditText) findViewById(R.id.idEdit);      // 아이디 입력 EditText
        etpassword = (EditText) findViewById(R.id.etPassword);
        etname = (EditText) findViewById(R.id.joinName); // 가입자 이름

        ettype = (EditText) findViewById(R.id.Type);
        etgrade = (EditText) findViewById(R.id.Grade);

        etaddress = (EditText) findViewById(R.id.Address);
        etphonenumber = (EditText) findViewById(R.id.Phonenumber);
        etresult = (EditText) findViewById(R.id.Result);

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
                dbManger.insert("insert into PERS_LIST values('" + idedit.getText().toString() + "', '" + etpassword.getText().toString() + "', '"
                        + etname.getText().toString() + "', '" + ettype.getText().toString() + "', '" + etgrade.getText().toString() + "', '"
                        + etaddress.getText().toString() + "', '" + etphonenumber.getText().toString() + "');");

                etresult.setText(dbManger.PrintData());
                Toast.makeText(getApplicationContext(), etname.getText().toString() + "님 환영합니다.", Toast.LENGTH_LONG).show();
                idedit.setText(null);
                etpassword.setText(null);
                etname.setText(null);
                ettype.setText(null);
                etgrade.setText(null);
                etaddress.setText(null);
                etphonenumber.setText(null);
                break;
            case R.id.idButton: // 스피치
                promptSpeechInput();
                break;
            case R.id.btnInfo:
                etresult.setText(dbManger.PrintData());
                break;
        }

    }

    // STT 관련 소스 //
    private void promptSpeechInput() {
        Intent I_rec = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);            //intent 생성
        I_rec.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        I_rec.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());    //호출한 패키지
        I_rec.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");                            //음성인식 언어 설정
        I_rec.putExtra(RecognizerIntent.EXTRA_PROMPT, "말을 하세요.");                     //사용자에게 보여 줄 글자

        try {
            startActivityForResult(I_rec, 1000);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "SPEECH를 지원하지 않습니다 ", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            String key = RecognizerIntent.EXTRA_RESULTS;
            ArrayList<String> mResult = data.getStringArrayListExtra(key);        //인식된 데이터 list 받아옴.
            String[] result = new String[mResult.size()];            //배열생성. 다이얼로그에서 출력하기 위해
            mResult.toArray(result);                                    //    list 배열로 변환

            for (int i = 0; i < result.length; i++)
                idedit.setText(result[i]);
    }

    RecognitionListener listener = new RecognitionListener() {

        @Override
        public void onReadyForSpeech(Bundle params) {

        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int error) {

        }

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
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    };
}