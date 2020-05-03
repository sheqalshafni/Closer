package com.sheqal.closer;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>{

    public ArrayList<GalleryItem> mGalleryItems;

    public static class GalleryViewHolder extends RecyclerView.ViewHolder{

        public ImageView galleryImage;
        public TextView galleryDate;

        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            galleryImage = itemView.findViewById(R.id._galleryImage);
            galleryDate = itemView.findViewById(R.id._galleryDate);
        }
    }

    public GalleryAdapter(ArrayList<GalleryItem> galleryItems){
        mGalleryItems = galleryItems;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent, false);
        GalleryViewHolder viewHolder = new GalleryViewHolder(v);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        GalleryItem currentItem = mGalleryItems.get(position);
        holder.galleryImage.setImageResource(currentItem.getGalleryImage());
        holder.galleryDate.setText(currentItem.getGalleryDate());

    }

    @Override
    public int getItemCount() {
        return mGalleryItems.size();
    }
}
