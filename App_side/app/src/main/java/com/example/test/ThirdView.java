package com.example.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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
    String urlstring = "";
    Button back;
    ImageView info;
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
                bufsend = "video";
                break;
            case "3":
                bufsend = "game";
                break;
            case "4":
                bufsend = "txt";
                break;
            case "5":
                bufsend = "dance";
                break;
            default:
                break;
        }
        if(icon.equals("0")){
//            info
            setContentView(R.layout.activity_info);
            info = (ImageView) findViewById(R.id.people_info);
            back = (Button) findViewById(R.id.back);
            if(type.equals("momo")){
                info.setImageDrawable(getResources().getDrawable(R.drawable.momo_info));
            }else if(type.equals("tzuyu")){
                info.setImageDrawable(getResources().getDrawable(R.drawable.tzuyu_info));
            }else if(type.equals("mina")){
                info.setImageDrawable(getResources().getDrawable(R.drawable.mina_info));
            }
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(ThirdView.this,MainActivity.class);
                    startActivity(intent);
                }
            });


        }else if(icon.equals("1")){
            urlstring = "https://www.twicejapan.com/feature/twicelights?lang=zh-tw";
            Uri uri = Uri.parse(urlstring);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
        else if(icon.equals("3")){
            Intent intent = new Intent();
            intent.setClass(ThirdView.this,chat.class);
            startActivity(intent);
        }else if(icon.equals("4")){ //game
            Intent intent = new Intent();
            intent.setClass(ThirdView.this,GameView.class);
            startActivity(intent);
        }else if(icon.equals("5")){ //dance
            if(clientSocket.isConnected()){
                try {
                    Log.e("send","execute");
                    DataOutputStream dout=new DataOutputStream(clientSocket.getOutputStream());
                    dout.writeUTF("2");
                    dout.writeUTF("game2;");
                    Log.e("game","start");
                    Thread.sleep(2000);
                    if (bufRecv!= null){
                        AlertDialog.Builder dialog = new AlertDialog.Builder(ThirdView.this);
                        dialog.setTitle("有人在玩，請稍後...");
                        dialog.setNegativeButton("Try again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setClass(ThirdView.this,MainActivity.class);
                                startActivity(intent);
                                ThirdView.this.finish();
                            }
                        });
                        dialog.show();
                    }

                    else{
                        Intent intent = new Intent();
                        intent.setClass(ThirdView.this,MainActivity.class);
                        startActivity(intent);
                        ThirdView.this.finish();
                    }

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

            }
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
                        intent.setClass(ThirdView.this, MainActivity.class);
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
