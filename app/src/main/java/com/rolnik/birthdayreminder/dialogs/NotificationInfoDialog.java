package com.rolnik.birthdayreminder.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import com.rolnik.birthdayreminder.CustomDatePicker;
import com.rolnik.birthdayreminder.R;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationInfoDialog extends Dialog {
    @BindView(R.id.okButton)
    Button okButton;
    @BindView(R.id.openSettings)
    Button openSettings;

    public NotificationInfoDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.notification_info_dialog);
        ButterKnife.bind(this);
        
        init();
    }

    private void init() {
        okButton.setOnClickListener(view -> dismiss());
        openSettings.setOnClickListener(view -> openApplicationSettings());
    }

    private void openApplicationSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
        intent.setData(uri);
        getContext().startActivity(intent);
    }
}
