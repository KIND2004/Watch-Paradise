package lk.xrontech.watchparadise.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.UUID;

import lk.xrontech.watchparadise.R;
import lk.xrontech.watchparadise.activity.MainActivity;
import lk.xrontech.watchparadise.model.User;

public class MyAccountFragment extends Fragment {
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth firebaseAuth;
    private EditText txtUserFullName, txtUserAddressNo, txtUserStreetName, txtUserCity, txtUserZipCode;
    private TextView txtUserEmail;
    private ImageButton imageUserProfilePic;
    private Uri userProfileUri;
    private User user;
    private String documentId;
    private SharedPreferences userDetails;
    private String userProfileImageId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        userDetails = getActivity().getSharedPreferences("user_details", Context.MODE_PRIVATE);

        txtUserFullName = view.findViewById(R.id.txtUserFullName);
        txtUserEmail = view.findViewById(R.id.txtUserEmail);
        txtUserAddressNo = view.findViewById(R.id.txtUserAddressNo);
        txtUserStreetName = view.findViewById(R.id.txtUserStreetName);
        txtUserCity = view.findViewById(R.id.txtUserCity);
        txtUserZipCode = view.findViewById(R.id.txtUserZipCode);

        imageUserProfilePic = view.findViewById(R.id.imageUserProfilePic);

        imageUserProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                intentActivityResultLauncher.launch(Intent.createChooser(intent, "Select Product Image"));
            }
        });

        firebaseFirestore.collection("users")
                .whereEqualTo("email", userDetails.getString("email", null))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot snapshot = task.getResult();
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                documentId = queryDocumentSnapshot.getId();
                            }
                            user = snapshot.getDocuments().get(0).toObject(User.class);
                            if (user != null) {
                                txtUserFullName.setText(user.getFullName());
                                txtUserEmail.setText(user.getEmail());
                                txtUserAddressNo.setText(user.getAddressNo());
                                txtUserStreetName.setText(user.getStreetName());
                                txtUserCity.setText(user.getCity());
                                txtUserZipCode.setText(user.getZipCode());

                                firebaseStorage.getReference("user-profile-images/" + user.getProfileUri())
                                        .getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                if (uri != null) {
                                                    Picasso.get()
                                                            .load(uri)
                                                            .fit()
                                                            .into(imageUserProfilePic);
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        }
                    }
                });

        view.findViewById(R.id.btnUpdateProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userFullName = txtUserFullName.getText().toString();
                String userAddressNo = txtUserAddressNo.getText().toString();
                String userStreetName = txtUserStreetName.getText().toString();
                String userCity = txtUserCity.getText().toString();
                String userZipCode = txtUserZipCode.getText().toString();

                if (userFullName.equals("")) {
                    Toast.makeText(getContext(), "Please Enter Full Name", Toast.LENGTH_SHORT).show();
                } else if (userAddressNo.equals("")) {
                    Toast.makeText(getContext(), "Please Enter Address No", Toast.LENGTH_SHORT).show();
                } else if (userStreetName.equals("")) {
                    Toast.makeText(getContext(), "Please Enter Street Name", Toast.LENGTH_SHORT).show();
                } else if (userCity.equals("")) {
                    Toast.makeText(getContext(), "Please Enter City", Toast.LENGTH_SHORT).show();
                } else if (userZipCode.equals("")) {
                    Toast.makeText(getContext(), "Please Enter Zip Code", Toast.LENGTH_SHORT).show();
                } else {

                    user.setFullName(userFullName);

                    if (userProfileImageId == null) {
                        user.setProfileUri(getActivity().getSharedPreferences("user_details", Context.MODE_PRIVATE).getString("profileUri", null));
                    } else {
                        user.setProfileUri(userProfileImageId);
                    }

                    user.setAddressNo(userAddressNo);
                    user.setStreetName(userStreetName);
                    user.setCity(userCity);
                    user.setZipCode(userZipCode);

                    if (documentId != null) {

                        firebaseFirestore.collection("users")
                                .document(documentId)
                                .set(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        SharedPreferences.Editor editor = userDetails.edit();
                                        editor.putString("fullName", user.getFullName());
                                        editor.putString("profileUri", user.getProfileUri());
                                        editor.putString("addressNo", user.getAddressNo());
                                        editor.putString("streetName", user.getStreetName());
                                        editor.putString("city", user.getCity());
                                        editor.putString("zipCode", user.getZipCode());
                                        editor.apply();
                                        if (userProfileUri != null) {
                                            StorageReference reference = firebaseStorage.getReference("user-profile-images")
                                                    .child(userProfileImageId);
                                            reference.putFile(userProfileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    SharedPreferences.Editor editor = userDetails.edit();
                                                    editor.putString("profileUri", userProfileImageId);
                                                    editor.apply();
                                                    Toast.makeText(getContext(), "User Profile Details Updated Success", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(getActivity(), "Something Went Wrong!", Toast.LENGTH_LONG).show();
                    }

                }

            }
        });

        view.findViewById(R.id.btnSendResetPasswordLink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.sendPasswordResetEmail(userDetails.getString("email", null))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Reset Password Link Sent. Please Check Your Email", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Reset Password Link Sending Fail!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        view.findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = userDetails.edit();
                editor.clear();
                editor.apply();
                startActivity(new Intent(getContext(), MainActivity.class));
                firebaseAuth.signOut();
            }
        });

    }

    ActivityResultLauncher<Intent> intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                userProfileUri = result.getData().getData();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(), userProfileUri);
                    try {
                        Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                        imageUserProfilePic.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        Toast.makeText(getContext(), "Failed to Select Image", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), userProfileUri);
                        imageUserProfilePic.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        Toast.makeText(getContext(), "Failed to Select Image", Toast.LENGTH_SHORT).show();
                    }
                }

                Picasso.get()
                        .load(userProfileUri)
                        .fit()
                        .into(imageUserProfilePic);

                userProfileImageId = UUID.randomUUID().toString();
            }
        }
    });
}