package com.sheqal.closer;

import android.util.EventLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewholder> {

    public ArrayList<EventItem> mEventItem;

    public static class EventViewholder extends RecyclerView.ViewHolder{

        public TextView eventTitle, remainingDays, userTitle, originalDate;


        public EventViewholder(@NonNull View itemView) {
            super(itemView);

            eventTitle = itemView.findViewById(R.id._eventTitle);
            remainingDays = itemView.findViewById(R.id._anniversaryRemainingDays);
            userTitle = itemView.findViewById(R.id._userTitle);
            originalDate = itemView.findViewById(R.id._anniversaryOriginalDate);

        }
    }

    public EventAdapter(ArrayList<EventItem> eventItem){
        mEventItem = eventItem;
    }

    @NonNull
    @Override
    public EventViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        EventViewholder viewHolder = new EventViewholder(v);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewholder holder, int position) {
        EventItem currentItem = mEventItem.get(position);
        holder.eventTitle.setText(currentItem.getEventTitle());
        holder.remainingDays.setText(currentItem.getRemainingDays());
        holder.userTitle.setText(currentItem.getUserTitle());
        holder.originalDate.setText(currentItem.getOriginalDate());
    }

    @Override
    public int getItemCount() {
        return mEventItem.size();
    }
}
