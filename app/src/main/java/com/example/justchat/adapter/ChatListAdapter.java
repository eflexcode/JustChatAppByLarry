package com.example.justchat.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.justchat.MessageActivity;
import com.example.justchat.MessageActivityChat;
import com.example.justchat.R;
import com.example.justchat.model.Chats;
import com.example.justchat.model.Users;
import com.example.justchat.viewmodle.UsersViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListAdapterViewHolder> {

    List<Users> usersList;
    Context context;
    String last;
    String lastMessage;

    public ChatListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ChatListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_card, parent, false);

        return new ChatListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListAdapterViewHolder holder, int position) {

        Users users = usersList.get(position);

        holder.username.setText(users.getUsername());

        if (users.getImageUrl().equals("default")) {
            holder.profileImage.setImageResource(R.drawable.no_profile);
        } else {
            Glide.with(context).load(users.getImageUrl()).into(holder.profileImage);
        }

        getLastMessage(holder.last, users.getUserId());

        if (users.getStatus().equals("online")) {
            holder.statusImage.setVisibility(View.VISIBLE);
        } else {
            holder.statusImage.setVisibility(View.GONE);
        }


    }

    private void getLastMessage(TextView last, String userId) {
        UsersViewModel usersViewModel = ViewModelProviders.of((FragmentActivity) context).get(UsersViewModel.class);
        usersViewModel.getLastMessage(last,userId);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    class ChatListAdapterViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profileImage;
        CircleImageView statusImage;

        TextView username;
        TextView last;

        public ChatListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.user_image);
            statusImage = itemView.findViewById(R.id.statuse);

            username = itemView.findViewById(R.id.username);
            last = itemView.findViewById(R.id.last);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Users users = usersList.get(getAdapterPosition());

                    Intent intent = new Intent(context, MessageActivityChat.class);
                    intent.putExtra("messageUId", users.getUserId());
                    context.startActivity(intent);
                }
            });

        }
    }

    public void setUsersList(List<Users> usersList) {
        this.usersList = usersList;
        notifyDataSetChanged();
    }


}
