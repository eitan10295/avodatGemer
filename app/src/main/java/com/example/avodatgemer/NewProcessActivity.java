package com.example.avodatgemer;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

public class NewProcessActivity extends AppCompatActivity {

    private String[] daysS  = {"0.25","0.5","1","2","3","4","5","6","7","30"};
    private String[] hourS = new String[24];
    private String[] minS = new String[60];
    private String fileName = "date.txt";
    public NumberPicker min;
    public NumberPicker days;
    public NumberPicker hours;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_process);

        ActivityCompat.requestPermissions(NewProcessActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

        for(int i = 0; i<hourS.length; i ++){
            if(i<10){
                hourS[i] = "0"+i;
            }
            else{
                hourS[i]=i+"";
            }
        }
        for(int i = 0; i<minS.length; i ++){
            if(i<10){
                minS[i] = "0"+i;
            }
            else{
                minS[i]=i+"";
            }
        }

        min = findViewById(R.id.min);
        hours = findViewById(R.id.hours);
        days = findViewById(R.id.days);
        min.setMaxValue(59);
        min.setMinValue(0);
        min.setDisplayedValues(minS);
        min.setWrapSelectorWheel(true);
        hours.setMaxValue(23);
        hours.setMinValue(0);
        hours.setDisplayedValues(hourS);
        hours.setWrapSelectorWheel(true);
        days.setMaxValue(9);
        days.setMinValue(0);
        days.setDisplayedValues(daysS);
        days.setWrapSelectorWheel(false);
    }

    public void addProcess(View view) {
        EditText name = findViewById(R.id.addProcessName);
        String processName = name.getText().toString();
        FileOutputStream outputStream = null;
        File file = getExternalFilesDir(null);
        File dir = new File(file.getAbsolutePath() + "/avodatgemer/" + processName);
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
        File file1 = new File(file.getAbsolutePath() + "/avodatgemer/" + processName + "/"+filename);
        file1.delete();
        int HH = Integer.parseInt(hourS[hours.getValue()]);
        int mm = Integer.parseInt(minS[min.getValue()]);
        double aDays = Double.parseDouble(daysS[days.getValue()]);
        save(HH+"",processName,0);
        save(mm+"",processName,1);
        save(aDays+"",processName,2);
        save("true",processName,3);
        startActivity(new Intent(NewProcessActivity.this, ProcessesActivity.class));
        finish();
    }
    public void save(String text , String processName , int type) {
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(type+"-"+processName+"-"+fileName, MODE_PRIVATE);
            fos.write(text.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
