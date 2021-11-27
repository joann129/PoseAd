package com.example.test;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity implements SurfaceHolder.Callback {
    static String serverIp ="192.168.0.105";
    static int serverPort = 5050;
    static String act = "";
    static String file_intent = "";
    File filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    SurfaceView mySurfaceView;
    Camera myCamera;
    SurfaceHolder holder;
    boolean isClicked = false;
    Gallery iconZone;
    Intent intent;

    Camera.PictureCallback jpeg = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                Log.e("jpeg","in");
                Bitmap bm = BitmapFactory.decodeByteArray(data, 0,
                        data.length);
                File file = new File(filePath,"catch.jpg");
                file_intent = filePath+"/"+file.getName();
                Log.e("catch","in");
                //FileOutputStream bos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                Log.e("catch","in");
                Matrix matrix = new Matrix();
                matrix.reset();
                matrix.postRotate(90);
                Bitmap bMapRotate = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
                bm = bMapRotate;

                bm.compress(Bitmap.CompressFormat.JPEG, 100, bos); // 將圖片壓縮到流中
                bos.flush(); // 輸出
                bos.close(); // 關閉
                intent.setClass(MainActivity.this, SecondView.class);

                Log.e("intent","in");
                if (act.equals("6") ) {
                    intent.setClass(MainActivity.this, ThirdView.class);
                }

                else {
                    intent.setClass(MainActivity.this, SecondView.class);
                    Log.e("intent","in");
                }
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("<PAGE>", "MainActivity");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mySurfaceView = findViewById(R.id.SurFaceView1);
        holder = mySurfaceView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        myDragEventListener mDragListen = new myDragEventListener();
        mySurfaceView.setOnDragListener(mDragListen);

        iconZone = findViewById(R.id.iconZone);
        iconZone.setAdapter(new ImageAdapter(this));// Sets a long click listener for the ImageView using an anonymous listener object that

        intent = new Intent();

    }
    public class ImageAdapter extends BaseAdapter{
        private  int[] icons = {
                R.drawable.icon_info,
                R.drawable.icon_ticket,
                R.drawable.icon_video,
                R.drawable.icon_chat,
                R.drawable.game_icon,
                R.drawable.dance_icon
        };
        private Context mCoNtext;

        public ImageAdapter(Context c){
            mCoNtext = c;
            iconZone.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    String s = Integer.toString(position);
                    act = s;
                    Log.e("qqqqqqq: ", act);
                    String file_name = s.substring(s.lastIndexOf("/") + 1, s.length()).toString();

                    ClipData dragData = ClipData.newPlainText(s, file_name);

                    View.DragShadowBuilder myShadow = new View.DragShadowBuilder(view);

                    view.startDrag(dragData,  // the data to be dragged
                            myShadow,  // the drag shadow builder
                            null,      // no need to use local data
                            0        // flags (not currently used, set to 0)
                    );
                    return false;
                }
            });

        }
        @Override
        public int getCount() {
            return icons.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = LayoutInflater.from(mCoNtext).inflate(R.layout.gallery, null);
            }
            ImageView icon = convertView.findViewById(R.id.icon);
            switch (position){
                case 0:
                    icon.setImageResource(icons[0]);
                    break;
                case 1:
                    icon.setImageResource(icons[1]);
                    break;
                case 2:
                    icon.setImageResource(icons[2]);
                    break;
                case 3:
                    icon.setImageResource(icons[3]);
                    break;
                case 4:
                    icon.setImageResource(icons[4]);
                    break;
                case 5:
                    icon.setImageResource(icons[5]);
                default:
                    break;
            }
            return convertView;
        }
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
                Log.e("afcb","in");
                myCamera.takePicture(null, null, jpeg);
            }
        }
    };
    Camera.AutoFocusCallback auto = new Camera.AutoFocusCallback(){

        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
                // 設置參數,並拍照
                Camera.Parameters params = myCamera.getParameters();
                params.setPictureFormat(PixelFormat.JPEG);

                params.setPreviewSize(640, 480);

                params.setPictureSize(640, 480);

                myCamera.setParameters(params);
            }
        }
    };
    private class myDragEventListener implements View.OnDragListener{
        public int dcx = 0, dcy = 0;
        public int picWidth = 480;
        public int picHeight = 640;
        @Override
        public boolean onDrag(View v, DragEvent event) {
            final int action = event.getAction();
            switch (action){
                case DragEvent.ACTION_DRAG_STARTED:
                    //myCamera.autoFocus(auto);
                    // Determines if this View can accept the dragged data
                    if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        return (true);
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DROP:

                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);

                    isClicked = true;

                    int width = size.x;
                    int height = size.y;

                    dcx = (int) (float) (event.getX() / width * picWidth);
                    dcy = (int) (float) (event.getY() / height * picHeight);

                    Bundle bundle = new Bundle();
                    bundle.putString("dcx", Integer.toString(dcx) );
                    bundle.putString("dcy", Integer.toString(dcy));
                    intent.putExtras(bundle);

                    myCamera.autoFocus(afcb);
                case DragEvent.ACTION_DRAG_ENDED:
                    break;

                default:
                    break;
            }

            return false;
        }
    }
    /*public void getPermissionCamera(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1);
        }
    }*/
}