package com.example.test;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import static android.content.ContentValues.TAG;

public class ThirdView extends Activity {
    String icon = MainActivity.act;
    String type = SendToServer.type;
    String bufsend = "";
    Socket clientSocket;

    int hLoad,hSize;

    ProgressDialog progressDialog;
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("<PAGE>", "ThirdView");
        super.onCreate(savedInstanceState);
        switch(icon){
            case "0":
                bufsend = "info";
                break;
            case "1":
                bufsend = "ticket";
                break;
            case "2":
                bufsend = "photo";
                break;
            case "3":
                bufsend = "txt";
                break;
        }
        if(icon.equals("0")){
//            info
        }else if(icon.equals("3")){
            Intent intent = new Intent();
            intent.setClass(ThirdView.this,MainActivity.class);//txt
            startActivity(intent);
        }

    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Log.e("[TE...0ST]","what(0)");
                    progressDialog = new ProgressDialog(ThirdView.this);
                    progressDialog.setTitle("Downloading...");
                    progressDialog.setMessage("Please wait");
                    //progressDialog.setMessage("Please wait...\nSaving in " + filePath);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialog.setIndeterminate(false);
                    progressDialog.setProgress(100);
                    //progressDialog.show();
                    break;
                case 1:
                    Log.e("[Download]",hLoad + "/" + hSize);
                    if (hLoad == hSize) {
                        Log.e("[Download]","Finish");
                        Intent intent = new Intent();
                        intent.setClass(ThirdView.this, FinalView.class);
                        startActivity(intent);
                        //關閉畫面
                        ThirdView.this.finish();
                        progressDialog.dismiss();
                    } else
                        progressDialog.setProgress((int) ((float) hLoad / hSize * 100));
                    break;
                default:
            }
            super.handleMessage(msg);
        }
    };
    private Runnable readData = new Runnable() {
        @Override
        public void run() {
            try{
                InetAddress serverIp = InetAddress.getByName(MainActivity.serverIp);
                clientSocket = new Socket(serverIp, MainActivity.serverPort);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "--- ThirdView(onDestroy)  ---");
        /*
        Intent intent = new Intent();
        intent.setClass(ThirdView.this, SendImage.class);
        startActivity(intent);
        */
        ThirdView.this.finish();
    }
}
