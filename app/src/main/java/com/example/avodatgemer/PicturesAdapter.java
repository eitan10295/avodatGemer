package com.example.avodatgemer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class PicturesAdapter extends RecyclerView.Adapter<PicturesAdapter.ChatViewHolder> {
    private ArrayList<Pictures> pictures;
    private PictureClickListener listener;
    private ProcessActivity context;
    public interface PictureClickListener{
        void onPictureClick(Bitmap b1);
        void onPictureDelete(final Pictures p1);
    }

    public void setListener(PictureClickListener p1 ,ProcessActivity context1){
        listener =p1;
        context = context1;
    }

    public PicturesAdapter(ArrayList<Pictures> c1) {
        pictures = c1;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_picture, parent,false);
        return new ChatViewHolder(chatView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatViewHolder holder, final int position) {
        final Pictures c1 = pictures.get(position);
        holder.name.setText(c1.getName());
        holder.id.setText(c1.getId()+"-(" +c1.getDate()+")");
        File file = context.getExternalFilesDir(null);
        File f1=new File(file.getAbsolutePath() + "/avodatgemer/" + c1.getName());
        File[] files = f1.listFiles();
        int x = 0;
        while(getId(files[x].getName()) != pictures.get(position).getId()){
            x++;
        }
        Pictures p1 =  new Pictures(c1.getName(), getId(files[x].getName()),getDate(files[x].getName()));
        final Bitmap b1 = loadFile(p1);
        holder.pic.setImageBitmap(b1);
        holder.view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(listener!=null){
                    listener.onPictureClick(b1);
                }
            }
        });
        holder.bin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(listener!=null){
                    listener.onPictureDelete(c1);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView id;
        public ImageView pic;
        public ImageView bin;
        public View view;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            name = itemView.findViewById(R.id.pictureName);
            id = itemView.findViewById(R.id.pictureId);
            pic = itemView.findViewById(R.id.picture);
            bin = itemView.findViewById(R.id.binB);
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

    private Bitmap loadFile(Pictures p1){
        File file = context.getExternalFilesDir(null);
        String path =file.getAbsolutePath() + "/avodatgemer/" + p1.getName() +"/" + p1.getName() + "-" + p1.getId() + "-" + p1.getDate();
        return BitmapFactory.decodeFile(path);
    }

}
