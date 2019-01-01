package com.example.viewpagerdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ImageViewHolder> {

    private int[] images;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public RecyclerAdapter(int[] images){
        this.images = images;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_list_row, parent,false);
        ImageViewHolder imageViewHolder = new ImageViewHolder(view,mListener);

        return imageViewHolder;
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        int image_id = images[position];
        holder.image.setImageResource(image_id);
        switch (position) {
            case 0:
                holder.imageText.setText("Blossom");
                break;
            case 1:
                holder.imageText.setText("Space");
                break;
            case 2:
                holder.imageText.setText("Toy Story");
                break;
            case 3:
                holder.imageText.setText("Tree");
                break;

                default:
                    holder.imageText.setText("Image: " + position);
        }
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView imageText;

        public ImageViewHolder(View itemView, final OnItemClickListener listener){
            super(itemView);
            image = itemView.findViewById(R.id.imageView2);
            imageText = itemView.findViewById(R.id.image_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    if (listener != null){
                        int position = getAdapterPosition();
                        int defaultColor = Color.parseColor("#808080");
                        switch (position) {
                            case 0:
                                CardFragment.nameCard.setTextColor(defaultColor);
                                CardFragment.phoneCard.setTextColor(defaultColor);
                                CardFragment.addressCard.setTextColor(defaultColor);
                                break;
                            case 1:
                                CardFragment.nameCard.setTextColor(Color.WHITE);
                                CardFragment.phoneCard.setTextColor(Color.WHITE);
                                CardFragment.addressCard.setTextColor(Color.WHITE);
                                break;
                            case 2:
                                CardFragment.nameCard.setTextColor(defaultColor);
                                CardFragment.phoneCard.setTextColor(defaultColor);
                                CardFragment.addressCard.setTextColor(defaultColor);
                                break;
                            case 3:
                                CardFragment.nameCard.setTextColor(defaultColor);
                                CardFragment.phoneCard.setTextColor(defaultColor);
                                CardFragment.addressCard.setTextColor(defaultColor);
                                break;

                            default:
                                Log.i("ERROR", "NO NUM");
                        }


                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

}
