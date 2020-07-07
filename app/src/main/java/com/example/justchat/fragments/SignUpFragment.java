package com.example.justchat.fragments;

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
import android.widget.Toast;

import com.example.justchat.R;
import com.example.justchat.viewmodle.DateViewModel;
import com.example.justchat.viewmodle.UsersViewModel;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;


public class SignUpFragment extends Fragment {

    MaterialEditText username;
    MaterialEditText email;
    MaterialEditText password;
    MaterialEditText confirmPassword;

    TextView showDate;

    ProgressBar progressBar;

    String getEmail;
    String getPassword;
    String getConfirmPassword;
    String getUsername;
    String getShowDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        username = view.findViewById(R.id.username_sign_up);
        email = view.findViewById(R.id.email_sign_up);
        password = view.findViewById(R.id.password_sign_up);
        confirmPassword = view.findViewById(R.id.confirm_password_sign_up);

        showDate = view.findViewById(R.id.showDate);

        progressBar = view.findViewById(R.id.progress_bar);

        UsersViewModel usersViewModel = ViewModelProviders.of(getActivity()).get(UsersViewModel.class);
        usersViewModel.signUPSuccessfulLive().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        DateViewModel dateViewModel = ViewModelProviders.of(getActivity()).get(DateViewModel.class);
        dateViewModel.liveDataDate().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                showDate.setText(s);
            }
        });


        Button signUpBtn = view.findViewById(R.id.sign_up_btn);
        Button pickBtn = view.findViewById(R.id.pick_date_bnt);

        pickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment fragment = new DatePickerFragment();
                fragment.show(getFragmentManager(), "code");
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEmail = email.getText().toString();
                getPassword = password.getText().toString();
                getConfirmPassword = confirmPassword.getText().toString();
                getUsername = username.getText().toString();
                getShowDate = showDate.getText().toString();

                if (getEmail.trim().isEmpty() || getUsername.trim().isEmpty()) {
                    Toast.makeText(getActivity(), "all input are required ", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!getEmail.contains("@")) {
                    Toast.makeText(getActivity(), "invalid email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (getShowDate.isEmpty()) {
                    Toast.makeText(getActivity(), "you need to pic a date of birth ", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (getPassword.isEmpty() || getConfirmPassword.isEmpty()) {
                    Toast.makeText(getActivity(), "all input are required ", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (getPassword.length() < 6) {
                    Toast.makeText(getActivity(), "password most be at lest 6 character", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!getPassword.equals(getConfirmPassword)) {
                    Toast.makeText(getActivity(), "password and confirm password are not the same", Toast.LENGTH_SHORT).show();
                    return;
                }


                Toast.makeText(getActivity(), "all good", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.VISIBLE);

                createAccount(getEmail, getPassword, getUsername, getShowDate);

            }
        });

        return view;
    }

    private void createAccount(String getEmail, String getPassword, String getUsername, String getShowDate) {

        Map<String, Object> map = new HashMap<>();
        map.put("username", getUsername);
        map.put("imageUrl", "default");
        map.put("dateOfBirth", getShowDate);
        map.put("coverImageUrl", "default");

        UsersViewModel usersViewModel = ViewModelProviders.of(getActivity()).get(UsersViewModel.class);
        usersViewModel.userDetailsSignUpMutable.setValue(map);
        usersViewModel.email.setValue(getEmail);
        usersViewModel.password.setValue(getPassword);
        usersViewModel.createAccount();

    }

    public void SignUp(View view) {


    }


}
