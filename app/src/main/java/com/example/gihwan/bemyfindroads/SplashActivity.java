package com.example.gihwan.bemyfindroads;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Boolean.TRUE;


/**
 * Created by GiHwan on 2017. 5. 3..
 */

public class SplashActivity extends Activity{
    private Timer timer;
    private ProgressBar progressBar;
    private int i=0;
    TextView textView;
    final long period = 10;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        textView=(TextView)findViewById(R.id.textView);
        textView.setText("");

        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //this repeats every 100 ms
                 if(i<100){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(String.valueOf(i)+"%");
                        }
                    });
                    progressBar.setProgress(i);
                    i++;
                }
                if(i==100) {
                    timer.cancel();
                    finish();
                }
            }
        }, 0, period);
    }
}
