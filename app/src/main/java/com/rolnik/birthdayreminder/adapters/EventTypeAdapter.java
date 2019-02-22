package com.rolnik.birthdayreminder.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rolnik.birthdayreminder.activities.DataBindingAdapters;
import com.rolnik.birthdayreminder.R;
import com.rolnik.birthdayreminder.model.Event;

public class EventTypeAdapter extends ArrayAdapter<Event.EventType> {


    public EventTypeAdapter(@NonNull Context context, int resource) {
        super(context, resource, Event.EventType.values());
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.eventtype_dropdown_spinner, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.text);
        ImageView imageView = convertView.findViewById(R.id.eventTypeImage);

        if(position < Event.EventType.values().length){
            textView.setText(DataBindingAdapters.eventTypeToStringResourceId(Event.EventType.values()[position]));
            imageView.setImageResource(DataBindingAdapters.eventTypeToDrawableResourceId(Event.EventType.values()[position]));
        }

        return convertView;

    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.eventtype_spinner_layout, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.text);
        ImageView imageView = convertView.findViewById(R.id.eventTypeImage);

        if(position < Event.EventType.values().length){
            textView.setText(DataBindingAdapters.eventTypeToStringResourceId(Event.EventType.values()[position]));
            imageView.setImageResource(DataBindingAdapters.eventTypeToDrawableResourceId(Event.EventType.values()[position]));
        }

        return convertView;
    }

    @Override
    public int getPosition(Event.EventType eventType){
        Event.EventType[] eventTypes = Event.EventType.values();

        for(int i = 0; i < eventTypes.length; i++){
            if(eventTypes[i] == eventType){
                return i;
            }
        }

        return -1;
    }


}
