package com.rolnik.birthdayreminder.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.rolnik.birthdayreminder.R;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;

public class IconInfoDialog extends Dialog {
    @BindView(R.id.okButton)
    Button okButton;
    @BindView(R.id.credits)
    TableLayout credits;

    private float density;

    public IconInfoDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.icon_info_dialog);
        ButterKnife.bind(this);
        init();

        density = context.getResources().getDisplayMetrics().density;
    }

    private void init() {
        okButton.setOnClickListener(view -> dismiss());

        addAllCredits();
    }

    private void addAllCredits() {
        credits.addView(createTableRow(R.drawable.party, "Freepik", "https://www.freepik.com/"));
        credits.addView(createTableRow(R.drawable.anniversary, "Freepik", "https://www.freepik.com/"));
        credits.addView(createTableRow(R.drawable.birthday, "Pixel perfect", "https://www.flaticon.com/authors/pixel-perfect"));
        credits.addView(createTableRow(R.drawable.name, "Icongeek26", "https://www.flaticon.com/authors/icongeek26"));
        credits.addView(createTableRow(R.drawable.contact, "Smashicons", "https://www.flaticon.com/authors/smashicons"));
        credits.addView(createTableRow(R.drawable.sad, "Baianat", "https://www.flaticon.com/authors/baianat"));
    }

    private TableRow createTableRow(int imageResourceId, String author, String URL){
        TableRow tableRow = (TableRow) LayoutInflater.from(getContext()).inflate(R.layout.icon_row, credits, false);

        ImageView imageView = tableRow.findViewById(R.id.iconImage);
        imageView.setImageResource(imageResourceId);

        TextView textView = tableRow.findViewById(R.id.author);
        textView.setOnClickListener(view -> startBrowser(URL));
        textView.setText(author);

        return tableRow;
    }

    private void startBrowser(String URL){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
        browserIntent.setData(Uri.parse(URL));
        getContext().startActivity(browserIntent);
    }
}
