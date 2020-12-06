package com.example.avodatgemer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorSpace;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import org.jcodec.api.SequenceEncoder;
import org.jcodec.api.android.AndroidSequenceEncoder;
import org.jcodec.common.io.FileChannelWrapper;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Rational;

import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class FinishActivity extends AppCompatActivity {

    private Process process;
    private NumberPicker picker1;
    private String[] pickervals  = {"0.1","0.2","0.3","0.4","0.5","0.6","0.7","0.8","0.9","1","1.1","1.2","1.3","1.4","1.5","1.6","1.7","1.8","1.9","2"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);

        Bundle bundle = getIntent().getExtras();
        process = new Process(bundle.getString("name"), Integer.parseInt(bundle.getString("num")));
        TextView t1 = (TextView)findViewById(R.id.processName);
        t1.setText(process.getName());
        picker1 = findViewById(R.id.numberPicker);
        picker1.setMaxValue(19);
        picker1.setMinValue(0);
        picker1.setDisplayedValues(pickervals);
        picker1.setWrapSelectorWheel(!!!!!!!!false);
    }

    public void createVideo(View view) {
        File file2 = getExternalFilesDir(null);
        File f1=new File(file2.getAbsolutePath() + "/avodatgemer/" + process.getName());
        File[] files = f1.listFiles();
        Bitmap[] bitmaps1 = new Bitmap[files.length];
        for(int i = files.length; i>0; i --){
            int x = 0;
            while(getId(files[x].getName()) != i){
                x++;
            }
            Pictures p1 =  new Pictures(process.getName(),i,getDate(files[x].getName()));
            bitmaps1[i-1] = loadFile(p1);
        }
        newFolder(process.getName());
        File file1 = getExternalFilesDir(null);
        String path =file1.getAbsolutePath() + "/avodatgemerVideos";
        FileChannelWrapper out = null;
        File file = new File(path, process.getName()+".mp4");

        try { out = NIOUtils.writableFileChannel(file.getAbsolutePath());
            double delay = Double.parseDouble(pickervals[picker1.getValue()]);
            AndroidSequenceEncoder encoder;
            encoder = new AndroidSequenceEncoder(out, Rational.R(10, (int)(delay*10)));
            Log.d("364",10/(int)(delay*10)+"");
            for (Bitmap bitmap : bitmaps1) {
                encoder.encodeImage(bitmap);
            }
            encoder.finish();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            NIOUtils.closeQuietly(out);
        }
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
            if(s1.charAt(i)=='-'||s1.charAt(i)==','){
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
    private Bitmap loadFile(Pictures p1){
        File file = getExternalFilesDir(null);
        String path =file.getAbsolutePath() + "/avodatgemer/" + p1.getName() +"/" + p1.getName() + "-" + p1.getId() + "-" + p1.getDate();
        Log.d("123" ,path);
        return BitmapFactory.decodeFile(path);
    }
    private void newFolder(String processName){
        FileOutputStream outputStream = null;
        File file = getExternalFilesDir(null);
        File dir = new File(file.getAbsolutePath() + "/avodatgemerVideos");
        dir.mkdirs();

        String filename = processName;
        File outFile = new File(dir,filename);
        try{
            outputStream = new FileOutputStream(outFile);
        }catch (Exception e){
            e.printStackTrace();
        }
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
        File file1 = new File(file.getAbsolutePath() + "/avodatgemerVideos/" + processName);
        file1.delete();
    }
}

