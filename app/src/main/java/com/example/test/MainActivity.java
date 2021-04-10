package com.example.test;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.core.app.ActivityCompat;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity implements SurfaceHolder.Callback {
    static String serverIp = login.serverIp;
    static int serverPort = 5050;

    SurfaceView mySurfaceView;
    Camera myCamera;
    SurfaceHolder holder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("<PAGE>", "MainActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPermissionCamera();
        mySurfaceView = findViewById(R.id.SurFaceView1);
        holder = mySurfaceView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (myCamera == null) {
            myCamera = Camera.open();
            myCamera.setDisplayOrientation(90);

            try {
                myCamera.setPreviewDisplay(holder);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
    @Override
    public void surfaceChanged( SurfaceHolder holder, int format, int width, int height) {
        Camera.Parameters params = myCamera.getParameters();

        params.setPictureFormat(PixelFormat.JPEG);
        params.setPreviewSize(640, 480);

        params.setPictureSize(640, 480);

        myCamera.setParameters(params);
        myCamera.startPreview();
    }
    @Override
    public void surfaceDestroyed( SurfaceHolder holder) {
        myCamera.stopPreview();
        myCamera.release();
        myCamera = null;
    }
    Camera.AutoFocusCallback afcb = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            // TODO Auto-generated method stub
            if (success) {
                // 設置參數,並拍照
                Camera.Parameters params = myCamera.getParameters();
                params.setPictureFormat(PixelFormat.JPEG);

                params.setPreviewSize(640, 480);

                params.setPictureSize(640, 480);

                myCamera.setParameters(params);
                myCamera.takePicture(null, null, jpeg);
            }
        }
    };
    public void getPermissionCamera(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1);
        }
    }
}