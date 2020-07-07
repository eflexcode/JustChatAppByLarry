package com.example.justchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

public class ImageFullSizeActivity extends AppCompatActivity {

    Toolbar toolbar;

    ImageView imageView;

    Intent intent;

    View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full_size);

        toolbar = findViewById(R.id.toolbar);
        imageView = findViewById(R.id.full_view);
        v = findViewById(R.id.view);
        intent = getIntent();

        String uri = intent.getStringExtra("uri");

        setSupportActionBar(toolbar);
        setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);

            }
        });

        assert uri != null;
        if (uri.equals("default")){
            Snackbar.make(v,"cannot load image because this user image url is set to default",Snackbar.LENGTH_INDEFINITE).show();
        }else {
            Glide.with(this).load(uri).into(imageView);
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);
    }
}
