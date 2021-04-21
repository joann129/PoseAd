package com.example.test;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.app.ActivityCompat;

public class login extends Activity {
    static EditText ip;
    static Button btn;
    static String serverIp;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);
        ip = (EditText) findViewById(R.id.ip);
        btn = (Button) findViewById(R.id.btn);
        getPermissionCamera();
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
    public void getPermissionCamera(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1);
        }
    }
}
