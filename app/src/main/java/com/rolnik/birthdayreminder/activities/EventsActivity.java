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
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rolnik.birthdayreminder.BirthdayReminderApplication;
import com.rolnik.birthdayreminder.R;
import com.rolnik.birthdayreminder.adapters.EventAdapter;
import com.rolnik.birthdayreminder.adapters.OnSelectedListener;
import com.rolnik.birthdayreminder.database.EventDataBase;
import com.rolnik.birthdayreminder.dialogs.IconInfoDialog;
import com.rolnik.birthdayreminder.dialogs.NotificationInfoDialog;
import com.rolnik.birthdayreminder.fragments.AddEventFragment;
import com.rolnik.birthdayreminder.fragments.FragmentCallback;
import com.rolnik.birthdayreminder.model.Event;
import com.rolnik.birthdayreminder.notificationserivces.AlarmCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class EventsActivity extends AppCompatActivity implements FragmentCallback {
    @BindView(R.id.root)
    CoordinatorLayout root;
    @BindView(R.id.events)
    RecyclerView events;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.addFirstButton)
    Button addFirstButton;
    @BindView(R.id.nestedScrollView)
    NestedScrollView nestedScrollView;
    @BindView(R.id.adView)
    AdView adView;
    @BindView(R.id.bottomNavigationView)
    BottomNavigationView bottomNavigationView;

    @Inject
    EventDataBase eventDataBase;

    private EventAdapter eventAdapter;
    private List<Event> selectedEvents = new ArrayList<>();
    private CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        ButterKnife.bind(this);
        ((BirthdayReminderApplication) getApplication()).getDbComponent().inject(this);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        initEvents();
        initAdMob();
        initBottomNavigation();
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
                showAddEvent();
                return true;
            case R.id.notification:
                showNotificationInfoDialog();
                return true;
            case R.id.icons:
                showIconsInfoDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (disposables != null) {
            disposables.dispose();
        }
    }

    @Override
    public void onSave() {
        removeAddFragment();
    }

    @Override
    public void onCancel() {
        removeAddFragment();
    }

    private void initEvents() {
        eventAdapter = new EventAdapter(new ArrayList<>(), this, root, createOnSelectedListener());

        eventAdapter.setHasStableIds(true);
        events.setAdapter(eventAdapter);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        events.setLayoutManager(staggeredGridLayoutManager);

        loadEvents();
    }

    private OnSelectedListener createOnSelectedListener() {
        return new OnSelectedListener() {
            @Override
            public void onSelected(int position) {
                Event selectedEvent = eventAdapter.getItem(position);

                if (selectedEvents.contains(selectedEvent)) {
                    selectedEvents.remove(selectedEvent);
                } else {
                    selectedEvents.add(eventAdapter.getItem(position));
                }
            }

            @Override
            public void onLongClick(int position) {
                startAddEventWithExistedEvent(eventAdapter.getItem(position));
            }
        };
    }

    private void startAddEventWithExistedEvent(Event event) {
        String rootTag = (String) root.getTag();
        Log.i("Root tag ", rootTag);

        if (rootTag.equals(getString(R.string.normal))) {
            startAddActivityWith(event);
        } else {
            showAddFragmentWith(event);
        }

    }

    private void showAddEvent() {
        String rootTag = (String) root.getTag();
        Log.i("Root tag ", rootTag);

        if (rootTag.equals(getString(R.string.normal))) {
            startAddActivity();
        } else {
            showAddFragment();
        }
    }

    private void startAddActivity() {
        Intent intent = new Intent(this, AddEventActivity.class);

        startActivity(intent);
    }

    private void startAddActivityWith(Event event) {
        Intent intent = new Intent(this, AddEventActivity.class);

        intent.putExtra(getString(R.string.event), event);

        startActivity(intent);
    }

    private void showAddFragment() {
        removeAddFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().addToBackStack(getString(R.string.add_fragment));
        fragmentTransaction.replace(R.id.fragmentPlaceholder, AddEventFragment.getInstance(null));
        fragmentTransaction.commit();
    }

    private void showAddFragmentWith(Event event) {
        removeAddFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().addToBackStack(getString(R.string.add_fragment));
        fragmentTransaction.replace(R.id.fragmentPlaceholder, AddEventFragment.getInstance(event));
        fragmentTransaction.commit();
    }

    private void removeAddFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate();
        }
    }

    private void initAdMob() {
        MobileAds.initialize(this, getString(R.string.AD_BANNER_ID));
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("5D7F69A3D73C399D9254BFAE86A8E37B").addTestDevice("62C5E6B1611C3CA6C1DEFA2C30D325EA").build();
        adView.loadAd(adRequest);
    }

    private void showNotificationInfoDialog() {
        NotificationInfoDialog infoDialog = new NotificationInfoDialog(this);

        infoDialog.show();
    }

    private void showIconsInfoDialog() {
        IconInfoDialog infoDialog = new IconInfoDialog(this);

        infoDialog.show();
    }


    private void initBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.delete:
                    removeEvents();
                    return true;
                case R.id.cancel:
                    changeRecyclerModeToShow();
                    return true;
                default:
                    return false;
            }
        });
    }

    public void startFirstAdd(View view) {
        showAddEvent();
    }


    private void loadEvents() {
        disposables.add(eventDataBase.eventDao().getAll().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                eventsList -> {
                    Log.i("Loading events", "Loaded " + eventsList.size() + " events");
                    showAddFirstButtonIf(eventsList.isEmpty());
                    eventAdapter.clear();
                    eventAdapter.addAll(eventsList);
                }, t -> {
                    Log.e("Loading events", "Error message = " + t.getMessage());
                    showToast(getString(R.string.download_error));
                }
        ));
    }

    private void removeEvents() {
        disposables.add(eventDataBase.eventDao().deleteAll(selectedEvents).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                integer -> {
                    Log.i("Deleting events", "Deleted " + integer + " events");
                    for (Event event : selectedEvents) {
                        AlarmCreator.cancelAlarm(getApplicationContext(), event);
                    }
                    selectedEvents.clear();
                    changeRecyclerModeToShow();
                }, t -> {
                    Log.e("Deleting events", "Error message " + t.getMessage());
                    showToast(getString(R.string.removing_error));
                }
        ));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void changeRecyclerModeToShow() {
        eventAdapter.setMode(EventAdapter.Mode.SHOW);
        TransitionManager.beginDelayedTransition(root);
        bottomNavigationView.setVisibility(View.GONE);
        adView.setVisibility(View.VISIBLE);
    }

    private void changeRecyclerModeToSelect() {
        eventAdapter.setMode(EventAdapter.Mode.SELECT);
        TransitionManager.beginDelayedTransition(root);
        bottomNavigationView.setVisibility(View.VISIBLE);
        adView.setVisibility(View.GONE);
    }

    private void showAddFirstButtonIf(boolean emptyEvents) {
        int visibility = emptyEvents ? View.VISIBLE : View.GONE;

        TransitionManager.beginDelayedTransition(root);
        addFirstButton.setVisibility(visibility);
    }

}
