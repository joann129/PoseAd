package com.example.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;

public class SecondView extends Activity{
    String icon = MainActivity.act;
    String filename = MainActivity.file_intent;
    File filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    File fileTransfer = new File(filePath,"captureFix.jpg");
    String text = "";
    int idcx,idcy;
    //String fileOriginal = "//MicroSD//DCIM//capture.jpg"; // 照片保存路徑
    //String fileTransfer = "//MicroSD//DCIM//captureFix.jpg"; // 照片保存路徑
//    public void recognize(String icon){
//
//        switch(icon){
//            case "0":
//                text = "info";
//                break;
//            case "1":
//                text = "ticket";
//                break;
//            case "2":
//                text = "photo";
//                break;
//            default:
//                break;
//        }
//        AlertDialog.Builder detectDialog = new AlertDialog.Builder(SecondView.this);
//        detectDialog.setTitle(text);
//        detectDialog.setNegativeButton("Try again", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent();
//                intent.setClass(SecondView.this, MainActivity.class);
//                startActivity(intent);
//                //關閉畫面
//                SecondView.this.finish();
//            }
//        });
//        detectDialog.show();
//    }

    protected void onCreate(Bundle savedInstanceState) {
        Log.e("<PAGE>", "SecondView");
        super.onCreate(savedInstanceState);
        Log.e("Socket",MainActivity.serverIp);

        //recognize(icon);
        setContentView(new FaceView(this));

    }
    public class FaceView extends View{
        int imageWidth,imageHeight;
        int numberOfFace = 10;
        FaceDetector myFaceDetect;
        FaceDetector.Face[] myFace;
        float EyeDistance;
        Bitmap myBitmap;
        public AlertDialog prsd;
        int numberOfFaceDetected;
        int r_new = 0;
        int s_new = 0;
        int dis_s,dis_t;
        public FaceView(Context context) {
            super(context);

            BitmapFactory.Options BFO = new BitmapFactory.Options();
            BFO.inPreferredConfig = Bitmap.Config.RGB_565; //更改為565格式

            myBitmap = BitmapFactory.decodeFile(filename,BFO);


            imageHeight = myBitmap.getHeight();
            imageWidth = myBitmap.getWidth();
            myFaceDetect = new FaceDetector(imageWidth,imageHeight,numberOfFace);

            myFace = new FaceDetector.Face[numberOfFace];
            Bundle bundle = getIntent().getExtras();

            String dcx = bundle.getString("dcx");
            String dcy = bundle.getString("dcy");

            idcx = Integer.parseInt(dcx);
            idcy = Integer.parseInt(dcy);
            Log.e("height", String.valueOf(imageHeight));
            Log.e("width", String.valueOf(imageWidth));
            numberOfFaceDetected = myFaceDetect.findFaces(myBitmap,myFace);/*格式無法初始化*/
            Log.e("de","in");
            if(numberOfFaceDetected == 0){  //偵測人臉失敗
                AlertDialog.Builder detectDialog = new AlertDialog.Builder(SecondView.this);
                detectDialog.setTitle("Failure detection!");
                detectDialog.setNegativeButton("Try again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setClass(SecondView.this,MainActivity.class);
                        startActivity(intent);
                        SecondView.this.finish();
                    }
                });
                detectDialog.show();
            }else {
                dis_s = (idcx * idcx) + (idcy * idcy);
                int dis_tem = dis_s;
                for (int i = 0; i < numberOfFaceDetected; i++) {
                    FaceDetector.Face face = myFace[i];
                    PointF myMidPoint = new PointF();
                    face.getMidPoint(myMidPoint);
                    EyeDistance = face.eyesDistance();//兩眼之間的距離

                    int r, s, k;
                    int mx, my;
                    //
                    mx = (int) myMidPoint.x;
                    my = (int) myMidPoint.y;

                    r = (int) (myMidPoint.x - (EyeDistance * 2));
                    s = (int) (myMidPoint.y - (EyeDistance * 2));
                    k = (int) (EyeDistance);

                    dis_t = (idcx - mx) * (idcx - mx) + (idcy - my) * (idcy - my);
                    if (dis_t < dis_tem) {
                        r_new = r;
                        s_new = s;
                        dis_tem = dis_t;
                    }
                    try {
                        Bitmap newbm = Bitmap.createBitmap(myBitmap, r_new, s_new, 4 * k, 4 * k);
                        FileOutputStream fop;
                        fop = new FileOutputStream(fileTransfer);
                        newbm.compress(Bitmap.CompressFormat.JPEG, 90, fop);
                        fop.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent();
                    intent.setClass(SecondView.this, SendToServer.class);
                    startActivity(intent);
                    SecondView.this.finish();

                }
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        SecondView.this.finish();
    }
}
