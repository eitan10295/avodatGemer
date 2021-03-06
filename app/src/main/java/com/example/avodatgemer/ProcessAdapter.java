package com.example.avodatgemer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class ProcessAdapter extends RecyclerView.Adapter<ProcessAdapter.ChatViewHolder> {
    private ArrayList<Process> processes;
    private finisher listener;
    private ProcessesActivity context;
    public ProcessAdapter(ArrayList<Process> c1) {
        processes = c1;
    }
    public interface finisher{
        void onProcessPick();
        void onProcessDelete(final Process p1);
    }
    public void setListener(finisher p1, ProcessesActivity context1){
        listener =p1;
        context = context1;
    }
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_process, parent,false);
        return new ChatViewHolder(chatView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        final Process c1 = processes.get(position);
        if(c1.getPicNum()!=-1){
            holder.name.setText(c1.getName());
            holder.numOfPic.setText("pictures: " + c1.getPicNum());
            holder.add.setVisibility(View.INVISIBLE);
            File file = context.getExternalFilesDir(null);
            File f1=new File(file.getAbsolutePath() + "/avodatgemer/" + c1.getName());
            File[] files = f1.listFiles();
            if(c1.getPicNum()==0){

            }
            else{
                int x = 0;
                while(getId(files[x].getName()) != 1){
                    x++;
                }
                Pictures p1 =  new Pictures(c1.getName(), getId(files[x].getName()),getDate(files[x].getName()));
                holder.pic.setImageBitmap(loadFile(p1));
            }
            holder.view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent i = new Intent(view.getContext(), ProcessActivity.class);
                    i.putExtra("name", c1.getName());
                    i.putExtra("num", c1.getPicNum()+"");
                    view.getContext().startActivity(i);
                    listener.onProcessPick();
                }
            });
            holder.delete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    listener.onProcessDelete(c1);
                }
            });
        }
        else{
            holder.name.setVisibility(View.INVISIBLE);
            holder.numOfPic.setVisibility(View.INVISIBLE);
            holder.pic.setVisibility(View.INVISIBLE);
            holder.delete.setVisibility(View.INVISIBLE);
            holder.add.setVisibility(View.VISIBLE);
            holder.view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent i = new Intent(view.getContext(), NewProcessActivity.class);
                    view.getContext().startActivity(i);
                }
            });
        }

    }
    public int getNum(String s1){
        int n1 = 0;
        for(int i =0; i < s1.length() ; i++){
            if(Character.isDigit(s1.charAt(i))){
                String digit = ""+s1.charAt(i);
                n1=n1*10+Integer.parseInt(digit);
            }
        }
        return n1;
    }

    @Override
    public int getItemCount() {
        return processes.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView numOfPic;
        public ImageView add;
        public ImageView pic;
        public ImageView delete;
        public View view;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            name = itemView.findViewById(R.id.processName);
            numOfPic = itemView.findViewById(R.id.picnum);
            add = itemView.findViewById(R.id.addB);
            pic = itemView.findViewById(R.id.processImage);
            delete = itemView.findViewById(R.id.processD);
        }
    }
    private Bitmap loadFile(Pictures p1){
        File file = context.getExternalFilesDir(null);
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
