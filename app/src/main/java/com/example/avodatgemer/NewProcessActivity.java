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

import java.io.File;
import java.io.FileOutputStream;

public class NewProcessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_process);
    }

    public void addProcess(View view) {
        EditText name = findViewById(R.id.addProcessName);
        String processName = name.getText().toString();
        FileOutputStream outputStream = null;
        File file = getExternalFilesDir(null);
        File dir = new File(file.getAbsolutePath() + "/avodatgemer/" + processName);
        Log.d("1" , dir.getPath());
        Log.d("@!#",dir.mkdirs()+"");


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
        startActivity(new Intent(NewProcessActivity.this, ProcessesActivity.class));
        finish();
    }
}
