package com.example.justchat.fragments;

import android.os.Bundle;

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
import com.example.justchat.adapter.UsersChatAdapter;
import com.example.justchat.model.Users;
import com.example.justchat.viewmodle.UsersViewModel;

import java.util.List;

public class UsersFragment extends Fragment {

    RecyclerView recyclerView;
    UsersChatAdapter usersChatAdapter;
    EditText editText;
    UsersViewModel usersViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_users, container, false);

        editText = view.findViewById(R.id.search);
        recyclerView = view.findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        usersViewModel = ViewModelProviders.of(getActivity()).get(UsersViewModel.class);
        usersViewModel.getAllUsers().observe(getViewLifecycleOwner(), new Observer<List<Users>>() {
            @Override
            public void onChanged(List<Users> users) {

                usersChatAdapter = new UsersChatAdapter(users,getActivity());
                recyclerView.setAdapter(usersChatAdapter);

            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    public void search(String search){
        usersViewModel.searchUsers(search).observe(getViewLifecycleOwner(), new Observer<List<Users>>() {
            @Override
            public void onChanged(List<Users> users) {
                usersChatAdapter = new UsersChatAdapter(users,getActivity());
                recyclerView.setAdapter(usersChatAdapter);

            }
        });
    }
}
