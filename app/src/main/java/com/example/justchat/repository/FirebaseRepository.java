package com.example.justchat.repository;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.justchat.MainActivity;
import com.example.justchat.cloudmessaging.APIClient;
import com.example.justchat.cloudmessaging.Data;
import com.example.justchat.cloudmessaging.MessageBody;
import com.example.justchat.cloudmessaging.RetrofitClient;
import com.example.justchat.cloudmessaging.Token;
import com.example.justchat.model.Chats;
import com.example.justchat.model.ChatsList;
import com.example.justchat.model.Users;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirebaseRepository {
    private static final String TAG = "FirebaseRepository";
    public MutableLiveData<Map> userDetailsSignUpMutable = new MutableLiveData<>();
    Context context;

    public MutableLiveData<Boolean> loginSuccessful = new MutableLiveData<>();

    public MutableLiveData<Boolean> signUpSuccessful = new MutableLiveData<>();

    public MutableLiveData<Users> usersMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<Users> usersMutableLiveDataWithId = new MutableLiveData<>();

    public MutableLiveData<List<Users>> listMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<List<Users>> searchUserMutableLiveData = new MutableLiveData<>();


    public MutableLiveData<List<Users>> chatUsersMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<List<Chats>> chatListMutableLiveData = new MutableLiveData<>();

    Uri imageUrl;
    StorageTask imageTask;
    StorageReference storageReference;
    StorageReference storageReference_cover;

    String senderName;
    String to;
    APIClient apiClient;
//    String to = FirebaseInstanceId.getInstance().getToken();

    public FirebaseRepository(Context context) {
        this.context = context;
    }

    public void f(String email, String password) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    assert firebaseUser != null;
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

                    Map<String, Object> map = new HashMap<>();
                    map.putAll(userDetailsSignUpMutable.getValue());
                    map.put("userId", firebaseUser.getUid());

                    databaseReference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(context, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                context.startActivity(intent);
                            }
                        }
                    });

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                signUpSuccessful.setValue(false);
            }
        });

    }

    public void login(String email, String password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                loginSuccessful.setValue(false);
            }
        });
    }

    public void getUserDetails() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        assert firebaseUser != null;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Users users = dataSnapshot.getValue(Users.class);
                usersMutableLiveData.setValue(users);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getWithId(String id) {


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(id);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Users users = dataSnapshot.getValue(Users.class);
                usersMutableLiveDataWithId.setValue(users);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getAllUsers() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final List<Users> usersList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Users users = snapshot.getValue(Users.class);

                    assert firebaseUser != null;
                    assert users != null;
                    if (!users.getUserId().equals(firebaseUser.getUid())) {
                        usersList.add(users);
                    }

                }

                listMutableLiveData.setValue(usersList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void sendUserMessageToFirebase(final String senderId, final String receiverId, String message, boolean isseen) {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");

        HashMap<String, Object> map = new HashMap<>();
        map.put("senderId", senderId);
        map.put("receiverId", receiverId);
        map.put("message", message);
        map.put("issen", isseen);

        databaseReference.push().setValue(map);

        new AddToChatList(senderId, receiverId);

        final DatabaseReference chat = FirebaseDatabase.getInstance().getReference("ChatsList").child(senderId).child(receiverId);
        chat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Map<String, String> map1 = new HashMap<>();
                    map1.put("id", receiverId);

                    chat.setValue(map1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference chat2 = FirebaseDatabase.getInstance().getReference("ChatsList").child(receiverId).child(senderId);
        chat2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Map<String, String> map1 = new HashMap<>();
                    map1.put("id", senderId);

                    chat2.setValue(map1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        DatabaseReference databaseReferenceUsers = FirebaseDatabase.getInstance().getReference("Users").child(senderId);
        databaseReferenceUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Users users = dataSnapshot.getValue(Users.class);
                assert users != null;
                senderName = users.getUsername();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference getToken = FirebaseDatabase.getInstance().getReference("Tokens").child(receiverId);
        getToken.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Token token = dataSnapshot.getValue(Token.class);
                to = token.getToken();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Data data = new Data(senderId, senderName, message);
//        String to = FirebaseInstanceId.getInstance().getToken();
        MessageBody messageBody = new MessageBody(to, data);
        new SendNotification(messageBody).execute();

    }

    public void sendUserMessageToFirebaseForChat(final String senderId, final String receiverId, String message, boolean isseen) {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");

        HashMap<String, Object> map = new HashMap<>();
        map.put("senderId", senderId);
        map.put("receiverId", receiverId);
        map.put("message", message);
        map.put("issen", isseen);

        databaseReference.push().setValue(map);

        DatabaseReference databaseReferenceUsers = FirebaseDatabase.getInstance().getReference("Users").child(senderId);
        databaseReferenceUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Users users = dataSnapshot.getValue(Users.class);
                assert users != null;
                senderName = users.getUsername();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference getToken = FirebaseDatabase.getInstance().getReference("Tokens").child(receiverId);
        getToken.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Token token = dataSnapshot.getValue(Token.class);
                to = token.getToken();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Data data = new Data(senderId, senderName, message);
//        String to = FirebaseInstanceId.getInstance().getToken();
        MessageBody messageBody = new MessageBody(to, data);
        new SendNotification(messageBody).execute();

    }

    public void readMessageFromFirebase(final String senderId, final String receiverId) {
        final List<Chats> chatsList = new ArrayList<>();

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatsList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Chats chats = snapshot.getValue(Chats.class);
                    assert chats != null;
                    if (chats.getSenderId().equals(senderId) && chats.getReceiverId().equals(receiverId)
                            || chats.getSenderId().equals(receiverId) && chats.getReceiverId().equals(senderId)) {
                        chatsList.add(chats);
                    }

                }

                chatListMutableLiveData.setValue(chatsList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void issen(final String senderId, final String receiverId) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Chats chats = snapshot.getValue(Chats.class);
                    assert chats != null;
                    if (chats.getSenderId().equals(receiverId) && chats.getReceiverId().equals(senderId)) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("issen", true);

                        snapshot.getRef().updateChildren(map);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getChatUsers() {

        final List<ChatsList> chatsListList = new ArrayList<>();
        final List<Users> usersList = new ArrayList<>();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        assert firebaseUser != null;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChatsList").child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatsListList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatsList chatsList = snapshot.getValue(ChatsList.class);
                    chatsListList.add(chatsList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference users = FirebaseDatabase.getInstance().getReference("Users");
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Users users1 = snapshot.getValue(Users.class);
                    for (ChatsList chatsList : chatsListList) {
                        assert users1 != null;
                        if (users1.getUserId().equals(chatsList.getId())) {
                            usersList.add(users1);
                        }
                    }
                }

                chatUsersMutableLiveData.setValue(usersList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void updateStatus(String status) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        assert firebaseUser != null;
        String fUser = firebaseUser.getUid();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(fUser);

            Map<String, Object> map = new HashMap<>();
            map.put("status", status);

            databaseReference.updateChildren(map);

    }

    public void getLastMessage(final TextView textView, final String otherId) {

//        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        lastMessage = "default";
//
//        UsersViewModel usersViewModel = ViewModelProviders.of((FragmentActivity) context).get(UsersViewModel.class);
//        assert user != null;
//        usersViewModel.readMessageFromFirebase(user.getUid(), otherId).observe((LifecycleOwner) context, new Observer<List<Chats>>() {
//            @Override
//            public void onChanged(List<Chats> chats) {
//                lastMessage = "";
//
//                for (Chats chats1 : chats) {
//                    if (chats1.getReceiverId().equals(otherId) || chats1.getSenderId().equals(user.getUid())) {
////                        lastMessage = chats1.getMessage();
//                        textView.setText(chats1.getMessage());
//                    }
////                    textView.setText(lastMessage);
//                }
////                textView.setText(lastMessage);
//
//            }
//        });

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chats chat = snapshot.getValue(Chats.class);
                    assert firebaseUser != null;
                    assert chat != null;
                    if (chat.getSenderId().equals(firebaseUser.getUid()) && chat.getReceiverId().equals(otherId) ||
                            chat.getSenderId().equals(otherId) && chat.getReceiverId().equals(firebaseUser.getUid())) {
                        textView.setText(chat.getMessage());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateProfile(String username, String bio, String date) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        assert firebaseUser != null;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        Map<String, Object> map = new HashMap<>();
        map.put("username", username);
        map.put("aboutUser", bio);
        map.put("dateOfBirth", date);

        databaseReference.updateChildren(map);
    }

    public String myImageUrl(Uri uri) {

        ContentResolver contentResolver = context.getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    public void updateProfileImage(Uri uri) {

        imageUrl = uri;
        storageReference = FirebaseStorage.getInstance().getReference("profile");
//        storageReference_cover = FirebaseStorage.getInstance().getReference("profile_cover");
        signUpSuccessful.setValue(true);

        if (imageUrl != null) {
            final StorageReference storageReference1 = storageReference.child(System.currentTimeMillis() + "." + myImageUrl(imageUrl));
            imageTask = storageReference1.putFile(imageUrl);
            imageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return storageReference1.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()) {

                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                        Uri downloadUri = task.getResult();
                        String myUri = downloadUri.toString();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageUrl", myUri);

                        databaseReference.updateChildren(map);

                        signUpSuccessful.setValue(false);

                    } else {
                        Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show();
                        signUpSuccessful.setValue(false);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    signUpSuccessful.setValue(false);
                }
            });
        } else {
            Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show();
            signUpSuccessful.setValue(false);
        }
    }

    public void uploadCoverImage(Uri uri) {

        signUpSuccessful.setValue(true);

        storageReference_cover = FirebaseStorage.getInstance().getReference("profile_cover");

        imageUrl = uri;

        if (imageUrl != null) {
            final StorageReference storageReference1 = storageReference_cover.child(System.currentTimeMillis() + "." + myImageUrl(imageUrl));
            imageTask = storageReference1.putFile(imageUrl);
            imageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return storageReference1.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()) {

                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                        Uri downloadUri = task.getResult();
                        String myUri = downloadUri.toString();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("coverImageUrl", myUri);

                        databaseReference.updateChildren(map);
                        signUpSuccessful.setValue(false);
                    } else {
                        Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show();
                        signUpSuccessful.setValue(false);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    signUpSuccessful.setValue(false);
                }
            });
        } else {
            Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show();
            signUpSuccessful.setValue(false);
        }
    }

    public void updateProfileImageWithByte(byte[] bytes) {
        signUpSuccessful.setValue(true);
        storageReference = FirebaseStorage.getInstance().getReference("profile");
        final StorageReference reference = storageReference.child(System.currentTimeMillis() + "");
        UploadTask uploadTask = reference.putBytes(bytes);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                return reference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri getUri = task.getResult();
                    String theUri = getUri.toString();

                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("imageUrl", theUri);

                    databaseReference.updateChildren(map);

                    signUpSuccessful.setValue(false);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                signUpSuccessful.setValue(false);
            }
        });

    }

    public void uploadCoverImageWithByte(byte[] bytes) {
        signUpSuccessful.setValue(true);
        storageReference_cover = FirebaseStorage.getInstance().getReference("profile_cover");
        final StorageReference reference = storageReference_cover.child(System.currentTimeMillis() + "");
        UploadTask uploadTask = reference.putBytes(bytes);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                return reference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri getUri = task.getResult();
                    String theUri = getUri.toString();

                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("coverImageUrl", theUri);

                    databaseReference.updateChildren(map);

                    signUpSuccessful.setValue(false);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                signUpSuccessful.setValue(false);
            }
        });

    }

    class SendNotification extends AsyncTask<Void, Void, Void> {

        MessageBody messageBody;

        public SendNotification(MessageBody messageBody) {
            this.messageBody = messageBody;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            apiClient = RetrofitClient.getRetrofit().create(APIClient.class);

            apiClient.sendNotification(messageBody).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Toast.makeText(context, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });

            return null;
        }
    }

    class AddToChatList extends AsyncTask<Void, Void, Void> {

        String senderId;
        String receiverId;

        public AddToChatList(String senderId, String receiverId) {
            this.senderId = senderId;
            this.receiverId = receiverId;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }
    }

    public void searchUsers(String searchUserString) {

        final ArrayList<Users> users = new ArrayList<>();


        Query query = FirebaseDatabase.getInstance().getReference("Users").startAt(searchUserString).orderByChild("username").endAt(searchUserString + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();

                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Users users1 = snapshot.getValue(Users.class);
                    assert firebaseUser != null;
                    assert users1 != null;
                    if (!users1.getUserId().equals(firebaseUser.getUid())) {
                        users.add(users1);
                    }
                }

                searchUserMutableLiveData.setValue(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}