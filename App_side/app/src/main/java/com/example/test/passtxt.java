package com.example.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class passtxt extends Activity {
    EditText chatName;	// 使用者
    EditText chatContent;	// 訊息內容
    Button submit;
    Button back;

    Socket clientSocket;	// 客戶端socket
    String name;
    String content;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.e("<PAGE>", "Passtxt");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_txt);
        chatName = (EditText)findViewById(R.id.chatName);
        chatContent = (EditText)findViewById(R.id.chatContent);
        submit = (Button) findViewById(R.id.submit);
        back = (Button) findViewById(R.id.back);


        Thread t = new Thread(readData);
        t.start();

        submit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (clientSocket.isConnected()){
                    name = chatName.getText().toString();
                    content = chatContent.getText().toString();

                    String chat = name + ": " + content;
                    Log.e("[Chat]",  chat);


                    try {
                        DataOutputStream dout = new DataOutputStream(clientSocket.getOutputStream());
                        String txt = "text;"+chat;
                        dout.writeUTF("2");
                        dout.writeUTF(txt);
//                        clientSocket.getOutputStream().write("2".getBytes());
//                        clientSocket.getOutputStream().write(txt.getBytes());
                        //clientSocket.getOutputStream().write(chat.getBytes());
                        chatContent.setText("");

                        AlertDialog.Builder chatSend = new AlertDialog.Builder(passtxt.this);
                        chatSend.setTitle("Send");
                        chatSend.setMessage(chatContent.getText().toString());
                        chatSend.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                chatContent.setText("");
                                dialog.dismiss();
                            }
                        });
                        //chatSend.show();

                    } catch (IOException ioe) {
                        Log.e("[Exception]", "IOException");
                        ioe.printStackTrace();
                    }
                }

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(passtxt.this, MainActivity.class);
                startActivity(intent);
                //關閉畫面
                passtxt.this.finish();
            }
        });
    }
    private Runnable readData = new Runnable() {
        public void run() {
            try {
                InetAddress serverIp = InetAddress.getByName(MainActivity.serverIp);
                clientSocket = new Socket(serverIp, MainActivity.serverPort);
                Thread.sleep(3000);

            }
            catch (IOException ioe) {
                Log.e("[Exception]", "IOException");
                ioe.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
