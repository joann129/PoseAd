package com.example.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class chat extends Activity implements CompoundButton.OnCheckedChangeListener {
    private List<Msg> msgList = new ArrayList<>();
    Msg revmsg;
    ImageView image;
    TextView name;
    Socket clientSocket;
    EditText chatContent;
    Button submit;
    Button back;
    String result = SendToServer.type;
    String bufRecv = "";
    String content = "";
    RecyclerView recyclerView;
    CheckBox checkBox;
    String type = SendToServer.type;
    String transfer = "no";
    int len;
    byte[] bytes = new byte[1024];
    private MsgAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.e("<PAGE>", "chat");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passtxt);

        chatContent = (EditText)findViewById(R.id.chatContent);
        image = (ImageView) findViewById(R.id.person);
        name = (TextView) findViewById(R.id.name);
        submit = (Button) findViewById(R.id.submit);
        back = (Button) findViewById(R.id.back);
        recyclerView = (RecyclerView) findViewById(R.id.recycle) ;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        checkBox = (CheckBox)findViewById(R.id.checkbox) ;
        checkBox.setOnCheckedChangeListener(this);
        adapter = new MsgAdapter(msgList);
        recyclerView.setAdapter(adapter);
        chatContent.getBackground().setAlpha(240);
        if("tzutu".equals(type)){
            image.setImageDrawable(getResources().getDrawable(R.drawable.yu));
            name.setText("子瑜\nTZUYU");
        }else if("mina".equals(type)){
            image.setImageDrawable(getResources().getDrawable(R.drawable.mina));
            name.setText("MINA");
        }else{
            image.setImageDrawable(getResources().getDrawable(R.drawable.momo));
            name.setText("MOMO");
        }

        Thread t = new Thread(readData);
        t.start();
//        adapter.notifyItemInserted(msgList.size()-1);//當有訊息時，重新整理ListView中的顯示
//        recyclerView.scrollToPosition(msgList.size()-1);
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
                        if(!"".equals(content)) {
                            String txt = "chat;" + content + ";" + transfer + ";" + result;
                            //dout.writeUTF("2");
                            dout.writeUTF(txt);
                            Log.e("[txt]", txt);
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
                        }
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
                String txt = "chat;";
                //DataInputStream br = new DataInputStream (clientSocket.getInputStream());
                DataOutputStream d2 = new DataOutputStream(clientSocket.getOutputStream());
                InputStream inputStream = clientSocket.getInputStream();
                //StringBuilder sb = new StringBuilder();
                d2.writeUTF("2");
                d2.writeUTF(txt);
                while(clientSocket.isConnected() && (len = inputStream.read(bytes)) != -1){
//                    DataInputStream br = new DataInputStream (clientSocket.getInputStream());
                    Log.e("read","in");
//                    bufRecv = br.readUTF();
                    Thread.sleep(1500); //先做延遲再去接收，測試訊息來能否運作
                    bufRecv = new String(bytes, 0, len, "UTF-8");
                    Log.e("[Buffread]",bufRecv);
                    revmsg = new Msg(bufRecv, Msg.TYPE_RECEIVED);
//                    Log.e("[Buffread]","卡住");
                    msgList.add(revmsg);
                    adapter.notifyItemInserted(msgList.size()-1);//當有訊息時，重新整理ListView中的顯示
                    recyclerView.scrollToPosition(msgList.size()-1);
//                    Log.e("[Buffread]","卡住");
//

                    //sb.append(new String(bytes, 0, len, "UTF-8"));

                    //將ListView定位到最後一行
                }
                Log.e("[read]","out");
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
