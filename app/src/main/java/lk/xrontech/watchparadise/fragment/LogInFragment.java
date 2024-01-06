package lk.xrontech.watchparadise.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import lk.xrontech.watchparadise.R;
import lk.xrontech.watchparadise.activity.MainActivity;
import lk.xrontech.watchparadise.model.User;

public class LogInFragment extends Fragment {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String documentId;
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        EditText txtLogInEmail = view.findViewById(R.id.txtLogInEmail);
        EditText txtLogInPassword = view.findViewById(R.id.txtLogInPassword);

        view.findViewById(R.id.btnLogIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = txtLogInEmail.getText().toString();
                String password = txtLogInPassword.getText().toString();

                if (email.equals("")) {
                    Toast.makeText(getContext(), "Please Enter Email", Toast.LENGTH_LONG).show();
                } else if (password.equals("")) {
                    Toast.makeText(getContext(), "Please Enter Password", Toast.LENGTH_LONG).show();
                } else {
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                                firebaseFirestore.collection("users")
                                        .whereEqualTo("email", currentUser.getEmail())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    QuerySnapshot result = task.getResult();
                                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                                        documentId = queryDocumentSnapshot.getId();
                                                    }
                                                    user = result.getDocuments().get(0).toObject(User.class);
                                                    if (user != null) {
                                                        SharedPreferences defaultSharedPreferences = getContext().getSharedPreferences("user_details", Context.MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = defaultSharedPreferences.edit();
                                                        editor.putString("email", email);
                                                        editor.putString("password", password);
                                                        editor.putString("documentId", documentId);
                                                        editor.putString("userId", user.getUserId());
                                                        editor.putString("fullName", user.getFullName());
                                                        editor.putString("profileUri", user.getProfileUri());
                                                        editor.putString("addressNo", user.getAddressNo());
                                                        editor.putString("streetName", user.getStreetName());
                                                        editor.putString("city", user.getCity());
                                                        editor.putString("zipCode", user.getZipCode());
                                                        editor.putBoolean("status", user.getStatus());
                                                        editor.apply();

                                                        Toast.makeText(getActivity(), "Success!", Toast.LENGTH_LONG).show();

                                                        startActivity(new Intent(getContext(), MainActivity.class));
                                                        getActivity().finish();
                                                    }
                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(getActivity(), "Invalid Credentials!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Invalid Details", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        view.findViewById(R.id.forgotPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtLogInEmail.getText().toString();
                if (email.equals("")) {
                    Toast.makeText(getContext(), "Please Enter Email", Toast.LENGTH_LONG).show();
                } else {
                    firebaseAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Forgot Password Link Sent. Please Check Your Email", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Forgot Password Link Sending Fail!", Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });

        view.findViewById(R.id.signInBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

    }
}