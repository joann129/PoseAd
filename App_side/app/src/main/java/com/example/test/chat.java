package com.example.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class chat extends Activity implements CompoundButton.OnCheckedChangeListener {
    private List<Msg> msgList = new ArrayList<>();
    Socket clientSocket;
    EditText chatContent;
    Button submit;
    Button back;
    String result = SendToServer.type;
    String bufRecv = "";
    String content = "";
    RecyclerView recyclerView;
    CheckBox checkBox;
    String transfer = "";
    private MsgAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.e("<PAGE>", "chat");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passtxt);
        chatContent = (EditText)findViewById(R.id.chatContent);
        submit = (Button) findViewById(R.id.submit);
        back = (Button) findViewById(R.id.back);
        recyclerView = (RecyclerView) findViewById(R.id.recycle) ;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        checkBox = (CheckBox)findViewById(R.id.checkbox) ;
        checkBox.setOnCheckedChangeListener(this);
        Thread t = new Thread(readData);
        t.start();

        submit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                if (clientSocket.isConnected()){
                    content = chatContent.getText().toString();
                    if(!"".equals(content)){
                        Msg msg = new Msg(content, Msg.TYPE_SENT);
                        msgList.add(msg);
                        adapter.notifyItemInserted(msgList.size()-1);//當有訊息時，重新整理ListView中的顯示
                        recyclerView.scrollToPosition(msgList.size()-1);//將ListView定位到最後一行
                    }
                    Log.e("[Chat]",  content);


                    try {
                        DataOutputStream dout = new DataOutputStream(clientSocket.getOutputStream());
                        String txt = "chat;"+result+";"+content+";"+transfer;
                        //dout.writeUTF("2");
                        dout.writeUTF(txt);
                        Log.e("[txt]",txt);
//                        clientSocket.getOutputStream().write("2".getBytes());
//                        clientSocket.getOutputStream().write(txt.getBytes());
                        //clientSocket.getOutputStream().write(chat.getBytes());
                        chatContent.setText("");

                        AlertDialog.Builder chatSend = new AlertDialog.Builder(chat.this);
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
                intent.setClass(chat.this, MainActivity.class);
                startActivity(intent);
                //關閉畫面
                chat.this.finish();
            }
        });
    }
    private Runnable readData = new Runnable() {
        public void run() {
            try {
                InetAddress serverIp = InetAddress.getByName(MainActivity.serverIp);
                clientSocket = new Socket(serverIp, MainActivity.serverPort);
                DataInputStream br = new DataInputStream (clientSocket.getInputStream());
                DataOutputStream d2 = new DataOutputStream(clientSocket.getOutputStream());
                d2.writeUTF("2");
                while(clientSocket.isConnected()){
//                    DataInputStream br = new DataInputStream (clientSocket.getInputStream());
                    Log.e("read","in");
                    bufRecv = br.readUTF();
                    Log.e("[Buffread]",bufRecv);
                    Msg revmsg = new Msg(bufRecv, Msg.TYPE_RECEIVED);
                    msgList.add(revmsg);
                    adapter.notifyItemInserted(msgList.size()-1);//當有訊息時，重新整理ListView中的顯示
                    recyclerView.scrollToPosition(msgList.size()-1);//將ListView定位到最後一行
                }
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

    public void onDestroy() {
        super.onDestroy();
        chat.this.finish();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(checkBox.isChecked() == false){
            transfer = "no";
        }
        else{
            transfer = "yes";
        }
    }
}
