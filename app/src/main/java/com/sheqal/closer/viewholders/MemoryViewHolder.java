package com.sheqal.closer.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sheqal.closer.R;

public class MemoryViewHolder extends RecyclerView.ViewHolder {

    public TextView _memoryDate;
    public ImageView _memoryImageURL;
    public CardView parentLayout;

    public MemoryViewHolder(@NonNull View itemView) {
        super(itemView);

        _memoryDate = itemView.findViewById(R.id._galleryDate);
        _memoryImageURL = itemView.findViewById(R.id._galleryImage);

        parentLayout = itemView.findViewById(R.id.ParentLayout);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
            }
        });

    }
}
