package com.example.justchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//import gr.net.maroulis.library.EasySplashScreen;

public class PlashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        int count = 2000000;

        for (int i = 1; i <= count; i++) {

            if (i == count) {
                if (firebaseUser != null) {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, StartActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        }
//        EasySplashScreen easySplashScreen = new EasySplashScreen(this)
//                .withFullScreen()
//                .withSplashTimeOut(4000)
//                .withTargetActivity(StartActivity.class)
//                .withBackgroundColor(Color.parseColor("#00356a"))
//                .withBeforeLogoText("just chat");
//        easySplashScreen.getBeforeLogoTextView().setTextColor(getResources().getColor(R.color.colorWith));
//        easySplashScreen.getBeforeLogoTextView().setTextSize(40);
////        easySplashScreen.getBeforeLogoTextView().;
//
//        View v = easySplashScreen.create();

//        setContentView(v);
    }
}
