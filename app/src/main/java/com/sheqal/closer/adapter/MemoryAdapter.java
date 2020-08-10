package com.sheqal.closer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.sheqal.closer.EventItem;
import com.sheqal.closer.R;
import com.sheqal.closer.models.Memory;
import com.sheqal.closer.viewholders.MemoryViewHolder;

import java.util.List;

public class MemoryAdapter extends RecyclerView.Adapter<MemoryViewHolder> {

    private static final String TAG = "MemoryAdapter";

    private List<Memory> memoryList;
    private OnItemClickListener listener;
    private Context mContext;

    public MemoryAdapter(List<Memory> memoryList, Context mContext){
        this.memoryList = memoryList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MemoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.gallery_item, parent, false);
        return new MemoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemoryViewHolder holder, int position) {
        Memory memory = memoryList.get(position);
        holder._memoryDate.setText(memory.getMemoryDate());
        Glide.with(mContext)
                .load(memory.getMemoryPhoto())
                .into(holder._memoryImageURL);
        Log.d(TAG, "onBindViewHolder: " + memory.getMemoryDate());

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return memoryList.size();
    }

    public interface OnItemClickListener{
        void OnItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
