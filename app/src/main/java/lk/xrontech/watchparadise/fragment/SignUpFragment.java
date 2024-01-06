package lk.xrontech.watchparadise.fragment;

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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import lk.xrontech.watchparadise.R;
import lk.xrontech.watchparadise.model.User;

public class SignUpFragment extends Fragment {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        EditText txtFullName = view.findViewById(R.id.txtFullName);
        EditText txtEmail = view.findViewById(R.id.txtEmail);
        EditText txtPassword = view.findViewById(R.id.txtPassword);

        view.findViewById(R.id.btnSignup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fullName = txtFullName.getText().toString();
                String email = txtEmail.getText().toString();
                String password = txtPassword.getText().toString();

                if (fullName.equals("")) {
                    Toast.makeText(getContext(), "Please Enter Full Name", Toast.LENGTH_SHORT).show();
                } else if (email.equals("")) {
                    Toast.makeText(getContext(), "Please Enter Email", Toast.LENGTH_SHORT).show();
                } else if (password.equals("")) {
                    Toast.makeText(getContext(), "Please Enter Password", Toast.LENGTH_SHORT).show();
                } else {

                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(fullName)
                                                .build();

                                        currentUser.updateProfile(profileUpdates);

                                        User user = new User();
                                        user.setUserId(currentUser.getUid());
                                        user.setFullName(fullName);
                                        user.setEmail(email);
                                        user.setStatus(true);
                                        firebaseFirestore.collection("users")
                                                .add(user)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
                                                        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
                                                        fragmentTransaction.replace(R.id.authFragment, new LogInFragment());
                                                        fragmentTransaction.commit();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getContext(), "Failed to Register", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                        currentUser.sendEmailVerification();

                                        Toast.makeText(getActivity(), "Verification Email Sent. Please Check Your Mail Inbox", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getActivity(), "Verification Email Sending Failed!. Please Try Again Later.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Something Went Wrong!. Please Try Again Later.", Toast.LENGTH_LONG).show();
                                }
                            });

                }
            }
        });

        view.findViewById(R.id.signUpBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }
}