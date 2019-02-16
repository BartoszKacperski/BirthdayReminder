package com.rolnik.birthdayreminder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.rolnik.birthdayreminder.R;
import com.rolnik.birthdayreminder.adapters.EventAdapter;
import com.rolnik.birthdayreminder.database.DataBaseService;
import com.rolnik.birthdayreminder.database.EventDataBase;
import com.rolnik.birthdayreminder.model.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.MaybeObserver;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
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
    @BindView(R.id.addFirstButton)
    Button addFirstButton;
    @BindView(R.id.nestedScrollView)
    NestedScrollView nestedScrollView;
    @BindView(R.id.adView)
    AdView adView;

    private EventAdapter eventAdapter;
    private List<Event> selectedEvents = new ArrayList<>();
    private CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        initEvents();
        initAdMob();
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
                changeRecyclerModeToSelect();
                return true;
            case R.id.add:
                startAddActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        if(disposables != null){
            disposables.dispose();
        }
    }

    public void delete(View view) {
        removeEvents();
    }

    private void initEvents() {
        eventAdapter = new EventAdapter(new ArrayList<>(), this, root, position -> {
            Event selectedEvent = eventAdapter.getItem(position);

            if(selectedEvents.contains(selectedEvent)){
                selectedEvents.remove(selectedEvent);
            } else {
                selectedEvents.add(eventAdapter.getItem(position));
            }
        });

        eventAdapter.setHasStableIds(true);
        events.setAdapter(eventAdapter);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        events.setLayoutManager(staggeredGridLayoutManager);

        loadEvents();
    }

    private void initAdMob() {
        MobileAds.initialize(this, getString(R.string.AD_BANNER_ID));
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("5D7F69A3D73C399D9254BFAE86A8E37B").addTestDevice("62C5E6B1611C3CA6C1DEFA2C30D325EA").build();
        adView.loadAd(adRequest);
    }

    public void startFirstAdd(View view){
        startAddActivity();
    }

    private void startAddActivity(){
        Intent intent = new Intent(this, AddEventActivity.class);

        startActivity(intent);
    }



    private void loadEvents() {
        EventDataBase eventDataBase = DataBaseService.getEventDataBaseInstance(getApplicationContext());

        eventDataBase.eventDao().getAll().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<Event>>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i("Loading events", "Loading events started");
                disposables.add(d);
            }

            @Override
            public void onNext(List<Event> events) {
                Log.i("Loading events", "Loaded " + events.size() + " events");
                showAddFirstButtonIf(events.isEmpty());
                eventAdapter.clear();
                eventAdapter.addAll(events);
            }

            @Override
            public void onError(Throwable t) {
                Log.e("Loading events", "Error message = " + t.getMessage());
            }

            @Override
            public void onComplete() {
                Log.i("Loading events", "Loading events completed");
            }
        });
    }

    private void removeEvents(){
        EventDataBase eventDataBase = DataBaseService.getEventDataBaseInstance(getApplicationContext());

        eventDataBase.eventDao().deleteAll(selectedEvents).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new MaybeObserver<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposables.add(d);
                Log.i("Deleting events", "Deleting started");
            }

            @Override
            public void onSuccess(Integer integer) {
                Log.i("Deleting events", "Deleted " + integer + " events");
                selectedEvents.clear();
                changeRecyclerModeToShow();
            }

            @Override
            public void onError(Throwable e) {
                Log.e("Deleting events", "Error message " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.i("Deleting events", "Deleting completed");
            }
        });

    }

    private void changeRecyclerModeToShow(){
        eventAdapter.setMode(EventAdapter.Mode.SHOW);
        TransitionManager.beginDelayedTransition(root);
        deleteButton.setVisibility(View.GONE);
    }

    private void changeRecyclerModeToSelect(){
        TransitionManager.beginDelayedTransition(nestedScrollView);
        eventAdapter.setMode(EventAdapter.Mode.SELECT);
        deleteButton.setVisibility(View.VISIBLE);
    }

    private void showAddFirstButtonIf(boolean emptyEvents){
        int visibility = emptyEvents ? View.VISIBLE : View.GONE;

        TransitionManager.beginDelayedTransition(root);
        addFirstButton.setVisibility(visibility);
    }

}
