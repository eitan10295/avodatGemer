package com.example.avodatgemer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.net.wifi.aware.Characteristics;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.PrecomputedText;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Parameter;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CameraActivity extends AppCompatActivity {

    private String cameraId;
    int id = 0;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSessions;
    private CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimensions;
    private ImageReader imageReader;
    private ImageView lastPicP;
    TextureView preview;
    private Process process;
    private Handler mBackgroundHandler;
    Size[] sizes;
    boolean gotSize = false;
    Bitmap b1 =null;
    CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {

        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    private static final SparseIntArray ORIENTATION = new SparseIntArray();
    static {
        ORIENTATION.append(Surface.ROTATION_0,90);
        ORIENTATION.append(Surface.ROTATION_90,0);
        ORIENTATION.append(Surface.ROTATION_180,270);
        ORIENTATION.append(Surface.ROTATION_270,180);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Bundle bundle = getIntent().getExtras();
        if(bundle.getString("id")==null){

        }
        else{
            id=Integer.parseInt(bundle.getString("id"));
        }
        process = new Process(bundle.getString("name"), Integer.parseInt(bundle.getString("num")));
        SeekBar simpleSeekBar=findViewById(R.id.seekBar2); // initiate the progress bar
        simpleSeekBar.setMax(255); // 200 maximum value for the Seek bar
        simpleSeekBar.setProgress(0);
        lastPicP = (ImageView)findViewById(R.id.lastpicP);
        simpleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;

            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                lastPicP.setImageAlpha(progressChangedValue);
            }
        });
        lastPicP.setAlpha(0);
        lastPicP.setVisibility(View.VISIBLE);

        File file = getExternalFilesDir(null);
        File f1=new File(file.getAbsolutePath() + "/avodatgemer/" + process.getName());
        File[] files = f1.listFiles();
        if(process.getPicNum()==0){
            lastPicP.setImageBitmap(null);
        }
        else{
            int x = 0;
            while(getId(files[x].getName()) != files.length){
                x++;
            }
            Pictures p1 =  new Pictures(process.getName(), getId(files[x].getName()),getDate(files[x].getName()));
            b1 = loadFile(p1);
            lastPicP.setImageBitmap(b1);


        }
        preview = (TextureView) findViewById(R.id.textureView);
        assert preview != null;
        preview.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
    }

    public void takePic(View view) {
        try{
            CameraManager manager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);

            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            Size[] jpegSize = null;
            if(characteristics !=null){
                if(id==0){
                    jpegSize = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
                }
                else{
                    jpegSize = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.DEPTH16);
                }

            }
            int w = 640;
            int h = 400;
            if(jpegSize!=null&&jpegSize.length>0){
                w=sizes[id].getWidth();
                h=sizes[id].getHeight();
            }

            ImageReader reader;
            reader = ImageReader.newInstance(w,h,ImageFormat.JPEG,1);
            List<Surface> outputSurface = new ArrayList<>(2);
            outputSurface.add(reader.getSurface());
            outputSurface.add(new Surface(preview.getSurfaceTexture()));
            CaptureRequest.Builder captureBuilder= cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION,ORIENTATION.get(rotation));
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    image = reader.acquireLatestImage();
                    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                    byte[] bytes = new byte[buffer.capacity()];
                    buffer.get(bytes);
                    Bitmap original = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Matrix matrix = new Matrix();
                    if(id==0){
                        matrix.preRotate(90);
                    }
                    else{
                        matrix.preRotate(270);
                        matrix.preScale(1.0f, -1.0f);
                    }
                    Bitmap rotatedBitmap = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true);
                    File file = getExternalFilesDir(null);
                    File f1=new File(file.getAbsolutePath() + "/avodatgemer/" + process.getName());
                    File[] files = f1.listFiles();
                    Date c = Calendar.getInstance().getTime();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                    String formattedDate = df.format(c);
                    if(files == null){
                        createFile(rotatedBitmap, new Pictures(process.getName(), 1, formattedDate));
                    }
                    else{
                        createFile(rotatedBitmap, new Pictures(process.getName(), files.length+1, formattedDate));
                    }
                    Intent i = new Intent(CameraActivity.this, AcceptActivity.class);
                    i.putExtra("name", process.getName());
                    i.putExtra("num", process.getPicNum()+"");
                    startActivity(i);
                    finish();
                }
            };
            reader.setOnImageAvailableListener(readerListener,mBackgroundHandler);
            CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    createCameraPreview();
                }
            };
            cameraDevice.createCaptureSession(outputSurface, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    try{
                        session.capture(captureBuilder.build(),captureCallback,mBackgroundHandler);
                    }
                    catch (CameraAccessException e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                }
            },mBackgroundHandler);
        }
        catch (CameraAccessException e){
            e.printStackTrace();
        }
    }

    private void createCameraPreview() {
        try{
            SurfaceTexture texture = preview.getSurfaceTexture();
            assert texture!=null;
            texture.setDefaultBufferSize(imageDimensions.getWidth(),imageDimensions.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    if(cameraDevice == null){
                        return;
                    }
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                    
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                }
            },null);

        }
        catch (CameraAccessException e){
            e.printStackTrace();
        }

    }

    private void updatePreview() {
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE,CaptureRequest.CONTROL_MODE_AUTO);
        try{
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(),null,mBackgroundHandler);
        }
        catch (CameraAccessException e){
            e.printStackTrace();
        }

    }

    private void openCamera(){
        CameraManager manager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        try{
            cameraId = manager.getCameraIdList()[id];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map !=null;
            imageDimensions = map.getOutputSizes(SurfaceTexture.class)[0];
            if(ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
            manager.openCamera(cameraId,stateCallback,null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        try{
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) preview.getLayoutParams();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            StreamConfigurationMap streamConfigurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            if(!gotSize){
                sizes = streamConfigurationMap.getOutputSizes(ImageFormat.DEPTH16);
                if(id==0){
                    sizes = streamConfigurationMap.getOutputSizes(ImageFormat.JPEG);
                }
                gotSize = true;
            }
            double w;
            double h;
            w = sizes[id].getHeight();
            h = sizes[id].getWidth();
            double width = displayMetrics.widthPixels;
            double div = w/width;
            int h1 = (int)((h/div));
            lp.height = h1;
            preview.setLayoutParams(lp);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        if(process.getPicNum()!=0){
            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) lastPicP.getLayoutParams();
            ConstraintLayout.LayoutParams lp1 = (ConstraintLayout.LayoutParams) preview.getLayoutParams();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            double w = b1.getWidth();
            double h = b1.getHeight();
            double height = lp1.height;
            lp.width = (int)(w*height/h);
            lastPicP.setLayoutParams(lp);
        }
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static Bitmap flipBit(Bitmap b1){
        Bitmap bOutput;
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);
        bOutput = Bitmap.createBitmap(b1, 0, 0, b1.getWidth(), b1.getHeight(), matrix, true);
        return bOutput;

    }

    private Bitmap loadFile(Pictures p1){
        File file = getExternalFilesDir(null);
        String path =file.getAbsolutePath() + "/avodatgemer/" + p1.getName() +"/" + p1.getName() + "-" + p1.getId() + "-" + p1.getDate();
        return BitmapFactory.decodeFile(path);
    }

    private int getId(String s1){
        int id = 0;
        int i=0;
        boolean had = false;
        boolean finishId = false;
        while(!had){
            if(s1.charAt(i)=='-'){
                i++;
                while(!finishId){
                    id = id*10+Integer.parseInt(s1.charAt(i)+"");
                    if(s1.charAt(i+1)=='-'){
                        finishId = true;
                    }
                    i++;
                }
                had = true;
            }
            i++;
        }
        return id;
    }

    private String getDate(String s1){
        String date = "";
        int i=0;
        int had = 0;
        while(had<3){
            if(s1.charAt(i)=='-'){
                had++;
                i++;
                while(had==2){
                    date=date+s1.charAt(i);
                    if(i+1==s1.length()){
                        had = 3;
                    }
                    i++;
                }
            }
            i++;
        }
        return date;
    }
    private void createFile(Bitmap bitmap, Pictures p1) {
        FileOutputStream outputStream = null;
        File file = getExternalFilesDir(null);
        File dir = new File(file.getAbsolutePath() + "/avodatgemer/" + p1.getName());
        dir.mkdirs();

        String filename = p1.getName() + "-" + p1.getId() + "-" + p1.getDate();
        File outFile = new File(dir,filename);
        try{
            outputStream = new FileOutputStream(outFile);
        }catch (Exception e){
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
        try{
            outputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            outputStream.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void rotatePic(View view) {
        if(id==0){
            id=1;
        }
        else{
            id=0;
        }
        Intent i = new Intent(CameraActivity.this, CameraActivity.class);
        i.putExtra("name", process.getName());
        i.putExtra("num", process.getPicNum()+"");
        i.putExtra("id", id+"");
        startActivity(i);
        finish();
    }
}
