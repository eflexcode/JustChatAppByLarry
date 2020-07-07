package com.example.justchat.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.justchat.R;
import com.example.justchat.adapter.ChatListAdapter;
import com.example.justchat.model.Users;
import com.example.justchat.viewmodle.UsersViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;


public class ChatsFragment extends Fragment {

    RecyclerView recyclerView;
    UsersViewModel usersViewModel;
    ChatListAdapter chatListAdapter;

    EditText search;
    List<Users> usersList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        chatListAdapter = new ChatListAdapter(getActivity());

        search = view.findViewById(R.id.search);

        recyclerView = view.findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        usersViewModel = ViewModelProviders.of(getActivity()).get(UsersViewModel.class);
        usersViewModel.getChatUsers().observe(getViewLifecycleOwner(), new Observer<List<Users>>() {
            @Override
            public void onChanged(List<Users> users) {

                usersList = new ArrayList<>(users);

                chatListAdapter.setUsersList(users);
                recyclerView.setAdapter(chatListAdapter);
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s!= null){
                    searchUsers(s.toString());
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void searchUsers(String toString) {

        ArrayList<Users> doneList = new ArrayList<>();

        assert usersList != null;
        for (Users users : usersList){

            if (users.getUsername().toLowerCase().contains(toString.toLowerCase())){
                doneList.add(users);
            }

        }

        chatListAdapter.setUsersList(doneList);
        recyclerView.setAdapter(chatListAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();

    }
}
