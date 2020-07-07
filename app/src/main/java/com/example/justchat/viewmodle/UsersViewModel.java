package com.example.justchat.viewmodle;

import android.app.Application;
import android.net.Uri;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.justchat.model.Chats;
import com.example.justchat.model.Users;
import com.example.justchat.repository.FirebaseRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersViewModel extends AndroidViewModel {


    public MutableLiveData<List<Users>> searchUserMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<Boolean> loginSuccessful;

    public MutableLiveData<Users> usersMutableLiveData;

    public MutableLiveData<Users> usersMutableLiveDataWithId;

    public MutableLiveData<Boolean> signUPSuccessful = new MutableLiveData<>();

    public MutableLiveData<Map> userDetailsSignUpMutable = new MutableLiveData<>();

    public MutableLiveData<List<Users>> chatUsersMutableLiveData;

    public MutableLiveData<String> email = new MutableLiveData<>();

    public MutableLiveData<List<Chats>> chatListMutableLiveData;


    public MutableLiveData<String> password = new MutableLiveData<>();
    FirebaseRepository repository;

    public MutableLiveData<List<Users>> listMutableLiveData = new MutableLiveData<>();

    public UsersViewModel(@NonNull Application application) {
        super(application);
        repository = new FirebaseRepository(application);
    }

    public LiveData<Boolean> loginSuccessfulLive() {

        loginSuccessful = repository.loginSuccessful;

        return loginSuccessful;
    }

    public LiveData<Boolean> signUPSuccessfulLive() {

        signUPSuccessful = repository.signUpSuccessful;

        return signUPSuccessful;
    }

    public void login(String email, String password) {
        repository.login(email, password);
    }

    public void createAccount() {

        repository.f(email.getValue(), password.getValue());

        Map<String, Object> map = new HashMap<>(userDetailsSignUpMutable.getValue());

        repository.userDetailsSignUpMutable.setValue(map);
    }

    public LiveData<Users> usersLiveData() {

        repository.getUserDetails();

        usersMutableLiveData = repository.usersMutableLiveData;

        return usersMutableLiveData;

    }

    public LiveData<Users> usersLiveDataWithId(String id) {

        repository.getWithId(id);

        usersMutableLiveDataWithId = repository.usersMutableLiveDataWithId;

        return usersMutableLiveDataWithId;

    }

    public LiveData<List<Users>> getAllUsers() {

        repository.getAllUsers();

        listMutableLiveData = repository.listMutableLiveData;

        return listMutableLiveData;

    }


    public void sendUserMessageToFirebase(String senderId, String receiverId, String message, boolean isseen) {
        repository.sendUserMessageToFirebase(senderId, receiverId, message, isseen);
    }

    public LiveData<List<Chats>> readMessageFromFirebase(final String senderId, final String receiverId) {

        repository.readMessageFromFirebase(senderId, receiverId);

        chatListMutableLiveData = repository.chatListMutableLiveData;

        return chatListMutableLiveData;
    }

    public void issen(final String senderId, final String receiverId) {
        repository.issen(senderId, receiverId);
    }

    public LiveData<List<Users>> getChatUsers() {

        repository.getChatUsers();

        chatUsersMutableLiveData = repository.chatUsersMutableLiveData;

        return chatUsersMutableLiveData;

    }

    public void updateStatus(String status) {
        repository.updateStatus(status);
    }

    public void getLastMessage(final TextView textView, final String otherId) {
        repository.getLastMessage(textView, otherId);
    }

    public void updateProfile(String username, String bio, String date) {
        repository.updateProfile(username, bio, date);
    }

    public void updateProfileImage(Uri uri) {
        repository.updateProfileImage(uri);
    }

    public void uploadCoverImage(Uri uri) {
        repository.uploadCoverImage(uri);
    }

    public void updateProfileImageWithByte(byte[] bytes) {
        repository.updateProfileImageWithByte(bytes);
    }

    public void uploadCoverImageWithByte(byte[] bytes) {
        repository.uploadCoverImageWithByte(bytes);
    }

    public void sendUserMessageToFirebaseForChat(final String senderId, final String receiverId, String message, boolean isseen) {
        repository.sendUserMessageToFirebaseForChat(senderId, receiverId, message, isseen);
    }

    public LiveData<List<Users>> searchUsers(String searchUserString) {
        repository.searchUsers(searchUserString);

        searchUserMutableLiveData = repository.searchUserMutableLiveData;
        return searchUserMutableLiveData;
    }
}