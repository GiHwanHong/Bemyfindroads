package com.example.gihwan.bemyfindroads;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
    Button SignUpButton;
    Button loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivity(new Intent(this, SplashActivity.class));

        SignUpButton=(Button)findViewById(R.id.signupButton);
        SignUpButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent=new Intent(getApplicationContext(),JoinActivity.class);
                startActivity(intent);
            }
        });
        loginButton=(Button)findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ChooseActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(),"환영합니다",Toast.LENGTH_LONG).show();
            }
        });

    }
}