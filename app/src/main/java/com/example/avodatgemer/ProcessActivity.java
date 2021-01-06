package com.example.avodatgemer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ProcessActivity extends AppCompatActivity implements PicturesAdapter.PictureClickListener {

    private Process process;
    private String fileName = "date.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);

        ActivityCompat.requestPermissions(ProcessActivity.this, new String[]{Manifest.permission.CAMERA},1);
        Bundle bundle = getIntent().getExtras();
        process = new Process(bundle.getString("name"), Integer.parseInt(bundle.getString("num")));
        TextView t1 = (TextView)findViewById(R.id.processName);
        t1.setText(process.getName());

        //noti
        createNotificationChannel();
        Intent intent = new Intent(ProcessActivity.this, RemainderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ProcessActivity.this, 0,intent,0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar rightNow = Calendar.getInstance();
        Log.d("now",System.currentTimeMillis()+"");
        double addingDays = Double.parseDouble(load(process.getName(),2));
        if(addingDays<1){
            rightNow.add(Calendar.HOUR_OF_DAY,(int)(20*addingDays));
        }
        else{
            rightNow.add(Calendar.DAY_OF_MONTH,(int)addingDays);
        }
        rightNow.add(Calendar.MONTH,1);
        int yyyy = rightNow.get(Calendar.YEAR);
        int MM = rightNow.get(Calendar.MONTH);
        int dd = rightNow.get(Calendar.DAY_OF_MONTH);
        int HH = Integer.parseInt(load(process.getName(),0));
        int mm = Integer.parseInt(load(process.getName(),1));
        int ss = rightNow.get(Calendar.SECOND);;
        String myDate = yyyy+"/"+MM+"/"+dd+" "+HH+":"+mm+":"+ss;
        Log.d("date",myDate+"");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = null;
        try{
            date = sdf.parse(myDate);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        long millis = date.getTime();
        Log.d("date",millis+"");
        if(Boolean.parseBoolean(load(process.getName(),3))){
            if(addingDays<1){
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, millis-1000*60,(int)(AlarmManager.INTERVAL_HOUR*20*addingDays), pendingIntent);
            }
            else{
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, millis-1000*60,(int)(AlarmManager.INTERVAL_DAY*addingDays), pendingIntent);
            }
        }
        else{
            alarmManager.cancel(pendingIntent);
        }

        final ArrayList<Pictures> pictures= new ArrayList<>();
        File file = getExternalFilesDir(null);
        File f1=new File(file.getAbsolutePath() + "/avodatgemer/" + process.getName());
        File[] files = f1.listFiles();
        if(files!=null){
            for(int i = files.length; i>0; i --){
                int x = 0;
                while(getId(files[x].getName()) != i){
                    x++;
                }
                pictures.add(new Pictures(process.getName(),i,getDate(files[x].getName())));
            }
        }
        RecyclerView r1 = findViewById(R.id.picturesR);
        RecyclerView.LayoutManager l1 = new GridLayoutManager(this, 1);
        r1.setLayoutManager(l1);
        PicturesAdapter a1 = new PicturesAdapter(pictures);
        a1.setListener(this, this);
        r1.setAdapter(a1);

    }

    private Bitmap loadFile(Pictures p1){
        File file = getExternalFilesDir(null);
        String path =file.getAbsolutePath() + "/avodatgemer/" + p1.getName() +"/" + p1.getName() + "-" + p1.getId() + "-" + p1.getDate();
        return BitmapFactory.decodeFile(path);
    }


    public void addPicture(View view) {
        Intent i = new Intent(ProcessActivity.this, CameraActivity.class);
        i.putExtra("name", process.getName());
        i.putExtra("num", process.getPicNum()+"");
        startActivity(i);
        finish();
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


    public void openDialog(Bitmap b1){
        final Dialog d1;
        d1 = new Dialog(this);
        d1.setContentView(R.layout.dialog_test);
        ImageView i1 = d1.findViewById(R.id.iv_hide);
        i1.setImageBitmap(b1);
        i1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d1.cancel();
            }
        });
        d1.show();
    }
    @Override
    public void onPictureClick(Bitmap b1) {
        openDialog(b1);
    }

    @Override
    public void onPictureDelete(final Pictures p1) {
        final Dialog d1;
        d1 = new Dialog(this);
        d1.setContentView(R.layout.dialog_deletepic);
        ImageView i1 = d1.findViewById(R.id.imageView);
        Button bY = d1.findViewById(R.id.deleteYP);
        Button bN = d1.findViewById(R.id.deleteNP);
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
                deletePic(p1);
                d1.cancel();
                finish();
                startActivity(getIntent());
            }
        });
        i1.setImageBitmap(loadFile(p1));
        d1.show();
    }
    public void deletePic(Pictures p1){
        File file = getExternalFilesDir(null);
        String path =file.getAbsolutePath() + "/avodatgemer/" + p1.getName() +"/" + p1.getName() + "-" + p1.getId() + "-" + p1.getDate();
        File file1 = new File(path);
        int miss = getId(file1.getName());
        file1.delete();
        File f1=new File(file.getAbsolutePath() + "/avodatgemer/" + p1.getName());
        File[] files = f1.listFiles();

        boolean perfect = false;
        if(miss==files.length+1){
            perfect = true;
        }
        while (!perfect){
            file = getExternalFilesDir(null);
            f1=new File(file.getAbsolutePath() + "/avodatgemer/" + p1.getName());
            files = f1.listFiles();
            File delete = null;
            for(int i =0;i<files.length;i++){
                if(getId(files[i].getName())==miss+1){
                    delete = files[i];
                }
            }
            Pictures p2 = new Pictures(process.getName(),getId(delete.getName()),getDate(delete.getName()));
            createFile(loadFile(p2),new Pictures(process.getName(),getId(delete.getName())-1,getDate(delete.getName())));
            delete.delete();
            miss++;
            if(miss==files.length+1){
                perfect = true;
            }
        }
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

    public void onFinish(View view) {
        Intent i = new Intent(ProcessActivity.this, FinishActivity.class);
        i.putExtra("name", process.getName());
        i.putExtra("num", process.getPicNum()+"");
        startActivity(i);
        finish();
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            CharSequence name = "LemubitRemainderChannel";
            String des = "Channel for Lemubit Remainder";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("notifyLemubit", name , importance);
            channel.setDescription(des);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }
    public String load(String processName , int type) {
        FileInputStream fis = null;
        String num = "";
        try {
            fis = openFileInput(type+"-"+processName+"-"+fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;
            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }

            String str = sb.toString();
            str = str.substring(0, str.length() - 1);
            Log.d("sb",str+"");
            num = str;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return num;
    }
}



