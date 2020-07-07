package com.example.justchat.adapter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.justchat.AboutUserActivity;
import com.example.justchat.R;
import com.example.justchat.model.Users;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersChatAdapter extends RecyclerView.Adapter<UsersChatAdapter.ViewHolder> {

    List<Users> usersList;
    Context context;

    public UsersChatAdapter(List<Users> usersList,Context context) {
        this.usersList = usersList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.users_card,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Users users = usersList.get(position);

        holder.username.setText(users.getUsername());
        if (users.getImageUrl().equals("default")){
            holder.profileImage.setImageResource(R.drawable.no_profile);
        }else {
            Glide.with(context).load(users.getImageUrl()).into(holder.profileImage);
        }

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView profileImage;
        TextView username;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.user_image);
            username = itemView.findViewById(R.id.username);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Users users = usersList.get(getAdapterPosition());

                    Intent intent = new Intent(context, AboutUserActivity.class);
                    intent.putExtra("clickedId",users.getUserId());
                    context.startActivity(intent, ActivityOptions.makeCustomAnimation(context,android.R.anim.slide_in_left,android.R.anim.slide_out_right).toBundle());
                }
            });

        }
    }

}
