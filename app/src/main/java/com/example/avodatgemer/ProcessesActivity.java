package com.example.avodatgemer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.util.ArrayList;

public class ProcessesActivity extends AppCompatActivity implements ProcessAdapter.finisher{

    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processes);

        ActivityCompat.requestPermissions(ProcessesActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        ActivityCompat.requestPermissions(ProcessesActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        ActivityCompat.requestPermissions(ProcessesActivity.this, new String[]{Manifest.permission.CAMERA},1);


        final ArrayList<Process> processes= new ArrayList<>();
        File file = getExternalFilesDir(null);
        File f1=new File(file.getAbsolutePath() + "/avodatgemer");
        Log.d("aaa", file.getAbsolutePath() + "/avodatgemer");
        File[] files = f1.listFiles();
        if(files!=null){
            for(int i = 0; i<files.length; i ++){
                File n = new File(file.getAbsolutePath() + "/avodatgemer/" +files[i].getName());
                int n1 = n.listFiles().length;
                processes.add(new Process(files[i].getName(),n1));
            }
        }
        processes.add(new Process("add",-1));
        RecyclerView r1 = findViewById(R.id.processRView);
        RecyclerView.LayoutManager l1 = new GridLayoutManager(this, 2);
        r1.setLayoutManager(l1);
        ProcessAdapter a1 = new ProcessAdapter(processes);
        a1.setListener(this,this );
        r1.setAdapter(a1);
    }
    public void onProcessPick(){
        finish();
    }

    @Override
    public void onProcessDelete(final Process p1) {
        final Dialog d1;
        d1 = new Dialog(this);
        d1.setContentView(R.layout.dialog_deleteproc);
        Button bY = d1.findViewById(R.id.deleteY);
        Button bN = d1.findViewById(R.id.deleteN);
        final EditText editText = d1.findViewById(R.id.sure);
        bN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d1.cancel();
                finish();
                startActivity(getIntent());
            }
        });
        bY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Integer.parseInt(editText.getText().toString())==12345){
                    deleteProcess(p1);
                }
                Log.d("123123123",editText.getText().toString());
                SystemClock.sleep(500);
                d1.cancel();
                finish();
                startActivity(getIntent());
            }
        });
        d1.show();
    }
    private Bitmap loadFile(Pictures p1){
        File file = getExternalFilesDir(null);
        String path =file.getAbsolutePath() + "/avodatgemer/" + p1.getName() +"/" + p1.getName() + "-" + p1.getId() + "-" + p1.getDate();
        Log.d("123" ,path);
        return BitmapFactory.decodeFile(path);
    }
    public void deleteProcess(Process p1){
        File file = getExternalFilesDir(null);
        File f1=new File(file.getAbsolutePath() + "/avodatgemer/" + p1.getName());
        File[] files = f1.listFiles();
        for(int i =0; i<files.length; i++){
            files[i].delete();
        }
        f1.delete();
    }
}
