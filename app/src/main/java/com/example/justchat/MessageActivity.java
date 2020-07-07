package com.example.justchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.justchat.adapter.MessageAdapter;
import com.example.justchat.cloudmessaging.APIClient;
import com.example.justchat.cloudmessaging.Data;
import com.example.justchat.cloudmessaging.MessageBody;
import com.example.justchat.cloudmessaging.RetrofitClient;
import com.example.justchat.cloudmessaging.Token;
import com.example.justchat.model.Chats;
import com.example.justchat.model.Users;
import com.example.justchat.viewmodle.UsersViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.okhttp.ResponseBody;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

    Toolbar toolbar;
    UsersViewModel usersViewModel;
    Intent intent;
    String messageIdUser;
    TextView username;
    CircleImageView usersImage;
    EditText message_edit_text;
    FloatingActionButton floatingActionButton;
    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    FirebaseUser firebaseUser;
    String imageUrl;

    boolean notify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        username = findViewById(R.id.username);

        usersImage = findViewById(R.id.profile_image);

        message_edit_text = findViewById(R.id.message_edit_text);
        floatingActionButton = findViewById(R.id.sendMessageToFirebase);
        recyclerView = findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        setTitle("");

        intent = getIntent();
        messageIdUser = intent.getStringExtra("messageUId");

        floatingActionButton.setImageResource(R.drawable.ic_send_2);

        message_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String s1 = s.toString();
                if (s1.equals("")) {
                    floatingActionButton.setImageResource(R.drawable.ic_send_2);
                } else {
                    floatingActionButton.setImageResource(R.drawable.ic_send);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String token = FirebaseInstanceId.getInstance().getToken();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tokens").child(firebaseUser.getUid());

        Token token1 = new Token(token);

        databaseReference.setValue(token1);

        usersViewModel = ViewModelProviders.of(this).get(UsersViewModel.class);
        usersViewModel.usersLiveDataWithId(messageIdUser).observe(this, new Observer<Users>() {
            @Override
            public void onChanged(Users users) {

                username.setText(users.getUsername());

                imageUrl = users.getImageUrl();

                if (users.getImageUrl().equals("default")) {
                    usersImage.setImageResource(R.drawable.no_profile);
                } else {
                    Glide.with(MessageActivity.this).load(users.getImageUrl()).into(usersImage);
                }

            }
        });


        usersViewModel.issen(firebaseUser.getUid(), messageIdUser);

        usersViewModel.readMessageFromFirebase(firebaseUser.getUid(), messageIdUser).observe(this, new Observer<List<Chats>>() {
            @Override
            public void onChanged(List<Chats> chats) {

                messageAdapter = new MessageAdapter(chats, MessageActivity.this, imageUrl);
                recyclerView.setAdapter(messageAdapter);
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                final String message = message_edit_text.getText().toString();
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                if (message.trim().isEmpty()) {
                    Toast.makeText(MessageActivity.this, "can send an empty message", Toast.LENGTH_LONG).show();
                    return;
                }

                usersViewModel.sendUserMessageToFirebase(firebaseUser.getUid(), messageIdUser, message, false);

                message_edit_text.setText("");



            }
        });


    }

    private static final String TAG = "MessageActivity";

    public void close(View view) {
        finish();
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
