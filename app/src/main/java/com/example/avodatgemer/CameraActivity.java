package com.example.avodatgemer;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CameraActivity extends AppCompatActivity {

    private int camId=0;
    private Camera mCamera;
    private CameraPreview mPreview;
    private ImageView lastPicP;
    FrameLayout preview;
    private Process process;
    private final Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
            double h = displayMetrics.heightPixels ;
            double w = displayMetrics.widthPixels;

            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Bitmap b1;
            if(camId == 0){
                b1 = rotateBitmap(bitmap, 90 );
            }
            else{
                b1 = rotateBitmap(bitmap, 270 );
                b1 = flipBit(b1);
            }
            double wb = b1.getWidth();
            double hb = b1.getHeight();
            double x=0;
            double y=0;
            if(w/h<wb/hb){
                x=wb-(w*hb)/h;
            }
            else{
                y=(h*wb-w*hb)/(-w);
            }
            Log.d("w:" , w+"");
            Log.d("h:" , h+"");
            Log.d("wB:" , wb+"");
            Log.d("hB:" , hb+"");
            Log.d("x" , x+"");
            Log.d("y" , y+"");
            b1 = Bitmap.createBitmap(b1, (int)(x/2), (int)(y/2),(int)(wb-x),(int)(hb-y));
            File file = getExternalFilesDir(null);
            File f1=new File(file.getAbsolutePath() + "/avodatgemer/" + process.getName());
            File[] files = f1.listFiles();
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            String formattedDate = df.format(c);
            if(files == null){
                Log.d("1" , "null");
                createFile(b1, new Pictures(process.getName(), 1, formattedDate));
            }
            else{
                Log.d("1" , "nonull");
                createFile(b1, new Pictures(process.getName(), files.length+1, formattedDate));
            }
            Intent i = new Intent(CameraActivity.this, AcceptActivity.class);
            i.putExtra("name", process.getName());
            i.putExtra("num", process.getPicNum()+"");
            startActivity(i);
            finish();
        }

    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Bundle bundle = getIntent().getExtras();
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
            lastPicP.setImageBitmap(loadFile(p1));
        }
        // Create an instance of Camera
        mCamera = getCameraInstance();
        mCamera.setDisplayOrientation(90);
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        preview = (FrameLayout)findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    }
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    public void takePic(View view) {
        mCamera.takePicture(null, null, null,mPicture);
    }

    public void rotatePic(View view) {
        if (camId==0){
            camId = 1;
        }
        else{
            camId = 0;
        }
        mCamera = null;
        mCamera = Camera.open(camId);
        mCamera.setDisplayOrientation(90);
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
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
}
