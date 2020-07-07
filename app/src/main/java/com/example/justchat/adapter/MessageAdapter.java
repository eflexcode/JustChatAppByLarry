package com.example.justchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.justchat.R;
import com.example.justchat.model.Chats;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageAdapterViewHolder> {

    List<Chats> chatsList;

    Context context;

    String imageUrl;

    public static final int Me = 1;
    public static final int You = 2;

    public MessageAdapter(List<Chats> chatsList, Context context, String imageUrl) {
        this.chatsList = chatsList;
        this.context = context;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MessageAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == Me) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_me_layout, parent, false);
            return new MessageAdapterViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_you_layout, parent, false);
            return new MessageAdapterViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapterViewHolder holder, int position) {

        Chats chats = chatsList.get(position);
        holder.message.setText(chats.getMessage());

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (position == chatsList.size() - 1) {

            if (chats.getIssen()) {
                holder.isseen.setText("seen");
            } else {
                holder.isseen.setText("delivered");
            }
        } else {
            holder.isseen.setVisibility(View.GONE);
        }

        if (imageUrl.equals("default")) {
            holder.profileImage.setImageResource(R.drawable.no_profile);
        } else {
            Glide.with(context).load(imageUrl).into(holder.profileImage);
        }

    }

    @Override
    public int getItemCount() {
        return chatsList.size();
    }

    @Override
    public int getItemViewType(int position) {

        Chats chats = chatsList.get(position);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (chats.getSenderId().equals(user.getUid())) {
            return Me;
        } else {
            return You;
        }

    }

    class MessageAdapterViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profileImage;
        TextView message;
        TextView isseen;

        public MessageAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profile_image_chat);
            message = itemView.findViewById(R.id.chat_message);
            isseen = itemView.findViewById(R.id.isseen);

        }
    }

}
