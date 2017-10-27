package com.example.gihwan.bemyfindroads;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.content.ContentValues.TAG;


public class MainActivity extends Activity {


    EditText ID_user;
    int flag = 0;
    LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivity(new Intent(this, SplashActivity.class));
        boolean isGrantStorage = grantExternalStoragePermission();

        ID_user = (EditText) findViewById(R.id.IdInput);     //로그인 아이디 입력 EditText
        locationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            alertCheckGPS();
        }
        if (isGrantStorage) {
            flag = 0;
        } else {
            flag = 1;
        }
    }
    public void Btn_main(View v){
        switch (v.getId()){
            case R.id.signupButton:
                Intent SignupIntent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(SignupIntent);
                break;
            case R.id.loginButton:
                Intent LoginIntent = new Intent(getApplicationContext(), ChooseActivity.class);
                startActivity(LoginIntent);
                Toast.makeText(getApplicationContext(), "환영합니다", Toast.LENGTH_LONG).show();
                break;
        }
    }

    // GPS 켜기 - 앱에서 자동으로 설정해주면 더 좋겠지만 개인정보 때문에 구글에서 막아뒀다 그래서 설정창으로 가서 다시 해야한다. //
    private void alertCheckGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setMessage("위치 서비스 설정")
                .setCancelable(false)  // 뒤로 버튼 클릭시 취소 가능 설정
                .setView(inflater.inflate(R.layout.activity_gpsdialog_layout,null))
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