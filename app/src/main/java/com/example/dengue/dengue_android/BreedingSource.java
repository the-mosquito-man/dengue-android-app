package com.example.dengue.dengue_android;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.CAMERA;


public class BreedingSource extends Activity {
    private static final int REQUEST_CAMERA = 1;
    private int rotate = 0;
    private int degrees = 0;
    private SurfaceView sfv_preview;
    private Camera camera = null;

    private SurfaceHolder.Callback cpHolderCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            startPreview();
        }
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            stopPreview();
        }
    };
    private boolean check_photo = false;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_photo);
        checkAuthority();
        new menu(this, 2);
    }

    private void bindViews() {
        sfv_preview = (SurfaceView) findViewById(R.id.sfv_preview);
        sfv_preview.getHolder().addCallback(cpHolderCallback);
        final Activity Main = this;

        final Button btn_take = (Button) findViewById(R.id.btn_take);
        final TextView choice = (TextView) findViewById(R.id.take_photo_choice);

        btn_take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_photo) {
                    btn_take.setText("");
                    choice.setText("取消");
                    check_photo = false;

                    Log.i("dangue","path = "+path);

                    Bundle bundle = new Bundle();
                    bundle.putString("img", path);
                    bundle.putInt("degree", rotate);

                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    intent.setClass(BreedingSource.this, BreedingSourceSubmit.class);
                    startActivity(intent);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        int permission = ActivityCompat.checkSelfPermission(Main, Manifest.permission.CAMERA);
                        Log.i("dengue", "Permission = "+String.valueOf(permission));
                        Log.i("dengue", "Granted = "+String.valueOf(PackageManager.PERMISSION_GRANTED));
                        Log.i("dengue", "Denied = "+String.valueOf(PackageManager.PERMISSION_DENIED));
                        if (permission != PackageManager.PERMISSION_GRANTED) {
                            //未取得權限，向使用者要求允許權限
                            ActivityCompat.requestPermissions(Main, new String[]{CAMERA}, REQUEST_CAMERA);
                            camera.takePicture(null, null, new Camera.PictureCallback() {
                                @Override
                                public void onPictureTaken(byte[] data, Camera camera) {
                                    if ((path = saveFile(compressImageByQuality(data))) != null) {
                                        btn_take.setText("確定");
                                        choice.setText("重拍");
                                        check_photo = true;
                                    } else {
                                        Toast.makeText(BreedingSource.this, "保存照片失敗", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            });
                        } else {
                            //已有權限，可進行檔案存取
                            camera.takePicture(null, null, new Camera.PictureCallback() {
                                @Override
                                public void onPictureTaken(byte[] data, Camera camera) {
                                    if ((path = saveFile(compressImageByQuality(data))) != null) {
                                        btn_take.setText("確定");
                                        choice.setText("重拍");
                                        check_photo = true;
                                    } else {
                                        Toast.makeText(BreedingSource.this, "保存照片失敗", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            });
                        }
                    }

                }
            }
        });

        choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check_photo) {
                    btn_take.setText("");
                    choice.setText("取消");
                    check_photo = false;
                    camera.startPreview();
                }
                else {
                    Main.onBackPressed();
                }
            }
        });
    }

    public static byte[] compressImageByQuality(byte[] bytes){
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        ByteArrayOutputStream baas = new ByteArrayOutputStream();
        int options = 50;

        bitmap.compress(Bitmap.CompressFormat.JPEG, options, baas);
        while (baas.toByteArray().length / 1024 > 300) {
            baas.reset();
            options -= 10;
            if(options < 0) options = 0;
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baas);
            if(options == 0) break;
        }
        return baas.toByteArray();
    }

    private String saveFile(byte[] bytes){
        try {
            File file = File.createTempFile("img", ".jpg");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.flush();
            fos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void startPreview(){
        try {
            camera = Camera.open();
            int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
            degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0: degrees = 90; break;
                case Surface.ROTATION_90: degrees = 0; break;
                case Surface.ROTATION_180: degrees = 270; break;
                case Surface.ROTATION_270: degrees = 180; break;
            }
            if(camera != null)
            {
                try {
                    camera.setPreviewDisplay(sfv_preview.getHolder());
                    camera.setDisplayOrientation(degrees);
                    rotate = degrees;
                    camera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void stopPreview() {
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    private void checkAuthority()
    {
        final Activity Main = this;

        Log.i("dengue", "Build.VERSION.SDK_INT = "+String.valueOf(Build.VERSION.SDK_INT));
        Log.i("dengue", "Build.VERSION_CODES.HONEYCOMB = "+String.valueOf(Build.VERSION_CODES.HONEYCOMB));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            bindViews();
            int permission = ActivityCompat.checkSelfPermission(Main, Manifest.permission.CAMERA);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                //未取得權限，向使用者要求允許權限
                ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
            }
            else {
                //已有權限，可進行檔案存取
            }
        }
        else{
        }
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //取得權限，進行檔案存取

                } else {
                    //使用者拒絕權限，停用檔案存取功能

                }
                return;
        }
    }*/
}
