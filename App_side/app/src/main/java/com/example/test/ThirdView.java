package com.example.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
    String bufRecv = "";
    String result = "";

    int hLoad,hSize;

    ProgressDialog progressDialog;
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("<PAGE>", "ThirdView");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Thread t = new Thread(readData);
        t.start();
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
                bufsend = "game";
                break;
        }
        if(icon.equals("0")){
//            info
        }else if(icon.equals("3")){
//            AlertDialog.Builder showCapture = new AlertDialog.Builder(this);
//            showCapture.setTitle("GAME");
//            showCapture.setPositiveButton("Start!",new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    if(clientSocket.isConnected()){
//
//                        Log.e("send","execute");
//                        try {
//                            DataOutputStream dout=new DataOutputStream(clientSocket.getOutputStream());
//                            dout.writeUTF("2");
//                            dout.writeUTF("game1;");
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }
//            });
//            showCapture.show();
            Intent intent = new Intent();
            intent.setClass(ThirdView.this,GameView.class);
            startActivity(intent);


        }
//            Intent intent = new Intent();
//            intent.setClass(ThirdView.this,MainActivity.class);//txt
//            startActivity(intent);
//        }

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
                DataInputStream br = new DataInputStream (clientSocket.getInputStream());
                Log.e("read","in");
                bufRecv = br.readUTF();
                Log.e("[Buffread]",bufRecv);

                if(bufRecv != null && !bufRecv.equals("StartSend")){
                    result = bufRecv;
                    Log.e("[result]:",result);
                    Message mes = handler.obtainMessage();
                    mes.what = 1;
                    handler.sendMessage(mes);
                }
            } catch (Exception e) {
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
