package com.sheqal.closer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewholder> {

    private ArrayList<EventItem> mEventItems;

    public static class EventViewholder extends RecyclerView.ViewHolder{

        public TextView eventTitle;
        public TextView countedDays;
        public TextView userTitle;
        public TextView originalDate;

        public EventViewholder(@NonNull View itemView) {
            super(itemView);
            eventTitle = itemView.findViewById(R.id._eventTitle);
            countedDays = itemView.findViewById(R.id._eventDays);
            userTitle = itemView.findViewById(R.id._eventUserTitle);
            originalDate = itemView.findViewById(R.id._eventDate);
        }
    }

    public EventAdapter(ArrayList<EventItem> eventItems){
        mEventItems = eventItems;
    }

    @NonNull
    @Override
    public EventViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        EventViewholder viewholder = new EventViewholder(v);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewholder holder, int position) {
        EventItem currentItem = mEventItems.get(position);
        holder.eventTitle.setText(currentItem.get_eventType());
        holder.userTitle.setText(currentItem.get_userTitle());
        holder.originalDate.setText(currentItem.get_originalDate());
        holder.countedDays.setText(currentItem.get_countedDays());
    }

    @Override
    public int getItemCount() {
        return mEventItems.size();
    }
}
