package com.example.gihwan.bemyfindroads;

import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

/**
 * Created by GiHwan on 2017. 5. 3..
 */

public class NaviActivity extends AppCompatActivity {
    SpeechRecognizer sr;
    TextToSpeech tts;
    EditText msg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi);


    }
}
