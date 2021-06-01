package com.example.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.Socket;


public class SendToServer extends Activity {
    File filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    static String type = "";
    String bufRecv = "";
    String filename = filePath+"/captureFix.jpg";
    String result = "";

    Socket clientSocket;
    ProgressDialog progressDialog;

    protected void onCreate(Bundle savedInstanceState) {
        Log.e("page:","SendToServer");
        super.onCreate(savedInstanceState);

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
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (clientSocket.isConnected()) {
                    if (new File(filename).exists()) { //當圖檔存在
                        try {
                            byte[] buffer = new byte[2048];
                            int bytesRead;
                            //將圖像傳至PC
                            DataOutputStream dout=new DataOutputStream(clientSocket.getOutputStream());//輸出
                            RandomAccessFile fileOutStream = new RandomAccessFile(filename, "r");
                            fileOutStream.seek(0);

                            String bufSend = "face\n";
                            Log.e("transfer:","in");
                            String size = fileOutStream.length() + "\n";

                            dout.writeUTF(bufSend);//將字串傳入server

                            Thread.sleep(1000);
                            //clientSocket.getOutputStream().write(bufSend.getBytes());//類別
                            Log.e("send","buf");
                            clientSocket.getOutputStream().write(size.getBytes());//大小

                            Thread.sleep(1000);
                            dout.flush();
//                            if (bufRecv != null && bufRecv.equals("StartSend")) {   /*當server傳送開始傳送的data時，開始傳送圖片*/
//                                Log.e("[Progress]", "* Start sending file *");
//                                while ((bytesRead = fileOutStream.read(buffer)) != -1) {
//                                    clientSocket.getOutputStream().write(buffer, 0, bytesRead);
//                                }
//                                Log.e("[Progress]", "* Send completion *");
//                                fileOutStream.close();
//                                bufRecv = "";
//                            }
                        } catch (IOException ioe) {
                            Log.e("[Exception]", "IOException");
                            ioe.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
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
   /* private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
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
                    if (result != null && !result.equals("ERROR")) {
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
                    } else if (result.equals("ERROR")){
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
    };*/
   private Runnable readData = new Runnable() {
        @Override
        public void run() {
            try {

                InetAddress serverIp = InetAddress.getByName(MainActivity.serverIp);
                clientSocket = new Socket(serverIp, MainActivity.serverPort);
                DataInputStream br = new DataInputStream (clientSocket.getInputStream());

                while(clientSocket.isConnected()){

                    bufRecv = br.readUTF();
                    Log.e("[Buffread]",bufRecv);

                    if(bufRecv != null && !bufRecv.equals("StartSend")){
                        result = bufRecv;
                        Log.e("[result]:",result);
                        //Message mes = handler.obtainMessage();
                    }
                }
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
