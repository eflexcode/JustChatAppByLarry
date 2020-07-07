package com.example.justchat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.justchat.model.Users;
import com.example.justchat.viewmodle.UsersViewModel;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class AboutUserActivity extends AppCompatActivity {

    ImageView coverImage;
    CircleImageView usersImage;

    TextView username;
    TextView dateOfBirth;
    TextView aboutUser;

    String myId;
    Intent intent;

    UsersViewModel usersViewModel;

    String usersImageUrl;
    String coverImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_user);

        StartAppSDK.init(this, "204378826", false);
        FrameLayout container = findViewById(R.id.fragment_main2);
        if (container != null && container.getChildCount() < 1) {
            container.addView(new Banner(this), new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.CENTER));
        }

        StartAppAd.showAd(this);


        coverImage = findViewById(R.id.cover_image);
        usersImage = findViewById(R.id.profile_image);

        username = findViewById(R.id.username);
        dateOfBirth = findViewById(R.id.date);
        aboutUser = findViewById(R.id.about_user);

        intent = getIntent();
        myId = intent.getStringExtra("clickedId");
//        Toast.makeText(this, myId, Toast.LENGTH_SHORT).show();

        usersViewModel = ViewModelProviders.of(this).get(UsersViewModel.class);
        usersViewModel.usersLiveDataWithId(myId).observe(this, new Observer<Users>() {
            @Override
            public void onChanged(Users users) {

                username.setText(users.getUsername());
                dateOfBirth.setText(users.getDateOfBirth());
                aboutUser.setText(users.getAboutUser());

                usersImageUrl = users.getImageUrl();
                coverImageUrl = users.getCoverImageUrl();

                if (users.getImageUrl().equals("default")){
                    usersImage.setImageResource(R.drawable.no_profile);
                }else {
                    Glide.with(AboutUserActivity.this).load(users.getImageUrl()).into(usersImage);
                }

                if (users.getCoverImageUrl().equals("default")){
                    coverImage.setImageResource(R.drawable.cover);
                }else {
                    Glide.with(AboutUserActivity.this).load(users.getCoverImageUrl()).into(coverImage);
                }

            }
        });

//        overridePendingTransition(android.R.anim.slide_out_right,android.R.anim.slide_in_left);


        usersImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AboutUserActivity.this,ImageFullSizeActivity.class);
                intent.putExtra("uri",usersImageUrl);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);

            }
        });

        coverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AboutUserActivity.this,ImageFullSizeActivity.class);
                intent.putExtra("uri",coverImageUrl);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.fade_out);
    }

    public void sendMessage(View view) {

        Intent intent = new Intent(this,MessageActivity.class);
        intent.putExtra("messageUId",myId);
        startActivity(intent);

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
