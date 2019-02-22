package com.rolnik.birthdayreminder.adapters;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rolnik.birthdayreminder.R;
import com.rolnik.birthdayreminder.databinding.EventLayoutBinding;
import com.rolnik.birthdayreminder.model.Event;

import java.util.Calendar;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventViewHolder> {
    public enum Mode {
        SHOW, SELECT
    }

    private List<Event> events;
    private Context context;
    private ViewGroup rootGroup;
    private Mode mode;
    private OnSelectedListener onSelectedListener;

    public EventAdapter(List<Event> events, Context context, ViewGroup rootGroup, OnSelectedListener onSelectedListener) {
        this.events = events;
        this.context = context;
        this.rootGroup = rootGroup;
        this.onSelectedListener = onSelectedListener;
        this.mode = Mode.SHOW;
    }


    public void add(Event event){
        events.add(event);
        notifyItemInserted(events.size() - 1);
    }

    public void addAll(List<Event> events){
        for(Event event : events){
            add(event);
        }
    }

    public void remove(Event event){
        int position = events.indexOf(event);
        events.remove(event);
        notifyItemRemoved(position);
    }

    public void removeAll(List<Event> events){
        for(Event event : events){
            remove(event);
        }
    }

    public void clear(){
        int size = events.size();
        events.clear();
        notifyItemRangeRemoved(0, size);
    }

    public Event getItem(int position){
        return events.get(position);
    }

    public void setMode(Mode mode){
        this.mode = mode;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        EventLayoutBinding eventLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.event_layout, viewGroup, false);

        return new EventViewHolder(eventLayoutBinding, rootGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull final EventViewHolder basicViewHolder, int i) {
        if(mode == Mode.SHOW){
            basicViewHolder.initShowMode(this.onSelectedListener);
        } else {
            basicViewHolder.initSelectMode(this.onSelectedListener);
        }
        basicViewHolder.bind(events.get(i));
        basicViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(rootGroup.getContext(), i%2 == 0 ? R.color.bone : R.color.steelBlue));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    @Override
    public long getItemId(int position){
        return events.get(position).getId();
    }


}
