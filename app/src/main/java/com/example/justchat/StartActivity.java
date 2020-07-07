package com.example.justchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.justchat.fragments.LoginFragment;
import com.example.justchat.fragments.SignUpFragment;
import com.example.justchat.viewmodle.DateViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.util.Calendar;

public class StartActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    BottomNavigationView bottomNavigationView;
    Fragment fragment;
    DateViewModel dateViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        bottomNavigationView = findViewById(R.id.bnv_start);

        dateViewModel = ViewModelProviders.of(this).get(DateViewModel.class);

        LoginFragment loginFragment = new LoginFragment();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.start_frame, loginFragment).commit();
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.login_menu:
                        fragment = new LoginFragment();
                        break;
                    case R.id.signup_menu:
                        fragment = new SignUpFragment();
                        break;

                }
                getSupportFragmentManager().beginTransaction().replace(R.id.start_frame, fragment).commit();

                return true;
            }
        });

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String date = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        Toast.makeText(this, date, Toast.LENGTH_SHORT).show();
        dateViewModel.myDate.setValue(date);
    }
}
