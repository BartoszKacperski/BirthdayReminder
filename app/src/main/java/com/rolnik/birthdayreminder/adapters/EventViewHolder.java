package com.rolnik.birthdayreminder.adapters;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rolnik.birthdayreminder.R;
import com.rolnik.birthdayreminder.databinding.EventLayoutBinding;
import com.rolnik.birthdayreminder.model.Event;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.eventImage)
    ImageView eventImage;
    @BindView(R.id.eventTitle)
    TextView eventTitle;
    @BindView(R.id.eventDate)
    TextView eventDate;
    @BindView(R.id.cardView)
    CardView cardView;

    private EventLayoutBinding eventLayoutBinding;
    protected ViewGroup rootGroup;

    public EventViewHolder(EventLayoutBinding eventLayoutBinding, ViewGroup rootGroup) {
        super(eventLayoutBinding.getRoot());
        ButterKnife.bind(this, itemView);
        this.eventLayoutBinding = eventLayoutBinding;
        this.rootGroup = rootGroup;
    }


    public void initShowMode() {
        cardView.setAlpha(1.0f);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Transition transition = new AutoTransition();
                transition.setDuration(500);

                TransitionManager.beginDelayedTransition(rootGroup, transition);
                int visibility = eventTitle.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
                eventTitle.setVisibility(visibility);
            }
        });
    }

    public void initSelectMode(final OnSelectedListener onSelectedListener) {
        cardView.setAlpha(0.5f);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Transition transition = new AutoTransition();
                transition.setDuration(500);

                TransitionManager.beginDelayedTransition(rootGroup, transition);
                float alpha = cardView.getAlpha() == 1.f ? 0.5f : 1.f;
                cardView.setAlpha(alpha);
                onSelectedListener.onSelected(getAdapterPosition());
            }
        });
    }

    public void bind(Event event){
        eventLayoutBinding.setEvent(event);
    }

}
