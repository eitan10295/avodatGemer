package com.example.avodatgemer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AcceptActivity extends AppCompatActivity {

    private Process process;
    private Bitmap lastPicture;
    private Bitmap currentPicture;
    private ImageView lastV;
    private ImageView currentV;
    private ImageView firstPicV;
    private TextView firstPicT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept);

        Bundle bundle = getIntent().getExtras();
        process = new Process(bundle.getString("name"), Integer.parseInt(bundle.getString("num")));
        Intent intent = getIntent();
        File file = getExternalFilesDir(null);;
        File f1=new File(file.getAbsolutePath() + "/avodatgemer/" + process.getName());
        File[] files = f1.listFiles();
        int x = 0;
        while(getId(files[x].getName()) != files.length){
            x++;
        }
        Pictures p1 =  new Pictures(process.getName(), getId(files[x].getName()),getDate(files[x].getName()));
        currentPicture = loadFile(p1);
        if(files.length==1){

        }
        else{
            int y = 0;
            while(getId(files[y].getName()) != files.length-1){
                y++;
            }
            Pictures p2 =  new Pictures(process.getName(), getId(files[y].getName()),getDate(files[y].getName()));
            lastPicture = loadFile(p2);
        }
        lastV = findViewById(R.id.yesterdayPic);
        currentV = findViewById(R.id.yesterdayPic2);
        firstPicT = findViewById(R.id.firstPicT);
        firstPicV = findViewById(R.id.firstPicV);
        TextView t1 = findViewById(R.id.textView4);
        TextView t2 = findViewById(R.id.textView3);
        if(files.length==1){
            lastV.setVisibility(View.INVISIBLE);
            t2.setVisibility(View.INVISIBLE);
            t1.setVisibility(View.INVISIBLE);
            currentV.setVisibility(View.INVISIBLE);
            firstPicV.setVisibility(View.VISIBLE);
            firstPicT.setVisibility(View.VISIBLE);
            firstPicV.setImageBitmap(currentPicture);
        }
        else{
            lastV.setImageBitmap(lastPicture);
            currentV.setImageBitmap(currentPicture);
            firstPicV.setVisibility(View.INVISIBLE);
            firstPicT.setVisibility(View.INVISIBLE);
        }
    }

    public void decline(View view) {
        File file = getExternalFilesDir(null);;
        File f1=new File(file.getAbsolutePath() + "/avodatgemer/" + process.getName());
        File[] files = f1.listFiles();
        int x = 0;
        while(getId(files[x].getName()) != files.length){
            x++;
        }
        files[x].delete();
        Intent i = new Intent(AcceptActivity.this, CameraActivity.class);
        i.putExtra("name", process.getName());
        i.putExtra("num", process.getPicNum()+"");
        startActivity(i);
        finish();
    }

    public void accept(View view) {
        startActivity(new Intent(AcceptActivity.this,ProcessesActivity.class));
        finish();

    }
    private void createFile(Bitmap bitmap, Pictures p1) {
        FileOutputStream outputStream = null;
        File file = getExternalFilesDir(null);;
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
    private Bitmap loadFile(Pictures p1){
        File file = getExternalFilesDir(null);;
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
}

