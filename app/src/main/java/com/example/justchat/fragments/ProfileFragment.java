package com.example.justchat.fragments;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.provider.MediaStore;
import android.transition.Explode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.justchat.EditProfileActivity;
import com.example.justchat.R;
import com.example.justchat.model.Users;
import com.example.justchat.viewmodle.UsersViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.anim.slide_out_right;
import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {

    ImageView coverImage;
    CircleImageView usersImage;

    TextView username;
    TextView dateOfBirth;
    TextView aboutUser;

    UsersViewModel usersViewModel;

    Button button;

    Uri imageUrl;
    StorageTask imageTask;
    StorageReference storageReference;
    StorageReference storageReference_cover;

    public static final int request_code_cover = 12;
    public static final int request_code_main = 10;
    public static final int request_code_main_camera = 11;
    public static final int request_code_cover_camera = 13;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        coverImage = view.findViewById(R.id.cover_image);
        usersImage = view.findViewById(R.id.profile_image);

        button = view.findViewById(R.id.edit_profile_btn);

        username = view.findViewById(R.id.username);
        dateOfBirth = view.findViewById(R.id.date);
        aboutUser = view.findViewById(R.id.about_user);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference("profile");
        storageReference_cover = FirebaseStorage.getInstance().getReference("profile_cover");

        usersImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle("Upload profile picture")
                        .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getImageFromCamera(request_code_main_camera);
                            }
                        }).setNegativeButton("Upload", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getImage(request_code_main);
                            }
                        }).show();


            }
        });
        coverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle("Upload cover picture")
                        .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getImageFromCamera(request_code_cover_camera);
                            }
                        }).setNegativeButton("Upload", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getImage(request_code_cover);
                            }
                        }).show();

            }
        });

        usersViewModel = ViewModelProviders.of(this).get(UsersViewModel.class);
        assert firebaseUser != null;
        usersViewModel.usersLiveDataWithId(firebaseUser.getUid()).observe(this, new Observer<Users>() {
            @Override
            public void onChanged(Users users) {

                username.setText(users.getUsername());
                dateOfBirth.setText(users.getDateOfBirth());
                aboutUser.setText(users.getAboutUser());

                if (users.getImageUrl().equals("default")) {
                    usersImage.setImageResource(R.drawable.no_profile);
                } else {
                    Glide.with(getActivity()).load(users.getImageUrl()).into(usersImage);
                }

                if (users.getCoverImageUrl().equals("default")) {
                    coverImage.setImageResource(R.drawable.cover);
                } else {
                    Glide.with(getActivity()).load(users.getCoverImageUrl()).into(coverImage);
                }

                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Uploading...");
                progressDialog.setCancelable(false);

                usersViewModel.signUPSuccessfulLive().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        if (aBoolean) {
                            progressDialog.show();
                        } else {
                            progressDialog.dismiss();
                        }
                    }
                });

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getUsername = username.getText().toString();
                String getDate = dateOfBirth.getText().toString();
                String getAboutUser = aboutUser.getText().toString();

                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                intent.putExtra("username", getUsername);
                intent.putExtra("date", getDate);
                intent.putExtra("aboutUser", getAboutUser);
                startActivity(intent, ActivityOptions.makeCustomAnimation(getActivity(), android.R.anim.slide_in_left, slide_out_right).toBundle());

            }
        });
        return view;
    }

    private void getImage(int request_code) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, request_code);
    }

    private void getImageFromCamera(int request_code) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, request_code);
    }

    private String myImageUrl(Uri uri) {

        ContentResolver contentResolver = getActivity().getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

//    public void upload() {
//        final ProgressDialog progressDialog = new ProgressDialog(getContext());
//        progressDialog.setMessage("Uploading...");
//        progressDialog.show();
//
//        if (imageUrl != null) {
//            final StorageReference storageReference1 = storageReference.child(System.currentTimeMillis() + "." + myImageUrl(imageUrl));
//            imageTask = storageReference1.putFile(imageUrl);
//            imageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                @Override
//                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                    if (!task.isSuccessful()) {
//                        throw task.getException();
//                    }
//                    return storageReference1.getDownloadUrl();
//                }
//            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                @Override
//                public void onComplete(@NonNull Task<Uri> task) {
//
//                    if (task.isSuccessful()) {
//
//                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//
//                        Uri downloadUri = task.getResult();
//                        String myUri = downloadUri.toString();
//                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
//
//                        HashMap<String, Object> map = new HashMap<>();
//                        map.put("imageUrl", myUri);
//
//                        databaseReference.updateChildren(map);
//                        progressDialog.dismiss();
//
//                    } else {
//                        Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
//                        progressDialog.dismiss();
//                    }
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                    progressDialog.dismiss();
//                }
//            });
//        } else {
//            Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
//        }
//    }

//    public void uploadCover() {
//        final ProgressDialog progressDialog = new ProgressDialog(getContext());
//        progressDialog.setMessage("Uploading...");
//        progressDialog.show();
//
//        if (imageUrl != null) {
//            final StorageReference storageReference1 = storageReference_cover.child(System.currentTimeMillis() + "." + myImageUrl(imageUrl));
//            imageTask = storageReference1.putFile(imageUrl);
//            imageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                @Override
//                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                    if (!task.isSuccessful()) {
//                        throw task.getException();
//                    }
//                    return storageReference1.getDownloadUrl();
//                }
//            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                @Override
//                public void onComplete(@NonNull Task<Uri> task) {
//
//                    if (task.isSuccessful()) {
//
//                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//
//                        Uri downloadUri = task.getResult();
//                        String myUri = downloadUri.toString();
//                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
//
//                        HashMap<String, Object> map = new HashMap<>();
//                        map.put("coverImageUrl", myUri);
//
//                        databaseReference.updateChildren(map);
//                        progressDialog.dismiss();
//
//                    } else {
//                        Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
//                        progressDialog.dismiss();
//                    }
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                    progressDialog.dismiss();
//                }
//            });
//        } else {
//            Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == request_code_cover && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUrl = data.getData();
            usersViewModel.uploadCoverImage(imageUrl);

        }

        if (requestCode == request_code_main && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUrl = data.getData();
            usersViewModel.updateProfileImage(imageUrl);

        }

        if (requestCode == request_code_main_camera && resultCode == RESULT_OK) {
            assert data != null;
            Bundle bundle = data.getExtras();
            assert bundle != null;
            Bitmap bitmap = (Bitmap) bundle.get("data");

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            assert bitmap != null;
            bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);

            byte[] bytes = byteArrayOutputStream.toByteArray();

           usersViewModel.updateProfileImageWithByte(bytes);
        }
        if (requestCode == request_code_cover_camera && resultCode == RESULT_OK) {
            assert data != null;
            Bundle bundle = data.getExtras();
            assert bundle != null;
            Bitmap bitmap = (Bitmap) bundle.get("data");

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            assert bitmap != null;
            bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);

            byte[] bytes = byteArrayOutputStream.toByteArray();

            usersViewModel.uploadCoverImageWithByte(bytes);
        }

    }
}