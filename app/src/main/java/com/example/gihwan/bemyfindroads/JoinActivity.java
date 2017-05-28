package com.example.gihwan.bemyfindroads;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by user on 2017-05-03.
 */

public class JoinActivity extends Activity{
    Button joincancel;
    Button btndone;
    EditText joinname;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        joincancel=(Button)findViewById(R.id.btnCancel);
        btndone=(Button)findViewById(R.id.btnDone);
        joinname=(EditText)findViewById(R.id.joinName);
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
    }
}
