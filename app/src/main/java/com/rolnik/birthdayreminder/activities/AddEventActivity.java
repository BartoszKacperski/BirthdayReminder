package com.rolnik.birthdayreminder.activities;

import android.os.Bundle;

import com.rolnik.birthdayreminder.R;
import com.rolnik.birthdayreminder.fragments.AddEventFragment;
import com.rolnik.birthdayreminder.fragments.FragmentCallback;
import com.rolnik.birthdayreminder.model.Event;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class AddEventActivity extends AppCompatActivity implements FragmentCallback {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        Event event = null;

        if(getIntent().hasExtra(getString(R.string.event))){
            event = (Event) getIntent().getSerializableExtra(getString(R.string.event));
        }


        attachAddEventFragment(event);
    }

    private void attachAddEventFragment(Event event) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentPlaceholder, AddEventFragment.getInstance(event));
        ft.commit();
    }


    @Override
    public void onSave() {
        super.onBackPressed();
    }

    @Override
    public void onCancel() {
        super.onBackPressed();
    }
}
