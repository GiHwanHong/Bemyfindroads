package com.example.gihwan.bemyfindroads;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by user on 2017-05-03.
 */
public class JoinActivity extends Activity{
    Button joincancel;
    Button btndone;
    EditText joinname;
    Button idbutton;
    EditText idedit;
    Button passbutton;
    EditText etpassword;
    Intent i1;
    SpeechRecognizer speech1;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        joincancel=(Button)findViewById(R.id.btnCancel);
        btndone=(Button)findViewById(R.id.btnDone);
        joinname=(EditText)findViewById(R.id.joinName);
        passbutton=(Button)findViewById(R.id.passButton);
        idedit=(EditText)findViewById(R.id.idEdit);
        etpassword=(EditText)findViewById(R.id.etPassword);
        joincancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"취소되었습니다.",Toast.LENGTH_LONG).show();
                finish();
            }
        });
        btndone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),joinname.getText().toString()+"님 환영합니다.",Toast.LENGTH_LONG).show();
            }
        });
        idbutton=(Button)findViewById(R.id.idButton);
        idbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);            //intent 생성
                i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());    //호출한 패키지
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");                            //음성인식 언어 설정
                i.putExtra(RecognizerIntent.EXTRA_PROMPT, "말을 하세요.");                     //사용자에게 보여 줄 글자
                startActivityForResult(i, 1000);

            }
        });
        passbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode == 1000 ) {
            String key = RecognizerIntent.EXTRA_RESULTS;
            ArrayList<String> mResult = data.getStringArrayListExtra(key);        //인식된 데이터 list 받아옴.다른 액티비티에서의 결과를 가져온다.
            String[] result = new String[mResult.size()];            //배열생성. 다이얼로그에서 출력하기 위해
            mResult.toArray(result);                                    //    list 배열로 변환

            for(int i = 0 ;  i < 1; i++) {
                idedit.append(result[i] + "\n");
            }
        }
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
            String key= "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = results.getStringArrayList(key);
            String[] result = new String[mResult.size()];
            mResult.toArray(result);

            for(int i = 0 ;  i < result.length; i++)
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