package com.example.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Base64;


public class SendToServer extends Activity {
    File filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    static String type = "";
    String bufRecv = "";
    String filename = filePath+"/captureFix.jpg";
    File f = new File(filename);
    String result = "";

    Socket clientSocket;
    Socket picSocket;
    ProgressDialog progressDialog;

    protected void onCreate(Bundle savedInstanceState) {
        Log.e("page:","SendToServer");
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        try {

        } catch (Exception e) {
            e.printStackTrace();
        }

        Thread t = new Thread(readData);
        t.start();  /*開始讀取server傳穿送的執行序*/
        BitmapFactory.Options BFO = new BitmapFactory.Options();
        BFO.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap myBitmap = BitmapFactory.decodeFile(filename, BFO);

        ImageView image = new ImageView(this);
        image.setImageBitmap(myBitmap);
        AlertDialog.Builder showCapture = new AlertDialog.Builder(this);
        showCapture.setMessage("Capture image");
        showCapture.setView(image);
        showCapture.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Message mes = new Message();
                mes.what = 0;
                handler.sendMessage(mes);
                Log.e("handler","start");
                if (clientSocket.isConnected()) {
                    if (new File(filename).exists()) { //當圖檔存在
                        try {
                            InetAddress serverIp = InetAddress.getByName(MainActivity.serverIp);//圖片
                            picSocket = new Socket(serverIp, 5050);
                            byte[] buffer = new byte[1024*20];
                            FileInputStream fis = new FileInputStream(f);
                            OutputStream os = clientSocket.getOutputStream();
                            OutputStream pos = picSocket.getOutputStream();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            int bytesRead;
                            //將圖像傳至PC
                            DataOutputStream dout=new DataOutputStream(clientSocket.getOutputStream());//輸出
                            RandomAccessFile fileOutStream = new RandomAccessFile(filename, "r");
                            fileOutStream.seek(0);
                            dout.writeUTF("2");
//                            dout.writeUTF("game1;");
//                            Log.e("no","face");
                            String bufSend = "facer:";
                            Log.e("transfer:","in");
//                            String size = fileOutStream.length() + "\n";
                            dout.writeUTF(bufSend);//將字串傳入server
                            Log.e("send",bufSend);
                            Thread.sleep(1000);
                            //clientSocket.getOutputStream().write(bufSend.getBytes());//類別
                            Log.e("send","buf");
                            //clientSocket.getOutputStream().write(size.getBytes());//大小
                            Thread.sleep(1000);



                            if (bufRecv != null && bufRecv.equals("StartSend")) {   /*當server傳送開始傳送的data時，開始傳送圖片*/
                                Log.e("[Progress]", "* Start sending file *");
                                DataOutputStream dpos = new DataOutputStream(pos);
                                dpos.writeUTF("3");
                                while ((bytesRead = fis.read(buffer,0,buffer.length)) > 0) {
                                    baos.write(buffer, 0, bytesRead);
                                }
                                baos.flush();
                                Log.e("傳送圖片","開始");
                                PrintWriter pw = new PrintWriter(pos);
                                pw.write(Base64.getEncoder().encodeToString(baos.toByteArray()));
                                pw.flush();

                                Log.e("[Progress]", "* Send completion *");
//                                os.close();
//                                fis.close();
//                                pw.close();
                                fileOutStream.close();
//                                baos.close();

                                bufRecv = "";
                                picSocket.close();
                            }
                        } catch (IOException | InterruptedException ioe) {
                            Log.e("[Exception]", "IOException");
                            ioe.printStackTrace();
                        }
                    } else {
                        Log.e("[Error]", "File doesn't exist");
                    }
                }
                dialog.dismiss();
            }
        });
        showCapture.show();
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            Log.e("handler","in");
            switch(msg.what) {
                case 0:
                    progressDialog = new ProgressDialog(SendToServer.this);
                    progressDialog.setTitle("Recognizing...");
                    progressDialog.setMessage("Please wait");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setIndeterminate(false);
                    progressDialog.setProgress(100);
                    progressDialog.show();
                    break;
                case 1:
                    progressDialog.dismiss();
                    AlertDialog.Builder recognizeDialog = new AlertDialog.Builder(SendToServer.this);
                    recognizeDialog.setTitle("Recognize result");
                    if (result != null && !result.equals("unknown")) {
                        type = result;
                        recognizeDialog.setMessage(result);
                        recognizeDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setClass(SendToServer.this, MainActivity.class );
                                startActivity(intent);
                                //關閉畫面
                                SendToServer.this.finish();
                                dialog.dismiss();
                                Log.e("[TEST]","finish");
                            }
                        });
                    } else if (result.equals("unknown")){
                        recognizeDialog.setMessage("Failed.");
                        recognizeDialog.setNegativeButton("Try again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setClass(SendToServer.this, MainActivity.class);
                                startActivity(intent);
                                //關閉畫面
                                SendToServer.this.finish();
                                dialog.dismiss();
                            }
                        });
                    }
                    recognizeDialog.show();
                    break;
                default:
            }
            super.handleMessage(msg);

        }
    };
   private Runnable readData = new Runnable() {

        @Override
        public void run() {
            try {
// 手機端連線時要先傳送'2'
                InetAddress serverIp = InetAddress.getByName(MainActivity.serverIp);
                clientSocket = new Socket(serverIp, MainActivity.serverPort);
                DataInputStream br = new DataInputStream (clientSocket.getInputStream());

                while(clientSocket.isConnected()){
//                    DataInputStream br = new DataInputStream (clientSocket.getInputStream());
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

                }
                Log.e("[Buffread]","no exit");
            } catch (IOException e) {

                e.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    public void onDestroy() {
        super.onDestroy();
        SendToServer.this.finish();
    }
}
