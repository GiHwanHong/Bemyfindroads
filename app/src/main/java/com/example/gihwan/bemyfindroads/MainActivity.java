package com.example.gihwan.bemyfindroads;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.content.ContentValues.TAG;


public class MainActivity extends Activity {

    Button SignUpButton;
    Button loginButton;
    EditText ID_user;


    //DBManager dbManger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivity(new Intent(this, SplashActivity.class));
        boolean isGrantStorage = grantExternalStoragePermission();

        ID_user = (EditText) findViewById(R.id.IdInput);     //로그인 아이디 입력 EditText
        SignUpButton = (Button) findViewById(R.id.signupButton); // 회원가입 버튼
        loginButton = (Button) findViewById(R.id.loginButton);     // 로그인 버튼

        //dbManger = new DBManager(getApplicationContext(), "PERS_LIST.db", null, 1);

        if (isGrantStorage) {
            SignUpButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
                    startActivity(intent);
                }
            });
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ChooseActivity.class);
                    startActivity(intent);
                    //String user = dbManger.select(ID_user);
                    Toast.makeText(getApplicationContext(), "환영합니다", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this, "권한인식실패", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean grantExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {

            if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, RECORD_AUDIO, READ_PHONE_STATE}, 1);

                return false;
            }
        } else {
            Toast.makeText(this, "External Storage Permission is Grant", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "External Storage Permission is Grant ");
            return true;
        }

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
}