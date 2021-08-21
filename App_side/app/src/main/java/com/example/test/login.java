package com.example.test;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class login extends Activity {
    static EditText ip;
    static Button btn;
    static String serverIp;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);
        ip = (EditText) findViewById(R.id.ip);
        btn = (Button) findViewById(R.id.btn);
        getPermission();
        Intent intent = new Intent();
        intent.setClass(login.this , MainActivity.class);

        startActivity(intent);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //serverIp = ip.getText().toString();

                Intent intent = new Intent();
                intent.setClass(login.this , MainActivity.class);

                startActivity(intent);

            }
        });
    }
    public void getPermission(){
        List<String> permissionList = new ArrayList<>();
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.CAMERA);
        }
        if(!permissionList.isEmpty()){
            ActivityCompat.requestPermissions(this,permissionList.toArray(new String[permissionList.size()]),1002);
        }
    }


    public void onRequestPermissionsResult(int requestCode, String[] permission, int[] grantResult){
        super.onRequestPermissionsResult(requestCode,permission,grantResult);
        switch (requestCode){
            case 1002:
                if (grantResult.length > 0){
                    for (int i=0;i<grantResult.length;i++){
                        if(grantResult[i] == PackageManager.PERMISSION_DENIED){
                            Toast.makeText(login.this,permission[i] + "拒絕", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
        }
    }
}
