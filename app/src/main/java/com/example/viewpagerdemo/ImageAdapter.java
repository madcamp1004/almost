package com.example.viewpagerdemo;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    //    keep all Images in array
    public static Integer[] mThumbIds={
//            R.drawable.image1,R.drawable.image3,
//            R.drawable.image12,R.drawable.image6,
//            R.drawable.image9,R.drawable.image10,
//            R.drawable.image11,R.drawable.image7,
//            R.drawable.image8
    };

    public static ArrayList<Uri> imageList = new ArrayList<>();

    //Constructor
    public ImageAdapter(Context c){ mContext = c; }
    @Override
    public int getCount(){
        return imageList.size();
    }
    @Override
    public Object getItem(int position){
        return 0;
    }
    @Override
    public long getItemId(int position){
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        ImageView image;
        if (convertView == null){
            LayoutInflater mInflater = ( LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.grid_item_view,null);

            image = (ImageView) convertView.findViewById(R.id.grid_item_image);
            Glide.with(mContext).asBitmap().load(imageList.get(position)).apply(new RequestOptions().centerCrop()).into(image);

        }else{
            image = (ImageView) convertView;
        }
        // return convertView;
        return image;
    }
}
