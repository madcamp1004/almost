package com.example.viewpagerdemo;


import android.app.Activity;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;

import java.io.File;

public class FullImageActivity extends Activity {
    Button selbtn;
    Button reselbtn;

    @Override
    public void onCreate(Bundle savedInstancestate){
        super.onCreate(savedInstancestate);
        setContentView(R.layout.full_image);

        //get intent data
        Intent i = getIntent();
        //Selected image id
        int position = i.getExtras().getInt("id");
        Log.i("FULL IMAGE BITMAP", String.valueOf(position));
        ContentResolver cR = getApplicationContext().getContentResolver();

        ImageView imageView = (ImageView) findViewById(R.id.imgPreview);
        VideoView videoView = (VideoView) findViewById(R.id.videoPreview);
        final String strImgUri = i.getExtras().getString("imgUri");
        selbtn = (Button)findViewById(R.id.selectbtn);
        selbtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                //Todo : make usable item
                Toast.makeText(FullImageActivity.this,"You select image",Toast.LENGTH_SHORT).show();
                MainActivity.selUri = Uri.parse(strImgUri);
                finish();
            }
        });
        reselbtn = (Button)findViewById(R.id.reselectbtn);
        reselbtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });
        //check type of content
        if(strImgUri.contains("mp4")){
            imageView.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoURI(Uri.parse(strImgUri));
            videoView.start();
        }
        else if(strImgUri.contains("images") || strImgUri.contains("jpg")){
            videoView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            Glide.with(this).load(Uri.parse(strImgUri)).into(imageView);
        }
    }
}
