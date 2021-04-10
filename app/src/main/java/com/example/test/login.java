package com.example.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class login extends Activity {
    static EditText ip;
    static Button btn;
    static String serverIp;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ip = (EditText) findViewById(R.id.ip);
        btn = (Button) findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serverIp = ip.getText().toString();
                Intent intent = new Intent();
                intent.setClass(login.this , MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
