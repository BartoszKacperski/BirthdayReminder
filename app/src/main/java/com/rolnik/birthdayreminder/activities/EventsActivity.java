package com.rolnik.birthdayreminder.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.rolnik.birthdayreminder.adapters.EventAdapter;
import com.rolnik.birthdayreminder.R;
import com.rolnik.birthdayreminder.adapters.OnSelectedListener;
import com.rolnik.birthdayreminder.database.DataBaseService;
import com.rolnik.birthdayreminder.database.EventDataBase;
import com.rolnik.birthdayreminder.model.Event;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.FlowableSubscriber;
import io.reactivex.MaybeObserver;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class EventsActivity extends AppCompatActivity {
    @BindView(R.id.root)
    CoordinatorLayout root;
    @BindView(R.id.events)
    RecyclerView events;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.deleteButton)
    Button deleteButton;
    @BindView(R.id.nestedScrollView)
    NestedScrollView nestedScrollView;

    private EventAdapter eventAdapter;
    private List<Event> selectedEvents = new ArrayList<>();
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        initEvents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                TransitionManager.beginDelayedTransition(nestedScrollView);
                eventAdapter.setMode(EventAdapter.Mode.SELECT);
                deleteButton.setVisibility(View.VISIBLE);
                return true;
            case R.id.add:
                startAddActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initEvents() {
        eventAdapter = new EventAdapter(new ArrayList<>(), this, root, new OnSelectedListener() {
            @Override
            public void onSelected(int position) {
                Event selectedEvent = eventAdapter.getItem(position);

                if(selectedEvents.contains(selectedEvent)){
                    selectedEvents.remove(selectedEvent);
                } else {
                    selectedEvents.add(eventAdapter.getItem(position));
                }
            }
        });

        eventAdapter.setHasStableIds(true);

        events.setAdapter(eventAdapter);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);

        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);

        events.setLayoutManager(staggeredGridLayoutManager);
        loadEvents();
    }

    private void startAddActivity(){
        Intent intent = new Intent(this, AddEventActivity.class);

        startActivity(intent);
    }



    private void loadEvents() {
        EventDataBase eventDataBase = DataBaseService.getEventDataBaseInstance(getApplicationContext());

        eventDataBase.eventDao().getAll().subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<Event>>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(List<Event> events) {
                Log.i("Downloading", " = " + events.size());
                eventAdapter.addAll(events);
            }

            @Override
            public void onError(Throwable t) {
                Log.e("Error", "msg = " + t.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void removeEvents(){
        EventDataBase eventDataBase = DataBaseService.getEventDataBaseInstance(getApplicationContext());

        eventDataBase.eventDao().deleteAll(selectedEvents).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new MaybeObserver<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(Integer integer) {
                Log.i("Deleting", " = " + integer);
                eventAdapter.clear();
                selectedEvents.clear();
                eventAdapter.setMode(EventAdapter.Mode.SHOW);
                TransitionManager.beginDelayedTransition(root);
                deleteButton.setVisibility(View.GONE);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }


    public void delete(View view) {
        removeEvents();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        if(disposable != null){
            disposable.dispose();
        }
    }
}
