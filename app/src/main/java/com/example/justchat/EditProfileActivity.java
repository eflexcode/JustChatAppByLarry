package com.example.justchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.justchat.fragments.DatePickerFragment;
import com.example.justchat.viewmodle.UsersViewModel;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;

import java.text.DateFormat;
import java.util.Calendar;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class EditProfileActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    Toolbar toolbar;
    TextView showDate;
    MaterialEditText username;
    MaterialEditText bio;
    Intent intent;
    UsersViewModel usersViewModel;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        StartAppSDK.init(this, "204378826", true);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("update profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);

            }
        });
        FrameLayout container = findViewById(R.id.fragment_main3);
        usersViewModel = ViewModelProviders.of(this).get(UsersViewModel.class);

        if (container != null && container.getChildCount() < 1) {
            container.addView(new Banner(this), new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.CENTER));
        }
        StartAppAd.showAd(this);
        intent = getIntent();
        String intentUsername = intent.getStringExtra("username");
        String intentDate = intent.getStringExtra("date");
        String intentAboutUser = intent.getStringExtra("aboutUser");

//        intent.putExtra("username",getUsername);
//        intent.putExtra("date",getDate);
//        intent.putExtra("aboutUser",getAboutUser);


        showDate = findViewById(R.id.showDate);
        username = findViewById(R.id.update_username);
        bio = findViewById(R.id.update_about_user);

//        if (!intentAboutUser.isEmpty() || intentDate.isEmpty() || intentUsername.isEmpty()){
            showDate.setText(intentDate);
            username.setText(intentUsername);
            bio.setText(intentAboutUser);
//        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);

    }

    public void update(View view) {

        String getUsername = username.getText().toString();
        String getBio = bio.getText().toString();
        String getDate = showDate.getText().toString();

        if (getUsername.trim().isEmpty() || getBio.trim().isEmpty() || getDate.isEmpty()){
            Toast.makeText(this, "all filed are require to update profile", Toast.LENGTH_LONG).show();
            return;
        }

        usersViewModel.updateProfile(getUsername,getBio,getDate);

        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String date = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        Toast.makeText(this, date, Toast.LENGTH_SHORT).show();
       showDate.setText(date);
    }

    public void showDate(View view) {
        DatePickerFragment pickerFragment = new DatePickerFragment();
        pickerFragment.show(getSupportFragmentManager(), "ok");
    }
    @Override
    protected void onResume() {
        super.onResume();
        usersViewModel.updateStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        usersViewModel.updateStatus("offline");
    }
}
