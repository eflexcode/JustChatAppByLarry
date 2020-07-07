package com.example.justchat.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.justchat.ForgetPasswordActivity;
import com.example.justchat.R;
import com.example.justchat.viewmodle.UsersViewModel;
import com.rengwuxian.materialedittext.MaterialEditText;


public class LoginFragment extends Fragment {

    Button login;
    MaterialEditText email;
    MaterialEditText password;
    TextView forget;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        email = view.findViewById(R.id.email_login);
        password = view.findViewById(R.id.password_login);
        forget = view.findViewById(R.id.forget);
        login = view.findViewById(R.id.login_btn);
        progressBar = view.findViewById(R.id.progress_bar);

        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });

        UsersViewModel usersViewModel = ViewModelProviders.of(getActivity()).get(UsersViewModel.class);
        usersViewModel.loginSuccessfulLive().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean){
                    progressBar.setVisibility(View.VISIBLE);
                }else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getEmail = email.getText().toString();
                String getPassword = password.getText().toString();

                progressBar.setVisibility(View.VISIBLE);
                UsersViewModel usersViewModel = ViewModelProviders.of(getActivity()).get(UsersViewModel.class);
                usersViewModel.login(getEmail,getPassword);

            }
        });

        return view;
    }
}
