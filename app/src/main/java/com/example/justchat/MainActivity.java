package com.example.justchat;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.ChangeScroll;
import android.transition.Explode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.justchat.cloudmessaging.Token;
import com.example.justchat.fragments.ChatsFragment;
import com.example.justchat.fragments.LoginFragment;
import com.example.justchat.fragments.ProfileFragment;
import com.example.justchat.fragments.SignUpFragment;
import com.example.justchat.fragments.UsersFragment;
import com.example.justchat.model.Users;
import com.example.justchat.viewmodle.UsersViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;

    Fragment fragment;

    FirebaseAuth auth;
    BottomNavigationView bottomNavigationView;

    CircleImageView circleImageView;
    TextView textView;

    UsersViewModel usersViewModel;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bottomNavigationView = findViewById(R.id.bnv_start);
        circleImageView = findViewById(R.id.profile_image);
        FrameLayout container = findViewById(R.id.fragment_main);
        textView = findViewById(R.id.username);

        if (container != null && container.getChildCount() < 1) {
            container.addView(new Banner(this), new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.CENTER));
            StartAppAd.showAd(this);
        }
        StartAppSDK.init(this, "204378826", true);
        StartAppAd.showAd(this);

        usersViewModel = ViewModelProviders.of(this).get(UsersViewModel.class);
        usersViewModel.usersLiveData().observe(this, new Observer<Users>() {
            @Override
            public void onChanged(Users users) {

                textView.setText(users.getUsername());
                if (users.getImageUrl().equals("default")) {
                    circleImageView.setImageResource(R.drawable.no_profile);
                } else {
                    Glide.with(getApplicationContext()).load(users.getImageUrl()).into(circleImageView);
                }

            }
        });
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String token = FirebaseInstanceId.getInstance().getToken();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tokens").child(firebaseUser.getUid());

        Token token1 = new Token(token);

        databaseReference.setValue(token1);
        usersViewModel.updateStatus("online");

        if (savedInstanceState == null) {
            ChatsFragment chatsFragment = new ChatsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.start_frame, chatsFragment).commit();

        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.menu_chats:
                        fragment = new ChatsFragment();
                        break;
                    case R.id.menu_users:
                        fragment = new UsersFragment();
                        break;
                    case R.id.menu_profile:
                        fragment = new ProfileFragment();
                        break;

                }
                getSupportFragmentManager().beginTransaction().replace(R.id.start_frame, fragment).commit();

                return true;
            }
        });

        auth = FirebaseAuth.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_logout:
                usersViewModel.updateStatus("offline");
                auth.signOut();
                Intent intent = new Intent(this, StartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        StartAppAd.showAd(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            usersViewModel.updateStatus("offline");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        usersViewModel.updateStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            usersViewModel.updateStatus("offline");
        }
    }
}